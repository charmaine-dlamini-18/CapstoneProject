
package za.ac.cput.security;

/*
LoginRateLimiter.java
Counts events (failed logins, sign-ups) per key and blocks further attempts for a while.
Author: Charmaine Dlamini- 222056401
Date: 09/11/2026
*/

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// Registers this class as a Spring-managed component.
@Component
public class LoginRateLimiter {

    // Sets the maximum number of entries before expired records are cleaned up.
    private static final int CLEAN_UP_SIZE = 10_000;

    // Reads the maximum failed login attempts allowed per account.
    @Value("${app.security.max-failed-logins:10}")
    private int maxFailedLogins;

    // Reads the maximum failed login attempts allowed per IP address.
    @Value("${app.security.max-failed-logins-per-ip:50}")
    private int maxFailedLoginsPerIp;

    // Reads the maximum number of sign-ups allowed per IP address.
    @Value("${app.security.max-signups:20}")
    private int maxSignups;

    // Reads how many minutes an attempt window remains active.
    @Value("${app.security.lockout-minutes:15}")
    private long lockoutMinutes;

    // Stores attempt information for each account or IP address.
    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();

    // Returns the maximum failed login attempts allowed per account.
    public int getMaxFailedLogins() {
        return maxFailedLogins;
    }

    // Returns the maximum failed login attempts allowed per IP address.
    public int getMaxFailedLoginsPerIp() {
        return maxFailedLoginsPerIp;
    }

    // Returns the maximum number of sign-ups allowed per IP address.
    public int getMaxSignups() {
        return maxSignups;
    }

    // Checks whether a key has reached the normal failed login limit.
    public boolean isBlocked(String key) {
        return isBlocked(key, maxFailedLogins);
    }

    // Checks whether a key has reached its specified attempt limit.
    public synchronized boolean isBlocked(String key, int limit) {

        // Retrieves the attempt information for the specified key.
        Attempts current = attempts.get(key);

        // Returns false if no attempt information exists.
        if (current == null) {
            return false;
        }

        // Removes the record and allows attempts again if its time window has expired.
        if (isExpired(current)) {
            attempts.remove(key);
            return false;
        }

        // Returns true if the number of failed attempts has reached the limit.
        return current.failures >= limit;
    }

    // Records another failed login or sign-up attempt.
    public synchronized void recordAttempt(String key) {

        // Checks whether the map has reached its cleanup size.
        if (attempts.size() >= CLEAN_UP_SIZE) {

            // Loops through the stored entries to find expired attempt records.
            for (Map.Entry<String, Attempts> entry : attempts.entrySet()) {

                // Retrieves the attempt information for the current entry.
                Attempts current = entry.getValue();

                // Removes the entry if its time window has expired.
                if (isExpired(current)) {
                    attempts.remove(entry.getKey(), current);
                }
            }
        }

        // Gets the current time in milliseconds.
        long now = System.currentTimeMillis();

        // Retrieves the existing attempt information for this key.
        Attempts current = attempts.get(key);

        // Creates a new attempt record if none exists or the previous one has expired.
        if (current == null || isExpired(current)) {
            current = new Attempts(now);
        }

        // Increases the number of recorded attempts by one.
        current.failures++;

        // Saves the updated attempt information in the map.
        attempts.put(key, current);
    }

    // Removes the attempt information for a key, such as after a successful login.
    public synchronized void reset(String key) {
        attempts.remove(key);
    }

    // Calculates how many seconds remain before the attempt window expires.
    public synchronized long secondsUntilUnblocked(String key) {

        // Retrieves the attempt information for the specified key.
        Attempts current = attempts.get(key);

        // Returns zero if no attempt record exists.
        if (current == null) {
            return 0;
        }

        // Removes expired records and returns zero when the blocking period has ended.
        if (isExpired(current)) {
            attempts.remove(key);
            return 0;
        }

        // Calculates the remaining time in milliseconds.
        long remaining = windowMillis()
                - (System.currentTimeMillis() - current.windowStart);

        // Converts the remaining time into seconds.
        return Math.max(1, TimeUnit.MILLISECONDS.toSeconds(remaining));
    }

    // Checks whether the allowed attempt time window has expired.
    private boolean isExpired(Attempts attempts) {
        return System.currentTimeMillis() - attempts.windowStart > windowMillis();
    }

    // Converts the configured lockout time from minutes into milliseconds.
    private long windowMillis() {
        return TimeUnit.MINUTES.toMillis(Math.max(1, lockoutMinutes));
    }

    // Inner class that stores the number of attempts and when the time window started.
    private static final class Attempts {

        // Stores the starting time of the attempt window.
        private final long windowStart;

        // Stores the number of attempts recorded during the window.
        private int failures;

        // Constructor initializes the attempt window with its starting time.
        private Attempts(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
