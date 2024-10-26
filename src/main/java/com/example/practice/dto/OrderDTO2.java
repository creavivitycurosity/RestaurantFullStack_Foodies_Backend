package com.example.practice.dto;

import java.util.List;

public class OrderDTO2 {
    private Long id;
    private String userName;
    private List<OrderedItemDTO2> orderedItems;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<OrderedItemDTO2> getOrderedItems() {
		return orderedItems;
	}
	public void setOrderedItems(List<OrderedItemDTO2> orderedItems) {
		this.orderedItems = orderedItems;
	}
	public OrderDTO2(Long id, String userName, List<OrderedItemDTO2> orderedItems) {
		super();
		this.id = id;
		this.userName = userName;
		this.orderedItems = orderedItems;
	}
	public OrderDTO2() {
		
	}
    // Getters and setters
}