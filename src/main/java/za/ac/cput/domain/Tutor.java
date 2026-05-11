package za.ac.cput.domain;
import java.util.List;

/*
Tutor.java
Tutor domain class
Author: Imaan Achmat
(230458971)
Date : 15 March 2026
 */

public class Tutor {
    private String tutorId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private double hourlyRate;
    private List<Booking> bookings;

    private Tutor() {
    }

    private Tutor(Builder builder) {
        this.tutorId = builder.tutorId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.password = builder.password;
        this.hourlyRate = builder.hourlyRate;
        this.bookings = builder.bookings;
    }

    public String getTutorId() {
        return tutorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    @Override
    public String toString() {
        return "==Tutor==" +
                "\nTutor Id:" + tutorId +
                "\nFirst Name: " + firstName +
                "\nLast Name: " + lastName +
                "\nEmail: " + email +
                "\nPhone Number: " + phoneNumber +
                "\nPassword: " + password +
                "\nHourly Rate: " + hourlyRate +
                "\nBookings: " + bookings;
    }

    public static class Builder {
        private String tutorId;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String password;
        private double hourlyRate;
        private List<Booking> bookings;

        public Builder setTutorId(String tutorId) {
            this.tutorId = tutorId;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setHourlyRate(double hourlyRate) {
            this.hourlyRate = hourlyRate;
            return this;
        }

        public Builder setBookings(List<Booking> bookings) {
            this.bookings = bookings;
            return this;
        }

        public Builder copy(Tutor tutor) {
            this.tutorId = tutor.tutorId;
            this.firstName = tutor.firstName;
            this.lastName = tutor.lastName;
            this.email = tutor.email;
            this.phoneNumber = tutor.phoneNumber;
            this.password = tutor.password;
            this.hourlyRate = tutor.hourlyRate;
            this.bookings = tutor.bookings;
            return this;
        }

        public Tutor build() {
            return new Tutor(this);
        }
    }
}