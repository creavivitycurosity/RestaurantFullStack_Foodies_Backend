package com.example.practice.dto;


public class Amount {
    private String value; // Amount value
    private String currency; // Currency code

    // Constructor
    public Amount(String value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    // Getters and setters

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
