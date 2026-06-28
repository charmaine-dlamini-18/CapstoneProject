package za.ac.cput.service;
/*
BookingServiceTest.java
Booking Service Test
Author: Charmaine Dlamini - 222056401
Date: 26/06/2026
 */

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;
import za.ac.cput.factory.BookingFactory;
import za.ac.cput.factory.PaymentFactory;
import za.ac.cput.factory.StudentFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class BookingServiceTest {
    @Autowired
    private BookingService service;
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

    Booking booking = BookingFactory.createBooking(
            "B12345",
            "222056401",
            "ADP362S",
            "Online",
            "2 hours",
            "T987654",
            LocalDateTime.of(2026, 5, 20, 10, 30),
            student,
            payment
        );

    @Test
    void a_create() {
        Booking created = service.create(booking);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        Booking read = service.read(booking.getBookingId());
        assertNotNull(read);
        System.out.println(read);
    }

    @Test
    void c_update() {
        Booking newBooking = new Booking.Builder()
                .copy(booking)
                .setSessionType("Online")
                .setDuration("3 hours")
                .build();
        Booking updated = service.update(newBooking);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void e_delete() {
        boolean deleted = service.delete(booking.getBookingId());
        assertTrue(deleted);
        System.out.println(deleted);
        System.out.println("Deleted apartment ID: " + booking.getBookingId());

    }

    @Test
    void d_getAll() {
        System.out.println(service.getAll());
    }

}