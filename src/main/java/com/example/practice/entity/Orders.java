package com.example.practice.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)  // Fetch the user eagerly

    private OurUsers user;

    @Override
	public String toString() {
		return "Orders [id=" + id + ", user=" + user + ", orderItems=" + orderItems + ", totalAmount=" + totalAmount
				+ ", createdAt=" + createdAt + ", status=" + status + ", paymentId=" + paymentId + ", payerId="
				+ payerId + "]";
	}
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    private double totalAmount;

    private LocalDateTime createdAt; // New field to store creation date and time

    private String status;
    @Column(name = "payment_id")
    private String paymentId;
    @Column(name = "payer_id")
    private String payerId;  
	
	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Orders() {
        super();
    }

    public Orders(OurUsers user, List<OrderItem> orderItems) {
        this.user = user;
        this.orderItems = orderItems;
        this.totalAmount = calculateTotalAmount(orderItems);
        this.createdAt = LocalDateTime.now(); // Set the creation time
        for (OrderItem item : orderItems) {
            item.setOrder(this);
        }
    }

    private double calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OurUsers getUser() {
        return user;
    }

    public void setUser(OurUsers user) {
        this.user = user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.totalAmount = calculateTotalAmount(orderItems);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    @PrePersist
    public void setCreationTime() {
        this.createdAt = LocalDateTime.now();
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @JsonManagedReference

    private UserAddress orderAddress;  // Store selected address

    // Getters and Setters for orderAddress
    public UserAddress getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(UserAddress orderAddress) {
        this.orderAddress = orderAddress;
    }
    
}