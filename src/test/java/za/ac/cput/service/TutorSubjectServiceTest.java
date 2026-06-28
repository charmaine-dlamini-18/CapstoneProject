package za.ac.cput.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.*;
import za.ac.cput.factory.SubjectFactory;
import za.ac.cput.factory.TutorFactory;
import za.ac.cput.factory.TutorSubjectFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
StudentServiceTest.java
Student Service Test
Author: Sabelo Ceza - 220094489
Date: 25/06/2026
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class TutorSubjectServiceTest {

    @Autowired
    private TutorSubjectService service;
    private static final List<Subject> subjects = new ArrayList<>();
    private static final List<Tutor> tutors = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();

    private static final Tutor tutor = TutorFactory.createTutor(
            "T001",
            "Imaan",
            "Achmat",
            "imaan@gmail.com",
            "0211377053",
            "password",
            150.0,
            bookings
    );
    private static final Subject subject = SubjectFactory.createSubject(
            "ADP362S",
            "Application Development Practice",
            "Programming Module",
            "3rd Year"
    );

    private static final TutorSubject tutorSubject;

    static {
        subjects.add(subject);
        tutors.add(tutor);

        tutorSubject = TutorSubjectFactory.createTutorSubject(
                "ADP362S",
                "T001",
                5,
                subjects,
                tutors
        );
    }

    @Test
    void a_create() {
        TutorSubject created = service.create(tutorSubject);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        TutorSubjectId id = new TutorSubjectId(
                tutorSubject.getSubjectCode(),
                tutorSubject.getTutorId()
        );

        TutorSubject found = service.read(id);

        assertNotNull(found);
        assertEquals(tutorSubject.getSubjectCode(), found.getSubjectCode());
        assertEquals(tutorSubject.getTutorId(), found.getTutorId());
        System.out.println(found);
    }

    @Test
    void c_update() {
        TutorSubject newTutorSubject = new TutorSubject.Builder()
                .copy(tutorSubject)
                .setYearsTaught(10)
                .build();

        TutorSubject updated = service.update(newTutorSubject);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void e_delete() {
        TutorSubjectId id = new TutorSubjectId(
                tutorSubject.getSubjectCode(),
                tutorSubject.getTutorId()
        );
        boolean deleted = service.delete(id);
        assertTrue(deleted);
    }


    @Test
    void d_getAll() {
        System.out.println(service.getAll());
    }
}