package za.ac.cput.service;
/*
Subject Service Test
Author:Wendy Bayise
222828978
Date: 27/06/2026
 */
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Subject;
import za.ac.cput.factory.SubjectFactory;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class SubjectServiceTest {

    @Autowired
    private SubjectService subjectService;

    private Subject subject = SubjectFactory.createSubject(
            "ADP362S",
            "Application Development 3",
            "Intro to ADP",
            "3rd Year"

    );

    @Test
    void a_create() {
        Subject created = subjectService.create(subject);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        Subject read = subjectService.read(subject.getSubjectCode());
        assertNotNull(read);
        System.out.println(read);
    }

    @Test
    void c_update() {

        Subject newSubject = new Subject.Builder()
                .copy(subject)
                .setSubjectCode("ITS")
                .setSubjectDescription("Information Sysytems")
                .build();
        Subject updated = subjectService.update(newSubject);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    void d_getAll() {
        System.out.println(subjectService.getAll());
    }

    @Test
    @Disabled
    void e_delete() {
        subjectService.delete(subject.getSubjectCode());
        Subject deleted = subjectService.read(subject.getSubjectCode());
        assertNotNull(deleted);
        System.out.println("Subject deleted successfully");
    }
}

