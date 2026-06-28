/*
Tutor Service
Author: Imaan Achmat - 230458971
Date: 26/06/2026
 */
package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Tutor;
import za.ac.cput.repository.TutorRepository;

import java.util.List;

@Service
public class TutorService implements ITutorService {

    private TutorRepository repository;

    @Autowired
    TutorService(TutorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tutor create(Tutor tutor) {
        return this.repository.save(tutor);
    }

    @Override
    public Tutor read(String tutorId) {
        return this.repository.findById(tutorId).orElse(null);
    }

    @Override
    public Tutor update(Tutor tutor) {
        return this.repository.save(tutor);
    }

    @Override
    public boolean delete(String tutorId) {
        this.repository.deleteById(tutorId);
        return true;
    }

    @Override
    public List<Tutor> getAll() {
        return this.repository.findAll();
    }
}