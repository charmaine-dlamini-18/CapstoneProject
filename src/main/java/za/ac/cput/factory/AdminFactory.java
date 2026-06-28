package za.ac.cput.factory;
import za.ac.cput.domain.Admin;
import za.ac.cput.util.Helper;
/*
AdminFactory.java
Admin with factory class
Author: Thimna Booi - 230232108
Date: 16/03/2026
 */

public class AdminFactory {
    public static Admin createAdmin (String adminId, String role) {
        if (Helper.isNullOrEmpty(adminId)
                || Helper.isNullOrEmpty(role)) {
            return null;
        }
        return new Admin.Builder()
                .setAdminId(adminId)
                .setRole(role)
                .build();
    }
}
