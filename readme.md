# Exception Handling in Spring Boot

This project explains structured exception handling in Spring Boot using `@RestControllerAdvice`, `@ExceptionHandler`, and custom exception responses. It also includes an extra section describing how `Optional` works in service methods.


---

## 1. Global Exception Handling with `@RestControllerAdvice`

Spring Boot allows centralizing exception handling through a class annotated with `@RestControllerAdvice`.  
This component intercepts exceptions thrown anywhere in the controller layer and returns consistent error responses.

```java
@RestControllerAdvice
public class HandlerExceptionController {...}
```

#### Key Points:
- The class applies globally to all controllers.
- Each `@ExceptionHandler` maps one or more exception types.
- Handlers can return either `ResponseEntity` or plain objects.
- Using a custom `Error` model or `Map` ensures consistent outputs.
- This approach avoids duplicated `try/catch` blocks inside controllers.

---

## 2. Exception Handlers (Detailed Breakdown)

Below is a breakdown of each handler, including only the essential code segment and a short explanation.

### 2.1 Handling `ArithmeticException` (division by zero)

```java
@ExceptionHandler(ArithmeticException.class)
public ResponseEntity<Error> divisionByZero(Exception ex) { ... }
```
#### Key Points:
- Captures errors like 1/0.
- Returns an `Error` object with HTTP 500.
- Uses `ResponseEntity` to set the exact status code.
- Shows how to build a structured error body.

### 2.2 Handling `NoHandlerFoundException`, `NoResourceFoundException` (API not found)

```java
@ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
public ResponseEntity<Error> notFoundException(Exception ex) { ... }
```
#### Key Points:
- Triggers when no route matches the request.
- Returns HTTP 404.
- Useful for customizing API-level “not found” messages.
- Replaces the default white-label error page.


### 2.3 Handling `NumberFormatException` (invalid number format)

```java
@ExceptionHandler(NumberFormatException.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public Map<String, Object> numberFormatEx(Exception ex) { ... }
```
#### Key Points:
- Fired when parsing invalid numbers (`Integer.parseInt("1s")`).
- Uses `@ResponseStatus` instead of `ResponseEntity`.
- Returns a `Map<String,Object>` instead of a custom Error class.
- Demonstrates that handler methods support different return types.




### 2.4 Handling `NullPointerException`, serialization issues, or custom `UserNotFoundException`

```java
@ExceptionHandler({NullPointerException.class,
    HttpMessageNotWritableException.class,
    UserNotFoundException.class})
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public Map<String, Object> userNotFound(Exception ex) { ... }
```

#### Key points

- Groups multiple exception types in one handler.
- Returns consistent information for missing users or null values.
- Uses `@ResponseStatus` for a fixed HTTP code.
- Shows how to centralize related exception types.

### `@ResponseStatus` vs `ResponseEntity`

`@ResponseStatus`:
- Annotates a method or exception class with a fixed HTTP status.
- Simple to use for standard responses.
- Less flexible if you need dynamic status codes or custom headers.

`ResponseEntity`:
- Allows full control over HTTP status, headers, and body.
- Recommended for APIs where error information needs to be detailed or dynamic.
- Preferred in most production APIs.

| Feature / Use Case                     | `@ResponseStatus`                               | `ResponseEntity`                                  |
|---------------------------------------|------------------------------------------------|--------------------------------------------------|
| HTTP Status Control                     | Fixed at compile-time                          | Dynamic, can be set at runtime                  |
| Custom Headers                          | Not supported                                  | Fully supported                                  |
| Response Body Flexibility               | Limited (usually simple objects or messages)   | Full control over body content                  |
| Use Case Simplicity                      | Quick, standard error responses                | Complex APIs, dynamic error handling            |
| Recommended For                          | Standardized errors, simple REST endpoints     | Production APIs, detailed error reporting       |
| Exception Mapping                        | Can annotate exception class directly          | Typically used inside `@ExceptionHandler` methods |

---

