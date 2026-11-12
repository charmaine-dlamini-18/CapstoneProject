package za.ac.cput.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.Admin;
import za.ac.cput.factory.AdminFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
AdminControllerTest.java
AdminControllerTest
Author: Thimna Barbara Booi - 230232108
Date: 28/06/2026
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class AdminControllerTest {

    private static Admin admin;

    @Autowired
    private TestRestTemplate restTemplate;

    // Sends the admin login with every request, so Spring Security lets the test in
    private TestRestTemplate adminTemplate() {
        return restTemplate.withBasicAuth("admin@tutoring.co.za", "Password123");
    }
    private static final String BASE_URL = "/admin";

    @BeforeAll
    public static void setUp() {
        admin = AdminFactory.createAdminFull(
                "AD001",
                "Lebohang",
                "Booi",
                "adminctrl@cput.ac.za",
                "SecurePass123",
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void a_create() {
        String url = BASE_URL + "/create";
        // The password is write-only in JSON, so the test sends it in a plain map
        Map<String, Object> body = new HashMap<>();
        body.put("adminId", admin.getAdminId());
        body.put("firstName", admin.getFirstName());
        body.put("lastName", admin.getLastName());
        body.put("email", admin.getEmail());
        body.put("password", "SecurePass123");
        // The create call now refuses an ID that already exists, so start from a clean slate
        adminTemplate().delete(BASE_URL + "/delete/" + admin.getAdminId());
        Admin createdAdmin = adminTemplate().postForObject(url, body, Admin.class);
        assertNotNull(createdAdmin);
        assertEquals(admin.getAdminId(), createdAdmin.getAdminId());
        admin = createdAdmin;
        System.out.println("Created: " + createdAdmin);
    }

    @Test
    void b_read() {
        String url = BASE_URL + "/read/" + admin.getAdminId();
        ResponseEntity<Admin> createdAdmin =
                adminTemplate().getForEntity(url, Admin.class);
        assertNotNull(createdAdmin);
        System.out.println(createdAdmin.getStatusCode());
        System.out.println(createdAdmin.getBody());
    }

    @Test
    void c_update() {
        Admin updatedAdmin = new Admin.Builder().copy(admin)
                .setFirstName("Jonathan")
                .build();
        String url = BASE_URL + "/update";
        adminTemplate().put(url, updatedAdmin);
        ResponseEntity<Admin> response = adminTemplate().getForEntity(BASE_URL + "/read/" + updatedAdmin.getAdminId(), Admin.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        System.out.println("Updated: " + response.getBody());
    }

    @Test
    @Disabled
    void e_delete() {
        String url = BASE_URL + "/delete/" + admin.getAdminId();
        adminTemplate().delete(url);

        ResponseEntity<Admin> response = adminTemplate().getForEntity(BASE_URL + "/read/" + admin.getAdminId(), Admin.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("Admin deleted:" + "true");
    }

    @Test
    void d_getAll() {
        String url = BASE_URL + "/getAll";
        ResponseEntity<Admin[]> response = adminTemplate().getForEntity(url, Admin[].class);
        assertNotNull(response.getBody());
        System.out.println("Get All: ");
        for (Admin admin : response.getBody()) {
            System.out.println(admin);
        }
    }

    @Test
    void f_findByEmail() {
        String url = BASE_URL + "/findByEmail/" + admin.getEmail();
        ResponseEntity<Admin> response = adminTemplate().getForEntity(url, Admin.class);
        assertNotNull(response.getBody());
        assertEquals(admin.getEmail(), response.getBody().getEmail());
        System.out.println("Found by email: " + response.getBody());
    }
}