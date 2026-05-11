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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingFactoryTest {

    private static Booking booking;

    @BeforeEach
    void setUp() {
        booking = BookingFactory.createBooking("B12345", "222056401",
                "ADP362S", "Online", "2 hours", "T987654",
                LocalDateTime.of(2026, 5, 20 , 10, 30));
    }

    @Test
    void createBooking() {
        assertNotNull(booking);
        System.out.println(booking);

    }
}