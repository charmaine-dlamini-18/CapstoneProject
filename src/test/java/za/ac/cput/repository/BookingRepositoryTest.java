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

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.MethodName.class)
class BookingRepositoryTest {
    private static IBookingRepository repository = BookingRepository.getRepository();

    private Payment payment = PaymentFactory.createPayment("reference123", 1000.00,
            "23/03/2026", "Card", "pending");

    private Booking booking = BookingFactory.createBooking("12345", "Session1",
            "12:00", "Online", "2 hours", "222056401",
            "ADP362S", "T987654", payment);

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
        Booking newBooking = new Booking.Builder().copy(booking).setSessionTime("13:00")
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