package com.example.practice.dto;

public class SellerSummaryDTO {
    private String sellerName;
    private String restaurantName;
    private double totalIncome;
    private long totalOrders;
    private double monthlyIncome;
    private long monthlyOrders;

    // Constructor, Getters, and Setters

    public SellerSummaryDTO(String sellerName, String restaurantName, double totalIncome, long totalOrders, double monthlyIncome, long monthlyOrders) {
        this.sellerName = sellerName;
        this.restaurantName = restaurantName;
        this.totalIncome = totalIncome;
        this.totalOrders = totalOrders;
        this.monthlyIncome = monthlyIncome;
        this.monthlyOrders = monthlyOrders;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public long getMonthlyOrders() {
        return monthlyOrders;
    }
}
