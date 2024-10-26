package com.example.practice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {
    private Long id;
  
	private LocalDateTime createdAt;
    private String status;
    private double totalAmount;
    private List<OrderItemResponseDTO> orderItems;
    private String userEmail; // Add this field
    // Add AddressDTO for the order address
    private AddressDTO orderAddress;

    // Getters and Setters for AddressDTO
    public AddressDTO getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(AddressDTO orderAddress) {
        this.orderAddress = orderAddress;
    }
	public Long getId() {
		return id;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<OrderItemResponseDTO> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItemResponseDTO> orderItems) {
		this.orderItems = orderItems;
	}
	public OrderResponseDTO() {
		super();
	}

    // Getters and setters
}