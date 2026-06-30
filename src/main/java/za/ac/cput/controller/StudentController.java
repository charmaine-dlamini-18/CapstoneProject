package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.Student;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.service.StudentService;

import java.util.List;
/*
StudentController.java
Student Controller
Author: Sabelo Ceza - 220094489
Date: 28/06/2026
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    private StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    //Sending or storing info
    @PostMapping("/create")
    public Student create(@RequestBody Student student) {
        return studentService.create(student);
    }
    @GetMapping("/read/{studentNumber}")
    public Student read(@PathVariable String studentNumber) {
        return studentService.read(studentNumber);
    }
    @PutMapping("/update")
    public Student update(@RequestBody Student student) {
        return studentService.update(student);
    }
    @DeleteMapping("/delete/{studentNumber}")
    public void delete(@PathVariable String studentNumber) {
        studentService.delete(studentNumber);
    }
    @GetMapping("/getall")
    public List<Student> getAll() {
        return studentService.getAll();
    }

}
