package za.ac.cput.config;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.*;
import za.ac.cput.repository.*;
import za.ac.cput.security.EmailVerificationService;
import java.util.Optional;

/*
 * AppUserDetailsService.java
 * Finds a user by email when they try to log in.
 * Supports Admin, Tutor and Student accounts.
 * Author: Sabelo Ceza - 220094489
 * Date: 08/11/2026
 */

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final EmailVerificationService verificationService;

    // Constructor to get access to the repositories and verification service.
    public AppUserDetailsService(AdminRepository adminRepository,
                                 TutorRepository tutorRepository,
                                 StudentRepository studentRepository,
                                 EmailVerificationService verificationService) {

        this.adminRepository = adminRepository;
        this.tutorRepository = tutorRepository;
        this.studentRepository = studentRepository;
        this.verificationService = verificationService;
    }

    // Spring Security calls this method when a user tries to log in.
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Check that the email was provided.
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Email is required.");
        }

        try {

            // First, check if the email belongs to an Admin.
            Optional<Admin> admin =
                    adminRepository.findByEmailIgnoreCase(email);

            if (admin.isPresent()) {
                return build(email, admin.get().getPassword(), "ADMIN");
            }

            // Next, check if the email belongs to a Tutor.
            Optional<Tutor> tutor =
                    tutorRepository.findByEmailIgnoreCase(email);

            if (tutor.isPresent()) {
                // Check if the tutor still needs to verify their email.
                boolean notVerified = verificationService.isRequired()
                        && verificationService.isTutorPending(
                        tutor.get().getTutorId());

                return build(email, tutor.get().getPassword(),
                        "TUTOR", notVerified);
            }

            // check if the email belongs to a Student.
            Optional<Student> student =
                    studentRepository.findByEmailIgnoreCase(email);
            if (student.isPresent()) {

                // Check if the student still needs to verify their email.
                boolean notVerified = verificationService.isRequired()
                        && verificationService.isPending(
                        student.get().getStudentNumber());

                return build(email, student.get().getPassword(),
                        "STUDENT", notVerified);
            }

        } catch (IncorrectResultSizeDataAccessException e) {

            // This happens when the same email is found more than once
            // in the same database table.
            throw new UsernameNotFoundException(
                    "Email is not unique: " + email);
        }

        // No account was found with this email.
        throw new UsernameNotFoundException(
                "No user found with email " + email);
    }

    // Builds the user details when email verification is not required.
    private UserDetails build(String email, String passwordHash, String role) {
        return build(email, passwordHash, role, false);
    }

    // Creates the user details that Spring Security needs.
    private UserDetails build(String email, String passwordHash,
                              String role, boolean notVerified) {

        // Make sure the account has a password.
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new UsernameNotFoundException(
                    "Add password");
        }

        // Return the user's login details and role.
        return User.withUsername(email)
                .password(passwordHash)
                .roles(role)
                .credentialsExpired(notVerified)
                .build();
    }
}
