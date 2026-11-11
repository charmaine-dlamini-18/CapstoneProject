package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Admin;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.security.PasswordHasher;

import java.util.List;

/*
AdminService.java
AdminService
Author: Thimna Booi - 230232108
Date: 17/06/2026
 */

@Service
public class AdminService implements IAdminService {

  private AdminRepository repository;
  private PasswordHasher passwordHasher;

  @Autowired AdminService(AdminRepository repository, PasswordHasher passwordHasher){
      this.repository = repository;
      this.passwordHasher = passwordHasher;
  }

    @Override
    public Admin create(Admin admin) {
      // Hash the password before saving
      Admin secured = new Admin.Builder().copy(admin)
              .setPassword(passwordHasher.hashNew(admin.getPassword()))
              .build();
      return this.repository.save(secured);
    }

    @Override
    public Admin read(String adminId) {
        return this.repository.findById(adminId).orElse(null);
    }

    @Override
    public Admin update(Admin admin) {
        Admin existing = read(admin.getAdminId());
        // An update may never create a new admin by accident
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Admin not found: " + admin.getAdminId());
        }
        // Keep the old hash. A hash is never hashed again
        String password = passwordHasher.hashForUpdate(admin.getPassword(), existing.getPassword());
        Admin secured = new Admin.Builder().copy(admin)
                .setPassword(password)
                .build();
        return this.repository.save(secured);
    }

    @Override
    public boolean delete(String adminId) {
        this.repository.deleteById(adminId);
        return true;
    }

    @Override
    public List<Admin> getAll() {
        return this.repository.findAll();
    }

    @Override
    public Admin findByEmail(String email) {
        return this.repository.findByEmailIgnoreCase(email).orElse(null);
    }
}
