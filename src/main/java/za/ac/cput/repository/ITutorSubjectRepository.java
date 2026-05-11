package za.ac.cput.repository;


import za.ac.cput.domain.TutorSubject;
import java.util.List;
/*
TutorSubject.java
TutorSubject Repository class
Author: Charmaine Dlamini - 222056401
Date: 1/05/2026
*/

public interface ITutorSubjectRepository extends IRepository<TutorSubject, String>{
    List<TutorSubject> getAll();

}
