package com.moorixlabs.park.models;

import java.io.Serializable;
import java.util.UUID;

public class Payment implements Serializable {
    private String id;
    private String sessionId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private long timestamp;

    public enum PaymentMethod {
        CASH, CARD, MOBILE_WALLET, PREPAID_BALANCE
    }

    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    public Payment(String sessionId, double amount, PaymentMethod method) {
        this.id = UUID.randomUUID().toString();
        this.sessionId = sessionId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getSessionId() { return sessionId; }
    public double getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public long getTimestamp() { return timestamp; }

    public void setStatus(PaymentStatus status) { this.status = status; }
}