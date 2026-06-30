package za.ac.cput.controller;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Admin;
import za.ac.cput.factory.AdminFactory;

import static org.junit.jupiter.api.Assertions.*;

/*
AdminControllerTest.java
AdminControllerTest
Author: Thimna Barbara Booi - 230232108
Date: 28/06/2026
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class AdminControllerTest {

    private static Admin admin;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080/Capstone/admin";

    @BeforeAll
    public static void setUp() {
        admin = AdminFactory.createAdmin(
                "A02",
                "SystemAdmin"
        );
    }

    @Test
    void a_create() {
        String url = BASE_URL + "/create";
        Admin createdAdmin = this.restTemplate.postForObject(url, admin, Admin.class);

        assertNotNull(createdAdmin);
        assertEquals(admin.getAdminId(), createdAdmin.getAdminId());

        admin = createdAdmin;
        System.out.println("Created Admin: " + createdAdmin);
    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + admin.getAdminId();

        ResponseEntity<Admin> response =
                this.restTemplate.getForEntity(url, Admin.class);

        assertNotNull(response.getBody());
        assertEquals(admin.getAdminId(), response.getBody().getAdminId());

        System.out.println("Read Admin: " + response.getBody());
    }

    @Test
    void c_update() {
        Admin updatedAdmin = new Admin.Builder()
                .copy(admin)
                .setRole("DatabaseAdmin")
                .build();

        String url = BASE_URL + "/update";
        this.restTemplate.put(url, updatedAdmin);

        ResponseEntity<Admin> response = restTemplate.getForEntity(
                BASE_URL + "/read/" + updatedAdmin.getAdminId(),
                Admin.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        System.out.println("Updated Admin: " + response.getBody());
    }

    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";

        ResponseEntity<Admin[]> response =
                this.restTemplate.getForEntity(url, Admin[].class);

        assertNotNull(response.getBody());

        System.out.println("Get All Admins:");
        for (Admin admin : response.getBody()) {
            System.out.println(admin);
        }
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + admin.getAdminId();

        this.restTemplate.delete(url);

        ResponseEntity<Admin> response = this.restTemplate.getForEntity(
                BASE_URL + "/read/" + admin.getAdminId(),
                Admin.class);

        assertNull(response.getBody());

        System.out.println("Deleted Admin: true");
    }
}
