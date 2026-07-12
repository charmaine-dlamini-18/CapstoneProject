package za.ac.cput.domain;
/*
Admin.java
Admin POJO with builder
Author: Thimna Booi - 230232108
Date: 13/03/2026
 */
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;
@Entity
public class Admin {
@Id
    private String adminId;
    private  String role;

    protected Admin() {

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

        public Builder setAdminId(String  adminId) {
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

}
