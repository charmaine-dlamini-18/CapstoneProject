package za.ac.cput.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Admin;
import za.ac.cput.repository.AdminRepository;

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

    @Autowired
    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    @Override
    public Admin create(Admin admin) {
        return this.repository.save(admin);
    }

    @Override
    public Admin read(String adminId) {
        return this.repository.findById(adminId).orElse(null);
    }

    @Override
    public Admin update(Admin admin) {
        return this.repository.save(admin);
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
}