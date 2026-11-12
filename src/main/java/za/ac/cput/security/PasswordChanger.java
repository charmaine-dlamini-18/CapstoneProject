
package za.ac.cput.security;

/*
PasswordChanger.java
The one place that checks a password change: the current password must be right, the new one must be strong.
The passwords travel in the JSON body, never in the URL.
Author: Charmaine Dlamini- 222056401
Date: 10/11/2026
*/

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.util.Helper;
import java.nio.charset.StandardCharsets;
import java.util.Map;

// Registers this class as a Spring-managed component.
@Component
public class PasswordChanger {

    // Sets the maximum password length supported by BCrypt in bytes.
    private static final int MAX_PASSWORD_BYTES = 72;

    private final PasswordEncoder passwordEncoder;
    private final RegistrationGuard registrationGuard;

    // Constructor injection allows Spring to provide the required dependencies.
    public PasswordChanger(PasswordEncoder passwordEncoder, RegistrationGuard registrationGuard) {
        this.passwordEncoder = passwordEncoder;
        this.registrationGuard = registrationGuard;
    }

    // Checks the current password and returns a BCrypt hash of the new password.
    public String newHash(String currentHash, Map<String, String> body) {

        // Reads the current and new passwords from the request body.
        String current = null;
        String next = null;

        if (body != null) {
            current = body.get("currentPassword");
            next = body.get("newPassword");
        }

        // Checks that the current password was provided.
        if (Helper.isNullOrEmpty(current)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currentPassword is required.");
        }

        // Checks that the new password was provided.
        if (Helper.isNullOrEmpty(next)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "newPassword is required.");
        }

        // Checks that the stored password exists, the password length is supported, and the password matches.
        boolean currentIsRight = !Helper.isNullOrEmpty(currentHash)
                && current.getBytes(StandardCharsets.UTF_8).length <= MAX_PASSWORD_BYTES
                && passwordEncoder.matches(current, currentHash);

        // Rejects the request if the current password is incorrect.
        if (!currentIsRight) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The current password is not correct.");
        }

        // Prevents the user from reusing the current password.
        if (current.equals(next)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The new password must be different from the current password.");
        }

        // Checks whether the new password meets the registration password requirements.
        registrationGuard.checkNewPassword(next);

        // Encodes the new password and returns its BCrypt hash.
        return passwordEncoder.encode(next);
    }
}
