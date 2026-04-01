# Design: fix-errors

## Technical Approach

This design documents the technical implementation for standardizing error handling in the Petcare Backend Spring Boot application. The approach involves systematically replacing raw Java exceptions with domain-specific custom exceptions, ensuring proper transaction management, standardizing logging practices, and centralizing validation logic.

The implementation leverages the existing custom exception infrastructure in `com.Petcare.Petcare.Exception.Business` and the established `GlobalExceptionHandler` patterns.

## Architecture Decisions

### Decision: Exception Standardization Mapping

**Choice**: Replace raw Java exceptions with domain-specific custom exceptions using a mapping table approach.

**Alternatives considered**:
- Creating a generic wrapper exception (rejected - loses business context)
- Using a generic "BusinessException" for all cases (rejected - too generic)
- Keeping raw exceptions (rejected - inconsistent error handling)

**Rationale**: The codebase already has 30+ custom exceptions in the Business package. Each exception provides specific business context that enables better error handling, logging, and API responses. The mapping table ensures consistent replacement across all 172 instances found.

### Decision: Transaction Management Strategy

**Choice**: Audit each service method to determine transaction requirements based on Spring's default propagation (REQUIRED).

**Alternatives considered**:
- Adding @Transactional to all service methods (rejected - unnecessary overhead for read-only)
- Using transaction templates manually (rejected - unnecessary complexity)
- Leaving as-is (rejected - spec requires standardization)

**Rationale**: Spring's default @Transactional(REQUIRED) is appropriate for most cases. Read-only methods benefit from readOnly=true for performance. The audit ensures no missing transactions on write operations and proper optimization for reads.

### Decision: Logging Format Standardization

**Choice**: Standardize log format to `[Action] [Entity]: {identifier}` using SLF4J.

**Alternatives considered**:
- Using structured logging (JSON) (rejected - requires additional dependencies)
- Different format per service (rejected - inconsistent)
- No standardization (rejected - spec requires improvement)

**Rationale**: SLF4J is already in use. The format provides clear context while maintaining readability. Using appropriate log levels (INFO/WARN/ERROR) ensures proper monitoring without log spam.

### Decision: Validation Centralization

**Choice**: Use Bean Validation (@Valid + annotations) and remove manual validation in services.

**Alternatives considered**:
- Manual validation in services only (rejected - inconsistent, duplicated effort)
- Custom validators per DTO (rejected - over-engineering)
- Mix of both approaches (rejected - inconsistent)

**Rationale**: Spring's Bean Validation with @Valid is the standard approach. Controllers already use it. Removing manual validation in services eliminates duplication and ensures single source of truth for validation rules.

