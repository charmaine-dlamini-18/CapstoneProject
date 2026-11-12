/*
Tutor Controller Test
Author Imaan Achmat - 230458971
Date: 28/06/2026
 */

package za.ac.cput.controller;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Tutor;
import za.ac.cput.factory.TutorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)

class TutorControllerTest {

    private static Tutor tutor;

    @Autowired
    private TestRestTemplate restTemplate;

    // Sends the admin login with every request, so Spring Security lets the test in
    private TestRestTemplate adminTemplate() {
        return restTemplate.withBasicAuth("admin@tutoring.co.za", "Password123");
    }

    private static final String BASE_URL = "/tutor";

    @BeforeAll
    public static void setUp() {
        tutor = TutorFactory.createTutor(
                "T987654",
                "Imaan",
                "Achmat",
                "imaan@mycput.ac.za",
                "073 123 4567",
                "Imaan2026",
                150.00,
                new ArrayList<>()
        );
    }
    @Test
    void a_create() {
        String url = BASE_URL + "/create";
        // The password is write-only in JSON, so the test sends it in a plain map
        Map<String, Object> body = new HashMap<>();
        body.put("tutorId", tutor.getTutorId());
        body.put("firstName", tutor.getFirstName());
        body.put("lastName", tutor.getLastName());
        body.put("email", tutor.getEmail());
        body.put("phoneNumber", tutor.getPhoneNumber());
        body.put("password", "Imaan2026");
        body.put("hourlyRate", tutor.getHourlyRate());
        body.put("bookings", new ArrayList<>());
        // The create call now refuses an ID that already exists, so start from a clean slate
        adminTemplate().delete(BASE_URL + "/delete/" + tutor.getTutorId());
        Tutor createdTutor = adminTemplate().postForObject(url, body, Tutor.class);
        assertNotNull(createdTutor);
        assertEquals(tutor.getTutorId(), createdTutor.getTutorId());
        tutor = createdTutor;
        System.out.println("Created Tutor: "+ createdTutor);

    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + tutor.getTutorId();
        ResponseEntity<Tutor> response = adminTemplate().getForEntity(url, Tutor.class);
        assertNotNull(response.getBody());
        assertEquals(tutor.getTutorId(), response.getBody().getTutorId());
        System.out.println("Read Tutor: "+ response.getBody());
    }

    @Test
    void c_update() {
        Tutor updatedTutor = new Tutor.Builder().copy(tutor).setHourlyRate(200.00).setPhoneNumber("073 987 6543").build();
        String url = BASE_URL + "/update";
        adminTemplate().put(url, updatedTutor);

        ResponseEntity<Tutor> response = adminTemplate().getForEntity(BASE_URL + "/read/" + updatedTutor.getTutorId(), Tutor.class);
        assertNotNull(response.getBody());
        System.out.println("Updated Tutor: "+ response.getBody());
    }
    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";
        ResponseEntity<Tutor[]> response = adminTemplate().getForEntity(url, Tutor[].class);
        assertNotNull(response.getBody());
        System.out.println("Get all Tutors:");

        for (Tutor tutor : response.getBody()) {
            System.out.println(tutor);
        }
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + tutor.getTutorId();
        adminTemplate().delete(url);
        ResponseEntity<Tutor> response = adminTemplate().getForEntity(BASE_URL + "/read/" + tutor.getTutorId(), Tutor.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("Deleted Tutor : true ");
    }
}