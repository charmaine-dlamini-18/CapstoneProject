package za.ac.cput.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.PaymentFactory;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class PaymentControllerTest {

    private static Payment payment;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/tutoring/payment";

    @BeforeAll
    static void setUp() {

        Booking booking = new Booking.Builder()
                .setBookingId("BOOK001")
                .build();

        payment = PaymentFactory.createPayment(
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
        Payment createdPayment = restTemplate.postForObject(url, payment, Payment.class);
        assertNotNull(createdPayment);
        assertEquals(payment.getPaymentRef(), createdPayment.getPaymentRef());
        payment = createdPayment;
        System.out.println("Created Payment: " + createdPayment);
    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + payment.getPaymentRef();
        ResponseEntity<Payment> response = this.restTemplate.getForEntity(url, Payment.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(payment.getPaymentRef(), response.getBody().getPaymentRef());
        System.out.println("Read Payment: " + response.getBody());
    }

    @Test
    void c_update() {
        Payment updatedPayment = new Payment.Builder()
                .copy(payment)
                .setStatus("Paid")
                .build();
        String url = BASE_URL + "/update";
        this.restTemplate.put(url, updatedPayment);

        ResponseEntity<Payment> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + updatedPayment.getPaymentRef(), Payment.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Paid", response.getBody().getStatus());
        System.out.println("Updated Payment: " + response.getBody());
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + payment.getPaymentRef();
        this.restTemplate.delete(url);
        ResponseEntity<Payment> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + payment.getPaymentRef(), Payment.class);
        assertNull(response.getBody());
        System.out.println("Deleted Payment: " + "true");
    }

    @Test
    void d_getAll() {
        String url = BASE_URL + "/getall";
        ResponseEntity<Payment[]> response = this.restTemplate.getForEntity(url, Payment[].class);
        assertNotNull(response.getBody());
        System.out.println("Get All Payments:");
        for (Payment payment : response.getBody()) {
            System.out.println(payment);
        }
    }
}
