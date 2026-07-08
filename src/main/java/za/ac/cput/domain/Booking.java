/*
Booking.java
Booking POJO with builder
Author: Charmaine Dlamini-222056401
Date: 13/03/2026
 */

package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    private String bookingId;
    private String subjectCode;
    private String sessionType;
    private String duration;
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentNumber")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorId")
    private Tutor tutor;

    @OneToOne(mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Payment payment;

    protected Booking() {

    }

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.subjectCode = builder.subjectCode;
        this.sessionType = builder.sessionType;
        this.duration = builder.duration;
        this.date = builder.date;
        this.student = builder.student;
        this.tutor = builder.tutor;
        this.payment = builder.payment;

    }

    public String getBookingId() {
        return bookingId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSessionType() {
        return sessionType;
    }

    public String getDuration() {
        return duration;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Student getStudent() {
        return student;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public Payment getPayment() {
        return payment;
    }



    @Override
    public String toString() {
        return "Booking" +
                "\nBooking Id: " + bookingId +
                "\nSubject Code: " + subjectCode +
                "\nSession Type: " + sessionType +
                "\nDuration: " + duration +
                "\nDate: " + date +
                "\nStudent Number: " + student.getStudentNumber() +
                "\nTutor Id: " + tutor.getTutorId();
    }

    public static class Builder {
        private String bookingId;
        private String subjectCode;
        private String sessionType;
        private String duration;
        private LocalDateTime date;
        private Student student;
        private Payment payment;
        private Tutor tutor;

        public Builder copy(Booking booking) {
            this.bookingId = booking.bookingId;
            this.subjectCode = booking.subjectCode;
            this.sessionType = booking.sessionType;
            this.duration = booking.duration;
            this.date = booking.date;
            this.student = booking.student;
            this.payment = booking.payment;
            this.tutor = booking.tutor;
            return this;
        }


        public Builder setBookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public Builder setSessionType(String sessionType) {
            this.sessionType = sessionType;
            return this;
        }

        public Builder setDuration(String duration) {
            this.duration = duration;
            return this;
        }

        public Builder setDate(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public Builder setStudent(Student student) {
            this.student = student;
            return this;
        }

        public Builder setTutor(Tutor tutor) {
            this.tutor = tutor;
            return this;
        }

        public Builder setPayment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }
    }
}