package za.ac.cput.service;

import za.ac.cput.domain.Payment;

import java.util.List;

/*
PaymentRepository.java
Payment repository
Author: Safiya Elmi
(240500598)
Date: 22/06/2026
*/

public interface IPaymentService extends IService<Payment, String> {
    List<Payment> getAll();
}
