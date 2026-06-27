package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.TutorSubject;
import za.ac.cput.domain.TutorSubjectId;
import za.ac.cput.repository.ITutorSubjectRepository;
import za.ac.cput.repository.TutorSubjectRepository;

import java.util.List;

@Service
public class TutorSubjectService implements ITutorSubjectService {

    private TutorSubjectRepository repository;

    @Autowired TutorSubjectService(TutorSubjectRepository repository) {
        this.repository = repository;

    }
    @Override
    public TutorSubject create(TutorSubject tutorSubject) {
        return this.repository.save(tutorSubject);
    }

    @Override
    public TutorSubject read(TutorSubjectId id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public boolean delete(TutorSubjectId id) {
        this.repository.deleteById(id);
        return true;
    }

    @Override
    public boolean delete(String TutorSubjectId) {
        this.repository.deleteById(TutorSubjectId);
        return true;
    }
    @Override
    public List<TutorSubject> getAll() {
            return this.repository.findAll();
        }
    }

