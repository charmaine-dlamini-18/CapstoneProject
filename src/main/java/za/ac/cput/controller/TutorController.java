
package za.ac.cput.controller;

/*
Tutor Controller
Author: Imaan Achmat - 230458971
Date: 28/06/2026
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Tutor;
import za.ac.cput.security.*;
import za.ac.cput.service.TutorService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tutor")
public class TutorController {

    private TutorService service;
    private AccessChecker accessChecker;
    private RegistrationGuard registrationGuard;
    private PasswordChanger passwordChanger;
    private EmailVerificationService verificationService;

    // Receives the services needed by this controller.
    @Autowired
    public TutorController(TutorService service,
                           AccessChecker accessChecker,
                           RegistrationGuard registrationGuard,
                           PasswordChanger passwordChanger,
                           EmailVerificationService verificationService) {

        this.service = service;
        this.accessChecker = accessChecker;
        this.registrationGuard = registrationGuard;
        this.passwordChanger = passwordChanger;
        this.verificationService = verificationService;
    }

    // Creates a new tutor account.
    // Every registration is checked to prevent duplicate tutor IDs and email addresses.
    // Tutors who register themselves must verify their email before logging in.
    // Accounts created by admins are treated as trusted accounts.
    @PostMapping("/create")
    public ResponseEntity<Tutor> create(@RequestBody Tutor tutor,
                                        Authentication authentication) {

        // Checks that the tutor details are valid and not already in use.
        registrationGuard.checkNewTutor(tutor);

        // Stores the email verification token.
        String token = null;

        // Checks whether the person creating the account is an admin.
        if (!accessChecker.isAdmin(authentication)) {

            // Prevents a new tutor from adding bookings during registration.
            tutor = new Tutor.Builder()
                    .copy(tutor)
                    .setBookings(new ArrayList<>())
                    .build();

            // Creates a verification token if email verification is required.
            if (verificationService.isRequired()) {
                token = verificationService.issueTutorToken(tutor.getTutorId());
            }
        }

        Tutor created;

        // Saves the new tutor account.
        try {
            created = service.create(tutor);

        } catch (RuntimeException e) {

            // Removes the verification token if saving the tutor fails.
            if (token != null) {
                verificationService.cancelTutor(tutor.getTutorId());
            }

            // Sends the error back to Spring.
            throw e;
        }

        // Returns the tutor if email verification is not required.
        if (token == null) {
            return ResponseEntity.ok(created);
        }

        // Sends the verification link to the tutor's email address.
        verificationService.sendLink(created, token);

        // Tells the frontend that email verification is still pending.
        return ResponseEntity.ok()
                .header("X-Email-Verification", "pending")
                .body(created);
    }

    // Confirms the tutor's email address using the verification token.
    // This endpoint is public because the tutor has not logged in yet.
    @GetMapping("/verify")
    public Map<String, String> verify(
            @RequestParam(value = "token", required = false) String token) {

        // Checks the token and confirms the email address.
        verificationService.verify(token);

        return Map.of("message",
                "Your email address is confirmed. You can now log in with your email and password.");
    }

    // Sends another verification link to the tutor's email address.
    // The same response is returned whether the email exists or not.
    // This prevents people from checking which email addresses have accounts.
    @PostMapping("/resend-verification")
    public Map<String, String> resendVerification(
            @RequestBody Map<String, String> body) {

        String email = null;

        // Gets the email from the request body if the body exists.
        if (body != null) {
            email = body.get("email");
        }

        // Requests another verification link.
        verificationService.resendTutor(email);

        return Map.of("message",
                "If that email belongs to an account that is not verified yet, a new link has been sent.");
    }

    // Allows logged-in users to view tutor details.
    // Only the tutor and admins can see the tutor's bookings.
    @GetMapping("/read/{tutorId}")
    public Tutor read(@PathVariable String tutorId,
                      Authentication authentication) {

        // Searches for the tutor using the tutor ID.
        Tutor tutor = service.read(tutorId);

        // Returns an error if the tutor does not exist.
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tutor not found: " + tutorId);
        }

        // Returns the tutor details based on the logged-in user's permissions.
        return forViewer(tutor, authentication);
    }

    // Updates tutor details.
    // Only an admin or the tutor who owns the account can perform this action.
    // Tutors cannot change their password or email through this endpoint.
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isTutor(#tutor.tutorId, authentication)")
    public Tutor update(@RequestBody Tutor tutor,
                        Authentication authentication) {

        // Applies extra checks when a tutor updates their own details.
        if (!accessChecker.isAdmin(authentication)) {
            registrationGuard.checkTutorUpdate(tutor);
        }

        // Saves the updated tutor details.
        return service.update(tutor);
    }

    // Allows a tutor to change their own password.
    // The current password and new password must be sent in the request body.
    @PutMapping("/change-password")
    public Map<String, String> changePassword(
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        // Finds the tutor account belonging to the logged-in user.
        Tutor tutor = accessChecker.currentTutor(authentication);

        // Checks that the logged-in user is a tutor.
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only a tutor can change a tutor password here. An admin resets it with PUT /tutor/update.");
        }

        // Checks the current password and creates a hash of the new password.
        String newHash = passwordChanger.newHash(tutor.getPassword(), body);

        // Updates the tutor account with the new password hash.
        Tutor updatedTutor = new Tutor.Builder()
                .copy(tutor)
                .setPassword(newHash)
                .build();

        service.update(updatedTutor);

        return Map.of("message",
                "Your password was changed. Use the new password from now on.");
    }

    // Deletes a tutor account.
    // Only an admin or the tutor who owns the account can request deletion.
    @DeleteMapping("/delete/{tutorId}")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isTutor(#tutorId, authentication)")
    public boolean delete(@PathVariable String tutorId,
                          Authentication authentication) {

        // Finds the tutor before attempting to delete the account.
        Tutor existing = service.read(tutorId);

        // Checks whether the tutor exists.
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tutor not found: " + tutorId);
        }

        // Checks whether the person deleting the account is an admin.
        if (!accessChecker.isAdmin(authentication)) {

            // Prevents a tutor from deleting an account that still has bookings.
            if (existing.getBookings() != null
                    && !existing.getBookings().isEmpty()) {

                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "This tutor has bookings, so only an admin can delete the account.");
            }
        }

        // Deletes the tutor account and returns the result.
        return service.delete(tutorId);
    }

    // Returns the list of all tutors.
    // The details returned depend on the logged-in user's permissions.
    @GetMapping("/getAll")
    public List<Tutor> getAll(Authentication authentication) {

        // Creates a list to store the tutors that will be returned.
        List<Tutor> result = new ArrayList<>();

        // Gets all tutors from the service.
        List<Tutor> tutors = service.getAll();

        // Goes through each tutor in the list.
        for (Tutor tutor : tutors) {

            // Adds the tutor details based on the user's permissions.
            result.add(forViewer(tutor, authentication));
        }

        return result;
    }

    // Controls which tutor details a user is allowed to see.
    // Admins and the tutor themselves can see bookings.
    // Other logged-in users receive tutor details without bookings.
    private Tutor forViewer(Tutor tutor, Authentication authentication) {

        // Checks whether a tutor was provided.
        if (tutor == null) {
            return null;
        }

        // Allows admins and the tutor who owns the account to see all details.
        if (accessChecker.isAdmin(authentication)
                || accessChecker.isTutor(tutor.getTutorId(), authentication)) {

            return tutor;
        }

        // Removes bookings before returning the tutor to other users.
        Tutor safeTutor = new Tutor.Builder()
                .copy(tutor)
                .setBookings(new ArrayList<>())
                .build();

        return safeTutor;
    }
}
