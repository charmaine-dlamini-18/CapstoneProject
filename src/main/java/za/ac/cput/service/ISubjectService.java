package za.ac.cput.service;

import za.ac.cput.domain.Subject;
import java.util.List;

/*
Subject service
Author:Wendy Bayise
222828978
Date: 22/06/2026
*/

public interface ISubjectService extends IService<Subject, String> {
    List<Subject> getAll();
}
