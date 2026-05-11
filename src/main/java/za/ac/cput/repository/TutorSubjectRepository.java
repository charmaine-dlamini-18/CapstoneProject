package za.ac.cput.repository;
import za.ac.cput.domain.TutorSubject;
import java.util.ArrayList;
import java.util.List;

/*
TutorSubject.java
TutorSubject Repository class
Author: Charmaine Dlamini - 222056401
Date: 1/05/2026
*/

public class TutorSubjectRepository implements ITutorSubjectRepository {

    private static ITutorSubjectRepository repository = null;
    private List<TutorSubject> tutorSubjects;

    private TutorSubjectRepository() {
        tutorSubjects = new ArrayList<>();
    }
    public static ITutorSubjectRepository getRepository() {
        if (repository == null) {
            repository = new TutorSubjectRepository();
        }
        return repository;
    }

    @Override
    public TutorSubject create(TutorSubject tutorSubject) {
        boolean success = tutorSubjects.add(tutorSubject);
        if (success) {
            return tutorSubject;
        }
        return null;
    }

    @Override
    public TutorSubject read(String subjectCode) {
        for (TutorSubject tutorSubject : tutorSubjects) {
            if (tutorSubject.getSubjectCode().equals(subjectCode)) {
                return tutorSubject;
            }
        }
        return null;
    }
    @Override
    public TutorSubject update(TutorSubject tutorSubject) {
        String subjectCode = tutorSubject.getSubjectCode();
        TutorSubject oldTutorSubject = read(subjectCode);
        if (oldTutorSubject != null) {
            tutorSubjects.remove(oldTutorSubject);
            tutorSubjects.add(tutorSubject);
            return tutorSubject;
        }
        return null;
    }
    @Override
    public boolean delete(String subjectCode) {
        TutorSubject tutorSubjectToDelete = read(subjectCode);
        if (tutorSubjectToDelete != null) {
            tutorSubjects.remove(tutorSubjectToDelete);
            return true;
        }
        return false;
    }
    @Override
    public List<TutorSubject> getAll() {
        return tutorSubjects;
    }

}