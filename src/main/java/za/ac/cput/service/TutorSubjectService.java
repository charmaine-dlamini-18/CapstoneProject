package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.TutorSubject;
import za.ac.cput.domain.TutorSubjectId;
import za.ac.cput.repository.TutorSubjectRepository;

import java.util.List;
/*
TutorSubjectService.java
TutorSubject Service
Author: Sabelo Ceza - 220094489
Date: 27/06/2026
 */
@Service
public class TutorSubjectService implements ITutorSubjectService {

    private TutorSubjectRepository repository;

    @Autowired
    public TutorSubjectService(TutorSubjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public TutorSubject create(TutorSubject tutorSubject) {
        return repository.save(tutorSubject);
    }

    @Override
    public TutorSubject read(TutorSubjectId id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public TutorSubject update(TutorSubject tutorSubject) {
        return repository.save(tutorSubject);
    }

    @Override
    public boolean delete(TutorSubjectId id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<TutorSubject> getAll() {
        return repository.findAll();
    }
    }

