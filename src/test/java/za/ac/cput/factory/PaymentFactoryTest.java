package za.ac.cput.factory;

import org.junit.jupiter.api.*;
import za.ac.cput.domain.Payment;

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
        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                1500.00,
                "2026-03-25",
                "Card",
                "Completed"
        );

        assertNotNull(payment);
        System.out.println(payment);
    }
}
