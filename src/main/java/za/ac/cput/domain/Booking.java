/*
Booking.java
Booking POJO with builder
Author: Charmaine Dlamini-222056401
Date: 13/03/2026
 */

package za.ac.cput.domain;

import java.time.LocalDateTime;

public class Booking {
    private String bookingId;
    private String studentNumber;
    private String subjectCode;
    private String sessionType;
    private String duration;
    private String tutorId;
    private LocalDateTime date;


    private Booking() {

    }

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.studentNumber = builder.studentNumber;
        this.subjectCode = builder.subjectCode;
        this.sessionType = builder.sessionType;
        this.duration = builder.duration;
        this.tutorId = builder.tutorId;
        this.date = builder.date;


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


    @Override
    public String toString() {
        return "Booking" +
                "\nBooking Id: " + bookingId +
                "\nStudent Number: " + studentNumber +
                "\nSubject Code: " + subjectCode +
                "\nSession Type: " + sessionType +
                "\nDuration: " + duration +
                "\nTutor Id: " + tutorId +
                "\nDate: " + date;
    }

    public static class Builder {
        private String bookingId;
        private String studentNumber;
        private String subjectCode;
        private String sessionType;
        private String duration;
        private String tutorId;
        private LocalDateTime date;

        public Builder copy(Booking booking) {
            this.bookingId = booking.bookingId;
            this.studentNumber = booking.studentNumber;
            this.subjectCode = booking.subjectCode;
            this.sessionType = booking.sessionType;
            this.duration = booking.duration;
            this.tutorId = booking.tutorId;
            this.date = booking.date;
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

        public Booking build() {
            return new Booking(this);
        }
    }
}