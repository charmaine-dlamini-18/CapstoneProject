
package za.ac.cput.security;

/*
LegacyPasswordMigration.java
Hashes any old plain-text password that is still in the database when the application starts.
Author: Charmaine Dlamini- 222056401
Date: 09/11/2026
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import za.ac.cput.domain.Admin;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.TutorRepository;
import za.ac.cput.util.Helper;

// Registers this class as a Spring component and sets its startup execution order.
@Component
@Order(1)
public class LegacyPasswordMigration implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(LegacyPasswordMigration.class);

    private final AdminRepository adminRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;

    // Constructor injection allows Spring to provide the required dependencies.
    public LegacyPasswordMigration(AdminRepository adminRepository,
                                   TutorRepository tutorRepository,
                                   StudentRepository studentRepository,
                                   PasswordEncoder passwordEncoder) {

        // Assigns the injected dependencies to the class variables.
        this.adminRepository = adminRepository;
        this.tutorRepository = tutorRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Runs automatically when the Spring Boot application starts.
    @Override
    public void run(String[] args) {

        // Counts the number of passwords that are migrated.
        int migrated = 0;

        // Loops through all admin records in the database.
        for (Admin admin : adminRepository.findAll()) {

            // Checks whether the admin password needs migration.
            if (needsMigration(admin.getPassword())) {

                // Hashes the admin password using BCrypt.
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));

                // Saves the updated admin record.
                adminRepository.save(admin);

                // Increases the migration counter.
                migrated++;
            }
        }

        // Loops through all tutor records in the database.
        for (Tutor tutor : tutorRepository.findAll()) {

            // Checks whether the tutor password needs migration.
            if (needsMigration(tutor.getPassword())) {

                // Copies the tutor details and replaces the password with its BCrypt hash.
                Tutor hashed = new Tutor.Builder()
                        .copy(tutor)
                        .setPassword(passwordEncoder.encode(tutor.getPassword()))
                        .build();

                // Saves the updated tutor record.
                tutorRepository.save(hashed);

                // Increases the migration counter.
                migrated++;
            }
        }

        // Loops through all student records in the database.
        for (Student student : studentRepository.findAll()) {

            // Checks whether the student password needs migration.
            if (needsMigration(student.getPassword())) {

                // Copies the student details and replaces the password with its BCrypt hash.
                Student hashed = new Student.Builder()
                        .copy(student)
                        .setPassword(passwordEncoder.encode(student.getPassword()))
                        .build();

                // Saves the updated student record.
                studentRepository.save(hashed);

                // Increases the migration counter.
                migrated++;
            }
        }

        // Logs the number of passwords migrated if any were updated.
        if (migrated > 0) {
            log.info("Migrated {} plain-text password(s) to BCrypt.", migrated);
        }
    }

    // Checks whether a password is not empty and is not already a BCrypt hash.
    private boolean needsMigration(String password) {
        return !Helper.isNullOrEmpty(password)
                && !Helper.isBcryptHash(password);
    }
}
