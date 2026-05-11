package za.ac.cput.domain;
/*
Admin.java
Admin POJO with builder
Author: Thimna Booi - 230232108
Date: 13/03/2026
 */
import java.util.List;

public class Admin {

    private String adminId;
    private  String role;

    private Admin() {

    }

    private Admin(Builder builder) {
        this.adminId = builder.adminId;
        this.role = builder.role;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getRole() {
        return role;
    }


    @Override
    public String toString() {
        return "==Admin Details=={" +
                "\nAdmin Id='" + adminId +
                "\nRole='" + role +
                '}';
    }

    public static class Builder {

        private String adminId;
        private String role;

        public Builder setAdminId(String id) {
            this.adminId = adminId;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder copy(Admin admin) {
            this.adminId = admin.adminId;
            this.role = admin.role;
            return this;
        }

        public Admin build() {
            return new Admin(this);
        }
    }
    public boolean login() {
        return true;
    }

    public void logout() {
        System.out.println("Admin logged out");
    }

    public List<Student> viewStudents() {
        return null;
    }

    public List<Tutor> viewTutors() {
        return null;
    }

    public List<Subject> viewSubjects() {
        return null;
    }

    public List<Booking> viewBookings() {
        return null;
    }

}
