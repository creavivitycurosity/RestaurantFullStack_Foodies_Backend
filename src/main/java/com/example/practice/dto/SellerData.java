package com.example.practice.dto;

public class SellerData {
    private String sellerName;
    private String restaurantName;
    private double totalIncome;
    private long totalOrders;
    private double monthlyIncome;
    private long monthlyOrders;

    public SellerData() {}

    public SellerData(String sellerName, String restaurantName, double totalIncome, long totalOrders, double monthlyIncome, long monthlyOrders) {
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

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public long getMonthlyOrders() {
        return monthlyOrders;
    }

    public void setMonthlyOrders(long monthlyOrders) {
        this.monthlyOrders = monthlyOrders;
    }
}