package za.ac.cput.factory;
/*
BookingFactory.java
Booking Factory
Author: Charmaine Dlamini- 222056401
Date: 16/03/2026
*/
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.Student;
import za.ac.cput.util.Helper;

import java.time.LocalDateTime;

public class BookingFactory {
    public static Booking createBooking(String bookingId, String subjectCode, String sessionType, String duration,
                                        String tutorId, LocalDateTime date, Student student, Payment payment){

        if(Helper.isNullOrEmpty(bookingId)
                || Helper.isNullOrEmpty(subjectCode)
                || Helper.isNullOrEmpty(sessionType)
                || Helper.isNullOrEmpty(duration)
                || Helper.isNullOrEmpty(tutorId)

        ){
            return null;
        }
        if(Helper.isNull(date) || Helper.isNull(student)) {
            return null;
        }
        return new Booking.Builder()
                .setBookingId(bookingId)
                .setSubjectCode(subjectCode)
                .setSessionType(sessionType)
                .setDuration(duration)
                .setTutorId(tutorId)
                .setDate(date)
                .setStudent(student)
                .setPayment(payment)
                .build();

    }
}


