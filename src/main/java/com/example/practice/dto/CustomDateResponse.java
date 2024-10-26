package com.example.practice.dto;
public class CustomDateResponse {
    private long totalOrders;
    private double totalAmount;

    public CustomDateResponse(long totalOrders, double totalAmount) {
        this.totalOrders = totalOrders;
        this.totalAmount = totalAmount;
    }
    public CustomDateResponse() {
     
    }

	public long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

    // Getters and Setters
}
