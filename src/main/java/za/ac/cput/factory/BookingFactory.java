package za.ac.cput.factory;
/*
BookingFactory.java
Booking Factory
Author: Charmaine Dlamini- 222056401
Date: 16/03/2026
*/
import za.ac.cput.domain.Booking;
import za.ac.cput.util.Helper;

import java.time.LocalDateTime;

public class BookingFactory {
    public static Booking createBooking(String bookingId, String studentNumber,
                                        String subjectCode, String sessionType, String duration,
                                        String tutorId, LocalDateTime date){

        if(Helper.isNullOrEmpty(bookingId)
                || Helper.isNullOrEmpty(studentNumber)
                || Helper.isNullOrEmpty(subjectCode)
                || Helper.isNullOrEmpty(sessionType)
                || Helper.isNullOrEmpty(duration)
                || Helper.isNullOrEmpty(tutorId)

        ){
            return null;
        }
        if(Helper.isNull(date)){
            return null;
        }
        return new Booking.Builder()
                .setBookingId(bookingId)
                .setStudentNumber(studentNumber)
                .setSubjectCode(subjectCode)
                .setSessionType(sessionType)
                .setDuration(duration)
                .setTutorId(tutorId)
                .setDate(date)
                .build();

    }
}


