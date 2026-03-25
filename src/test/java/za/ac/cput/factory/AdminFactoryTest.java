package za.ac.cput.factory;

import org.junit.jupiter.api.*;
import za.ac.cput.domain.Admin;
import static org.junit.jupiter.api.Assertions.*;

/*
AdminFactoryTest.java
Admin factory testing
Author: Thimna Booi - 230232108
Date: 25/03/2026
 */

 class AdminFactoryTest {
     @Test
     void testCreateAdmin() {
         Admin admin = AdminFactory.createAdmin("A01", "userAdmin");
         assertNotNull(admin);
         System.out.println(admin);
     }
}
