package za.ac.cput.repository;

/*
BookingRepository.java
Booking Repository
Author: Charmaine Dlamini-222056401
Date: 20/03/2026
 */
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Booking;

import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

}