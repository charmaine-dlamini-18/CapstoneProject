
package za.ac.cput.security;

/*
RateLimitFilter.java
Stops an address that keeps sending wrong passwords, and an address that signs up too many users.
Author: Charmaine Dlamini- 222056401
Date: 09/11/2026
*/

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import za.ac.cput.exception.ApiError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

//RateLimitFilter is created by SecurityConfig and tracks failed logins per account,
 //failed logins per address, and sign-ups per address.

public class RateLimitFilter extends OncePerRequestFilter {

    // Sets the maximum length allowed for a username.
    private static final int MAX_NAME_LENGTH = 100;

    // Declares the dependencies used to check attempts and create JSON responses.
    private final LoginRateLimiter limiter;
    private final ObjectMapper objectMapper;

    // Constructor injection provides the required dependencies.
    public RateLimitFilter(LoginRateLimiter limiter, ObjectMapper objectMapper) {
        this.limiter = limiter;
        this.objectMapper = objectMapper;
    }

    // Runs the rate-limiting checks once for each request.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Gets the address of the client making the request.
        String address = request.getRemoteAddr();

        // Checks whether the request contains an Authorization header.
        boolean sentLogin = request.getHeader(HttpHeaders.AUTHORIZATION) != null;

        // Reads the username from the HTTP Basic login details.
        String email = basicUsername(request);

        // Creates separate keys for address, account and sign-up attempts.
        String ipKey = "ip:" + address;
        String accountKey = null;
        String signUpKey = "signup:" + address;

        if (email != null) {
            accountKey = "login:" + address + "|" + email.toLowerCase(Locale.ROOT);
        }

        // Blocks requests when the address or account has reached its failed login limit.
        if (limiter.isBlocked(ipKey, limiter.getMaxFailedLoginsPerIp())
                || (accountKey != null && limiter.isBlocked(accountKey, limiter.getMaxFailedLogins()))) {

            // Gets the remaining blocking time.
            long retryAfter = limiter.secondsUntilUnblocked(ipKey);

            if (accountKey != null) {
                retryAfter = Math.max(retryAfter, limiter.secondsUntilUnblocked(accountKey));
            }

            // Sends a response explaining when the user can try again.
            reject(response, request, retryAfter,
                    "Too many failed login attempts. Please try again in " + retryAfter + " seconds.");
            return;
        }

        // Checks whether this is a sign-up request without login credentials.
        if (!sentLogin && isSignUp(request)) {

            // Blocks the request if the address has reached its sign-up limit.
            if (limiter.isBlocked(signUpKey, limiter.getMaxSignups())) {
                long retryAfter = limiter.secondsUntilUnblocked(signUpKey);

                // Sends a response explaining when the user can sign up again.
                reject(response, request, retryAfter,
                        "Too many sign-ups from this address. Please try again in " + retryAfter + " seconds.");
                return;
            }

            // Records the sign-up attempt.
            limiter.recordAttempt(signUpKey);
        }

        try {
            // Continues processing the request through the remaining security filters.
            filterChain.doFilter(request, response);

        } finally {

            // Only requests containing login credentials are counted as login attempts.
            if (sentLogin) {

                // Gets the authentication details after the request has been processed.
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // Checks whether the request resulted in an authenticated, non-anonymous user.
                boolean loggedIn = authentication != null
                        && authentication.isAuthenticated()
                        && !(authentication instanceof AnonymousAuthenticationToken);

                if (loggedIn) {

                    // Clears the counter only when the same account successfully logs in.
                    if (accountKey != null && authentication.getName().equalsIgnoreCase(email)) {
                        limiter.reset(accountKey);
                    }

                } else if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {

                    // Records a failed login against the address.
                    limiter.recordAttempt(ipKey);

                    // Records a failed login against the specific account when its username is available.
                    if (accountKey != null) {
                        limiter.recordAttempt(accountKey);
                    }
                }
            }
        }
    }

    // Checks whether the request is a public account creation or verification resend request.
    private boolean isSignUp(HttpServletRequest request) {

        // Removes the application context path from the request URL.
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Allows only the specified POST endpoints to count as sign-up attempts.
        return "POST".equalsIgnoreCase(request.getMethod())
                && ("/student/create".equals(path) || "/student/resend-verification".equals(path));
    }

    // Reads the username from an HTTP Basic Authorization header.
    private String basicUsername(HttpServletRequest request) {

        // Gets the Authorization header.
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Returns null if the header is missing or does not use Basic authentication.
        if (header == null || !header.regionMatches(true, 0, "Basic ", 0, 6)) {
            return null;
        }

        try {
            // Decodes the Base64 login details into a username and password string.
            String decoded = new String(Base64.getDecoder().decode(header.substring(6).trim()),
                    StandardCharsets.UTF_8);

            // Finds the separator between the username and password.
            int colon = decoded.indexOf(':');

            // Returns null if the username is missing.
            if (colon <= 0) {
                return null;
            }

            // Extracts the username before the separator.
            String name = decoded.substring(0, colon);

            // Limits the username length before using it as a counter key.
            if (name.length() > MAX_NAME_LENGTH) {
                name = name.substring(0, MAX_NAME_LENGTH);
            }

            return name;

        } catch (IllegalArgumentException e) {

            // Returns null if the Base64 header cannot be decoded.
            return null;
        }
    }

    // Sends a JSON response when a request exceeds the allowed limit.
    private void reject(HttpServletResponse response, HttpServletRequest request,
                        long retryAfter, String message) throws IOException {

        // Sets the HTTP status to 429 Too Many Requests.
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

        // Tells the client how many seconds to wait before trying again.
        response.setHeader("Retry-After", String.valueOf(retryAfter));

        // Sets the response format to JSON and the character encoding to UTF-8.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // Writes the API error details to the response body.
        objectMapper.writeValue(response.getWriter(),
                ApiError.of(HttpStatus.TOO_MANY_REQUESTS, message, request.getRequestURI()));
    }
}
