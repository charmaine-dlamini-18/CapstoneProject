package za.ac.cput.service;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Admin;
import za.ac.cput.factory.AdminFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
AdminServiceTest.java
AdminServiceTest
Author: Thimna Booi - 230232108
Date: 25/06/2026
 */


@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class AdminServiceTest {

    @Autowired
    private AdminService service;

    private static final Admin admin =
            AdminFactory.createAdmin("A02", "SystemAdmin");

    @Test
    void a_create() {
        Admin created = service.create(admin);
        assertNotNull(created);
        System.out.println(created);
    }

    @Test
    void b_read() {
        Admin read = service.read(admin.getAdminId());
        assertNotNull(read);
        System.out.println(read);
    }

    @Test
    void c_update() {
        Admin updatedAdmin = new Admin.Builder()
                .copy(admin)
                .setRole("DatabaseAdmin")
                .build();

        Admin updated = service.update(updatedAdmin);
        assertNotNull(updated);
        System.out.println(updated);
    }

    @Test
    @Disabled
    void d_delete() {
        assertTrue(service.delete(admin.getAdminId()));
    }

    @Test
    void e_getAll() {
        List<Admin> admins = service.getAll();
        assertNotNull(admins);
        System.out.println(admins);
    }
}