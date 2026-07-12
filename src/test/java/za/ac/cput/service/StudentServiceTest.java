package za.ac.cput.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
StudentServiceTest.java
Student Service Test
Author: Sabelo Ceza - 220094489
Date: 25/06/2026
 */


@SpringBootTest         //for testing
@TestMethodOrder(MethodOrderer.MethodName.class)    //Alphanumeric (runs tests in alphabetical order)
class StudentServiceTest {
    @Autowired
    private StudentService studentService;          //Student service object

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
    //stores booking in a list
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
    private Booking booking = BookingFactory.createBooking(
            "B12345",
            "ADP362S",
            "Online",
            "2 hours",
            LocalDateTime.of(2026, 5, 20, 10, 30),student, tutor, payment
    );



    @Test
    void a_create() {
        Student created = studentService.create(student);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        Student read = studentService.read(student.getStudentNumber());
        assertNotNull(read);
        System.out.println(read);
    }

    @Test
    void c_update() {
        //using the copy method to make updates
        Student newStudent = new Student.Builder().copy(student)
                .setPhoneNumber("073 982 2222")
                .setPassword("Password123")
                .build();
        Student updated = studentService.update(newStudent);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void e_delete() {
        studentService.delete(student.getStudentNumber());
        Student deleted = studentService.read(student.getStudentNumber());
        assertNotNull(deleted);
        System.out.println("Student Deleted successfully");
    }

    @Test
    void d_getAll() {
        System.out.println(studentService.getAll());

    }
}