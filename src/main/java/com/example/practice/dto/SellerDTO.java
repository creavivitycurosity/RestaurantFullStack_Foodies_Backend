package com.example.practice.dto;

public class SellerDTO {
    private String sellerName;
    private String image;
    public SellerDTO() {
    	
    }

    public SellerDTO(String sellerName, String image) {
        this.sellerName = sellerName;
        this.image = image;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
