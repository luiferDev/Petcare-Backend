# Delta for Error Handling

## Purpose

This specification defines the requirements for standardizing error handling in the Petcare Backend Spring Boot application. It covers exception usage, transaction management, logging consistency, and validation error responses.

## ADDED Requirements

### Requirement: Domain-Specific Exception Usage

The system MUST use domain-specific custom exceptions from `com.Petcare.Petcare.Exception.Business` package instead of raw Java exceptions (IllegalArgumentException, NullPointerException, RuntimeException, etc.).

The system SHALL map business error scenarios to appropriate custom exceptions:
- Email conflicts → EmailAlreadyExistsException
- Resource not found → EntityNotFoundException (UserNotFoundException, PetNotFoundException, BookingNotFoundException, etc.)
- Invalid operations → InvalidOperationException
- Validation failures → ValidationException

#### Scenario: Creating user with duplicate email

- GIVEN a user with email "test@example.com" already exists in the database
- WHEN a new user registration is attempted with the same email
- THEN the system SHALL throw EmailAlreadyExistsException
- AND the system SHALL NOT create the duplicate user record

#### Scenario: Retrieving non-existent user

- GIVEN no user exists with ID 999 in the database
- WHEN UserService.getUserById(999) is called
- THEN the system SHALL throw UserNotFoundException
- AND the exception message SHALL include the requested ID

### Requirement: Transaction Management

The system MUST ensure proper @Transactional annotations on all service methods that perform database operations.

Write operations (create, update, delete) SHALL have @Transactional without readOnly=true.
Read operations SHALL have @Transactional(readOnly=true) for optimization.
Methods that don't require transactions SHALL NOT have the annotation.

#### Scenario: Creating a new booking with transaction

- GIVEN valid booking data is provided
- WHEN BookingService.createBooking() is called
- THEN the operation SHALL execute within a transaction
- AND if any step fails, all changes SHALL be rolled back
- AND the booking SHALL NOT be created in incomplete state

#### Scenario: Reading pets list with read-only transaction

- GIVEN the database contains pets
- WHEN PetService.getAllPets() is called
- THEN the operation SHALL use a read-only transaction for better performance

### Requirement: Logging Consistency

The system SHALL use SLF4J for all logging across service implementations.

All service implementation classes SHALL have @Slf4j annotation.
Log messages SHALL follow consistent format: "[Action] [Entity]: {details}"
Log levels SHALL be used appropriately:
- INFO: Significant business operations (creations, updates, deletions)
- WARN: Recoverable issues or expected errors
- DEBUG: Detailed tracing for debugging
- ERROR: Unexpected exceptions or system failures

#### Scenario: Logging user creation

- GIVEN valid user data is provided
- WHEN UserService.createUser() is called
- THEN the system SHALL log at INFO level: "Creating user with email: {email}"
- AND the log SHALL NOT include sensitive data (passwords, tokens)

#### Scenario: Logging unexpected errors

- GIVEN an unexpected exception occurs during service operation
- WHEN the exception is caught and logged
- THEN the system SHALL log at ERROR level with full context
- AND the stack trace SHALL be logged for debugging

### Requirement: Validation Error Responses

The system SHALL use Jakarta Bean Validation annotations (@NotBlank, @Email, @Size, etc.) for DTO validation.

Manual validation in service layers that duplicates Bean Validation SHALL be removed.
Service methods SHALL use @Valid on DTO parameters where appropriate.
Validation error messages SHALL be consistent across the application.

#### Scenario: Invalid email format in registration

- GIVEN a user registration request with invalid email "not-an-email"
- WHEN the request is processed
- THEN Bean Validation SHALL reject the request
- AND GlobalExceptionHandler SHALL return consistent error response with field "email" and message "must be a valid email"

#### Scenario: Missing required field

- GIVEN a user registration request with missing firstName
- WHEN the request is processed
- THEN the system SHALL return validation error for field "firstName" with message "must not be blank"

## MODIFIED Requirements

### Requirement: Exception Handling in Services

(Previously: Services used raw Java exceptions like IllegalArgumentException)

Service implementations SHALL now use domain-specific custom exceptions from the Business package instead of throwing raw Java exceptions.

#### Scenario: Invalid argument passed to service

- GIVEN an invalid argument (null or empty) is passed to a service method
- WHEN the service validates the input
- THEN the system SHALL throw a domain-specific exception (e.g., InvalidArgumentException)
- AND the exception SHALL be handled by GlobalExceptionHandler

## REMOVED Requirements

### Requirement: Raw Java Exception Usage in Services

(Reason: Raw Java exceptions don't provide business context and make error handling inconsistent across the application)

Service implementations SHALL NOT throw raw Java exceptions (IllegalArgumentException, NullPointerException, RuntimeException) directly.

### Requirement: Redundant Manual Validation

(Reason: Bean Validation with @Valid provides centralized validation; manual checks in services are redundant)

Service implementations SHALL NOT perform manual validation checks that duplicate Bean Validation annotations on DTOs.

---

## Summary

| Type | Count |
|------|-------|
| ADDED Requirements | 4 |
| MODIFIED Requirements | 1 |
| REMOVED Requirements | 2 |
| Total Scenarios | 10 |

All scenarios are testable and cover happy paths, edge cases, and error states.