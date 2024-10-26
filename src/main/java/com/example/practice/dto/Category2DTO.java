package com.example.practice.dto;

public class Category2DTO {
    private Long categoryId; 
    private String categoryName;
    private String image;
    private int highDiscount;
    private int lowDiscount;

    // Update constructor to include categoryId, highDiscount, and lowDiscount
    public Category2DTO(Long categoryId, String categoryName, String image, int highDiscount, int lowDiscount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.image = image;
        this.highDiscount = highDiscount;
        this.lowDiscount = lowDiscount;
    }

    // Getter and setter for categoryId
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    // Getter and setter for highDiscount
    public int getHighDiscount() {
        return highDiscount;
    }

    public void setHighDiscount(int highDiscount) {
        this.highDiscount = highDiscount;
    }

    // Getter and setter for lowDiscount
    public int getLowDiscount() {
        return lowDiscount;
    }

    public void setLowDiscount(int lowDiscount) {
        this.lowDiscount = lowDiscount;
    }

    // Existing getters and setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}