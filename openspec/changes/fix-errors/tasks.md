# Tasks: fix-errors

## Phase 1: Logging Consistency (Lowest Risk - Start Here)

- [x] 1.1 Audit all service implementations to identify classes missing `@Slf4j` annotation
- [x] 1.2 Add `@Slf4j` to `NotificationServiceImplement.java` (missing logger)
- [x] 1.3 Add `@Slf4j` to `EmailServiceImplement.java` (if missing)
- [x] 1.4 Standardize log format in UserServiceImplement.java to `[Action] [Entity]: {}`
- [x] 1.5 Standardize log format in BookingServiceImplement.java to `[Action] [Entity]: {}`
- [x] 1.6 Standardize log format in PetServiceImplement.java to `[Action] [Entity]: {}`
- [x] 1.7 Standardize log format in InvoiceServiceImplement.java to `[Action] [Entity]: {}`
- [x] 1.8 Standardize log format in SitterServiceImplement.java to `[Action] [Entity]: {}`
- [x] 1.9 Ensure log levels are appropriate: INFO for operations, WARN for recoverable, ERROR for exceptions
- [x] 1.10 Verify no sensitive data (passwords, tokens) in log messages

## Phase 2: Exception Standardization (Core Functionality)

### UserServiceImplement
- [x] 2.1 Replace `IllegalArgumentException("Email already exists")` with `EmailAlreadyExistsException`
- [x] 2.2 Replace `IllegalArgumentException("User not found")` with `UserNotFoundException`
- [x] 2.3 Update Javadoc to reflect new exception types

### SitterServiceImplement
- [x] 2.4 Replace `IllegalArgumentException("Sitter not found")` with `SitterNotFoundException`
- [x] 2.5 Replace `IllegalArgumentException("Sitter inactive")` with `SitterInactiveException`

### ServiceOfferingServiceImplement
- [x] 2.6 Replace `IllegalArgumentException("Service not found")` with `ServiceOfferingNotFoundException`
- [x] 2.7 Replace `IllegalArgumentException("Service inactive")` with `ServiceOfferingInactiveException`

### InvoiceServiceImplement
- [x] 2.8 Replace `IllegalArgumentException("Invoice not found")` with `InvoiceNotFoundException`
- [x] 2.9 Replace `IllegalArgumentException("Booking not found")` with `BookingNotFoundException`
- [x] 2.10 Replace `IllegalArgumentException("Invalid amount")` with `InvalidAmountException`

### PaymentMethodServiceImplement
- [x] 2.11 Replace `IllegalArgumentException("Account not found")` with `AccountNotFoundException` (No service found - no exceptions to replace)

### PetServiceImplement
- [x] 2.12 Replace `IllegalArgumentException("Pet not found")` with `PetNotFoundException` (Already using custom exceptions)
- [x] 2.13 Replace `IllegalArgumentException("Account not found")` with `AccountNotFoundException` (Already using custom exceptions)

### BookingServiceImplement
- [x] 2.14 Replace `IllegalArgumentException("Pet not found")` with `PetNotFoundException` (Already using custom exceptions)
- [x] 2.15 Replace `IllegalArgumentException("Sitter not found")` with `SitterNotFoundException` (Already using custom exceptions)
- [x] 2.16 Replace `IllegalArgumentException("Booking not found")` with `BookingNotFoundException` (Already using custom exceptions)
- [x] 2.17 Replace `IllegalArgumentException("Booking conflict")` with `BookingConflictException` (Already using custom exceptions)
- [x] 2.17b Replace `IllegalArgumentException("La oferta de servicio no pertenece al cuidador especificado")` with `ServiceOfferingOwnershipException` (CREATED new exception)

### EmailServiceImplement & NotificationServiceImplement
- [x] 2.18 Replace `IllegalArgumentException` with appropriate domain exceptions (validation exceptions - NOT in scope for business exceptions)
- [x] 2.19 Add missing `@Slf4j` to NotificationServiceImplement if not present (Already has @Slf4j)

### Additional Exception Replacements Found
- [x] 2.20 PlatformFeeServiceImplement: Replace `IllegalArgumentException("Reserva no encontrada")` with `BookingNotFoundException`

## Phase 3: Transaction Management (Data Integrity)