## 3. Exceptions Triggered in Controllers
The controller deliberately generates exceptions so they can be captured by the handlers.
```java
@GetMapping("/index")
public String index() {
    int i = 1 / 0; // ArithmeticException
    return "Hello World";
}
```
```java
@GetMapping("/number")
public String number(){
    int i = Integer.parseInt("1s"); // NumberFormatException
    return "ok 200" + i;
}
```
```java
@GetMapping("/show/{id}")
public User show(@PathVariable Long id){
    return service.findById(id)
      .orElseThrow(() -> new UserNotFoundException("Error: User does not exists"));
}
```
### Resulting Exceptions

- /index → ArithmeticException
- /number → NumberFormatException
- /show/{id} → UserNotFoundException

#### Key Points:
- Groups multiple exception types in one handler.
- Returns consistent information for missing users or null values.
- Uses `@ResponseStatus` for a fixed HTTP code.
- Shows how to centralize related exception types.

---

## 4. Standard Error Response Model

The application uses a simple model to represent API errors. This provides a **consistent structure** for all error responses, making it easier for clients to parse and handle them.

```java
public class Error {

    private String error;
    private String message;
    private int status;
    private Date date;

    // getters and setters
}
```

#### Key points:
- `error`: A short description of the type of error.
- `message`: Detailed message, usually the exception message.
- `status`: HTTP status code corresponding to the error.
- `date`: Timestamp indicating when the error occurred.

Using a standard model ensures uniform responses across different endpoints and exception types.

#### Example JSON Output:
```json
{
  "error": "Division by zero",
  "message": "/ by zero",
  "status": 500,
  "date": "2025-02-10T10:00:00"
}
```
This structure can be easily extended if needed, for example by adding fields for path, requestId, or errorCode to support more detailed API error reporting.

---

## 5. Custom Exceptions

Spring Boot allows you to define **custom exceptions** to represent specific error conditions in your application. This improves code readability and makes it easier to map different errors to appropriate HTTP responses.

```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```
#### Key points:
- Custom exceptions extend RuntimeException or Exception.
- They can carry specific messages describing the error.
- When used with @ExceptionHandler, you can customize the response for that particular exception.
- Encourages cleaner controller logic, as you can throw exceptions instead of returning error codes manually.
- Helps centralize error handling with @RestControllerAdvice, ensuring all errors are returned in a consistent format.

#### Example Usage in Controller:

```java
@GetMapping("/show/{id}")
public User show(@PathVariable Long id){
    return service.findById(id)
                  .orElseThrow(() -> new UserNotFoundException("Error: User does not exist"));
}
```

Example JSON Output:
```json
{
  "error": "User or role not found",
  "message": "Error: User does not exist",
  "status": 500,
  "date": "2025-02-10T11:00:00"
}
```
##### Using custom exceptions allows your API to clearly communicate specific problems to clients while keeping your code modular and maintainable.

--- 

## 6. Using Optional in Service Methods

The service returns `Optional<User>`:

```java
@Override
public Optional<User> findById(Long id) {
    return users.stream()
                .filter(usr -> usr.getId().equals(id))
                .findFirst();
}
```

#### Why Optional?
- Avoids null and accidental NullPointerException.
- Forces callers to decide how to handle the absence of data.
- Provides fluent and expressive API methods.

#### Common Optional Patterns
##### Throw if empty
```java
userOpt.orElseThrow(() -> new UserNotFoundException("User not found"));
```
##### Default value
```java
User user = userOpt.orElse(new User());
```
##### Check presence
```java
if (userOpt.isPresent()) {
    System.out.println(userOpt.get().getName());
}
```
##### Functional style
```java
userOpt.ifPresent(u -> System.out.println(u.getName()));
```

##### By adopting Optional, your service layer communicates clearly which results may be empty and ensures that controllers handle these cases in a structured, safe way.
---

## Summary

This guide covers:
- Global exception handling with @RestControllerAdvice
- Different patterns of @ExceptionHandler
- Proper error response structures
- Use of custom exception types
- Safe data retrieval using Optional

It serves as a reference for learning and implementing robust exception handling in Spring Boot.