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
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "tutorId", insertable = false, updatable = false)
    private Tutor tutor;




        protected TutorSubject() {
        }

        private TutorSubject(Builder builder) {
            this.subjectCode = builder.subjectCode;
            this.tutorId = builder.tutorId;
            this.yearsTaught = builder.yearsTaught;
            this.subject = builder.subject;
            this.tutor = builder.tutor;

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
            return subject;
        }

        public Tutor getSubjectTutors() {
            return tutor;
        }

        @Override
        public String toString() {
            return "===TutorSubject===:" +
                    "\nsubjectCode: " + subjectCode +
                    "\nTutor Id: " + tutorId +
                    "\nYears Taught: " + yearsTaught+
                    "\nSubjects: " + subject +
                    "\nTutors: " + tutor;

        }

        public static class Builder {

            private String subjectCode;
            private String tutorId;
            private int yearsTaught;
            private Subject subject;
            private Tutor tutor;


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

            public Builder setTutor(Tutor tutor) {
                this.tutor = tutor;
                return this;
            }

            public Builder setSubject(Subject subject) {
                this.subject = subject;
                return this;
            }

            public Builder copy(TutorSubject tutorSubject) {
                this.subjectCode = tutorSubject.subjectCode;
                this.tutorId = tutorSubject.tutorId;
                this.yearsTaught = tutorSubject.yearsTaught;
                this.subject = tutorSubject.subject;
                this.tutor = tutorSubject.tutor;
                return this;
            }

            public TutorSubject build() {
                return new TutorSubject(this);
            }
        }
    }

