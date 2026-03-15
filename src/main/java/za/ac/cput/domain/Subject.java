package za.ac.cput.domain;
/*
subject.java
Subject domain class
Author:Wendy Bayise
(222828978)
Date: 15 March 2026
 */

public class Subject {
    private String subjectCode;
    private String subjectName;
    private String subjectDescription;
    private String gradeLevel;

    private Subject() {
    }

    private Subject(Builder bulider){
        this.subjectCode = bulider.subjectCode;
        this.subjectName = bulider.subjectName;
        this.subjectDescription = bulider.subjectDescription;
        this.gradeLevel = bulider.gradeLevel;
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
        public Subject bulid(){
            return new Subject(this);
        }
    }
}

