
package za.ac.cput.security;

/*
RestAccessDeniedHandler.java
Answers a logged-in user who is not allowed to do something with a clear 403 JSON message.
Author: Charmaine Dlamini- 222056401
Date: 09/11/2026
*/

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import za.ac.cput.exception.ApiError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Registers this class as a Spring-managed component.
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    // Declares the object mapper used to create JSON responses.
    private final ObjectMapper objectMapper;

    // Constructor injection allows Spring to provide the object mapper.
    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Handles requests when an authenticated user does not have permission to access a resource.
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // Sets the HTTP response status to 403 Forbidden.
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // Sets the response format to JSON and the character encoding to UTF-8.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // Creates and writes the error response containing the status, message and request URL.
        objectMapper.writeValue(response.getWriter(), ApiError.of(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action.",
                request.getRequestURI()));
    }
}
