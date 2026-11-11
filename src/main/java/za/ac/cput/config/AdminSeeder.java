
package za.ac.cput.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import za.ac.cput.domain.Admin;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.util.Helper;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * AdminSeeder.java
 * Creates an admin account when the application starts.
 * Author: Sabelo Ceza - 220094489
 * Date: 08/11/2026
 */

@Component
@Order(2)
public class AdminSeeder implements CommandLineRunner {

    // Used to display messages in the console.
    private static final Logger log =
            LoggerFactory.getLogger(AdminSeeder.class);

    // The password must have at least 8 characters.
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final AdminRepository repo;
    private final PasswordEncoder encoder;

    // Gets the admin password from the environment variable.
    @Value("${ADMIN_PASSWORD: StrongPass123}")
    private String adminPassword;

    // Gets the admin email. Uses this email if none is provided.
    @Value("${ADMIN_EMAIL:admin@tutoring.co.za}")
    private String adminEmail;

    // Constructor used to access the repository and password encoder.
    public AdminSeeder(AdminRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String[] args) {

        // Check if the password is missing or too short.
        if (Helper.isNullOrEmpty(adminPassword)
                || adminPassword.length() < MIN_PASSWORD_LENGTH) {

            log.warn("Admin account was not created. Please set ADMIN_PASSWORD.");
            return;
        }

        // Check if an admin with this email already exists.
        // This prevents creating the same admin more than once.
        if (repo.findByEmailIgnoreCase(adminEmail).isPresent()) {
            log.info("Admin account already exists.");
            return;
        }

        // Generate a unique ID for the new admin.
        String adminId = "ADM-" + UUID.randomUUID();

        // Create the admin account.
        Admin admin = new Admin.Builder()
                .setAdminId(adminId)
                .setFirstName("Admin")
                .setLastName("Admin")
                .setEmail(adminEmail)

                // Encrypt the password before saving it.
                .setPassword(encoder.encode(adminPassword))

                .setCreatedAt(LocalDateTime.now())
                .build();

        // Save the new admin in the database.
        repo.save(admin);

        log.info("Admin account created successfully.");
    }
}
