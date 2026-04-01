# Fix Errors Proposal

## Intent and Scope

This proposal aims to standardize error handling in the Petcare Backend Spring Boot application by:
1. Replacing raw Java exceptions (IllegalArgumentException, NullPointerException, etc.) with domain-specific custom exceptions
2. Ensuring proper transaction management with @Transactional annotations
3. Enhancing logging consistency across the application
4. Improving validation error response handling

The changes will focus on service layer implementations where business logic resides, ensuring consistent error handling patterns that align with the existing GlobalExceptionHandler.

## Specific Changes

### 1. Standardizing Exception Usage

**Problem**: 172 instances of raw Java exceptions found throughout the codebase (IllegalArgumentException, NullPointerException, etc.) that should be replaced with domain-specific custom exceptions.

**Solution**: Replace raw exceptions with appropriate custom exceptions from `com.Petcare.Petcare.Exception.Business` package.

**Files to Modify** (based on grep findings):
- `src/main/java/com/Petcare/Petcare/Services/Implement/UserServiceImplement.java` - Replace IllegalArgumentException with EmailAlreadyExistsException, UserNotFoundException
- `src/main/java/com/Petcare/Petcare/Services/Implement/SitterServiceImplement.java` - Replace IllegalArgumentException with SitterNotFoundException
- `src/main/java/com/Petcare/Petcare/Services/Implement/ServiceOffering/ServiceOfferingServiceImplement.java` - Replace IllegalArgumentException with ServiceOfferingNotFoundException, ServiceOfferingInactiveException
- `src/main/java/com/Petcare/Petcare/Services/Implement/InvoiceServiceImplement.java` - Replace IllegalArgumentException with InvoiceNotFoundException, BookingNotFoundException, InvalidAmountException
- `src/main/java/com/Petcare/Petcare/Services/Implement/PaymentMethodServiceImplement.java` - Replace IllegalArgumentException with AccountNotFoundException
- `src/main/java/com/Petcare/Petcare/Services/Implement/PetServiceImplement.java` - Replace IllegalArgumentException with PetNotFoundException, AccountNotFoundException
- `src/main/java/com/Petcare/Petcare/Services/Implement/BookingServiceImplement.java` - Replace IllegalArgumentException with PetNotFoundException, SitterNotFoundException, BookingNotFoundException
- `src/main/java/com/Petcare/Petcare/Services/Implement/EmailServiceImplement.java` - Replace IllegalArgumentException with appropriate validation exceptions
- `src/main/java/com/Petcare/Petcare/DTOs/*/*.java` - Replace IllegalArgumentException in DTO conversion methods with appropriate validation exceptions

**Approach**:
1. Identify the specific business meaning of each raw exception
2. Map to existing custom exception or create new one if needed
3. Replace throw statements with appropriate custom exceptions
4. Update Javadoc to reflect new exception types

### 2. Transaction Management Improvements

**Problem**: Need to ensure @Transactional annotations are properly applied, especially on write operations.

**Solution**: Review and standardize @Transactional usage across service implementations.

**Files to Check**:
- All service implementation files in `src/main/java/com/Petcare/Petcare/Services/Implement/`

**Approach**:
1. Ensure all methods that modify data have @Transactional (not just readOnly = true)
2. Ensure read-only methods are marked with @Transactional(readOnly = true)
3. Remove @Transactional from methods that don't need it (pure computation methods)
4. Verify propagation settings where necessary

### 3. Logging Consistency Enhancements

**Problem**: Inconsistent logging practices across service implementations.

**Solution**: Standardize logging using SLF4J with appropriate log levels.

**Files to Modify**:
- Service implementation classes missing @Slf4j annotation
- Inconsistent log message formats

**Approach**:
1. Ensure all service implementations have `@Slf4j` annotation
2. Standardize log message format: `"[Action] [Entity]: {}` for consistency
3. Use appropriate log levels:
   - INFO for significant business operations
   - WARN for recoverable issues
   - DEBUG for detailed tracing
   - ERROR for unexpected exceptions

### 4. Validation Error Response Improvements

**Problem**: While GlobalExceptionHandler handles validation well, some validation logic is duplicated or inconsistent.

**Solution**: Centralize validation logic and ensure consistent use of Bean Validation annotations.

**Files to Modify**:
- DTO classes in `src/main/java/com/Petcare/Petcare/DTOs/`
- Service validation logic

**Approach**:
1. Ensure all DTOs use appropriate Bean Validation annotations (@NotBlank, @Email, @Size, etc.)
2. Remove manual validation checks that duplicate Bean Validation
3. Ensure service methods use @Valid on DTO parameters where appropriate
4. Standardize validation error messages

## Recommended Approaches

### Exception Standardization Process
1. Create mapping document of raw exceptions to custom exceptions
2. Process each service implementation file systematically
3. Run tests to ensure behavior hasn't changed
4. Verify GlobalExceptionHandler mappings are correct

### Transaction Management Review
1. Audit all service methods for proper @Transactional usage
2. Focus on methods that perform creates, updates, deletes
3. Ensure read-only queries are properly marked
4. Verify no missing transactions on cascading operations

### Logging Standardization
1. Add @Slf4j to all service implementations missing it
2. Create logging template: `log.info("Creating {} with email: {}", entityType, identifier)`
3. Ensure exception logging includes context but doesn't leak sensitive data

### Validation Improvements
1. Audit all DTOs for proper validation annotations
2. Remove redundant validation in service layers
3. Ensure controller methods use @Valid appropriately
4. Test validation error responses match existing format

## Estimated Effort

- **Exception Standardization**: 8-12 hours (172 instances across ~15 files)
- **Transaction Management**: 4-6 hours (review of ~20 service files)
- **Logging Consistency**: 2-3 hours (adding annotations and standardizing format)
- **Validation Improvements**: 3-5 hours (review of DTOs and service validation)
- **Testing & Verification**: 4-6 hours
- **Total Estimated Effort**: 21-32 hours

## Dependencies and Prerequisites

1. **Prerequisites**:
   - Understanding of existing custom exceptions in `com.Petcare.Petcare.Exception.Business`
   - Familiarity with Spring @Transactional semantics
   - Knowledge of SLF4J logging practices
   - Understanding of Bean Validation (Jakarta Validation)

2. **Dependencies**:
   - No external library changes required
   - All custom exceptions already exist in the codebase
   - GlobalExceptionHandler already handles most scenarios
   - Existing test suite should continue to pass after changes

3. **Risk Mitigation**:
   - Changes are largely mechanical (replacing exception types)
   - Transactional changes mainly involve adding/removing annotations
   - Logging changes are additive and non-breaking
   - Validation improvements rely on existing framework features
   - Recommend creating backup branch before implementation
   - Run full test suite after changes to ensure no regressions

## Implementation Order

1. **Start with Logging Consistency** (lowest risk, immediate benefit)
2. **Proceed with Exception Standardization** (core functionality, moderate risk)
3. **Address Transaction Management** (important for data integrity)
4. **Finish with Validation Improvements** (polish and consistency)

Each phase should include:
- Implementation
- Local testing
- Test suite execution
- Code review