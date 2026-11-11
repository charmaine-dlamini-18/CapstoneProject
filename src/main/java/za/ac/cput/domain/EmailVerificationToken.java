package za.ac.cput.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class EmailVerificationToken {

    @Id
    private String tokenHash;

    @Column(nullable = true)
    private String studentNumber;

    @Column(nullable = true)
    private String tutorId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    protected EmailVerificationToken() {
    }

    public EmailVerificationToken(String tokenHash, String studentNumber, String tutorId, LocalDateTime expiresAt) {
        this.tokenHash = tokenHash;
        this.studentNumber = studentNumber;
        this.tutorId = tutorId;
        this.expiresAt = expiresAt;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getTutorId() {
        return tutorId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
