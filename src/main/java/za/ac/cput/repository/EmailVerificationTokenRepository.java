package za.ac.cput.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.EmailVerificationToken;

import java.util.List;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {

    // student still waiting to confirm the email
    boolean existsByStudentNumber(String studentNumber);
    List<EmailVerificationToken> findAllByStudentNumber(String studentNumber);

    // tutor still waiting to confirm the email?
    boolean existsByTutorId(String tutorId);

    List<EmailVerificationToken> findAllByTutorId(String tutorId);
}
