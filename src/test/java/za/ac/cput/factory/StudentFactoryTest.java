package za.ac.cput.factory;
import org.junit.jupiter.api.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


/*
StudentFactoryTest.java
Student factory test
Author: Sabelo Ceza - 220094489
Date: 23/03/2026
 */

class StudentFactoryTest {

    @Test
    void createStudent() {

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
        List<Booking> bookings = new ArrayList<>();

        Tutor tutor = TutorFactory.createTutor("T001", "Imaan", "Achmat",
                "imaan@gmail.com", "0211377053",
                "password", 150.0, bookings);

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
                "ADP362S",
                "Online",
                "2 hours",
                LocalDateTime.of(2026, 5, 20, 10, 30),
                student, tutor,
                payment
        );

        student.getBookings().add(booking);
        assertNotNull(student);
        System.out.println(student);
    }

    }
