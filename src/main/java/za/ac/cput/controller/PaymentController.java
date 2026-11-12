package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Tutor;
import za.ac.cput.security.AccessChecker;
import za.ac.cput.service.BookingService;
import za.ac.cput.service.PaymentService;
import za.ac.cput.util.Helper;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private PaymentService paymentService;
    private BookingService bookingService;
    private AccessChecker accessChecker;

    // Provides the services needed by this controller.
    @Autowired
    public PaymentController(PaymentService paymentService,
                             BookingService bookingService,
                             AccessChecker accessChecker) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.accessChecker = accessChecker;
    }

    // Creates a payment for a booking.
    // Students can only pay for their own bookings, while admins can create any payment.
    @PostMapping("/create")
    public Payment create(@RequestBody Payment payment,
                          @RequestParam(value = "bookingId", required = false) String bookingId,
                          Authentication authentication) {

        // Checks that payment details were provided.
        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A payment is required.");
        }

        // Allows an admin to create a payment.
        if (accessChecker.isAdmin(authentication)) {
            if (Helper.isNullOrEmpty(payment.getPaymentRef())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentRef is required.");
            }

            return paymentService.create(payment);
        }

        // Checks that a booking ID was provided.
        if (Helper.isNullOrEmpty(bookingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required.");
        }

        // Checks that the booking belongs to the logged-in user.
        if (!accessChecker.canAccessBooking(bookingId, authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "That is not your booking.");
        }

        // Checks that the payment reference was provided.
        if (Helper.isNullOrEmpty(payment.getPaymentRef())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentRef is required.");
        }

        // Checks that the payment method was provided.
        if (Helper.isNullOrEmpty(payment.getPaymentMethod())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentMethod is required.");
        }

        // Prevents an existing payment reference from being used again.
        if (paymentService.read(payment.getPaymentRef()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That payment reference is already used.");
        }

        // Finds the booking linked to the payment.
        Booking booking = bookingService.read(bookingId);

        // Checks that the booking exists.
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + bookingId);
        }

        // Prevents more than one payment from being added to the same booking.
        if (booking.getPayment() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This booking already has a payment.");
        }

        // Calculates the payment amount using the tutor's hourly rate and booking duration.
        double amount = calculateAmount(booking);

        // Uses the current date and time if no payment date was provided.
        LocalDateTime paymentDate = payment.getPaymentDate();

        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }

        // Creates the payment with the booking, calculated amount and payment status.
        // Cash, EFT and bank transfers remain pending until confirmed.
        Payment secured = new Payment.Builder().copy(payment)
                .setBooking(booking)
                .setAmount(amount)
                .setStatus(isCash(payment.getPaymentMethod()) ? "PENDING" : "PAID")
                .setPaymentDate(paymentDate)
                .build();

        return paymentService.create(secured);
    }

    // Finds a payment using its reference.
    // Only an admin or the student linked to the booking can access it.
    @GetMapping("/read/{paymentRef}")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.canAccessPayment(#paymentRef, authentication)")
    public Payment read(@PathVariable String paymentRef) {

        Payment payment = paymentService.read(paymentRef);

        // Returns a 404 error if the payment does not exist.
        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Payment not found: " + paymentRef);
        }

        return payment;
    }

    // Allows only admins to update payment details.
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Payment update(@RequestBody Payment payment) {

        // Checks that a payment and its reference were provided.
        if (payment == null || Helper.isNullOrEmpty(payment.getPaymentRef())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentRef is required.");
        }

        return paymentService.update(payment);
    }

    // Allows only admins to delete payments.
    @DeleteMapping("/delete/{paymentRef}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String paymentRef) {
        paymentService.delete(paymentRef);
    }

    // Calculates the payment amount using the tutor's hourly rate and booking duration.
    private double calculateAmount(Booking booking) {

        // Gets the tutor linked to the booking.
        Tutor tutor = booking.getTutor();

        // Checks that a tutor is linked to the booking.
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This booking has no tutor, so the price cannot be calculated.");
        }

        // Checks that the tutor has an hourly rate.
        if (tutor.getHourlyRate() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This tutor has no hourly rate, so the price cannot be calculated.");
        }

        // Converts the booking duration into hours.
        double hours = hoursFrom(booking.getDuration());

        // Checks that the booking duration is valid.
        if (hours <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The booking duration is not valid, so the price cannot be calculated.");
        }

        // Multiplies the hourly rate by the number of hours and rounds the amount to two decimal places.
        return Math.round(tutor.getHourlyRate() * hours * 100.0) / 100.0;
    }

    // Converts the booking duration into hours.
    // Accepts values such as "2 hours", "1.5 hrs", "30 minutes" or "60".
    private double hoursFrom(String duration) {

        // Checks that a duration was provided.
        if (Helper.isNullOrEmpty(duration)) {
            return -1;
        }

        // Removes extra spaces at the beginning and end and changes the text to lowercase.
        String text = duration.trim().toLowerCase();

        // Splits the duration into the number and the unit.
        String[] parts = text.split("\\s+");

        // If only a number is provided, it is treated as minutes.
        if (parts.length == 1) {
            try {
                return Double.parseDouble(parts[0]) / 60.0;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        // Checks that the duration contains a number and a unit.
        if (parts.length != 2) {
            return -1;
        }

        double value;

        // Converts the number from text into a decimal value.
        try {
            value = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e) {
            return -1;
        }

        // Returns the value as hours.
        if (parts[1].startsWith("hour") || parts[1].startsWith("hr")) {
            return value;
        }

        // Converts minutes into hours.
        if (parts[1].startsWith("min")) {
            return value / 60.0;
        }

        // Returns -1 if the duration unit is not recognised.
        return -1;
    }

    // Checks whether the payment method is cash, EFT or bank transfer.
    private boolean isCash(String paymentMethod) {

        // Returns false if no payment method was provided.
        if (Helper.isNullOrEmpty(paymentMethod)) {
            return false;
        }

        // Removes extra spaces and changes the payment method to lowercase.
        String method = paymentMethod.trim().toLowerCase();

        return method.equals("cash")
                || method.equals("eft")
                || method.contains("bank transfer");
    }

    // Returns the payments the logged-in user is allowed to see.
    // Admins can see all payments, while students only see payments linked to their bookings.
    @GetMapping("/getAll")
    public List<Payment> getAll(Authentication authentication) {
        return accessChecker.paymentsVisibleTo(authentication);
    }
}