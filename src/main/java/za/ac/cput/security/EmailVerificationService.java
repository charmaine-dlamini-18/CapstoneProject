
package za.ac.cput.security;

/*
EmailVerificationService.java
Creates and checks verification links for student and tutor accounts.
Verification links are written to the backend log instead of being sent by email.
Author: Charmaine Dlamini - 222056401
Date: 10/11/2026
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.EmailVerificationToken;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.repository.EmailVerificationTokenRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.TutorRepository;
import za.ac.cput.util.Helper;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

// Registers this class as a Spring service.
@Service

// Makes the methods in this class run within a database transaction.
@Transactional
public class EmailVerificationService {

    // Creates a logger to write verification links and error messages to the backend log.
    private static final Logger log =
            LoggerFactory.getLogger(EmailVerificationService.class);

    // Message shown when a user needs to request another verification link.
    private static final String RESEND_HINT =
            "request a new link using the correct resend-verification endpoint.";

    private final EmailVerificationTokenRepository tokenRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;

    // Generates secure random values for verification tokens.
    private final SecureRandom random = new SecureRandom();

    // Controls whether new accounts must verify their email before logging in.
    @Value("${app.security.require-email-verification:true}")
    private boolean verificationRequired;

    // The backend address used when creating verification links.
    @Value("${app.public-url:http://localhost:8080/tutoring}")
    private String publicUrl;

    // Determines how many hours a verification link remains valid.
    @Value("${app.security.verification-hours:24}")
    private long validHours;

    // Constructor injection provides the repositories needed by this service.
    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepository,
            StudentRepository studentRepository,
            TutorRepository tutorRepository) {

        this.tokenRepository = tokenRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
    }

    // Checks whether email verification is required for new accounts.
    public boolean isRequired() {
        return verificationRequired;
    }

    // Checks whether a student still has a pending verification token.
    // A pending token means the student has not verified their account.
    @Transactional(readOnly = true)
    public boolean isPending(String studentNumber) {

        if (studentNumber == null) {
            return false;
        }

        return tokenRepository.existsByStudentNumber(studentNumber);
    }

    // Checks whether a tutor still has a pending verification token.
    @Transactional(readOnly = true)
    public boolean isTutorPending(String tutorId) {

        if (tutorId == null) {
            return false;
        }

        return tokenRepository.existsByTutorId(tutorId);
    }

    // Creates a new verification token for a student.
    // Any previous verification token for that student is deleted.
    public String issueToken(String studentNumber) {

        // Removes the student's previous verification tokens.
        tokenRepository.deleteAll(
                tokenRepository.findAllByStudentNumber(studentNumber)
        );

        // Creates an array of 32 random bytes.
        byte[] bytes = new byte[32];

        // Fills the array with secure random values.
        random.nextBytes(bytes);

        // Converts the random bytes into a URL-safe token.
        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        // Saves the hashed token and its expiry date in the database.
        tokenRepository.save(new EmailVerificationToken(
                hash(token),
                studentNumber,
                null,
                LocalDateTime.now().plusHours(validHours)
        ));

        // Returns the original token so it can be included in the verification link.
        return token;
    }

    // Creates a new verification token for a tutor.
    // Any previous verification token for that tutor is deleted.
    public String issueTutorToken(String tutorId) {

        // Removes the tutor's previous verification tokens.
        tokenRepository.deleteAll(
                tokenRepository.findAllByTutorId(tutorId)
        );

        // Creates an array of 32 random bytes.
        byte[] bytes = new byte[32];

        // Fills the array with secure random values.
        random.nextBytes(bytes);

        // Converts the random bytes into a URL-safe token.
        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        // Saves the hashed token and its expiry date in the database.
        tokenRepository.save(new EmailVerificationToken(
                hash(token),
                null,
                tutorId,
                LocalDateTime.now().plusHours(validHours)
        ));

        // Returns the original token for the verification link.
        return token;
    }

    // Removes a student's pending verification token.
    public void cancel(String studentNumber) {

        tokenRepository.deleteAll(
                tokenRepository.findAllByStudentNumber(studentNumber)
        );
    }

    // Removes a tutor's pending verification token.
    public void cancelTutor(String tutorId) {

        tokenRepository.deleteAll(
                tokenRepository.findAllByTutorId(tutorId)
        );
    }

    // Checks the verification token when a student or tutor opens the link.
    public void verify(String token) {

        // Checks whether a token was provided.
        if (Helper.isNullOrEmpty(token) || token.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A verification token is required. Use the full verification link from the backend log."
            );
        }

        // Searches for the token using its hash.
        String hashedToken = hash(token.trim());

        // Checks whether the token exists in the database.
        if (tokenRepository.findById(hashedToken).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This verification link is not valid or was already used. If you still cannot log in, "
                            + RESEND_HINT
            );
        }

        // Gets the saved verification token.
        EmailVerificationToken stored =
                tokenRepository.findById(hashedToken).get();

        // Checks whether the verification link has expired.
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "This verification link has expired. Please " + RESEND_HINT
            );
        }

        // Removes the token to mark the account as verified.
        if (stored.getStudentNumber() != null) {

            // Removes the student's verification token.
            cancel(stored.getStudentNumber());

        } else {

            // Removes the tutor's verification token.
            cancelTutor(stored.getTutorId());
        }
    }

    // Creates a new verification link for a student.
    // The response does not reveal whether the email address exists.
    public void resend(String email) {

        // Checks whether a valid email address was provided.
        if (Helper.isNullOrEmpty(email) || !Helper.isValidEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid email is required."
            );
        }

        // Searches for the student using their email address.
        if (studentRepository.findByEmailIgnoreCase(email).isPresent()) {

            // Gets the student record.
            Student student =
                    studentRepository.findByEmailIgnoreCase(email).get();

            // Creates a new link only if the student is still waiting for verification.
            if (isPending(student.getStudentNumber())) {

                // Creates a new token and writes the verification link to the backend log.
                String token = issueToken(student.getStudentNumber());

                sendLink(student, token);
            }
        }
    }

    // Creates a new verification link for a tutor.
    // The response does not reveal whether the email address exists.
    public void resendTutor(String email) {

        // Checks whether a valid email address was provided.
        if (Helper.isNullOrEmpty(email) || !Helper.isValidEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid email is required."
            );
        }

        // Searches for the tutor using their email address.
        if (tutorRepository.findByEmailIgnoreCase(email).isPresent()) {

            // Gets the tutor record.
            Tutor tutor =
                    tutorRepository.findByEmailIgnoreCase(email).get();

            // Creates a new link only if the tutor is still waiting for verification.
            if (isTutorPending(tutor.getTutorId())) {

                // Creates a new token and writes the verification link to the backend log.
                String token = issueTutorToken(tutor.getTutorId());

                sendLink(tutor, token);
            }
        }
    }

    // Creates the student's verification link and writes it to the backend log.
    // No email is sent.
    public void sendLink(Student student, String token) {

        // Builds the student's verification URL.
        String link = publicUrl + "/student/verify?token=" + token;

        // Writes the verification link to the backend log.
        log.info(
                "Student verification link for {}: {}",
                student.getEmail(),
                link
        );
    }

    // Creates the tutor's verification link and writes it to the backend log.
    // No email is sent.
    public void sendLink(Tutor tutor, String token) {

        // Builds the tutor's verification URL.
        String link = publicUrl + "/tutor/verify?token=" + token;

        // Writes the verification link to the backend log.
        log.info(
                "Tutor verification link for {}: {}",
                tutor.getEmail(),
                link
        );
    }

    // Converts the verification token into an SHA-256 hash.
    // Only the hash is stored in the database.
    private String hash(String token) {

        try {

            // Creates an SHA-256 hashing tool.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Converts the token into bytes and creates its hash.
            byte[] hashedToken = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            // Converts the hashed bytes into a hexadecimal string.
            return HexFormat.of().formatHex(hashedToken);

        } catch (NoSuchAlgorithmException e) {

            // Handles the situation where SHA-256 is not available.
            throw new IllegalStateException(
                    "SHA-256 is not available.",
                    e
            );
        }
    }
}
