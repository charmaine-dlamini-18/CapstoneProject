package za.ac.cput.util;
import org.apache.commons.validator.routines.*;

/*
Helper.java
Helper class
Sabelo Ceza - 220094489
Date: 16/03/2026
*/

public class Helper {
    public static boolean isNullOrEmpty(String str){
        if(str == null || str.isEmpty()){
            return true;
        }
        return false;
    }
    public static boolean isValidEmail(String email){
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
    public static boolean isNull(Object obj){
        if(obj == null){
            return true;
        }
        return false;
    }
    public static boolean isValidInt(int value) {
        return value > 0;

    }
}
