
package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Student;
import za.ac.cput.security.AccessChecker;
import za.ac.cput.security.EmailVerificationService;
import za.ac.cput.security.PasswordChanger;
import za.ac.cput.security.RegistrationGuard;
import za.ac.cput.service.StudentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
StudentController.java
Student Controller
Author: Sabelo Ceza - 220094489
Date: 28/06/2026
*/

@RestController
@RequestMapping("/student")
public class StudentController {

    private StudentService studentService;
    private AccessChecker accessChecker;
    private RegistrationGuard registrationGuard;
    private EmailVerificationService verificationService;
    private PasswordChanger passwordChanger;

    // Receives the services needed by this controller.
    @Autowired
    public StudentController(StudentService studentService,
                             AccessChecker accessChecker,
                             RegistrationGuard registrationGuard,
                             EmailVerificationService verificationService,
                             PasswordChanger passwordChanger) {

        this.studentService = studentService;
        this.accessChecker = accessChecker;
        this.registrationGuard = registrationGuard;
        this.verificationService = verificationService;
        this.passwordChanger = passwordChanger;
    }

    // Creates a new student account.
    // Anyone can register, but existing student numbers and emails cannot be reused.
    @PostMapping("/create")
    public ResponseEntity<Student> create(@RequestBody Student student,
                                          Authentication authentication) {

        // Checks that the student details are valid.
        registrationGuard.checkNewStudent(student);

        // Stores the email verification token.
        String token = null;

        // Checks whether the person creating the account is an admin.
        if (!accessChecker.isAdmin(authentication)) {

            // Prevents a new student from adding bookings during registration.
            student = new Student.Builder()
                    .copy(student)
                    .setBookings(new ArrayList<>())
                    .build();

            // Creates a verification token if email verification is required.
            if (verificationService.isRequired()) {
                token = verificationService.issueToken(student.getStudentNumber());
            }
        }

        Student created;

        // Saves the new student account.
        try {
            created = studentService.create(student);

        } catch (RuntimeException e) {

            // Removes the verification token if saving the student fails.
            if (token != null) {
                verificationService.cancel(student.getStudentNumber());
            }

            // Sends the error back to Spring.
            throw e;
        }

        // Returns the student if email verification is not required.
        if (token == null) {
            return ResponseEntity.ok(created);
        }

        // Sends the verification link to the student's email address.
        verificationService.sendLink(created, token);

        // Tells the frontend that email verification is still pending.
        return ResponseEntity.ok()
                .header("X-Email-Verification", "pending")
                .body(created);
    }

    // Confirms the student's email address using the verification token.
    // This endpoint is public because the student has not logged in yet.
    @GetMapping("/verify")
    public Map<String, String> verify(
            @RequestParam(value = "token", required = false) String token) {

        // Checks the token and confirms the email address.
        verificationService.verify(token);

        return Map.of("message",
                "Your email address is confirmed. You can now log in with your email and password.");
    }

    // Sends another verification link to the student's email address.
    // The same response is returned whether the email exists or not.
    @PostMapping("/resend-verification")
    public Map<String, String> resendVerification(
            @RequestBody Map<String, String> body) {

        String email = null;

        // Gets the email from the request body if the body exists.
        if (body != null) {
            email = body.get("email");
        }

        // Requests a new verification link.
        verificationService.resend(email);

        return Map.of("message",
                "If that email belongs to an account that is not verified yet, a new link has been sent.");
    }

    // Allows an admin or the student who owns the account to view student details.
    @GetMapping("/read/{studentNumber}")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isStudent(#studentNumber, authentication)")
    public Student read(@PathVariable("studentNumber") String studentNumber) {

        // Searches for the student using their student number.
        Student student = studentService.read(studentNumber);

        // Checks whether the student exists.
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Student not found: " + studentNumber);
        }

        return student;
    }

    // Updates student details.
    // An admin or the student who owns the account can perform this action.
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isStudent(#student.studentNumber, authentication)")
    public Student update(@RequestBody Student student,
                          Authentication authentication) {

        // Applies extra checks when a student updates their own details.
        if (!accessChecker.isAdmin(authentication)) {
            registrationGuard.checkStudentUpdate(student);
        }

        return studentService.update(student);
    }

    // Allows a student to change their own password.
    // The current password and new password must be sent in the request body.
    @PutMapping("/change-password")
    public Map<String, String> changePassword(
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        // Finds the student account belonging to the logged-in user.
        Student student = accessChecker.currentStudent(authentication);

        // Checks that the logged-in user is a student.
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only a student can change a student password here. An admin resets it with PUT /student/update.");
        }

        // Checks the current password and creates a hash of the new password.
        String newHash = passwordChanger.newHash(student.getPassword(), body);

        // Updates the student account with the new password hash.
        Student updatedStudent = new Student.Builder()
                .copy(student)
                .setPassword(newHash)
                .build();

        studentService.update(updatedStudent);

        return Map.of("message",
                "Your password was changed. Use the new password from now on.");
    }

    // Deletes a student account.
    // An admin or the student who owns the account can request deletion.
    @DeleteMapping("/delete/{studentNumber}")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isStudent(#studentNumber, authentication)")
    public void delete(@PathVariable String studentNumber,
                       Authentication authentication) {

        // Finds the student before attempting to delete the account.
        Student existing = studentService.read(studentNumber);

        // Checks whether the student exists.
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Student not found: " + studentNumber);
        }

        // Checks whether the person deleting the account is an admin.
        if (!accessChecker.isAdmin(authentication)) {

            boolean hasPayment = false;

            // Checks whether the student has any bookings.
            if (existing.getBookings() != null) {

                // Goes through each booking belonging to the student.
                for (int i = 0; i < existing.getBookings().size(); i++) {

                    // Gets the current booking.
                    if (existing.getBookings().get(i).getPayment() != null) {
                        hasPayment = true;
                        break;
                    }
                }
            }

            // Only an admin can delete a student account with payment records.
            if (hasPayment) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "This account has payments, so only an admin can delete it.");
            }
        }

        // Deletes the student account.
        studentService.delete(studentNumber);

        // Removes any verification token linked to the deleted student.
        verificationService.cancel(studentNumber);
    }

    // Returns the full list of students.
    // Only admins are allowed to access this endpoint.
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Student> getAll() {
        return studentService.getAll();
    }
}
