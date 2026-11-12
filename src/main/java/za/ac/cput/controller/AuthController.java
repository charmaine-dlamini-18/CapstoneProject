package za.ac.cput.controller;

/*
AuthController.java
Tells the frontend which user is currently logged in.
Spring Security checks the login details before this controller is accessed.
Author: Charmaine Dlamini - 222056401
Date: 10/11/2026
 */

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Admin;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.TutorRepository;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
// Sets /auth as the starting URL for the endpoints in this controller.
@RequestMapping("/auth")
public class AuthController {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;

    // Receives the repositories needed by this controller.
    public AuthController(AdminRepository adminRepository,
                          StudentRepository studentRepository,
                          TutorRepository tutorRepository) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
    }

    // Returns the account details of the user who is currently logged in.
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {

        // Checks whether a user is logged in.
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in first.");
        }

        // Gets the email address of the logged-in user.
        String email = authentication.getName();

        // Finds the user's role from their login information.
        String role = "";

        // Checks the roles assigned to the logged-in user.
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String authorityName = authority.getAuthority();

            // Removes ROLE_ to get the actual role name.
            if (authorityName.startsWith("ROLE_")) {
                role = authorityName.substring("ROLE_".length());
                break;
            }
        }

        // If the user is a student, find and return their account details.
        if ("STUDENT".equals(role)) {
            Student student = studentRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Your student account was not found."));

            return body("student", student.getStudentNumber(),
                    student.getFirstName(), student.getLastName(), student.getEmail());
        }

        // If the user is a tutor, find and return their account details.
        if ("TUTOR".equals(role)) {
            Tutor tutor = tutorRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Your tutor account was not found."));

            return body("tutor", tutor.getTutorId(),
                    tutor.getFirstName(), tutor.getLastName(), tutor.getEmail());
        }

        // If the user is an admin, find and return their account details.
        if ("ADMIN".equals(role)) {
            Admin admin = adminRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Your admin account was not found."));

            return body("admin", admin.getAdminId(),
                    admin.getFirstName(), admin.getLastName(), admin.getEmail());
        }

        // Returns a 403 error if the user does not have a recognized role.
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This account has no recognized role.");
    }

    // Creates the response containing the user's role, ID, name and email.
    private Map<String, Object> body(String role, String id, String firstName,
                                     String lastName, String email) {

        // Uses an empty string if the first name or last name is missing.
        if (firstName == null) {
            firstName = "";
        }

        if (lastName == null) {
            lastName = "";
        }

        // Joins the first name and last name to create the full name.
        String name = (firstName + " " + lastName).trim();

        // Creates a map to store the user's details.
        Map<String, Object> body = new LinkedHashMap<>();

        // Adds the user's role to the response.
        body.put("role", role);

        // Adds the user's ID. Uses an empty string if the ID is missing.
        if (id == null) {
            id = "";
        }
        body.put("id", id);

        // Adds the user's name. Uses "User" if the name is empty.
        if (name.isEmpty()) {
            name = "User";
        }
        body.put("name", name);

        // Adds the user's email. Uses an empty string if the email is missing.
        if (email == null) {
            email = "";
        }
        body.put("email", email);

        // Returns all the user's details to the frontend.
        return body;
    }
}