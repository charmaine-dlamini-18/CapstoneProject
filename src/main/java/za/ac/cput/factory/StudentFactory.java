package za.ac.cput.factory;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Student;
import za.ac.cput.util.Helper;

import java.util.List;
/*
StudentFactory.java
Student with factory class
Author: Sabelo Ceza - 220094489
Date: 16/03/2026
 */

public class StudentFactory {

    public static Student createStudent(String studentNumber, String firstName, String lastName,
                                        String email, String phoneNumber,
                                        String password, String levelOfStudy, List<Booking> bookings) {

        if(Helper.isNullOrEmpty(studentNumber)
         ||Helper.isNullOrEmpty(firstName)
         ||Helper.isNullOrEmpty(lastName)
         ||Helper.isNullOrEmpty(email)
         ||Helper.isNullOrEmpty(phoneNumber)
         ||Helper.isNullOrEmpty(password)
         ||Helper.isNullOrEmpty(levelOfStudy))
        {
            return null;
        }
        if (!Helper.isValidEmail(email)) {
            return null;
        }
        if(Helper.isNull(bookings)) {
            return null;
        }
        return new Student.Builder()
                .setStudentNumber(studentNumber)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .setPassword(password)
                .setLevelOfStudy(levelOfStudy)
                .setBookings(bookings)
                .build();

    }
}
