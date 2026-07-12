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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class BookingControllerTest {

    private static Booking booking;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/tutoring/booking";

    @BeforeAll
    public static void setUp(){
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
        String url = BASE_URL + "/create";
        Booking createdBooking = this.restTemplate.postForObject(url, booking, Booking.class);
        assertNotNull(createdBooking);
        assertEquals(booking.getBookingId(), createdBooking.getBookingId());
        booking = createdBooking;
        System.out.println("Created Booking: " + createdBooking);
    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + booking.getBookingId();
        ResponseEntity<Booking> response = this.restTemplate.getForEntity(url, Booking.class);
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
        this.restTemplate.put(url, updatedBooking);

        ResponseEntity<Booking> response = restTemplate.getForEntity(BASE_URL + "/read/" + updatedBooking.getBookingId(), Booking.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        System.out.println("Updated Booking" + response.getBody());


    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + booking.getBookingId();
        this.restTemplate.delete(url);

        ResponseEntity<Booking> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + booking.getBookingId(), Booking.class);
        assertNull(response.getBody());
        System.out.println("Deleted Booking: " + "true");

    }

    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";
        ResponseEntity<Booking[]> response = this.restTemplate.getForEntity(url, Booking[].class);
        assertNotNull(response.getBody());
        System.out.println("Get All Booking: ");
        for (Booking booking : response.getBody()){
            System.out.println(booking);
        }
    }

}