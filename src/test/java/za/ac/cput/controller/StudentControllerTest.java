package za.ac.cput.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class StudentControllerTest {

    private static Student student;

    @Autowired
    private TestRestTemplate restTemplate;

    // Sends the admin login with every request, so Spring Security lets the test in
    private TestRestTemplate adminTemplate() {
        return restTemplate.withBasicAuth("admin@tutoring.co.za", "Password123");
    }
    private static final String BASE_URL = "/student";

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
        List<Booking> bookings = new ArrayList<>();

        Tutor tutor = TutorFactory.createTutor("T001", "Imaan", "Achmat",
                "imaan@gmail.com", "0211377053",
                "password", 150.0, bookings);


        Booking booking = BookingFactory.createBooking(
                "B12345",
                "ADP362S",
                "Online",
                "2 hours",
                LocalDateTime.of(2026, 5, 20, 10, 30), student, tutor, null
        );

        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                1500.00,
                LocalDateTime.now(),
                "Card",
                "Completed",
                booking
        );
    }
    @Test
    void a_create() {
        String url = BASE_URL + "/create";
        // The password is write-only in JSON, so the test sends it in a plain map
        Map<String, Object> body = new HashMap<>();
        body.put("studentNumber", student.getStudentNumber());
        body.put("firstName", student.getFirstName());
        body.put("lastName", student.getLastName());
        body.put("email", student.getEmail());
        body.put("phoneNumber", student.getPhoneNumber());
        body.put("password", "SabieCeza2026");
        body.put("levelOfStudy", student.getLevelOfStudy());
        body.put("bookings", new ArrayList<>());
        // The create call now refuses an ID that already exists, so start from a clean slate
        adminTemplate().delete(BASE_URL + "/delete/" + student.getStudentNumber());
        Student createdStudent = adminTemplate().postForObject(url, body, Student.class);
        assertNotNull(createdStudent);
        assertEquals(student.getStudentNumber(), createdStudent.getStudentNumber());
        student = createdStudent;
        System.out.println("Created: " + createdStudent);
    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + student.getStudentNumber();
        ResponseEntity<Student> createdStud =
                adminTemplate().getForEntity(url, Student.class);
        assertNotNull(createdStud);
        System.out.println(createdStud.getStatusCode());
        System.out.println(createdStud.getBody());
    }

    @Test
    void c_update() {
        Student updatedStudent = new Student.Builder().copy(student)
                .setPhoneNumber("073 982 2003")
                .build();
        String url = BASE_URL + "/update";
        adminTemplate().put(url, updatedStudent);
        ResponseEntity<Student> response = adminTemplate().getForEntity(BASE_URL + "/read/" + updatedStudent.getStudentNumber(), Student.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        System.out.println("Updated: " + response.getBody());
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + student.getStudentNumber();
        adminTemplate().delete(url);

        //Verify that the student was deleted
        ResponseEntity <Student> response = adminTemplate().getForEntity(BASE_URL + "/read/" + student.getStudentNumber(), Student.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("Student deleted:" + "true");

    }

    @Test
    void d_getAll() {
        String url = BASE_URL +"/getAll";
        ResponseEntity <Student[]>  response = adminTemplate().getForEntity(url, Student[].class);
        assertNotNull(response.getBody());
        System.out.println("Get All: " );
        for(Student student : response.getBody()){
            System.out.println(student);
        }
    }
}