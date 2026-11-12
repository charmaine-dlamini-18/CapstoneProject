
package za.ac.cput.security;

/*
AccessChecker.java
Checks who is logged in and whether a record belongs to them.
It is used by @PreAuthorize to control access to records.
Author: Charmaine Dlamini - 222056401
Date: 08/11/2026
*/

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.PaymentRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.TutorRepository;

import java.util.ArrayList;
import java.util.List;

// Registers this class as a Spring component.
@Component("accessChecker")
public class AccessChecker {

    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public AccessChecker(StudentRepository studentRepository,
                         TutorRepository tutorRepository,
                         BookingRepository bookingRepository,
                         PaymentRepository paymentRepository) {

        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    // Checks whether the logged-in user has the ADMIN role.
    public boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ADMIN");
    }

    // Checks whether the student number belongs to the logged-in student.
    public boolean isStudent(String studentNumber, Authentication authentication) {

        // Rejects the request if the student number is missing
        // or the logged-in user does not have the STUDENT role.
        if (studentNumber == null || !hasRole(authentication, "STUDENT")) {
            return false;
        }

        // Searches for the student using their student number.
        if (studentRepository.findById(studentNumber).isEmpty()) {
            return false;
        }

        // Gets the student record from the database.
        Student student = studentRepository.findById(studentNumber).get();

        // Checks whether the student's email matches the logged-in user's email.
        return sameEmail(authentication, student.getEmail());
    }

    // Checks whether the tutor ID belongs to the logged-in tutor.
    public boolean isTutor(String tutorId, Authentication authentication) {

        // Rejects the request if the tutor ID is missing
        // or the logged-in user does not have the TUTOR role.
        if (tutorId == null || !hasRole(authentication, "TUTOR")) {
            return false;
        }

        // Checks whether the tutor exists in the database.
        if (!tutorRepository.findById(tutorId).isPresent()) {
            return false;
        }

        // Gets the tutor record from the database.
        Tutor tutor = tutorRepository.findById(tutorId).get();

        // Checks whether the tutor's email matches the logged-in user's email.
        return sameEmail(authentication, tutor.getEmail());
    }

    // Gets the Student record of the currently logged-in student.
    // Returns null if the user is not a student or no matching record exists.
    public Student currentStudent(Authentication authentication) {

        if (!hasRole(authentication, "STUDENT")) {
            return null;
        }

        // Finds the student using the email of the logged-in user.
        return studentRepository.findByEmailIgnoreCase(authentication.getName())
                .orElse(null);
    }

    // Gets the Tutor record of the currently logged-in tutor.
    // Returns null if the user is not a tutor or no matching record exists.
    public Tutor currentTutor(Authentication authentication) {

        if (!hasRole(authentication, "TUTOR")) {
            return null;
        }

        // Finds the tutor using the email of the logged-in user.
        return tutorRepository.findByEmailIgnoreCase(authentication.getName())
                .orElse(null);
    }

    // Checks whether the logged-in user is allowed to access a booking.
    // A student can access their own booking.
    // A tutor can access a booking assigned to them.
    @Transactional(readOnly = true)
    public boolean canAccessBooking(String bookingId, Authentication authentication) {

        // Rejects the request if the booking ID is missing.
        if (bookingId == null) {
            return false;
        }

        // Checks whether the booking exists.
        if (!bookingRepository.findById(bookingId).isPresent()) {
            return false;
        }

        // Gets the booking from the database.
        Booking booking = bookingRepository.findById(bookingId).get();

        // Checks whether the booking belongs to the logged-in student or tutor.
        return ownsBooking(booking, authentication);
    }

    // Checks whether the logged-in student can access a payment.
    // The student must own the booking connected to that payment.
    @Transactional(readOnly = true)
    public boolean canAccessPayment(String paymentRef, Authentication authentication) {

        // Rejects the request if the payment reference is missing.
        if (paymentRef == null) {
            return false;
        }

        // Checks whether the payment exists.
        if (!paymentRepository.findById(paymentRef).isPresent()) {
            return false;
        }

        // Gets the payment from the database.
        Payment payment = paymentRepository.findById(paymentRef).get();

        // Checks whether the payment belongs to the logged-in student.
        return ownsPayment(payment, authentication);
    }

