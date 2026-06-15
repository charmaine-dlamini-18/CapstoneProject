package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Student;
import za.ac.cput.repository.StudentRepository;

import java.util.List;
@Service
public class StudentService implements IStudentService {

  private StudentRepository repository;

  @Autowired StudentService(StudentRepository repository){
      this.repository = repository;
  }

    @Override
    public Student create(Student student) {
      return this.repository.save(student);
    }

    @Override
    public Student read(String studentNumber) {
        return this.repository.findById(studentNumber).orElse(null);
    }

    @Override
    public Student update(Student student) {
        return this.repository.save(student);
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
