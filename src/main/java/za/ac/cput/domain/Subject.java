
package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/*
subject.java
Subject domain class
Author:Wendy Bayise
(222828978)
Date: 15 March 2026
 */
@Entity
public class Subject {
@Id
    private String subjectCode;
    private String subjectName;
    private String subjectDescription;
    private String gradeLevel;

    protected Subject() {
    }

    private Subject(Builder builder){
        this.subjectCode = builder.subjectCode;
        this.subjectName = builder.subjectName;
        this.subjectDescription = builder.subjectDescription;
        this.gradeLevel = builder.gradeLevel;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }


    @Override
    public String toString() {
        return "==Subject Details==" +
                "\nSubject Code: " + subjectCode +
                "\nSubject Name: " + subjectName +
                "\nSubject Description: " + subjectDescription +
                "\nGrade Level: " + gradeLevel;

    }

    public static class Builder {
        private String subjectCode;
        private String subjectName;
        private String subjectDescription;
        private String gradeLevel;

        public Builder setSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public Builder setSubjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public Builder setSubjectDescription(String subjectDescription) {
            this.subjectDescription = subjectDescription;
            return this;
        }

        public Builder setGradeLevel(String gradeLevel) {
            this.gradeLevel = gradeLevel;
            return this;
        }
        public Builder copy(Subject subject){
            this.subjectCode = subject.subjectCode;
            this.subjectName = subject.subjectName;
            this.subjectDescription = subject.subjectDescription;
            this.gradeLevel = subject.gradeLevel;
            return this;
        }
        public Subject build(){
            return new Subject(this);
        }
    }
}