## Data Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        REQUEST FLOW                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  HTTP Request                                                             │
│       │                                                                   │
│       ▼                                                                   │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                  │
│  │ Controller │────▶│  Service    │────▶│ Repository  │                  │
│  └─────────────┘     └─────────────┘     └─────────────┘                  │
│       │                   │                   │                             │
│       │                   │                   │                             │
│       ▼                   ▼                   ▼                             │
│  @Valid (Bean         @Transactional    JPA/Hibernate                     │
│  Validation)         (REQUIRED)                                      │
│                                                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                    ERROR HANDLING FLOW                               │  │
│  ├─────────────────────────────────────────────────────────────────────┤  │
│  │                                                                      │  │
│  │  BusinessException thrown                                            │  │
│  │       │                                                              │  │
│  │       ▼                                                              │  │
│  │  GlobalExceptionHandler                                             │  │
│  │       │                                                              │  │
│  │       ▼                                                              │  │
│  │  ErrorResponseDTO (consistent format)                               │  │
│  │       │                                                              │  │
│  │       ▼                                                              │  │
│  │  HTTP Response (status + body)                                      │  │
│  │                                                                      │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                           │
└─────────────────────────────────────────────────────────────────────────────┘
```

## File Changes

### Exception Standardization

| File | Action | Description |
|------|--------|-------------|
| `Services/Implement/UserServiceImplement.java` | Modify | Replace IllegalArgumentException → EmailAlreadyExistsException, UserNotFoundException |
| `Services/Implement/SitterServiceImplement.java` | Modify | Replace IllegalArgumentException → SitterNotFoundException |
| `Services/Implement/ServiceOffering/ServiceOfferingServiceImplement.java` | Modify | Replace IllegalArgumentException → ServiceOfferingNotFoundException, ServiceOfferingInactiveException |
| `Services/Implement/InvoiceServiceImplement.java` | Modify | Replace IllegalArgumentException → InvoiceNotFoundException, BookingNotFoundException, InvalidAmountException |
| `Services/Implement/PaymentMethodServiceImplement.java` | Modify | Replace IllegalArgumentException → AccountNotFoundException |
| `Services/Implement/PetServiceImplement.java` | Modify | Replace IllegalArgumentException → PetNotFoundException, AccountNotFoundException |
| `Services/Implement/BookingServiceImplement.java` | Modify | Replace IllegalArgumentException → PetNotFoundException, SitterNotFoundException, BookingNotFoundException |
| `Services/Implement/EmailServiceImplement.java` | Modify | Replace IllegalArgumentException → validation exceptions |
| `DTOs/*/*.java` | Modify | Replace IllegalArgumentException in DTO conversion methods |

### Transaction Management

| File | Action | Description |
|------|--------|-------------|
| All service implementations | Audit + Modify | Add @Transactional(readOnly=true) for reads, verify writes have @Transactional |

### Logging Consistency

| File | Action | Description |
|------|--------|-------------|
| Service implementations missing @Slf4j | Modify | Add @Slf4j annotation |
| All service implementations | Modify | Standardize log format to `[Action] [Entity]: {}` |

### Validation Improvements

| File | Action | Description |
|------|--------|-------------|
| DTOs without validation annotations | Modify | Add Bean Validation annotations (@NotBlank, @Email, @Size) |
| Services with manual validation | Modify | Remove redundant manual validation |
| Controllers without @Valid | Modify | Add @Valid on DTO parameters |

## Interfaces / Contracts

### Custom Exception Mapping Table

```java
// Mapping: Raw Exception → Custom Exception
IllegalArgumentException (null check)          → IllegalArgumentException (keep for truly invalid args)
IllegalArgumentException (not found)           → *NotFoundException (entity-specific)
IllegalArgumentException (already exists)      → *AlreadyExistsException (entity-specific)
IllegalArgumentException (invalid state)       → *StateException (entity-specific)
IllegalArgumentException (validation)          → ValidationException (or rely on Bean Validation)
NullPointerException                           → Should NOT occur (use Optional or null checks)
RuntimeException                               → Domain-specific exception (if business logic)
```

### Log Format Standard

```java
// Standard format: [Action] [Entity]: {identifier}
log.info("Creating user with email: {}", email);
log.info("Updating booking with id: {}", bookingId);
log.info("Deleting pet with id: {}", petId);

// Error logging
log.error("Failed to create user with email: {} - {}", email, ex.getMessage(), ex);
```

### GlobalExceptionHandler Already Handles

```java
// Existing mappings in GlobalExceptionHandler:
@ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
// → 400 Bad Request

@ExceptionHandler({UserNotFoundException.class, PetNotFoundException.class, ...})
// → 404 Not Found

@ExceptionHandler(MethodArgumentNotValidException.class)
// → 400 Bad Request with field errors
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Exception throwing in services | Mock repository, verify correct exception type thrown |
| Unit | Transaction annotations | Verify method-level annotations match intent |
| Unit | Log format output | Verify log messages follow standard format |
| Integration | End-to-end error responses | HTTP calls, verify error format consistency |
| Integration | Validation errors | Submit invalid DTOs, verify error response format |

## Migration / Rollout

**No database migration required.**

Phased rollout:
1. First: Deploy with logging changes (lowest risk)
2. Second: Deploy exception standardization (moderate risk)
3. Third: Deploy transaction management (integrity-critical)
4. Fourth: Deploy validation improvements (final polish)

Each phase should run the test suite and verify in staging before production.

## Open Questions

- [ ] Should we create additional custom exceptions for edge cases not covered by existing ones?
- [ ] Should we add a global handler for unexpected exceptions that logs stack traces differently?
- [ ] Should we implement rate limiting on error endpoints to prevent abuse?

**Recommendation**: Address after initial implementation based on real-world usage patterns.