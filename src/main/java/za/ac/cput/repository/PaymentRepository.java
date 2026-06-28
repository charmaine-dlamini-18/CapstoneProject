package za.ac.cput.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Payment;

/*
PaymentRepository.java
Payment repository
Author: Safiya Elmi
(240500598)
Date: 22/03/2026
*/

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
}
