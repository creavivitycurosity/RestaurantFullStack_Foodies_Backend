package com.example.practice.entity;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
@JsonInclude(JsonInclude.Include.ALWAYS)
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] image;
    private int quantity;  // New field to store item quantity
    private boolean featured;  // New field to indicate if the item is featured
    private boolean discountActive;
    private int discountPercentage;
    private int previousAmount;  // New field to store the original price
    private String description;  // New description field
    
    @ElementCollection
    private List<String> tags; // Add this field to store tags

    // Getters and setters
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public String getDescription() {
		return description;
	}

    public void setDescription(String description) {
        // Limit description to 255 characters if that's the column limit
        if (description.length() > 254) {
            this.description = description.substring(0, 254);
        } else {
            this.description = description;
        }
    }


	public boolean isDiscountActive() {
		return discountActive;
	}

	public void setDiscountActive(boolean discountActive) {
		this.discountActive = discountActive;
	}

	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public int getPreviousAmount() {
		return previousAmount;
	}

	public void setPreviousAmount(int previousAmount) {
		this.previousAmount = previousAmount;
	}

	public int getQuantity() {
		return quantity;
	}

//	public void setQuantity(int quantity) {
//		this.quantity = quantity;
//	}
	
    public void setQuantity(int quantity) {
        // Ensure quantity is non-negative
        if (quantity < 0) {
            this.quantity = 0;
        } else {
            this.quantity = quantity;
        }
    }

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	@ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;
    private int price;

    @JsonProperty
    @Column(name = "seller_name")
    private String sellerName;
    
    @JsonProperty
    @Column(name = "restaurant_name") // Updated column name
    private String restaurantName; // Changed field name

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Item() {
        super();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    
    private boolean available=true; // New field for availability status

    // Getters and setters
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    private boolean isVeg;  // New field for veg or non-veg status

    // Getters and Setters for the new field
    
    public boolean isVeg() {
        return isVeg;
    }

    public void setVeg(boolean isVeg) {
        this.isVeg = isVeg;
    }

	@Override
	public String toString() {
		return "Item [id=" + id +  ", quantity=" + quantity 
				+ ", category=" + category + ", name=" + name + ", price=" + price + ", sellerName=" + sellerName
				+ ", restaurantName=" + restaurantName + ", available=" + available + ", isVeg=" + isVeg + "]";
	}
    
}
