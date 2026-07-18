package za.ac.cput.controller;

/*
TutorSubjectControllerTest.java
TutorSubject Controller Test
Author: Charmaine Dlamini - 222056401
Date: 28/06/2026
*/

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.TutorSubject;
import za.ac.cput.domain.Tutor;
import za.ac.cput.domain.Subject;
import za.ac.cput.factory.SubjectFactory;
import za.ac.cput.factory.TutorFactory;
import za.ac.cput.factory.TutorSubjectFactory;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class TutorSubjectControllerTest {

    private static TutorSubject tutorSubject;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/tutoring/tutorsubject";

    @BeforeAll
    public static void setUp() {

        Subject subject = SubjectFactory.createSubject(
                "ADP362S",
                "Application Development Practice",
                "Programming Module",
                "3rd Year"
        );

        Tutor tutor = TutorFactory.createTutor(
                "T987654",
                "Imaan",
                "Achmat",
                "imaan@gmail.com",
                "0211377053",
                "password",
                150.0,
                new ArrayList<>()
        );

        tutorSubject = TutorSubjectFactory.createTutorSubject(
                "ADP362S",
                "T987654",
                5,
                subject,
                tutor
        );
    }

    @Test
    void a_create() {
        String url = BASE_URL + "/create";
        TutorSubject created = this.restTemplate.postForObject(url, tutorSubject, TutorSubject.class);
        assertNotNull(created);
        assertEquals(tutorSubject.getSubjectCode(), created.getSubjectCode());
        assertEquals(tutorSubject.getTutorId(), created.getTutorId());
        tutorSubject = created;
        System.out.println("Created TutorSubject: " + created);
    }

    @Test
    void b_read() {

        String url = BASE_URL + "/read/" + tutorSubject.getSubjectCode() + "/" + tutorSubject.getTutorId();
        ResponseEntity<TutorSubject> response = this.restTemplate.getForEntity(url, TutorSubject.class);
        assertNotNull(response.getBody());
        assertEquals(tutorSubject.getSubjectCode(), response.getBody().getSubjectCode());
        assertEquals(tutorSubject.getTutorId(), response.getBody().getTutorId());
        System.out.println("Read TutorSubject: " + response.getBody());
    }

    @Test
    void c_update() {
        TutorSubject updated = new TutorSubject.Builder()
                .copy(tutorSubject)
                .setYearsTaught(10)
                .build();

        String url = BASE_URL + "/update";
        restTemplate.put(url, updated);

        ResponseEntity<TutorSubject> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + updated.getSubjectCode() + "/" + updated.getTutorId(), TutorSubject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getYearsTaught());
        System.out.println("Updated TutorSubject: " + response.getBody());
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + tutorSubject.getSubjectCode() + "/" + tutorSubject.getTutorId();
        this.restTemplate.delete(url);

        ResponseEntity<TutorSubject> response = this.restTemplate.getForEntity(BASE_URL + "/read/" + tutorSubject.getSubjectCode() + "/" + tutorSubject.getTutorId(), TutorSubject.class);
        assertNull(response.getBody());
        System.out.println("Deleted TutorSubject: true");
    }

    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";
        ResponseEntity<TutorSubject[]> response = this.restTemplate.getForEntity(url, TutorSubject[].class);
        assertNotNull(response.getBody());
        System.out.println("Get All TutorSubject: ");
        for (TutorSubject ts : response.getBody()) {
            System.out.println(ts);
        }
    }
}
