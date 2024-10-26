//package com.example.practice.dto;
//
//public class SellerInfoDTO {
//    private String sellerName;
//    private int totalItems;
//
//    public SellerInfoDTO(String sellerName, int totalItems) {
//        this.sellerName = sellerName;
//        this.totalItems = totalItems;
//    }
//
//    public String getSellerName() {
//        return sellerName;
//    }
//
//    public void setSellerName(String sellerName) {
//        this.sellerName = sellerName;
//    }
//
//    public int getTotalItems() {
//        return totalItems;
//    }
//
//    public void setTotalItems(int totalItems) {
//        this.totalItems = totalItems;
//    }
//}
package com.example.practice.dto;

public class SellerInfoDTO {
    private String sellerName;
    private int totalItems;
    private String restaurantName;

    public SellerInfoDTO(String sellerName, int totalItems, String restaurantName) {
        this.sellerName = sellerName;
        this.totalItems = totalItems;
        this.restaurantName = restaurantName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
