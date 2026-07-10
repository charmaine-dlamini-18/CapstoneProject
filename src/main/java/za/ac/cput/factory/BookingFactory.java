/*
BookingFactory.java
Booking Factory
Author: Charmaine Dlamini- 222056401
Date: 16/03/2026
*/
package za.ac.cput.factory;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Tutor;
import za.ac.cput.util.Helper;

import java.time.LocalDateTime;

public class BookingFactory {
    public static Booking createBooking(String bookingId, String subjectCode, String sessionType, String duration,
                                        LocalDateTime date, Student student, Tutor tutor, Payment payment){

        if(Helper.isNullOrEmpty(bookingId)
                || Helper.isNullOrEmpty(subjectCode)
                || Helper.isNullOrEmpty(sessionType)
                || Helper.isNullOrEmpty(duration)
        ){
            return null;
        }
        if(Helper.isNull(date) || Helper.isNull(student) || Helper.isNull(tutor)) {
            return null;
        }
        return new Booking.Builder()
                .setBookingId(bookingId)
                .setSubjectCode(subjectCode)
                .setSessionType(sessionType)
                .setDuration(duration)
                .setDate(date)
                .setStudent(student)
                .setTutor(tutor)
                .setPayment(payment)
                .build();

    }
}
