package com.moorixlabs.park.models;

import java.util.Random;

public class PaymentManager {
    private double userBalance = 100.0;
    private Random random = new Random();

    public static class PaymentResult {
        public boolean success;
        public String messageKey;

        public PaymentResult(boolean success, String messageKey) {
            this.success = success;
            this.messageKey = messageKey;
        }
    }

    public PaymentResult processPayment(Payment payment) {
        
        boolean networkSuccess = random.nextDouble() > 0.05;

        if (payment.getMethod() == Payment.PaymentMethod.PREPAID_BALANCE) {
            if (userBalance >= payment.getAmount()) {
                userBalance -= payment.getAmount();
                return new PaymentResult(true, "msg_payment_success");
            } else {
                return new PaymentResult(false, "err_insufficient_balance");
            }
        }

        if (networkSuccess) {
            return new PaymentResult(true, "msg_payment_success");
        } else {
            return new PaymentResult(false, "err_payment_failed");
        }
    }

    public double getBalance() { return userBalance; }
    
    public void addBalance(double amount) {
        if (amount > 0) {
            userBalance += amount;
        }
    }
}