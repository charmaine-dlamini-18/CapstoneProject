package za.ac.cput.factory;

import org.junit.jupiter.api.*;
import za.ac.cput.domain.Student;
import static org.junit.jupiter.api.Assertions.*;

/*
StudentFactoryTest.java
Student factory test
Author: Sabelo Ceza - 220094489
Date: 23/03/2026
 */

class StudentFactoryTest {

    @Test
    void createStudent() {
        Student student = StudentFactory.createStudent("220094489","John",
                "Doe","220094489@mycput.ac.za","073 985 1110",
                "JohnDoe2026","Third year");
        assertNotNull(student);
        System.out.println(student);
    }
}