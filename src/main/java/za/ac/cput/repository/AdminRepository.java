package za.ac.cput.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Admin;
/*
AdminRepository.java
Admin repository
Author: Thimna Booi - 230232108
Date: 20/03/2026
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

}