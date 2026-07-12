package za.ac.cput.domain;
import jakarta.persistence.*;

import java.util.List;

/*
TutorSubject.java
TutorSubject domain class
Author: Sabelo Ceza - 220094489
Date: 1/05/2026
*/
@Entity
@IdClass(TutorSubjectId.class)
public class TutorSubject {
    @Id
    @Column(name = "subjectCode")
    private String subjectCode;

    @Id
    @Column(name = "tutorId")
    private String tutorId;

    private int yearsTaught;
    @ManyToOne
    @JoinColumn(name = "subjectCode", insertable = false, updatable = false)
    private Subject subjects;

    @ManyToOne
    @JoinColumn(name = "tutorId", insertable = false, updatable = false)
    private Tutor tutors;




        protected TutorSubject() {
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

        public Subject getTutorSubjects() {
            return subjects;
        }

        public Tutor getSubjectTutors() {
            return tutors;
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
            private Subject subjects;
            private Tutor tutors;


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

            public Builder setTutors(Tutor tutors) {
                this.tutors = tutors;
                return this;
            }

            public Builder setSubjects(Subject subjects) {
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

