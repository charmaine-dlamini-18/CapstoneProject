/*
BookingService.java
Booking Service
Author: Charmaine Dlamini-222056401
Date: 22/06/2026
 */
package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Booking;
import za.ac.cput.repository.BookingRepository;

import java.util.List;

@Service
public class BookingService implements IBookingService {
        private BookingRepository repository;

        @Autowired
        BookingService(BookingRepository repository){
            this.repository = repository;
        }

        @Override
        public Booking create(Booking booking) {
            return this.repository.save(booking);
        }

        @Override
        public Booking read(String bookingId) {
            return this.repository.findById(bookingId).orElse(null);
        }

        @Override
        public Booking update(Booking booking) {
            return this.repository.save(booking);
        }

        public boolean delete(String bookingId){
            this.repository.deleteById(bookingId);
            return true;
        }

        @Override
        public List<Booking> getAll() {
            return this.repository.findAll();
        }
    }

