
package za.ac.cput.exception;

/*
ApiError.java
Stores error information in one standard format.
This helps the frontend receive the same type of information
whenever an API error occurs.
Author: Charmaine Dlamini - 222056401
Date: 09/11/2026
*/

import org.springframework.http.HttpStatus;
import java.time.Instant;

// A record is used to store data without writing getters and constructors manually.
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path) {

    // Creates an ApiError object using the HTTP status, error message and request path.
    public static ApiError of(HttpStatus status, String message, String path) {

        return new ApiError(
                Instant.now(),              // Gets the current date and time.
                status.value(),              // Gets the HTTP status code, for example 404.
                status.getReasonPhrase(),    // Gets the status description, for example "Not Found".
                message,                     // Stores the explanation of the error.
                path                         // Stores the API endpoint where the error happened.
        );
    }
}
