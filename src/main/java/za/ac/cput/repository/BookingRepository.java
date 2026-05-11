package za.ac.cput.repository;
/*
BookingRepository.java
Booking Repository
Author: Charmaine Dlamini-222056401
Date: 20/03/2026
 */

import za.ac.cput.domain.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository implements IBookingRepository{

    private static IBookingRepository repository = null;
    private List<Booking> bookings;

    private BookingRepository(){
        bookings = new ArrayList<>();
    }


    public static IBookingRepository getRepository(){
        if (repository == null){
            repository = new BookingRepository();
        }
        return repository;
    }


    @Override
    public Booking create(Booking booking) {
        boolean success = bookings.add(booking);
        if (success){
            return booking;
        }
        return null;
    }

    @Override
    public Booking read(String bookingId) {
        for (Booking booking: bookings){
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    @Override
    public Booking update(Booking booking) {
        String bookingId = booking.getBookingId();
        Booking oldBooking = read(bookingId);
        if (oldBooking == null) {
            return null;
        }
        boolean success = bookings.remove(oldBooking);
        if (success){
            if (bookings.add(booking)) {
                return booking;
            }
        }
        return null;
    }

    @Override
    public boolean delete(String bookingId) {
        Booking bookingToDelete = read(bookingId);
        if (bookingToDelete == null) {
            return false;
        }
        return (bookings.remove(bookingToDelete));
    }

    @Override
    public List<Booking> getAll() {
        return bookings;
    }

}

