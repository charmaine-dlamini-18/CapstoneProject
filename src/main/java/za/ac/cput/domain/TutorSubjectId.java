package za.ac.cput.domain;

import java.io.Serializable;
import java.util.Objects;
/*
TutorSubject.java
TutorSubject domain class
Author: Sabelo Ceza - 220094489
Date: 1/05/2026
*/

public class TutorSubjectId implements Serializable {
    private String subjectCode;
    private String tutorId;

    public TutorSubjectId() {
    }

    public TutorSubjectId(String subjectCode, String tutorId) {
        this.subjectCode = subjectCode;
        this.tutorId = tutorId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getTutorId() {
        return tutorId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TutorSubjectId that = (TutorSubjectId) o;
        return Objects.equals(subjectCode, that.subjectCode) && Objects.equals(tutorId, that.tutorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectCode, tutorId);
    }
}
