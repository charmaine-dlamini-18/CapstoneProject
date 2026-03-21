package za.ac.cput.repository;
import za.ac.cput.domain.Admin;
import java.util.ArrayList;
import java.util.List;

public class AdminRepository implements IAdminRepository {

    private static IAdminRepository repository = null;
    private List<Admin> adminList;

    private AdminRepository() {
        adminList = new ArrayList<>();
    }

    public static IAdminRepository getRepository() {
        if (repository == null) {
            repository = new AdminRepository();
        }
        return repository;
    }

    @Override
    public List<Admin> getAll() {
        return adminList;
    }

    @Override
    public Admin create(Admin admin) {
        boolean success = adminList.add(admin);
        if (success) {
            return admin;
        }
        return null;
    }

    @Override
    public Admin read(String adminId) {
        for (Admin admin : adminList) {
            if (admin.getId().equals(adminId)) {
                return admin;
            }
        }
        return null;
    }

    @Override
    public Admin update(Admin admin) {
        String adminId = admin.getId();
        Admin oldAdmin = read(adminId);

        if (oldAdmin == null) {
            return null;
        }

        boolean success = adminList.remove(oldAdmin);
        if (success) {
            if (adminList.add(admin)) {
                return admin;
            }
        }
        return null;
    }

    @Override
    public boolean delete(String adminId) {
        Admin adminToDelete = read(adminId);

        if (adminToDelete == null) {
            return false;
        }

        return adminList.remove(adminToDelete);
    }
}