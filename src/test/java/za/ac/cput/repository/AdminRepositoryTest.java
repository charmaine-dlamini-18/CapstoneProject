package za.ac.cput.repository;

import org.junit.jupiter.api.*;
import za.ac.cput.domain.Admin;
import za.ac.cput.factory.AdminFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
AdminRepositoryTest.java
Admin repository testing
Author: Thimna Booi - 230232108
Date: 25/03/2026
 */

@TestMethodOrder(MethodOrderer.MethodName.class)
public class AdminRepositoryTest {
    private static IAdminRepository repository = AdminRepository.getRepository();

    Admin admin = AdminFactory.createAdmin("A02", "SystemAdmin");
    @Test
    void a_create() {
        Admin created = repository.create(admin);
        assertNotNull(created);
        System.out.println(created.toString());
    }
    @Test
    void b_read() {
        Admin read = repository.read(admin.getId());
        assertNotNull(read);
        System.out.println(read.toString());
    }
    @Test
    void c_update() {
        Admin newRecord = new Admin.Builder()
                .copy(admin)
                .setRole("DatabaseAdmin")
                .build();
        Admin updated = repository.update(newRecord);
        assertNotNull(updated);
        System.out.println(updated);
    }
    @Test
    @Disabled
    void d_delete() {
        assertTrue(repository.delete(admin.getId()));
        System.out.println("Admin has been deleted successfully");
    }
    @Test
    void e_getAll(){
        List<Admin> admins = repository.getAll();
        assertNotNull(admins);
        System.out.println(admins);
    }
}
