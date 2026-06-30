/*
Tutor Controller Test
Author Imaan Achmat - 230458971
Date: 28/06/2026
 */

package za.ac.cput.controller;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Tutor;
import za.ac.cput.factory.TutorFactory;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)

class TutorControllerTest {

    private static Tutor tutor;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080/Capstone/tutor";

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
        Tutor createdTutor = this.restTemplate.postForObject(url, tutor, Tutor.class);
        assertNotNull(createdTutor);
        assertEquals(tutor.getTutorId(), createdTutor.getTutorId());
        tutor = createdTutor;
                System.out.println("Created Tutor: "+ createdTutor);

    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + tutor.getTutorId();
        ResponseEntity<Tutor> response = this.restTemplate.getForEntity(url, Tutor.class);
        assertNotNull(response.getBody());
        assertEquals(tutor.getTutorId(), response.getBody().getTutorId());
        System.out.println("Read Tutor: "+ response.getBody());
    }

    @Test
    void c_update() {
        Tutor updatedTutor = new Tutor.Builder().copy(tutor).setHourlyRate(200.00).setPhoneNumber("073 987 6543").build();
        String url = BASE_URL + "/update";
        this.restTemplate.put(url, updatedTutor);

        ResponseEntity<Tutor> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + updatedTutor.getTutorId(), Tutor.class);
        assertNotNull(response.getBody());
        System.out.println("Updated Tutor: "+ response.getBody());
    }
    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";
        ResponseEntity<Tutor[]> response = this.restTemplate.getForEntity(url, Tutor[].class);
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
        this.restTemplate.delete(url);
        ResponseEntity<Tutor> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + tutor.getTutorId(), Tutor.class);
        assertNull(response.getBody());
        System.out.println("Deleted Tutor : true ");
    }
}