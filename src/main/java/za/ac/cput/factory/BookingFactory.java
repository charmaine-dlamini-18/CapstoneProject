package za.ac.cput.factory;
/*
BookingFactory.java
Booking Factory
Author: Charmaine Dlamini- 222056401
Date: 16/03/2026
*/
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.util.Helper;

public class BookingFactory {
    public static Booking createBooking(String bookingId, String sessionId,
                                        String sessionTime, String sessionType,
                                        String duration, String studentNumber,
                                        String subjectCode, String tutorId,
                                        Payment payment){

        if(Helper.isNullOrEmpty(bookingId)
                || Helper.isNullOrEmpty(sessionId)
                || Helper.isNullOrEmpty(sessionTime)
                || Helper.isNullOrEmpty(sessionType)
                || Helper.isNullOrEmpty(duration)
                || Helper.isNullOrEmpty(studentNumber)
                || Helper.isNullOrEmpty(subjectCode)
                || Helper.isNullOrEmpty(tutorId)
                || Helper.isNull(payment)
        ){

            return null;
        }
        return new Booking.Builder()
                .setBookingId(bookingId)
                .setSessionId(sessionId)
                .setSessionTime(sessionTime)
                .setSessionType(sessionType)
                .setDuration(duration)
                .setStudentNumber(studentNumber)
                .setSubjectCode(subjectCode)
                .setTutorId(tutorId)
                .setPayment(payment)
                .build();

    }
}


