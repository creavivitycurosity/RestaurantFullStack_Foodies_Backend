package com.example.practice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRequestDTO {
    private String paymentId;
    private String payerId;
    private UserDTO user;
    private List<ProductDTO> product;
    private Integer orderAddressId; // Add this field to get the selected address ID

    public OrderRequestDTO(String paymentId, String payerId, UserDTO user, List<ProductDTO> product,
			Integer orderAddressId) {
		super();
		this.paymentId = paymentId;
		this.payerId = payerId;
		this.user = user;
		this.product = product;
		this.orderAddressId = orderAddressId;
	}

	public Integer getOrderAddressId() {
		return orderAddressId;
	}

	public void setOrderAddressId(Integer orderAddressId) {
		this.orderAddressId = orderAddressId;
	}

	// Default constructor
    public OrderRequestDTO() {}

    // Parameterized constructor
    @JsonCreator
    public OrderRequestDTO(@JsonProperty("paymentId") String paymentId, 
                           @JsonProperty("payerId") String payerId, 
                           @JsonProperty("user") UserDTO user, 
                           @JsonProperty("product") List<ProductDTO> product) {
        this.paymentId = paymentId;
        this.payerId = payerId;
        this.user = user;
        this.product = product;
    }

    // Getters and setters
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

    @JsonProperty("user")
    public UserDTO getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(UserDTO user) {
        this.user = user;
    }

    @JsonProperty("product")
    public List<ProductDTO> getProduct() {
        return product;
    }

    @JsonProperty("product")
    public void setProduct(List<ProductDTO> product) {
        this.product = product;
    }

	@Override
	public String toString() {
		return "OrderRequestDTO [paymentId=" + paymentId + ", payerId=" + payerId + ", user=" + user + ", product="
				+ product + "]";
	}
  
}
