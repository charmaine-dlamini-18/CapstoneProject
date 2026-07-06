package za.ac.cput.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;
import za.ac.cput.factory.BookingFactory;
import za.ac.cput.factory.PaymentFactory;
import za.ac.cput.factory.StudentFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class StudentControllerTest {

    private static Student student;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/tutoring/student";

    @BeforeAll
    public static void setUp() {
     student = StudentFactory.createStudent(
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
        Booking booking = BookingFactory.createBooking(
                "B12345",
                "ADP362S",
                "Online",
                "2 hours",
                "T987654",
                LocalDateTime.of(2026, 5, 20, 10, 30), student, payment
        );
    }
    @Test
    void a_create() {
        String url = BASE_URL + "/create";
        Student createdStudent = this.restTemplate.postForObject(url, student, Student.class);
        assertNotNull(createdStudent);
        assertEquals(student.getStudentNumber(), createdStudent.getStudentNumber());
        student = createdStudent;
        System.out.println("Created: " + createdStudent);
    }

    @Test
    void b_read() {
        String url = BASE_URL +"/read/" +student.getStudentNumber();
        ResponseEntity <Student> response = this.restTemplate.getForEntity(url, Student.class);       //2 parameteres, where you will gwt it and the return type
        assertNotNull(response.getBody());
        assertEquals(student.getStudentNumber(), response.getBody().getStudentNumber());
        System.out.println("Read Student" +response.getBody());
    }

    @Test
    void c_update() {
        Student updatedStudent = new Student.Builder().copy(student)
                .setPhoneNumber("073 982 2003")
                .build();
        String url = BASE_URL + "/update";
        this.restTemplate.put(url, updatedStudent);
        ResponseEntity<Student> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + updatedStudent.getStudentNumber(), Student.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        System.out.println("Updated: " + response.getBody());
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + student.getStudentNumber();
        this.restTemplate.delete(url);

        //Verify that the student was deleted
        ResponseEntity <Student> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + student.getStudentNumber(), Student.class);
        assertNull(response.getBody());
        System.out.println("Student deleted:" + "true");

    }

    @Test
    void d_getAll() {
        String url = BASE_URL +"/getall";
        ResponseEntity <Student[]>  response = this.restTemplate.getForEntity(url, Student[].class);
        assertNotNull(response.getBody());
        System.out.println("Get All: " );
        for(Student student : response.getBody()){
            System.out.println(student);
        }
    }
}