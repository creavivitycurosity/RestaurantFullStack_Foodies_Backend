package com.example.practice.dto;

public class OrderItemDTO {
    private Long itemId;
    private int quantity;
    // getters and setters
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}