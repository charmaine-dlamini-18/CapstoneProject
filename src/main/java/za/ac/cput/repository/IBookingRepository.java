package za.ac.cput.repository;

/*
IBookingRepository.java
IBooking Repository
Author: Charmaine Dlamini-222056401
Date: 20/03/2026
 */
import za.ac.cput.domain.Booking;

import java.util.List;

public interface IBookingRepository extends IRepository<Booking, String> {

    List<Booking> getAll();
}