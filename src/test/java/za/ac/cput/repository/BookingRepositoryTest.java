package za.ac.cput.repository;
/*
BookingRepositoryTest.java
Booking Repository Test
Author: Charmaine Dlamini-222056401
Date: 23/03/2026
 */
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.BookingFactory;
import za.ac.cput.factory.PaymentFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.MethodName.class)
class BookingRepositoryTest {
    private static IBookingRepository repository = BookingRepository.getRepository();

   Booking booking = BookingFactory.createBooking("B12345", "222056401",
            "ADP362S", "Online", "2 hours", "T987654",
            LocalDateTime.of(2026, 6, 14, 5, 30));

    @Test
    void a_create() {
        Booking created = repository.create(booking);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        Booking read = repository.read(booking.getBookingId());
        assertNotNull(read);
        System.out.println(read);
    }

    @Test
    void c_update() {
        Booking newBooking = new Booking.Builder().copy(booking)
                .setDuration("4 hours")
                .build();
        Booking updated = repository.update(newBooking);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void d_delete() {
        assertTrue(repository.delete(booking.getBookingId()));
        System.out.println("Success: booking deleted");
    }

    @Test
    void e_getAll() {
        System.out.println(repository.getAll());
    }
}