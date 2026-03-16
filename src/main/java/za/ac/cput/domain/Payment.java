package za.ac.cput.domain;

public class Payment {
    private String paymentRef;
    private double amount;
    private String paymentDate;
    private String paymentMethod;
    private String status;

    private Payment() {
    }

    private Payment(Builder builder) {
        this.paymentRef = builder.paymentRef;
        this.amount = builder.amount;
        this.paymentDate = builder.paymentDate;
        this.paymentMethod = builder.paymentMethod;
        this.status = builder.status;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "==Payment Details==" +
                "\nPayment Reference: " + paymentRef +
                "\nAmount: " + amount +
                "\nPayment Date: " + paymentDate +
                "\nPayment Method: " + paymentMethod +
                "\nStatus: " + status;
    }

    public static class Builder {
        private String paymentRef;
        private double amount;
        private String paymentDate;
        private String paymentMethod;
        private String status;

        public Builder setPaymentRef(String paymentRef) {
            this.paymentRef = paymentRef;
            return this;
        }

        public Builder setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Builder setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder copy(Payment payment) {
            this.paymentRef = payment.paymentRef;
            this.amount = payment.amount;
            this.paymentDate = payment.paymentDate;
            this.paymentMethod = payment.paymentMethod;
            this.status = payment.status;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}