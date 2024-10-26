package com.example.practice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDTO {
    private Long id;
    private int quantity;
    private String name;
    private double price;
    private String sellerName;  // Changed from Long sellerId to String sellerEmail
    public ProductDTO() {
    	
    	
    }
 
    @Override
	public String toString() {
		return "ProductDTO [id=" + id + ", quantity=" + quantity + ", name=" + name + ", price=" + price
				+ ", sellerName=" + sellerName + "]";
	}

	@JsonCreator
    public ProductDTO(@JsonProperty("id") Long id,
                      @JsonProperty("name") String name,
                      @JsonProperty("quantity") int quantity,
                      @JsonProperty("price") double price,
                      @JsonProperty("sellerName") String sellerName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.sellerName = sellerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public void setPrice(double price) {
        this.price = price;
    }

  
}
