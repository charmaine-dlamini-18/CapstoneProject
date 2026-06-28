package za.ac.cput.repository;

/*
TutorRepository.java
Tutor Repository
Author: Imaan Achmat
(230458971)
Date: 26/06/2026
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Tutor;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, String> {
}