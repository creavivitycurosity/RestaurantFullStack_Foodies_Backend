package com.example.practice.dto;

import com.example.practice.entity.Item;
import com.example.practice.entity.ItemReview;

public class ItemReviewResponse {
    private Item item;
    private ItemReview review;

  
    // Getters and Setters
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemReview getReview() {
        return review;
    }

    public void setReview(ItemReview review) {
        this.review = review;
    }
    
    // Additional field to expose user email in the DTO
    private String userEmail;

    // Constructor to map the review and user email
    public ItemReviewResponse(Item item, ItemReview review) {
        this.item = item;
        this.review = review;
        this.userEmail = review.getUser() != null ? review.getUser().getEmail() : "Email not available";
    }

    // Getters and Setters...
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
