
package za.ac.cput.security;

/*
RegistrationGuard.java
Safety checks for sign-up and for changing an email or password.
Author: Charmaine Dlamini- 222056401
Date: 08/11/2026
*/

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Admin;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.TutorRepository;
import za.ac.cput.util.Helper;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

// Registers this class as a Spring-managed component.
@Component
public class RegistrationGuard {

    // Sets the minimum and maximum password lengths.
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_BYTES = 72;

    // Uses the same message when an ID or email is already registered.
    private static final String TAKEN_MESSAGE =
            "Registration failed. That ID or email is already registered.";

    private final AdminRepository adminRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final EmailVerificationService verificationService;

    // Constructor injection allows Spring to provide the required dependencies.
    public RegistrationGuard(AdminRepository adminRepository,
                             TutorRepository tutorRepository,
                             StudentRepository studentRepository,
                             EmailVerificationService verificationService) {
        this.adminRepository = adminRepository;
        this.tutorRepository = tutorRepository;
        this.studentRepository = studentRepository;
        this.verificationService = verificationService;
    }

    // Checks whether a new student can register.
    public void checkNewStudent(Student student) {

        // Checks that the student and student number have been provided.
        if (student == null || Helper.isNullOrEmpty(student.getStudentNumber())) {
            throw badRequest("Student number is required.");
        }

        // Validates the student's email and password.
        checkEmail(student.getEmail());
        checkPassword(student.getPassword(), true);

        // Prevents registration with an email already used by an admin or tutor.
        if (adminRepository.findByEmailIgnoreCase(student.getEmail()).isPresent()
                || tutorRepository.findByEmailIgnoreCase(student.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, TAKEN_MESSAGE);
        }

        // Removes previous unconfirmed student registrations using the same ID or email.
        removeUnconfirmedSignUp(studentRepository.findById(student.getStudentNumber()));
        removeUnconfirmedSignUp(studentRepository.findByEmailIgnoreCase(student.getEmail()));

        // Prevents an existing student ID or email from being overwritten.
        if (studentRepository.existsById(student.getStudentNumber())
                || studentRepository.findByEmailIgnoreCase(student.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, TAKEN_MESSAGE);
        }
    }

    // Checks whether a new tutor can register.
    public void checkNewTutor(Tutor tutor) {

        // Checks that the tutor and tutor ID have been provided.
        if (tutor == null || Helper.isNullOrEmpty(tutor.getTutorId())) {
            throw badRequest("Tutor ID is required.");
        }

        // Validates the tutor's email and password.
        checkEmail(tutor.getEmail());
        checkPassword(tutor.getPassword(), true);

        // Removes previous unconfirmed tutor registrations using the same ID or email.
        removeUnconfirmedTutorSignUp(tutorRepository.findById(tutor.getTutorId()));
        removeUnconfirmedTutorSignUp(tutorRepository.findByEmailIgnoreCase(tutor.getEmail()));

        // Prevents registration with an existing tutor ID or email.
        if (tutorRepository.existsById(tutor.getTutorId()) || emailUsedByAnyone(tutor.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, TAKEN_MESSAGE);
        }
    }

    // Checks whether a new admin can be registered.
    public void checkNewAdmin(Admin admin) {

        // Checks that the admin and admin ID have been provided.
        if (admin == null || Helper.isNullOrEmpty(admin.getAdminId())) {
            throw badRequest("Admin ID is required.");
        }

        // Validates the admin's email and password.
        checkEmail(admin.getEmail());
        checkPassword(admin.getPassword(), true);

        // Prevents an existing admin ID or email from being overwritten.
        if (adminRepository.existsById(admin.getAdminId()) || emailUsedByAnyone(admin.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, TAKEN_MESSAGE);
        }
    }

    // Checks whether a student can update their details.
    public void checkStudentUpdate(Student student) {

        // Validates the updated email and prevents password changes through this method.
        checkEmail(student.getEmail());
        rejectPasswordInUpdate(student.getPassword(), "/student/change-password");

        // Prevents students from changing an email address that was confirmed during registration.
        Optional<Student> existing = studentRepository.findById(student.getStudentNumber());

        if (existing.isPresent() && !student.getEmail().equalsIgnoreCase(existing.get().getEmail())) {
            throw emailLocked();
        }
    }