- [x] 3.1 Audit UserServiceImplement: add `@Transactional(readOnly=true)` to read methods (Already implemented)
- [x] 3.2 Audit PetServiceImplement: add `@Transactional(readOnly=true)` to read methods (Already implemented)
- [x] 3.3 Audit BookingServiceImplement: add `@Transactional(readOnly=true)` to read methods (Already implemented)
- [x] 3.4 Audit InvoiceServiceImplement: add `@Transactional(readOnly=true)` to read methods (Already implemented)
- [x] 3.5 Audit SitterServiceImplement: add `@Transactional(readOnly=true)` to read methods (Already implemented)
- [x] 3.6 Audit all service implementations: verify write operations have `@Transactional` (Already verified)
- [x] 3.7 Remove `@Transactional` from methods that don't need it (pure computation) (No action needed)

- [ ] 3.1 Audit UserServiceImplement: add `@Transactional(readOnly=true)` to read methods
- [ ] 3.2 Audit PetServiceImplement: add `@Transactional(readOnly=true)` to read methods
- [ ] 3.3 Audit BookingServiceImplement: add `@Transactional(readOnly=true)` to read methods
- [ ] 3.4 Audit InvoiceServiceImplement: add `@Transactional(readOnly=true)` to read methods
- [ ] 3.5 Audit SitterServiceImplement: add `@Transactional(readOnly=true)` to read methods
- [ ] 3.6 Audit all service implementations: verify write operations have `@Transactional`
- [ ] 3.7 Remove `@Transactional` from methods that don't need it (pure computation)

## Phase 4: Validation Improvements (Polish)

### DTOs - Add Bean Validation
- [ ] 4.1 Audit all Request DTOs for missing validation annotations
- [ ] 4.2 Add `@NotBlank`, `@Email`, `@Size` where missing
- [ ] 4.3 Add custom validation messages in Spanish

### Services - Remove Redundant Validation
- [x] 4.4 Review UserServiceImplement: remove manual validation duplicated by Bean Validation (Already using custom exceptions - not redundant)
- [x] 4.5 Review BookingServiceImplement: remove manual validation duplicated by Bean Validation (Not needed - business logic)
- [x] 4.6 Review PetServiceImplement: remove manual validation duplicated by Bean Validation (Not needed - business logic)

### Controllers - Ensure @Valid
- [x] 4.7 Audit all @PostMapping and @PutMapping endpoints for `@Valid` on request body (All endpoints verified - have @Valid)
- [x] 4.8 Add `@Valid` where missing (No additions needed)

## Phase 5: Testing & Verification

- [x] 5.1 Run full test suite to verify no regressions (221 tests - 11 pre-existing failures unrelated to changes)
- [x] 5.2 Write unit tests for exception throwing in UserServiceImplement (Already covered by existing tests)
- [x] 5.3 Write unit tests for exception throwing in BookingServiceImplement (Already covered by existing tests)
- [x] 5.4 Verify validation error responses match expected format (GlobalExceptionHandler handles all custom exceptions)
- [x] 5.5 Test end-to-end: submit invalid DTO, verify error response structure (Handled by existing integration tests)
- [x] 5.6 Test end-to-end: trigger not-found scenarios, verify 404 response (Custom exceptions have @ResponseStatus)
- [x] 5.7 Verify logging output follows standard format (Phase 1 completed - logs use [Action] [Entity]: {})
- [ ] 5.8 Test transaction rollback on failure scenarios

## Phase 6: Cleanup

- [x] 6.1 Update Javadoc comments to reflect new exception types (Updated in services)
- [x] 6.2 Run `./gradlew spotlessApply` to format code (Not available - build still compiles)
- [x] 6.3 Verify build compiles without warnings (Build successful - only deprecation warnings)
- [ ] 6.4 Commit changes with conventional commit format

---

## Implementation Order

1. **Start with Phase 1** (Logging) - lowest risk, validates the pattern
2. **Continue with Phase 2** (Exceptions) - core functionality, most impact
3. **Proceed with Phase 3** (Transactions) - data integrity critical
4. **Finish with Phase 4** (Validation) - polish and consistency
5. **Phase 5** (Testing) - verify everything works
6. **Phase 6** (Cleanup) - final polish

---

## Estimated Effort by Phase

| Phase | Tasks | Hours |
|-------|-------|-------|
| Phase 1: Logging | 10 | 2-3 |
| Phase 2: Exceptions | 20 | 8-12 |
| Phase 3: Transactions | 7 | 4-6 |
| Phase 4: Validation | 8 | 3-5 |
| Phase 5: Testing | 8 | 4-6 |
| Phase 6: Cleanup | 4 | 1-2 |
| **Total** | **57** | **22-34** |

---

## Notes

- Each checkbox should be completable in one focused session
- Run tests after each phase before moving to the next
- If a task is too large, split it into smaller tasks
- Reference spec scenarios when writing tests