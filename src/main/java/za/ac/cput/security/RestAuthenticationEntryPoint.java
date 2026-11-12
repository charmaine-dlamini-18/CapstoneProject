
package za.ac.cput.security;

/*
RestAuthenticationEntryPoint.java
Answers a missing or wrong login with a clear 401 JSON message (instead of an empty page).
Author: Charmaine Dlamini- 222056401
Date: 09/11/2026
*/

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import za.ac.cput.exception.ApiError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Registers this class as a Spring-managed component.
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // Constructor injection allows Spring to provide the object mapper.
    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Handles requests when authentication is missing or fails.
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Stores the message that will explain why authentication failed.
        String message;

        // Selects an appropriate message based on the authentication error.
        if (authException instanceof CredentialsExpiredException) {
            message = "Your email address is not verified yet. Open the link sent to you.";

        } else if (authException instanceof UsernameNotFoundException) {
            message = "No account is registered with this email address. Please register first.";

        } else if (authException instanceof BadCredentialsException) {
            message = "The password is not correct. Please try again.";

        } else if (request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            message = "The email or password is not correct.";

        } else {
            message = "You must log in first. Send your email and password using HTTP Basic authentication.";
        }

        // Sets the HTTP response status to 401 Unauthorized.
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // Sets the response format to JSON and the character encoding to UTF-8.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // Creates and writes the error response containing the status, message and request URL.
        objectMapper.writeValue(response.getWriter(), ApiError.of(
                HttpStatus.UNAUTHORIZED,
                message,
                request.getRequestURI()));
    }
}
