package za.ac.cput.service;

import za.ac.cput.domain.Student;

import java.util.List;
/*
IStudentService.java
IStudentService
Author: Sabelo Ceza - 220094489
Date: 17/06/2026
 */
public interface IStudentService extends IService<Student, String> {
    List<Student> getAll();
}
