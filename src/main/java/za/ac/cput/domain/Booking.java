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
    private String studentNumber;
    private String subjectCode;
    private String sessionType;
    private String duration;
    private String tutorId;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToOne(mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Payment payment;

    protected Booking() {

    }

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.studentNumber = builder.studentNumber;
        this.subjectCode = builder.subjectCode;
        this.sessionType = builder.sessionType;
        this.duration = builder.duration;
        this.tutorId = builder.tutorId;
        this.date = builder.date;
        this.student = builder.student;
        this.payment = builder.payment;


    }

    public String getBookingId() {
        return bookingId;
    }

    public String getStudentNumber() {
        return studentNumber;
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

    public String getTutorId() {
        return tutorId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Student getStudent() {
        return student;
    }
    public Payment getPayment() {
        return payment;
    }


    @Override
    public String toString() {
        return "Booking" +
                "\nBooking Id: " + bookingId +
                "\nStudent Number: " + studentNumber +
                "\nSubject Code: " + subjectCode +
                "\nSession Type: " + sessionType +
                "\nDuration: " + duration +
                "\nTutor Id: " + tutorId +
                "\nDate: " + date +
                "\nStudent: " + student +
                "\nPayment: " + payment;
    }

    public static class Builder {
        private String bookingId;
        private String studentNumber;
        private String subjectCode;
        private String sessionType;
        private String duration;
        private String tutorId;
        private LocalDateTime date;
        private Student student;
        private Payment payment;

        public Builder copy(Booking booking) {
            this.bookingId = booking.bookingId;
            this.studentNumber = booking.studentNumber;
            this.subjectCode = booking.subjectCode;
            this.sessionType = booking.sessionType;
            this.duration = booking.duration;
            this.tutorId = booking.tutorId;
            this.date = booking.date;
            this.student = booking.student;
            this.payment = booking.payment;
            return this;
        }


        public Builder setBookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setStudentNumber(String studentNumber) {
            this.studentNumber = studentNumber;
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

        public Builder setTutorId(String tutorId) {
            this.tutorId = tutorId;
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

        public Builder setPayment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }
    }
}