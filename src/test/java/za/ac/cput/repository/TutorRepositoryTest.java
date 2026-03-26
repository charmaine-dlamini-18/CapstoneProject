package za.ac.cput.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Tutor;
import za.ac.cput.factory.TutorFactory;
import static org.junit.jupiter.api.Assertions.*;

/*
TutorRepositoryTest.java
Tutor repository testing
Author: Imaan Achmat
230458971
Date: 25 March 2026
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TutorRepositoryTest {

    private ITutorRepository repository = TutorRepository.getRepository();

    private Tutor tutor = TutorFactory.createTutor("T001", "Imaan", "Achmat",
            "imaan@gmail.com", "0211377053",
            "password", 150.0);

    @Test
    void a_create() {
        Tutor created = repository.create(tutor);
        assertNotNull(created);
        System.out.println("Created: " +created);
    }
    @Test
    void b_read() {
        Tutor read = repository.read(tutor.getTutorId());
        assertNotNull(read);
        System.out.println("Read: " +read);
    }
    @Test
    void c_update() {
        Tutor updatedRecord = new Tutor.Builder().copy(tutor).setPhoneNumber("0672414363").setPassword("NewPassword").build();

        Tutor updated = repository.update(updatedRecord);
        assertNotNull(updated);
        System.out.println("Updated: " +updated);
    }
    @Test
    @Disabled
    void d_delete() {
        boolean success = repository.delete(tutor.getTutorId());
        assertTrue(success);
        System.out.println("Tutor Deleted Successfully");
    }
    @Test
    void e_getAll() {
        System.out.println("All tutors: " + repository.getAll());
    }
}
