
package za.ac.cput.controller;

/*
SecurityControllerTest.java
Proves that the security rules work (login, roles, sign-up safety, hidden passwords).
Author: Charmaine Dlamini - 222056401
Date: 08/11/2026
*/

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import za.ac.cput.security.EmailVerificationService;
import org.springframework.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

// Starts the application on a random port and runs the tests using the test profile.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class SecurityControllerTest {

    // Provides an HTTP client for sending requests to the application.
    @Autowired
    private TestRestTemplate restTemplate;

    // Generates verification tokens for testing email verification.
    @Autowired
    private EmailVerificationService verificationService;

    // Stores the passwords used during the tests.
    private static final String NEW_PASSWORD = "BrandNewPass123";
    private static final String ADMIN_EMAIL = "admin@tutoring.co.za";
    private static final String ADMIN_PASSWORD =
            System.getenv().getOrDefault("ADMIN_PASSWORD", "Password123");
    private static final String PASSWORD = "StrongPass123";

    // Creates unique student numbers and email addresses for each test run.
    private static final String RUN = String.valueOf(System.currentTimeMillis());
    private static final String STUDENT_A = "A" + RUN;
    private static final String STUDENT_B = "B" + RUN;
    private static final String EMAIL_A = "a" + RUN + "@test.co.za";
    private static final String EMAIL_B = "b" + RUN + "@test.co.za";

    // Creates an HTTP client authenticated as the admin.
    private TestRestTemplate asAdmin() {
        return restTemplate.withBasicAuth(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    // Creates an HTTP client authenticated as the first student.
    private TestRestTemplate asStudentA() {
        return restTemplate.withBasicAuth(EMAIL_A, PASSWORD);
    }

    // Creates a student request body with the supplied details.
    private Map<String, Object> newStudent(String number, String email, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("studentNumber", number);
        body.put("firstName", "Test");
        body.put("lastName", "Student");
        body.put("email", email);
        body.put("phoneNumber", "0710000000");
        body.put("password", password);
        body.put("levelOfStudy", "Third year");
        return body;
    }

    // Checks that a request without login credentials is rejected.
    @Test
    void a_noLogin_isRejected() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/student/getAll", String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // Checks that the response does not trigger the browser's own login prompt.
        assertNull(response.getHeaders().getFirst("WWW-Authenticate"));
    }

    // Checks that students can register without exposing their passwords.
    @Test
    void b_signUp_isOpen_andNeverReturnsThePassword() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/student/create", newStudent(STUDENT_A, EMAIL_A, PASSWORD), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().toLowerCase().contains("password"));
    }

    // Checks that a student cannot log in before verifying their email.
    @Test
    void ba_unverifiedStudent_cannotLogIn_andGetsAClearMessage() {
        ResponseEntity<String> response =
                asStudentA().getForEntity("/student/read/" + STUDENT_A, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("not verified"));
    }

    // Checks that a verification link works only once.
    @Test
    void bb_verificationLink_confirmsTheEmail_butOnlyOnce() {
        String token = verificationService.issueToken(STUDENT_A);

        ResponseEntity<String> first = restTemplate.getForEntity(
                "/student/verify?token={token}", String.class, token);
        assertEquals(HttpStatus.OK, first.getStatusCode());

        ResponseEntity<String> second = restTemplate.getForEntity(
                "/student/verify?token={token}", String.class, token);
        assertEquals(HttpStatus.BAD_REQUEST, second.getStatusCode());
        assertTrue(second.getBody().contains("already used"));
    }

    // Checks that resending verification does not reveal whether an email has an account.
    @Test
    void fa_resend_neverRevealsWhetherTheEmailHasAnAccount() {
        Map<String, String> known = new HashMap<>();
        known.put("email", EMAIL_B);

        Map<String, String> unknown = new HashMap<>();
        unknown.put("email", "nobody" + RUN + "@test.co.za");

        ResponseEntity<String> forKnown = restTemplate.postForEntity(
                "/student/resend-verification", known, String.class);

        ResponseEntity<String> forUnknown = restTemplate.postForEntity(
                "/student/resend-verification", unknown, String.class);

        assertEquals(HttpStatus.OK, forKnown.getStatusCode());
        assertEquals(HttpStatus.OK, forUnknown.getStatusCode());
        assertEquals(forKnown.getBody(), forUnknown.getBody());
    }

    // Checks that registration cannot overwrite an existing student.
    @Test
    void c_signUp_cannotOverwriteAnExistingStudent() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/student/create",
                newStudent(STUDENT_A, "other" + RUN + "@test.co.za", PASSWORD),
                String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    // Checks that a registered email address cannot be reused.
    @Test
    void d_signUp_cannotReuseAnEmail() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/student/create",
                newStudent("X" + RUN, EMAIL_A, PASSWORD),
                String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    // Checks that registration rejects a weak password.
    @Test
    void e_signUp_rejectsAWeakPassword() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/student/create",
                newStudent("W" + RUN, "w" + RUN + "@test.co.za", "short"),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Registers a second student for access-control tests.
    @Test
    void f_signUp_secondStudent() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/student/create", newStudent(STUDENT_B, EMAIL_B, PASSWORD), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Checks that a student can read their own record but not another student's record.
    @Test
    void g_student_readsOwnRecord_butNotAnotherStudents() {
        ResponseEntity<String> own =
                asStudentA().getForEntity("/student/read/" + STUDENT_A, String.class);
        assertEquals(HttpStatus.OK, own.getStatusCode());

        ResponseEntity<String> other =
                asStudentA().getForEntity("/student/read/" + STUDENT_B, String.class);
        assertEquals(HttpStatus.FORBIDDEN, other.getStatusCode());
    }

    // Checks that a student cannot retrieve the full student list.
    @Test
    void h_student_cannotListAllStudents() {
        ResponseEntity<String> response =
                asStudentA().getForEntity("/student/getAll", String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Checks that a student cannot access admin endpoints.
    @Test
    void i_student_cannotUseTheAdminArea() {
        ResponseEntity<String> response =
                asStudentA().getForEntity("/admin/getAll", String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Checks that an admin can retrieve the student list.
    @Test
    void j_admin_canListStudents() {
        ResponseEntity<String> response =
                asAdmin().getForEntity("/student/getAll", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Checks that an incorrect password is rejected.
    @Test
    void k_wrongPassword_isRejected() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth(EMAIL_A, "WrongPass999")
                .getForEntity("/student/read/" + STUDENT_A, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Checks that changing a password requires the correct current password.
    @Test
    void m_changePassword_needsTheCurrentPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", "NotMyPassword1");
        body.put("newPassword", NEW_PASSWORD);

        ResponseEntity<String> response = asStudentA().exchange(
                "/student/change-password",
                HttpMethod.PUT,
                new HttpEntity<>(body),
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("current password"));
    }

    // Checks that a password can be changed and the old password no longer works.
    @Test
    void n_changePassword_works_andTheOldPasswordStopsWorking() {
        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", PASSWORD);
        body.put("newPassword", NEW_PASSWORD);

        ResponseEntity<String> response = asStudentA().exchange(
                "/student/change-password",
                HttpMethod.PUT,
                new HttpEntity<>(body),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Confirms that the old password is rejected.
        ResponseEntity<String> oldLogin =
                asStudentA().getForEntity("/student/read/" + STUDENT_A, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, oldLogin.getStatusCode());

        // Confirms that the new password works.
        ResponseEntity<String> newLogin = restTemplate
                .withBasicAuth(EMAIL_A, NEW_PASSWORD)
                .getForEntity("/student/read/" + STUDENT_A, String.class);
        assertEquals(HttpStatus.OK, newLogin.getStatusCode());
    }

    // Checks that a normal profile update cannot change a password.
    @Test
    void o_update_doesNotChangeAPassword() {
        Map<String, Object> body = newStudent(STUDENT_A, EMAIL_A, "SneakyNewPass1");

        ResponseEntity<String> response = restTemplate
                .withBasicAuth(EMAIL_A, NEW_PASSWORD)
                .exchange("/student/update", HttpMethod.PUT, new HttpEntity<>(body), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("change-password"));
    }

    // Checks that registration rejects passwords longer than BCrypt supports.
    @Test
    void l_signUp_rejectsAPasswordLongerThanBcryptAllows() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/student/create",
                newStudent("L" + RUN, "l" + RUN + "@test.co.za", "a".repeat(73)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Deletes the test students after all security tests have completed.
    @Test
    void z_cleanUp() {
        asAdmin().delete("/student/delete/" + STUDENT_A);
        asAdmin().delete("/student/delete/" + STUDENT_B);
    }
}
