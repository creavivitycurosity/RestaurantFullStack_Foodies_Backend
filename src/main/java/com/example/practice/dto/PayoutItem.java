// PayoutItem.java
package com.example.practice.dto;

public class PayoutItem {
    private String email; // Recipient email address
    private double amount; // Amount to be transferred
    private String note; // Note or description for the payout

    // Constructor
    public PayoutItem(String email, double amount, String note) {
        this.email = email;
        this.amount = amount;
        this.note = note;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "PayoutItem{" +
                "email='" + email + '\'' +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                '}';
    }
}