    // Checks whether a tutor can update their details.
    public void checkTutorUpdate(Tutor tutor) {

        // Validates the updated email and prevents password changes through this method.
        checkEmail(tutor.getEmail());
        rejectPasswordInUpdate(tutor.getPassword(), "/tutor/change-password");

        // Prevents tutors from changing an email address that was confirmed during registration.
        Optional<Tutor> existing = tutorRepository.findById(tutor.getTutorId());

        if (existing.isPresent() && !tutor.getEmail().equalsIgnoreCase(existing.get().getEmail())) {
            throw emailLocked();
        }
    }

    // Checks whether a new password meets the registration requirements.
    public void checkNewPassword(String password) {
        checkPassword(password, true);
    }

    // Removes an old student registration that has not been verified and has no bookings.
    private void removeUnconfirmedSignUp(Optional<Student> existing) {

        // Stops if no matching student record exists.
        if (existing.isEmpty()) {
            return;
        }

        // Gets the existing student and checks whether they have any bookings.
        Student old = existing.get();
        boolean hasNoBookings = old.getBookings() == null || old.getBookings().isEmpty();

        // Cancels the pending verification and deletes the old registration when it is safe to do so.
        if (hasNoBookings && verificationService.isPending(old.getStudentNumber())) {
            verificationService.cancel(old.getStudentNumber());
            studentRepository.delete(old);
        }
    }

    // Removes an old tutor registration that has not been verified and has no bookings.
    private void removeUnconfirmedTutorSignUp(Optional<Tutor> existing) {

        // Stops if no matching tutor record exists.
        if (existing.isEmpty()) {
            return;
        }

        // Gets the existing tutor and checks whether they have any bookings.
        Tutor old = existing.get();
        boolean hasNoBookings = old.getBookings() == null || old.getBookings().isEmpty();

        // Cancels the pending verification and deletes the old registration when it is safe to do so.
        if (hasNoBookings && verificationService.isTutorPending(old.getTutorId())) {
            verificationService.cancelTutor(old.getTutorId());
            tutorRepository.delete(old);
        }
    }

    // Checks whether an email is already used by an admin, tutor or student.
    private boolean emailUsedByAnyone(String email) {
        return adminRepository.findByEmailIgnoreCase(email).isPresent()
                || tutorRepository.findByEmailIgnoreCase(email).isPresent()
                || studentRepository.findByEmailIgnoreCase(email).isPresent();
    }

    // Checks whether the supplied email address is valid.
    private void checkEmail(String email) {
        if (Helper.isNullOrEmpty(email) || !Helper.isValidEmail(email)) {
            throw badRequest("A valid email is required.");
        }
    }

    // Prevents password changes through normal profile update methods.
    private void rejectPasswordInUpdate(String password, String endpoint) {
        if (!Helper.isNullOrEmpty(password)) {
            throw badRequest("The update call does not change passwords. To change your password use PUT "
                    + endpoint + " with currentPassword and newPassword.");
        }
    }

    // Checks whether a password meets the required length and format.
    private void checkPassword(String password, boolean required) {

        // Checks whether a password is required.
        if (Helper.isNullOrEmpty(password)) {
            if (required) {
                throw badRequest("Password is required.");
            }
            return;
        }

        // Checks that the password meets the minimum length.
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw badRequest("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }

        // Checks that the password does not exceed BCrypt's supported byte length.
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw badRequest("Password must be at most " + MAX_PASSWORD_BYTES + " characters.");
        }

        // Prevents users from submitting an already hashed password.
        if (Helper.isBcryptHash(password)) {
            throw badRequest("That password is not allowed.");
        }
    }

    // Creates an error when a user attempts to change a confirmed email address.
    private ResponseStatusException emailLocked() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You cannot change your email address here, because it was confirmed when you signed up. "
                        + "Ask an admin to change it.");
    }

    // Creates a 400 Bad Request exception with the supplied message.
    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
