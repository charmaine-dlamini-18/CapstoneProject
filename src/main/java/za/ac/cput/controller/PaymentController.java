package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Payment;
import za.ac.cput.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public Payment  create(@RequestBody Payment payment) {
        return paymentService.create(payment);
    }
    @GetMapping("/read/{paymentRef}")
    public Payment read(@PathVariable String paymentRef) {
        return paymentService.read(paymentRef);
    }
    @PutMapping("/update")
    public Payment update(@RequestBody Payment payment) {
        return paymentService.update(payment);
    }
    @DeleteMapping("/delete/{paymentRef}")
    public void delete(@PathVariable String paymentRef) {
        paymentService.delete(paymentRef);
    }
    @GetMapping("/getall")
    public List<Payment> getAll() {
        return paymentService.getAll();
    }


}
