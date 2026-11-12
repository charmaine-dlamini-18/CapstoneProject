package za.ac.cput.controller;
/*
BookingControllerTest.java
Booking Controller Test
Author: Charmaine Dlamini - 222056401
Date: 26/06/2026
 */
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.*;
import za.ac.cput.factory.*;
import za.ac.cput.service.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingControllerTest {

    private static Booking booking;

    @Autowired
    private TestRestTemplate restTemplate;

    // Sends the admin login with every request, so Spring Security lets the test in
    private TestRestTemplate adminTemplate() {
        return restTemplate.withBasicAuth("admin@tutoring.co.za", "Password123");
    }
    @Autowired
    private StudentService studentService;
    @Autowired
    private TutorService tutorService;
    @Autowired
    private BookingService bookingService;
    private static final String BASE_URL = "/booking";

    @BeforeAll
    public void setUp(){
        Student student = studentService.create(StudentFactory.createStudent(
                "220094489",
                "Sabelo",
                "Ceza",
                "220094489@mycput.ac.za",
                "073 985 1110",
                "SabieCeza2026",
                "Third year",
                new ArrayList<>()
        ));

        List<Booking> bookings = new ArrayList<>();

        Tutor tutor = tutorService.create(TutorFactory.createTutor("T001", "Imaan", "Achmat",
                "imaan@gmail.com", "0211377053",
                "password", 150.0, bookings));

        booking = BookingFactory.createBooking(
                "B12345",
                "ADP362S",
                "Online",
                "2 hours",
                LocalDateTime.of(2026, 5, 20, 10, 30),
                student,
                tutor,
                null
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
        String url = BASE_URL + "/create?studentNumber=220094489&tutorId=T001";
        Booking createdBooking = adminTemplate().postForObject(url, booking, Booking.class);
        assertNotNull(createdBooking);
        assertEquals(booking.getBookingId(), createdBooking.getBookingId());
        booking = createdBooking;
        System.out.println("Created Booking: " + createdBooking);
    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + booking.getBookingId();
        ResponseEntity<Booking> response = adminTemplate().getForEntity(url, Booking.class);
        assertNotNull(response.getBody());
        assertEquals(booking.getBookingId(), response.getBody().getBookingId());
        System.out.println("Read Booking: " + response.getBody());

    }

    @Test
    void c_update() {
        Booking updatedBooking = new Booking.Builder()
                .copy(booking)
                .setDuration("4 hours")
                .build();

        String url = BASE_URL + "/update";
        adminTemplate().put(url, updatedBooking);

        ResponseEntity<Booking> response = adminTemplate().getForEntity(BASE_URL + "/read/" + updatedBooking.getBookingId(), Booking.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        System.out.println("Updated Booking" + response.getBody());


    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + booking.getBookingId();
        adminTemplate().delete(url);

        ResponseEntity<Booking> response = adminTemplate().getForEntity(BASE_URL + "/read/" + booking.getBookingId(), Booking.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("Deleted Booking: " + "true");

    }

    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";
        ResponseEntity<Booking[]> response = adminTemplate().getForEntity(url, Booking[].class);
        assertNotNull(response.getBody());
        System.out.println("Get All Booking: ");
        for (Booking booking : response.getBody()){
            System.out.println(booking);
        }
    }

}