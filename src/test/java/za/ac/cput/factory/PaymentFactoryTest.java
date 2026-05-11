package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/*
PaymentFactoryTest.java
Payment factory test
Author: Safiya Elmi
(240500598)
Date: 25/03/2026
*/

class PaymentFactoryTest {

    @Test
    void createPayment() {

        Booking booking = new Booking.Builder()
                .setBookingId("BOOK001")
                .build();

        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                1500.00,
                LocalDateTime.now(),
                "Card",
                "Completed",
                booking
        );

        assertNotNull(payment);
        System.out.println(payment);
    }
}
