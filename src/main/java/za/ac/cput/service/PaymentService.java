package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Payment;
import za.ac.cput.repository.PaymentRepository;

import java.util.List;

/*
PaymentService.java
Payment service
Author: Safiya Elmi
(240500598)
Date: 22/06/2026
*/

@Service
public class PaymentService implements IPaymentService {

    private PaymentRepository repository;

    @Autowired
    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment create(Payment payment) {
        return this.repository.save(payment);
    }

    @Override
    public Payment read(String paymentRef) {
        return this.repository.findById(paymentRef).orElse(null);
    }

    @Override
    public Payment update(Payment payment) {
        // An update may never create a new payment by accident
        Payment existing = read(payment.getPaymentRef());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Payment not found: " + payment.getPaymentRef());
        }
        Payment merged = new Payment.Builder().copy(payment)
                .setBooking(payment.getBooking() != null ? payment.getBooking() : existing.getBooking())
                .build();
        return this.repository.save(merged);
    }

    @Override
    public boolean delete(String paymentRef) {
        if (read(paymentRef) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Payment not found: " + paymentRef);
        }
        this.repository.deleteById(paymentRef);
        return true;
    }

    @Override
    public List<Payment> getAll() {
        return this.repository.findAll();
    }
}
