package com.example.practice.dto;

public class OrderedItemDTO2 {
    public OrderedItemDTO2(Long id, String itemName, double price, int quantity) {
		super();
		this.id = id;
		this.itemName = itemName;
		this.price = price;
		this.quantity = quantity;
	}
    public OrderedItemDTO2() {
    		
     }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	private Long id;
    private String itemName;
    private double price;
    private int quantity;

    // Getters and setters
}