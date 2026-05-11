package za.ac.cput.repository;

/*
ITutorRepository.java
ITutor Repository
Author: Imaan Achmat
20 March 2026
 */

import za.ac.cput.domain.Tutor;
import java.util.List;

public interface ITutorRepository extends IRepository<Tutor, String> {

    List<Tutor> getAll();
}
