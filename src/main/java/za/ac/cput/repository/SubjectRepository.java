/*
Subject Repository
Author:Wendy Bayise 222828978
Date: 22/03/2026
*/

package za.ac.cput.repository;

import za.ac.cput.domain.Subject;
import java.util.ArrayList;
import java.util.List;

public class SubjectRepository implements ISubjectRepository {
    private static ISubjectRepository repository = null;
    private List<Subject> subjectList;

    private SubjectRepository() {
        subjectList = new ArrayList<>();
    }

    public static ISubjectRepository getRepository() {
        if (repository == null) {
            repository = new SubjectRepository();
        }
        return repository;

    }

    @Override
    public Subject create(Subject subject) {
        boolean success = subjectList.add(subject);
        if (success) {
            return subject;
        }
        return null;
    }

    @Override
    public Subject read(String subjectCode) {
        for (Subject subject : subjectList) {
            if (subject.getSubjectCode().equals(subjectCode)) {
            return subject;
            }
        }
        return null;
    }

    @Override
    public Subject update(Subject subject) {
        String id = subject.getSubjectCode();
        Subject oldSubject = read(id);
        if (oldSubject == null) {
            return null;
        }

        boolean success = subjectList.remove(oldSubject);
        if (!success) {
            return null;
        }
        if (subjectList.add(subject)){
            return subject;

        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        Subject subjectToDelete = read(id);
        if (subjectToDelete == null) {
            return false;
        }
        return (subjectList.remove(subjectToDelete));
    }

    @Override
    public List<Subject> getAll() {
        return subjectList;
    }
}