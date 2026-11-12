
package za.ac.cput.security;

/*
LoginRateLimiterTest.java
Proves that the counters block at the limit, stay separate per key, and can be cleared one key at a time.
Author: Charmaine Dlamini - 222056401
Date: 09/11/2026
*/

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimiterTest {

    // Stores the rate limiter used in each test.
    private LoginRateLimiter limiter;

    // Creates a new rate limiter and sets test values before each test.
    @BeforeEach
    void setUp() {
        limiter = new LoginRateLimiter();

        // Sets the limits normally loaded from application.properties.
        ReflectionTestUtils.setField(limiter, "maxFailedLogins", 3);
        ReflectionTestUtils.setField(limiter, "maxFailedLoginsPerIp", 5);
        ReflectionTestUtils.setField(limiter, "maxSignups", 2);
        ReflectionTestUtils.setField(limiter, "lockoutMinutes", 15L);
    }

    // Checks that a key is blocked only after reaching its login limit.
    @Test
    void blocksOnlyWhenTheLimitIsReached() {
        limiter.recordAttempt("login:1.2.3.4|a@test.co.za");
        limiter.recordAttempt("login:1.2.3.4|a@test.co.za");

        assertFalse(limiter.isBlocked("login:1.2.3.4|a@test.co.za"));

        limiter.recordAttempt("login:1.2.3.4|a@test.co.za");

        assertTrue(limiter.isBlocked("login:1.2.3.4|a@test.co.za"));
        assertTrue(limiter.secondsUntilUnblocked("login:1.2.3.4|a@test.co.za") > 0);
    }

    // Checks that failed login attempts are counted separately for each account.
    @Test
    void keysAreCountedSeparately() {
        for (int i = 0; i < 3; i++) {
            limiter.recordAttempt("login:1.2.3.4|victim@test.co.za");
        }

        assertTrue(limiter.isBlocked("login:1.2.3.4|victim@test.co.za"));

        // Checks that another account from the same address is not blocked.
        assertFalse(limiter.isBlocked("login:1.2.3.4|attacker@test.co.za"));
    }

    // Checks that resetting one key does not clear another key.
    @Test
    void resetClearsOnlyThatKey() {
        for (int i = 0; i < 3; i++) {
            limiter.recordAttempt("login:1.2.3.4|victim@test.co.za");
            limiter.recordAttempt("login:1.2.3.4|attacker@test.co.za");
        }

        limiter.reset("login:1.2.3.4|attacker@test.co.za");

        assertFalse(limiter.isBlocked("login:1.2.3.4|attacker@test.co.za"));
        assertTrue(limiter.isBlocked("login:1.2.3.4|victim@test.co.za"));
    }

    // Checks that the configured sign-up limit is used.
    @Test
    void customLimitIsUsed() {
        limiter.recordAttempt("signup:1.2.3.4");

        assertFalse(limiter.isBlocked("signup:1.2.3.4", limiter.getMaxSignups()));

        limiter.recordAttempt("signup:1.2.3.4");

        assertTrue(limiter.isBlocked("signup:1.2.3.4", limiter.getMaxSignups()));
    }
}
