package za.ac.cput.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Subject;

/*
Subjectt repository
Author: Wendy Bayise
 222828978
Date: 20/03/2026
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {
}