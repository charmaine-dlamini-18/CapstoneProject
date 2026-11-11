/*
Booking.java
Booking POJO with builder
Author: Charmaine Dlamini-222056401
Date: 13/03/2026
*/

package za.ac.cput.domain;

import com.fasterxml.jackson.annotation.*;
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
    // Prevents recursive JSON serialization by stopping the Student
    // from being serialized through Booking
    @JsonBackReference("student-bookings")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorId")
    // Prevents recursive JSON serialization by stopping the Tutor
    // from being serialized through Booking
    @JsonBackReference("tutor-bookings")
    private Tutor tutor;

    @OneToOne(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    // Allows Payment to be serialized while preventing the Booking
    // from being serialized again through Payment
    @JsonManagedReference("booking-payment")
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

    // The tutor is hidden from the JSON (see @JsonBackReference), but students
    // still need the ID to show the tutor's name and price on their own bookings.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getTutorId() {
        return tutor == null ? null : tutor.getTutorId();
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public String toString() {
        String studentNumber = "null";
        String tutorId = "null";
        String paymentReference = "null";

        // Checks if a student exists before retrieving the student number
        if (student != null) {
            studentNumber = student.getStudentNumber();
        }

        // Checks if a tutor exists before retrieving the tutor ID
        if (tutor != null) {
            tutorId = tutor.getTutorId();
        }

        // Checks if a payment exists before retrieving the payment reference
        if (payment != null) {
            paymentReference = payment.getPaymentRef();
        }

        return "Booking" +
                "\nBooking Id: " + bookingId +
                "\nSubject Code: " + subjectCode +
                "\nSession Type: " + sessionType +
                "\nDuration: " + duration +
                "\nDate: " + date +
                "\nStudent Number: " + studentNumber +
                "\nTutor Id: " + tutorId +
                "\nPayment Reference: " + paymentReference;
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