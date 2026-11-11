/*
Tutor Service
Author: Imaan Achmat - 230458971
Date: 26/06/2026
 */
package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Tutor;
import za.ac.cput.repository.TutorRepository;
import za.ac.cput.security.PasswordHasher;

import java.util.ArrayList;
import java.util.List;

@Service
public class TutorService implements ITutorService {

    private TutorRepository repository;
    private PasswordHasher passwordHasher;

    @Autowired
    TutorService(TutorRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Tutor create(Tutor tutor) {
        // Hash the password before saving
        Tutor secured = new Tutor.Builder().copy(tutor)
                .setPassword(passwordHasher.hashNew(tutor.getPassword()))
                .build();
        return this.repository.save(secured);
    }

    @Override
    public Tutor read(String tutorId) {
        return this.repository.findById(tutorId).orElse(null);
    }

    @Override
    public Tutor update(Tutor tutor) {
        Tutor existing = read(tutor.getTutorId());
        // An update may never create a new tutor by accident
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tutor not found: " + tutor.getTutorId());
        }
        // Keep the old hash. A hash is never hashed again
        String password = passwordHasher.hashForUpdate(tutor.getPassword(), existing.getPassword());
        // Bookings are managed through /booking, so keep the saved ones
        List<Booking> bookings = new ArrayList<>(existing.getBookings());
        Tutor secured = new Tutor.Builder().copy(tutor)
                .setPassword(password)
                .setBookings(bookings)
                .build();
        return this.repository.save(secured);
    }

    @Override
    public boolean delete(String tutorId) {
        this.repository.deleteById(tutorId);
        return true;
    }

    @Override
    public List<Tutor> getAll() {
        return this.repository.findAll();
    }
}
