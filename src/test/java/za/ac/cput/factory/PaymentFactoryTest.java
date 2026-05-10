package za.ac.cput.factory;

import org.junit.jupiter.api.*;
import za.ac.cput.domain.Payment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/*
PaymentFactoryTest.java
Payment factory test
Author: Safiya Elmi
(240500598)
Date: 11/05/2026
*/

class PaymentFactoryTest {

    @Test
    void createPayment() {
        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                1500.00,
                LocalDateTime.now(),
                "Card",
                "Completed"
        );

        assertNotNull(payment);
        System.out.println(payment);
    }
}
