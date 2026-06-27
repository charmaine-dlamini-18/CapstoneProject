package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Subject;
import za.ac.cput.repository.SubjectRepository;
import java.util.List;

/*
SubjectService
Author:Wendy Bayise
222828978
Date: 27/06/2026
 */
@Service
public class SubjectService implements ISubjectService {

    private SubjectRepository repository;

    @Autowired SubjectService(SubjectRepository repository){
        this.repository = repository;
    }

    @Override
    public Subject create(Subject subject) {
        return this.repository.save(subject);
    }

    @Override
    public Subject read(String subjectCode) {
        return this.repository.findById(subjectCode).orElse(null);
    }

    @Override
    public Subject update(Subject subject) {
        return this.repository.save(subject);
    }

    @Override
    public boolean delete(String subjectCode) {
        this.repository.deleteById(subjectCode);
        return true;
    }
    @Override
    public List<Subject> getAll() {
        return this.repository.findAll();
    }
}