    // Admins can see all bookings.
    // Students and tutors can only see bookings that belong to them.
    @Transactional(readOnly = true)
    public List<Booking> bookingsVisibleTo(Authentication authentication) {

        // Gets all bookings from the database.
        List<Booking> all = bookingRepository.findAll();

        // Admins are allowed to see every booking.
        if (isAdmin(authentication)) {
            return all;
        }

        // Creates a list to store bookings the user is allowed to see.
        List<Booking> visibleBookings = new ArrayList<>();

        // Goes through all bookings one by one.
        for (int i = 0; i < all.size(); i++) {

            Booking booking = all.get(i);

            // Adds the booking if it belongs to the logged-in user.
            if (ownsBooking(booking, authentication)) {
                visibleBookings.add(booking);
            }
        }

        return visibleBookings;
    }

    // Admins can see all payments.
    // Students can only see payments connected to their own bookings.
    @Transactional(readOnly = true)
    public List<Payment> paymentsVisibleTo(Authentication authentication) {

        // Gets all payments from the database.
        List<Payment> all = paymentRepository.findAll();

        // Admins are allowed to see every payment.
        if (isAdmin(authentication)) {
            return all;
        }

        // Creates a list to store payments the user is allowed to see.
        List<Payment> visiblePayments = new ArrayList<>();

        // Goes through all payments one by one.
        for (int i = 0; i < all.size(); i++) {

            Payment payment = all.get(i);

            // Adds the payment if it belongs to the logged-in student.
            if (ownsPayment(payment, authentication)) {
                visiblePayments.add(payment);
            }
        }

        return visiblePayments;
    }

    // Checks whether a booking belongs to the logged-in student or tutor.
    private boolean ownsBooking(Booking booking, Authentication authentication) {

        // Checks whether the logged-in student owns this booking.
        if (hasRole(authentication, "STUDENT")
                && booking.getStudent() != null
                && sameEmail(authentication, booking.getStudent().getEmail())) {

            return true;
        }

        // Checks whether the logged-in tutor is assigned to this booking.
        if (hasRole(authentication, "TUTOR")
                && booking.getTutor() != null
                && sameEmail(authentication, booking.getTutor().getEmail())) {

            return true;
        }

        // The booking does not belong to the logged-in user.
        return false;
    }

    // Checks whether a payment belongs to the logged-in student.
    private boolean ownsPayment(Payment payment, Authentication authentication) {

        // Only students can access payments through this method.
        if (!hasRole(authentication, "STUDENT")) {
            return false;
        }

        // Checks that the payment has a booking connected to it.
        if (payment.getBooking() == null) {
            return false;
        }

        // Checks that the booking has a student connected to it.
        if (payment.getBooking().getStudent() == null) {
            return false;
        }

        // Checks whether the booking belongs to the logged-in student.
        return sameEmail(
                authentication,
                payment.getBooking().getStudent().getEmail()
        );
    }

    // Checks whether the logged-in user has the requested role.
    private boolean hasRole(Authentication authentication, String role) {

        // Returns false if nobody is logged in.
        if (authentication == null) {
            return false;
        }

        // Gets the roles and permissions assigned to the logged-in user.
        for (GrantedAuthority authority : authentication.getAuthorities()) {

            // Spring Security stores roles with the prefix "ROLE_".
            String requiredRole = "ROLE_" + role;

            // Checks whether the user has the required role.
            if (requiredRole.equals(authority.getAuthority())) {
                return true;
            }
        }

        // The user does not have the required role.
        return false;
    }

    // Checks whether the logged-in user's email matches the record's email.
    private boolean sameEmail(Authentication authentication, String email) {

        // Rejects the comparison if the user is not logged in or the email is missing.
        if (authentication == null || email == null) {
            return false;
        }

        // Compares the emails without considering uppercase or lowercase letters.
        return email.equalsIgnoreCase(authentication.getName());
    }
}
