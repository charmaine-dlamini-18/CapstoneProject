package za.ac.cput.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.factory.BookingFactory;
import za.ac.cput.factory.PaymentFactory;
import za.ac.cput.factory.StudentFactory;
import za.ac.cput.factory.TutorFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
PaymentServiceTest.java
Payment Service Test
Author: Safiya Elmi
(240500598)
Date: 28/06/2026
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService service;

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

    List<Booking> bookings = new ArrayList<>();

    Tutor tutor = TutorFactory.createTutor("T001", "Imaan", "Achmat",
            "imaan@gmail.com", "0211377053",
            "password", 150.0, bookings);

        Booking booking = BookingFactory.createBooking(
                "B12345",
                "ADP362S",
                "Online",
                "2 hours",
                LocalDateTime.of(2026, 5, 20, 10, 30),
                student, tutor, payment
        );

    @Test
    void a_create() {
        Payment created = service.create(payment);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        Payment read = service.read(payment.getPaymentRef());
        assertNotNull(read);
        System.out.println(read);
    }

    @Test
    void c_update() {
        Payment updatedPayment = new Payment.Builder()
                .copy(payment)
                .setStatus("Pending")
                .build();

        Payment updated = service.update(updatedPayment);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void e_delete() {
        service.delete(payment.getPaymentRef());
        Payment  deleted = service.read(payment.getPaymentRef());
        assertNotNull(deleted);
        System.out.println("Payment deleted");

    }
    @Test
    void e_getAll() {
        System.out.println(service.getAll());
    }
}