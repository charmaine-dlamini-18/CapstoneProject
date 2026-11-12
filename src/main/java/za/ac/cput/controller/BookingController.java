package za.ac.cput.controller;
/*
BookingController.java
Booking Controller
Author: Charmaine Dlamini - 222056401
Date: 26/06/2026
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.security.AccessChecker;
import za.ac.cput.service.BookingService;
import za.ac.cput.service.StudentService;
import za.ac.cput.service.TutorService;
import za.ac.cput.util.Helper;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private BookingService service;
    private TutorService tutorService;
    private StudentService studentService;
    private AccessChecker accessChecker;

    // Provides the services needed by this controller.
    @Autowired
    public BookingController(BookingService service, TutorService tutorService,
                             StudentService studentService, AccessChecker accessChecker) {
        this.service = service;
        this.tutorService = tutorService;
        this.studentService = studentService;
        this.accessChecker = accessChecker;
    }

    // Creates a new booking.
    // Admins can choose the student and tutor, while students can only book for themselves.
    @PostMapping("/create")
    public Booking create(@RequestBody Booking booking,
                          @RequestParam(value = "studentNumber", required = false) String studentNumber,
                          @RequestParam(value = "tutorId", required = false) String tutorId,
                          Authentication authentication) {

        // Checks that all required booking details have been provided.
        validateBooking(booking);

        // Checks that the booking ID is not already being used.
        if (service.read(booking.getBookingId()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That booking ID is already used.");
        }

        // Checks whether the logged-in user is an admin.
        if (accessChecker.isAdmin(authentication)) {

            // Gets the student from the booking or finds the student using their student number.
            Student student = booking.getStudent();
            if (student == null && !Helper.isNullOrEmpty(studentNumber)) {
                student = studentService.read(studentNumber);
            }

            // Gets the tutor from the booking or finds the tutor using their tutor ID.
            Tutor tutor = booking.getTutor();
            if (tutor == null && !Helper.isNullOrEmpty(tutorId)) {
                tutor = tutorService.read(tutorId);
            }

            // A booking cannot be created without a student.
            if (student == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The booking needs a student.");
            }

            // A booking cannot be created without a tutor.
            if (tutor == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The booking needs a tutor.");
            }

            // Copies the booking details and adds the selected student and tutor.
            Booking secured = new Booking.Builder().copy(booking)
                    .setStudent(student)
                    .setTutor(tutor)
                    .build();

            return service.create(secured);
        }

        // Finds the student account belonging to the logged-in user.
        Student student = accessChecker.currentStudent(authentication);

        // Prevents users who are not students from creating bookings.
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can create bookings.");
        }

        // Checks that a tutor ID was provided.
        if (Helper.isNullOrEmpty(tutorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tutorId is required.");
        }

        // Finds the selected tutor.
        Tutor tutor = tutorService.read(tutorId);

        // Checks that the selected tutor exists.
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That tutor does not exist.");
        }

        // Creates the booking using the logged-in student and selected tutor.
        // The payment is added separately through the payment endpoint.
        Booking secured = new Booking.Builder().copy(booking)
                .setStudent(student)
                .setTutor(tutor)
                .setPayment(null)
                .build();

        return service.create(secured);
    }

    // Finds a booking using its ID.
    // Only an admin, the student or the tutor linked to the booking can access it.
    @GetMapping("/read/{bookingId}")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.canAccessBooking(#bookingId, authentication)")
    public Booking read(@PathVariable String bookingId) {
        Booking booking = service.read(bookingId);

        // Returns a 404 error if the booking does not exist.
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + bookingId);
        }

        return booking;
    }

    // Updates an existing booking.
    // Only an admin or someone linked to the booking can update it.
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.canAccessBooking(#booking.bookingId, authentication)")
    public Booking update(@RequestBody Booking booking, Authentication authentication) {

        // Checks that a booking and its ID were provided.
        if (booking == null || Helper.isNullOrEmpty(booking.getBookingId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required.");
        }

        // Finds the existing booking before updating it.
        Booking existing = service.read(booking.getBookingId());

        // Returns a 404 error if the booking does not exist.
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Booking not found: " + booking.getBookingId());
        }

        // Admins can update the booking without these restrictions.
        if (accessChecker.isAdmin(authentication)) {
            return service.update(booking);
        }

        // Students and tutors can update session details,
        // but cannot change the student, tutor or payment linked to the booking.
        Booking secured = new Booking.Builder().copy(booking)
                .setStudent(existing.getStudent())
                .setTutor(existing.getTutor())
                .setPayment(existing.getPayment())
                .build();

        return service.update(secured);
    }

    // Deletes a booking using its ID.
    // Only an admin or someone linked to the booking can attempt to delete it.
    @DeleteMapping("/delete/{bookingId}")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.canAccessBooking(#bookingId, authentication)")
    public boolean delete(@PathVariable String bookingId, Authentication authentication) {

        // Finds the booking before trying to delete it.
        Booking existing = service.read(bookingId);

        // Returns a 404 error if the booking does not exist.
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + bookingId);
        }

        // Only admins can delete a booking that already has a payment.
        if (existing.getPayment() != null && !accessChecker.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This booking has a payment, so only an admin can delete it.");
        }

        return service.delete(bookingId);
    }

    // Checks that all required booking details are provided before saving a booking.
    private void validateBooking(Booking booking) {

        // Checks that a booking was provided.
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A booking is required.");
        }

        // Checks that the booking ID is provided.
        if (Helper.isNullOrEmpty(booking.getBookingId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required.");
        }

        // Checks that the subject code is provided.
        if (Helper.isNullOrEmpty(booking.getSubjectCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "subjectCode is required.");
        }

        // Checks that the session type is provided.
        if (Helper.isNullOrEmpty(booking.getSessionType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionType is required.");
        }

        // Checks that the session duration is provided.
        if (Helper.isNullOrEmpty(booking.getDuration())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "duration is required.");
        }

        // Checks that a booking date is provided.
        if (booking.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date is required.");
        }
    }

    // Returns all bookings that the logged-in user is allowed to see.
    // Admins can see all bookings, while students and tutors only see their own.
    @GetMapping("/getAll")
    public List<Booking> getAll(Authentication authentication) {
        return accessChecker.bookingsVisibleTo(authentication);
    }
}
