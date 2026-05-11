package za.ac.cput.repository;

import org.junit.jupiter.api.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.PaymentFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/*
PaymentRepositoryTest.java
Payment repository testing
Author: Safiya Elmi
(240500598)
Date: 25/03/2026
*/

@TestMethodOrder(MethodOrderer.MethodName.class)
class PaymentRepositoryTest {

    private static IPaymentRepository repository = PaymentRepository.getRepository();

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

    @Test
    void a_create() {
        Payment created = repository.create(payment);
        assertNotNull(created);
        System.out.println(created.toString());
    }

    @Test
    void b_read() {
        Payment read = repository.read(payment.getPaymentRef());
        assertNotNull(read);
        System.out.println(read.toString());
    }

    @Test
    void c_update() {
        Payment newRecord = new Payment.Builder()
                .copy(payment)
                .setAmount(2000.00)
                .setStatus("Pending")
                .build();

        Payment updated = repository.update(newRecord);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void d_delete() {
        assertTrue(repository.delete(payment.getPaymentRef()));
        System.out.println("Payment has been successfully deleted");
    }

    @Test
    void e_getAll() {
        System.out.println(repository.getAll());
    }
}    }

    @Test
    void b_read() {
        Payment read = repository.read(payment.getPaymentRef());
        assertNotNull(read);
        System.out.println(read.toString());
    }

    @Test
    void c_update() {
        Payment newRecord = new Payment.Builder()
                .copy(payment)
                .setAmount(2000.00)
                .setStatus("Pending")
                .build();

        Payment updated = repository.update(newRecord);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void d_delete() {
        assertTrue(repository.delete(payment.getPaymentRef()));
        System.out.println("Payment has been successfully deleted");
    }

    @Test
    void e_getAll() {
        System.out.println(repository.getAll());
    }
}
