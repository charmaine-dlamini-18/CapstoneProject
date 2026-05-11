package za.ac.cput.factory;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Payment;
import za.ac.cput.util.Helper;

import java.time.LocalDateTime;

/*
PaymentFactory.java
Payment factory class
Author: Safiya Elmi
(240500598)
Date: 17 March 2026
*/

public class PaymentFactory {

    public static Payment createPayment(String paymentRef,
                                        double amount,
                                        LocalDateTime paymentDate,
                                        String paymentMethod,
                                        String status,
                                        Booking booking) {

        if (Helper.isNullOrEmpty(paymentRef)) {
            throw new IllegalArgumentException("Payment reference cannot be empty");
        }

        if (Helper.isNullOrEmpty(paymentMethod)) {
            throw new IllegalArgumentException("Payment method cannot be empty");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        return new Payment.Builder()
                .setPaymentRef(paymentRef)
                .setAmount(amount)
                .setPaymentDate(paymentDate)
                .setPaymentMethod(paymentMethod)
                .setStatus(status)
                .setBooking(booking)

                .build();
    }
}                .setPaymentMethod(paymentMethod)
                .setStatus(status)
                .build();
    }
}
