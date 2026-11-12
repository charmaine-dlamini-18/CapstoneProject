package za.ac.cput.security;

/*
PasswordHasher.java
Hashes passwords with BCrypt, never twice, and never crashes on an empty password
Author: Sabelo Ceza - 220094489
Date: 08/11/2026
 */

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.util.Helper;

@Component
public class PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public PasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // For a NEW record: a password is required
    public String hashNew(String password) {
        if (Helper.isNullOrEmpty(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required.");
        }
        return hash(password);
    }

    // For an UPDATE: no password sent means "keep the old one"
    public String hashForUpdate(String password, String existingHash) {
        if (Helper.isNullOrEmpty(password)) {
            if (Helper.isNullOrEmpty(existingHash)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required.");
            }
            return existingHash;
        }
        return hash(password);
    }

    private String hash(String password) {
        // Already a BCrypt hash (for example a saved object that was copied): do not hash it again
        if (Helper.isBcryptHash(password)) {
            return password;
        }
        return passwordEncoder.encode(password);
    }
}
