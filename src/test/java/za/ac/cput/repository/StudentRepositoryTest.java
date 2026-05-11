package za.ac.cput.repository;
import org.junit.jupiter.api.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Student;
import za.ac.cput.factory.BookingFactory;
import za.ac.cput.factory.StudentFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
StudentRepositoryTest.java
Student repository testing
Author: Sabelo Ceza - 220094489
Date: 23/03/2026
 */

//AlphanumericOrder
@TestMethodOrder(MethodOrderer.MethodName.class)
class StudentRepositoryTest {
    List<Booking> bookings = new ArrayList<>();
    private static IStudentRepository repository = StudentRepository.getRepository();        //Enforcing singleton

    Student student = StudentFactory.createStudent("220094489","John",
            "Doe","220094489@mycput.ac.za","073 985 1110",
            "JohnDoe2026","Third year", bookings);

    @Test
    void a_create() {
        Student created = repository.create(student);
        assertNotNull(created);
        System.out.println(created.toString());
    }

    @Test
    void b_read() {
        Student read = repository.read(student.getStudentNumber());
        assertNotNull(read);
        System.out.println(read.toString());
    }
    @Test
    void c_update() {
        Student newStudent = new Student.Builder()
                .copy(student)
                .setPhoneNumber("082 889 2236")         //updating the student's phone number
                .setPassword("Help12546@@")             //updating the password also
                .build();

        Student updated = repository.update(newStudent);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled           //I will not use it as yet
    void d_delete() {
        assertTrue(repository.delete(student.getStudentNumber()));      //we delete using a primary key
        System.out.println("Student has been successfully deleted");
    }

    @Test
    void e_getAll() {
        System.out.println(repository.getAll());
    }
}
