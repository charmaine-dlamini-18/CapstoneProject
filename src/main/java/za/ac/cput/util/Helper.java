package za.ac.cput.util;

import java.util.Locale;

/*
Helper.java
Helper class
Sabelo Ceza - 220094489
Date: 16/03/2026
*/

public class Helper {

    // Checks if a String is null or empty
    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;
    }

    // Checks if an email has a valid format
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }

        // Removes spaces at the beginning and end
        String trimmed = email.trim();

        // Finds the position of the @ symbol
        int atIndex = trimmed.lastIndexOf('@');

        // Checks that the email has only one @ symbol
        if (atIndex <= 0 || atIndex != trimmed.indexOf('@') || atIndex == trimmed.length() - 1) {
            return false;
        }

        // Separates the email name and domain
        String localPart = trimmed.substring(0, atIndex);
        String domain = trimmed.substring(atIndex + 1).toLowerCase(Locale.ROOT);

        // Checks that the email name is not empty and has allowed characters
        if (localPart.isBlank() || !localPart.matches("^[A-Za-z0-9._%+-]+$")) {
            return false;
        }

        // Checks that the domain does not have spaces or incorrect dots
        if (domain.isBlank() || domain.contains(" ") || domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }

        // Allows only the email domains used by the application
        return domain.equals("gmail.com")
                || domain.equals("ac.za")
                || domain.equals("co.za")
                || domain.endsWith(".ac.za")
                || domain.endsWith(".co.za");
    }

    // Checks if an object has no value
    public static boolean isNull(Object obj) {
        if (obj == null) {
            return true;
        }
        return false;
    }

    // Checks if an integer is greater than zero
    public static boolean isValidInt(int value) {
        return value > 0;
    }

    // Checks if a decimal number is greater than zero
    public static boolean isPositive(double value) {
        return value > 0;
    }

    // Checks if a password is already in BCrypt format
    // This helps prevent hashing the same password twice
    public static boolean isBcryptHash(String value) {
        return value != null && value.matches("\\$2[aby]?\\$\\d{2}\\$[./A-Za-z0-9]{53}");
    }
}
