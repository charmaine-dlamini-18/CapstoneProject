/*
IBookingService.java
Author: Charmaine Dlamini-222056401
Date: 22/06/2026
 */
package za.ac.cput.service;

import za.ac.cput.domain.Booking;
import java.util.List;

public interface IBookingService extends IService <Booking, String>{
    List<Booking> getAll();
}
