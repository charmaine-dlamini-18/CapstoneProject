/*
Tutor Service Test
Author: Imaan Achmat - 230458971
Date: 26/06/2026
 */

package za.ac.cput.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Tutor;
import za.ac.cput.factory.TutorFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class TutorServiceTest {

    @Autowired
    private TutorService tutorService;

    private Tutor tutor = TutorFactory.createTutor(
            "T001",
            "Imaan",
            "Achmat",
            "imaan@gmail.com",
            "0721234567",
            "Password123",
            150.00,
            List.of()
    );

    @Test
    void a_create() {
        Tutor created = tutorService.create(tutor);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        Tutor read = tutorService.read(tutor.getTutorId());
        assertNotNull(read);
        System.out.println(read);
    }

    @Test
    void c_update() {

        Tutor newTutor = new Tutor.Builder()
                .copy(tutor)
                .setPhoneNumber("0712345678")
                .setHourlyRate(200.00)
                .build();
        Tutor updated = tutorService.update(newTutor);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    void d_getAll() {
        System.out.println(tutorService.getAll());
    }

    @Test
    @Disabled
    void e_delete() {
        tutorService.delete(tutor.getTutorId());
        Tutor deleted = tutorService.read(tutor.getTutorId());
        assertNotNull(deleted);
        System.out.println("Tutor deleted successfully");
    }
}
