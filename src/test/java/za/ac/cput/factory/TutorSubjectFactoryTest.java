package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Subject;
import za.ac.cput.domain.Tutor;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.TutorSubject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
TutorSubjectFactoryTest.java
TutorSubject Factory Test
Author: Sabelo Ceza - 220094489
Date: 01/05/2026
 */

class TutorSubjectFactoryTest {

    private TutorSubject tutorSubject;

    @BeforeEach
    void setUp() {


        Subject subject = SubjectFactory.createSubject(
                "ADP362S",
                "Application Development Practice",
                "Programming Module",
                "3rd Year"
        );
        assertNotNull(subject);

        List<Booking> bookings = new ArrayList<>();
        Tutor tutor = TutorFactory.createTutor(
                "T001",
                "Imaan",
                "Achmat",
                "imaan@gmail.com",
                "0211377053",
                "password",
                150.0, bookings);

        assertNotNull(tutor);

        tutorSubject = TutorSubjectFactory.createTutorSubject(
                "ADP362S",
                "T001",
                5,
                subject,
                tutor
        );
    }

    @Test
    void createTutorSubject() {

        assertNotNull(tutorSubject);
        System.out.println(tutorSubject);

    }
}