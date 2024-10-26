package com.example.practice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    @JsonBackReference
    private Orders order;
    @Override
	public String toString() {
		return "OrderItem [id=" + id + ", status=" + status + ", item=" + item + ", seller="
				+ seller + ", name=" + name + ", price=" + price + ", quantity=" + quantity + "]";
	}

	private String status; // Add status field

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
	
	@ManyToOne
	@JoinColumn(name = "seller_id")
	private OurUsers seller;
	    
	    
    public OurUsers getSeller() {
			return seller;
		}

		public void setSeller(OurUsers seller) {
			this.seller = seller;
		}

	private String name;
    private double price;
    private int quantity; // Add this field
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public OrderItem() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderItem(Item item, int quantity) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.quantity = quantity; // Initialize quantity
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
    
    private String refundStatus;  // Add refund status field

    // Other fields like item, price, quantity, etc.

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;  // If null, set default
    }
}
