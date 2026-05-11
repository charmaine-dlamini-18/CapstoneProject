package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.Helper;

import java.util.List;
/*
TutorSubject.java
TutorSubject Factory class
Author: Sabelo Ceza - 220094489
Date: 1/05/2026
*/

public class TutorSubjectFactory {

    public static TutorSubject createTutorSubject(String subjectCode, String tutorId, int yearsTaught,
                                             List<Subject> subjects, List<Tutor> tutors) {

        if (Helper.isNullOrEmpty(subjectCode)
                || Helper.isNullOrEmpty(tutorId)) {
            return null;
        }
        if (!Helper.isValidInt(yearsTaught)) {
            return null;
        }
        if (Helper.isNull(subjects)
                || Helper.isNull(tutors)) {
            return null;
        }
        return new TutorSubject.Builder()
                .setSubjectCode(subjectCode)
                .setTutorId(tutorId)
                .setYearsTaught(yearsTaught)
                .setSubjects(subjects)
                .setTutors(tutors)
                .build();

    }
}
