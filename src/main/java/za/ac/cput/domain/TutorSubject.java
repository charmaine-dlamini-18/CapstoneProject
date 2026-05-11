package za.ac.cput.domain;
import java.util.List;

/*
TutorSubject.java
TutorSubject domain class
Author: Sabelo Ceza - 220094489
Date: 1/05/2026
*/

    public class TutorSubject {

        private String subjectCode;
        private String tutorId;
        private int yearsTaught;
        private List<Subject> subjects;
        private List<Tutor> tutors;

        private TutorSubject() {
        }

        private TutorSubject(Builder builder) {
            this.subjectCode = builder.subjectCode;
            this.tutorId = builder.tutorId;
            this.yearsTaught = builder.yearsTaught;
            this.subjects = builder.subjects;
            this.tutors = builder.tutors;

        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public String getTutorId() {
            return tutorId;
        }

        public int getYearsTaught() {
            return yearsTaught;
        }

        public List<Subject> getTutorSubjects() {
            return null;
        }

        public List<Tutor> getSubjectTutors() {
            return null;
        }

        @Override
        public String toString() {
            return "===TutorSubject===:" +
                    "\nsubjectCode: " + subjectCode +
                    "\nTutor Id: " + tutorId +
                    "\nYears Taught: " + yearsTaught+
                    "\nSubjects: " + subjects +
                    "\nTutors: " + tutors;

        }

        public static class Builder {

            private String subjectCode;
            private String tutorId;
            private int yearsTaught;
            private List<Subject> subjects;
            private List<Tutor> tutors;


            public Builder setSubjectCode(String subjectCode) {
                this.subjectCode = subjectCode;
                return this;
            }

            public Builder setTutorId(String tutorId) {
                this.tutorId = tutorId;
                return this;
            }

            public Builder setYearsTaught(int yearsTaught) {
                this.yearsTaught = yearsTaught;
                return this;
            }

            public Builder setTutors(List<Tutor> tutors) {
                this.tutors = tutors;
                return this;
            }

            public Builder setSubjects(List<Subject> subjects) {
                this.subjects = subjects;
                return this;
            }

            public Builder copy(TutorSubject tutorSubject) {
                this.subjectCode = tutorSubject.subjectCode;
                this.tutorId = tutorSubject.tutorId;
                this.yearsTaught = tutorSubject.yearsTaught;
                this.subjects = tutorSubject.subjects;
                this.tutors = tutorSubject.tutors;
                return this;
            }

            public TutorSubject build() {
                return new TutorSubject(this);
            }
        }
    }

