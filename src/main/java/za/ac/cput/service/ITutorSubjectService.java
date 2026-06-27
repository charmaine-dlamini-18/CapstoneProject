package za.ac.cput.service;

import za.ac.cput.domain.TutorSubject;

import java.util.List;
/*
ITutorSubjectService.java
ITutorSubjectService
Author: Sabelo Ceza - 220094489
Date: 27/06/2026
 */

public interface ITutorSubjectService extends IService<TutorSubject, String> {
    List<TutorSubject> getAll();
}
