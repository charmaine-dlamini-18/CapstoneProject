package za.ac.cput.factory;
/*
BookingFactoryTest.java
Booking Factory Test
Author: Charmaine Dlamini-222056401
Date: 23/03/2026
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BookingFactoryTest {

    private static Booking booking;

    @BeforeEach
    void setUp() {
        Student student = StudentFactory.createStudent(
                "220094489",
                "Sabelo",
                "Ceza",
                "220094489@mycput.ac.za",
                "073 985 1110",
                "SabieCeza2026",
                "Third year",
                new ArrayList<>()
        );

        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                1500.00,
                LocalDateTime.now(),
                "Card",
                "Completed",
                null
        );

        booking = BookingFactory.createBooking(
                "B12345",
                "ADP362S",
                "Online",
                "2 hours",
                "T987654",
                LocalDateTime.of(2026, 5, 20, 10, 30),
                student,
                payment
        );
    }

    @Test
    void createBooking() {
        assertNotNull(booking);
        System.out.println(booking);
        assertEquals("B12345", booking.getBookingId());
        assertEquals("ADP362S", booking.getSubjectCode());
        System.out.println(booking.getStudent().getStudentNumber());

    }
}