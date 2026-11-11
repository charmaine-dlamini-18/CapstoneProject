/*
BookingService.java
Booking Service
Author: Charmaine Dlamini-222056401
Date: 22/06/2026
 */
package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
            // An update may never create a new booking by accident
            Booking existing = read(booking.getBookingId());
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Booking not found: " + booking.getBookingId());
            }
            // A normal update only changes the session details. Keep the people and
            // the payment if the client did not send them (the JSON hides them on purpose).
            Booking merged = new Booking.Builder().copy(booking)
                    .setStudent(booking.getStudent() != null ? booking.getStudent() : existing.getStudent())
                    .setTutor(booking.getTutor() != null ? booking.getTutor() : existing.getTutor())
                    .setPayment(booking.getPayment() != null ? booking.getPayment() : existing.getPayment())
                    .build();
            return this.repository.save(merged);
        }

        public boolean delete(String bookingId){
            if (read(bookingId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Booking not found: " + bookingId);
            }
            this.repository.deleteById(bookingId);
            return true;
        }

        @Override
        public List<Booking> getAll() {
            return this.repository.findAll();
        }
    }

