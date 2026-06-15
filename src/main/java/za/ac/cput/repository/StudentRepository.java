package za.ac.cput.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Student;

/*
IStudentRepository.java
Student repository
Author: Sabelo Ceza - 220094489
Date: 20/03/2026
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
}