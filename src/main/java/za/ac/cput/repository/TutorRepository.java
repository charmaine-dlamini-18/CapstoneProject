package za.ac.cput.repository;

/*
TutorRepository.java
Tutor Repository
Author: Imaan Achmat
(230458971)
Date: 2026
 */

import za.ac.cput.domain.Tutor;
import java.util.ArrayList;
import java.util.List;

public class TutorRepository implements ITutorRepository {

    private static ITutorRepository repository = null;
    private List<Tutor> tutorList;

    private TutorRepository() {
        tutorList = new ArrayList<Tutor>();

    }

    public static ITutorRepository getRepository() {
        if (repository == null) {
            repository = new TutorRepository();
        }
        return repository;
    }
    @Override
    public Tutor create(Tutor tutor) {
        boolean success = tutorList.add(tutor);
        if (success){
            return tutor;
        }
        return null;
    }
    @Override
    public Tutor read(String tutorId) {
        for (Tutor tutor : tutorList){
            if (tutor.getTutorId().equals(tutorId)) {
                return tutor;
            }
        }
        return null;
    }
    @Override
    public Tutor update(Tutor tutor) {
        String tutorId = tutor.getTutorId();
        Tutor oldTutor = read(tutorId);
        if (oldTutor == null) {
            return null;
        }
        boolean success = tutorList.remove(oldTutor);
        if (success){
            if (tutorList.add(tutor)) {
                return tutor;
            }
        }
        return null;
    }
    @Override
    public boolean delete(String tutorId) {
        Tutor tutorToDelete = read(tutorId);
        if (tutorToDelete == null) {
            return false;
        }
        return (tutorList.remove(tutorToDelete));
    }

    @Override
    public List<Tutor> getAll() {
        return new ArrayList<>(tutorList);
    }
}
