package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Student;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.security.PasswordHasher;

import java.util.ArrayList;
import java.util.List;
/*
StudentService.java
StudentService
Author: Sabelo Ceza - 220094489
Date: 17/06/2026
 */
@Service
public class StudentService implements IStudentService {

  private StudentRepository repository;
  private PasswordHasher passwordHasher;

  @Autowired StudentService(StudentRepository repository, PasswordHasher passwordHasher){
      this.repository = repository;
      this.passwordHasher = passwordHasher;
  }

    @Override
    public Student create(Student student) {
      // Hash the password before saving
      Student secured = new Student.Builder().copy(student)
              .setPassword(passwordHasher.hashNew(student.getPassword()))
              .build();
      return this.repository.save(secured);
    }

    @Override
    public Student read(String studentNumber) {
        return this.repository.findById(studentNumber).orElse(null);
    }

    @Override
    public Student update(Student student) {
        Student existing = read(student.getStudentNumber());
        // An update may never create a new student by accident
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Student not found: " + student.getStudentNumber());
        }
        // Keep the old hash. A hash is never hashed again
        String password = passwordHasher.hashForUpdate(student.getPassword(), existing.getPassword());
        // Bookings are managed through /booking, so keep the saved ones
        List<Booking> bookings = new ArrayList<>(existing.getBookings());
        Student secured = new Student.Builder().copy(student)
                .setPassword(password)
                .setBookings(bookings)
                .build();
        return this.repository.save(secured);
    }

    @Override
    public boolean delete(String studentNumber) {
        this.repository.deleteById(studentNumber);
        return true;
    }
    @Override
    public List<Student> getAll() {
        return this.repository.findAll();
    }
}
