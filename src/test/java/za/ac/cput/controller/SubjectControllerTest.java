package za.ac.cput.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Subject;
import za.ac.cput.factory.SubjectFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)

class SubjectControllerTest {

    private static Subject subject;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080/tutoring/subject";

    @BeforeAll
    public static void setUp() {
        subject = SubjectFactory.createSubject(
                "ADP3",
                "Application Development 3",
                "Intro to ADP",
                "3rd Year"
        );
    }

    @Test
    void a_create() {
        String url = BASE_URL + "/create";
        Subject createdSubject = this.restTemplate.postForObject(url, subject, Subject.class);
        assertNotNull(createdSubject);
        assertEquals(subject.getSubjectCode(), createdSubject.getSubjectCode());
        subject = createdSubject;
        System.out.println("Created Subject: " + createdSubject);
    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + subject.getSubjectCode();
        ResponseEntity<Subject> response = this.restTemplate.getForEntity(url, Subject.class);
        assertNotNull(response.getBody());
        assertEquals(subject.getSubjectCode(), response.getBody().getSubjectCode());
        System.out.println("Read Subject: " + response.getBody());
    }

    @Test
    void c_update() {
        Subject updatedSubject = new Subject.Builder().copy(subject)
                .setSubjectDescription("Information System")
                .build();
        String url = BASE_URL + "/update";
        this.restTemplate.put(url, updatedSubject);

        ResponseEntity<Subject> response = this.restTemplate.getForEntity(
                BASE_URL + "/read/" + updatedSubject.getSubjectCode(), Subject.class);
        assertNotNull(response.getBody());
        System.out.println("Updated Subject: " + response.getBody());
    }

    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";
        ResponseEntity<Subject[]> response = this.restTemplate.getForEntity(url, Subject[].class);
        assertNotNull(response.getBody());
        System.out.println("Get all Subjects:");
        for (Subject s : response.getBody()) {
            System.out.println(s);
        }
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + subject.getSubjectCode();
        this.restTemplate.delete(url);

        ResponseEntity<Subject> response = this.restTemplate.getForEntity(
                BASE_URL + "/read/" + subject.getSubjectCode(), Subject.class);
        assertNull(response.getBody());
        System.out.println("Deleted Subject: true");
    }
}
