
package za.ac.cput.exception;

/*
GlobalExceptionHandler.java
Handles errors and returns clear JSON messages to the frontend.
This prevents the frontend from receiving confusing error pages or stack traces.
Author: Charmaine Dlamini - 222056401
Date: 09/11/2026
*/

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// Allows this class to handle exceptions from all controllers.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Creates a logger to record errors for developers.
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handles errors where a specific HTTP status and message were provided.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        // Gets the HTTP status code from the exception.
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());

        // Uses INTERNAL_SERVER_ERROR if the status cannot be found.
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // Gets the error message from the exception.
        String message = ex.getReason();

        // Uses the default status description if no message was provided.
        if (message == null || message.isBlank()) {
            message = status.getReasonPhrase();
        }

        return build(status, message, request);
    }

    // Handles validation errors when request fields are missing or incorrect.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Gets all the field errors from the request.
        String message = "The request is not valid.";

        // Goes through the field errors one by one.
        for (int i = 0; i < ex.getBindingResult().getFieldErrors().size(); i++) {

            // Gets the current field error.
            org.springframework.validation.FieldError error =
                    ex.getBindingResult().getFieldErrors().get(i);

            // Uses the first field error as the response message.
            message = error.getField() + ": " + error.getDefaultMessage();

            // Stops after finding the first error.
            break;
        }

        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    // Handles request bodies that are missing or contain invalid JSON.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.BAD_REQUEST,
                "The request body is missing or malformed.",
                request
        );
    }

    // Handles incorrect values in the URL.
    // Example: text is provided when a number is expected.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String message = "The value '" + ex.getValue()
                + "' is not valid for " + ex.getName() + ".";

        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    // Handles database errors, such as duplicate emails or IDs.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        // Records the database error for the developer.
        log.warn("Database refused a change: {}",
                ex.getMostSpecificCause().getMessage());

        return build(
                HttpStatus.CONFLICT,
                "That email or ID is already in use.",
                request
        );
    }

    // Handles cases where a logged-in user does not have permission.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action.",
                request
        );
    }

    // Handles requests for resources or URLs that do not exist.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.NOT_FOUND,
                "The requested resource was not found.",
                request
        );
    }

    // Handles unexpected errors that are not covered by the other methods.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        // Records the full error details for the developer.
        log.error("Unexpected error while handling {}",
                request.getRequestURI(), ex);

        // Sends a safe message to the frontend instead of exposing technical details.
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again.",
                request
        );
    }

    // Builds the final response using the ApiError class.
    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        // Sets the HTTP status and attaches the error information as the response body.
        return ResponseEntity.status(status)
                .body(ApiError.of(status, message, request.getRequestURI()));
    }
}
