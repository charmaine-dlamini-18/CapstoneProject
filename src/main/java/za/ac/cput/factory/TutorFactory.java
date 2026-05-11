package za.ac.cput.factory;

/*
tutorfactory.java
Tutor Factory Class
Author: Imaan Achmat
(230458971)
Date: 15 March 2026
 */

import za.ac.cput.domain.*;
import za.ac.cput.util.Helper;

import java.util.List;

public class TutorFactory {
    public static Tutor createTutor(String tutorId, String firstName, String lastName, String email,
                                    String phoneNumber, String password,
                                    double hourlyRate, List<Booking> bookings) {

        if (Helper.isNullOrEmpty(tutorId)
                || Helper.isNullOrEmpty(firstName)
                || Helper.isNullOrEmpty(lastName)
                || Helper.isNullOrEmpty(phoneNumber)
                || Helper.isNullOrEmpty(password)
                || Helper.isNullOrEmpty(email))
            return null;


        if (!Helper.isValidEmail(email)) {
            return null;
        }

        if (hourlyRate <= 0) {
            return null;
        }

        if (Helper.isNull(bookings)) {
            return null;
        }

        return new Tutor.Builder()
                .setTutorId(tutorId)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .setPassword(password)
                .setHourlyRate(hourlyRate)
                .setBookings(bookings)
                .build();
    }
}


