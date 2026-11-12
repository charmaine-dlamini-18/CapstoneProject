package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Admin;
import za.ac.cput.security.PasswordChanger;
import za.ac.cput.security.RegistrationGuard;
import za.ac.cput.service.AdminService;

import java.util.List;
import java.util.Map;

/*
AdminController.java
AdminController
Author: Thimna Barbara Booi - 230232108
Date: 28/06/2026
 */

// Allows only users with the ADMIN role to access this controller.
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final RegistrationGuard registrationGuard;
    private final PasswordChanger passwordChanger;

    // Creates the controller and provides the services it needs.
    @Autowired
    public AdminController(AdminService adminService,
                           RegistrationGuard registrationGuard,
                           PasswordChanger passwordChanger) {
        this.adminService = adminService;
        this.registrationGuard = registrationGuard;
        this.passwordChanger = passwordChanger;
    }

    // Creates a new admin account after checking the email and password.
    @PostMapping("/create")
    public Admin create(@RequestBody Admin admin) {
        // Checks the new admin details and prevents an existing account from being overwritten.
        registrationGuard.checkNewAdmin(admin);
        return adminService.create(admin);
    }

    // Finds an admin account using its ID.
    @GetMapping("/read/{adminId}")
    public Admin read(@PathVariable("adminId") String adminId) {
        Admin admin = adminService.read(adminId);

        // Returns a 404 error if the admin account does not exist.
        if (admin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found: " + adminId);
        }
        return admin;
    }

    // Updates an admin account. This can also be used to reset another admin's password.
    @PutMapping("/update")
    public Admin update(@RequestBody Admin admin) {
        return adminService.update(admin);
    }

    // Allows the logged-in admin to change their own password.
    @PutMapping("/change-password")
    public Map<String, String> changePassword(@RequestBody Map<String, String> body,
                                              Authentication authentication) {

        // Finds the admin account using the email of the logged-in user.
        Admin admin = adminService.findByEmail(authentication.getName());

        // Returns a 404 error if the admin account cannot be found.
        if (admin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Your admin account was not found.");
        }

        // Checks the current password and creates a secure hash for the new password.
        String newHash = passwordChanger.newHash(admin.getPassword(), body);

        // Saves the updated admin account with the new password hash.
        adminService.update(new Admin.Builder().copy(admin).setPassword(newHash).build());

        // Returns a message to confirm that the password was changed.
        return Map.of("message", "Your password was changed. Use the new password from now on.");
    }

    // Deletes an admin account using its ID.
    @DeleteMapping("/delete/{adminId}")
    public void delete(@PathVariable String adminId) {

        // Checks whether the admin account exists before deleting it.
        if (adminService.read(adminId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found: " + adminId);
        }
        adminService.delete(adminId);
    }

    // Returns a list of all admin accounts.
    @GetMapping("/getAll")
    public List<Admin> getAll() {
        return adminService.getAll();
    }

    // Finds an admin account using its email address.
    @GetMapping("/findByEmail/{email}")
    public Admin findByEmail(@PathVariable String email) {
        Admin admin = adminService.findByEmail(email);

        // Returns a 404 error if no admin account has that email address.
        if (admin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No admin has the email " + email);
        }
        return admin;
    }
}