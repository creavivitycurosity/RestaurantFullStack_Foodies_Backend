package com.example.practice.dto;

public class OrderItemResponseDTO2 {

    private Long id;
    private String name;
    private int price;
    private Long totalOrders;
    private double totalAmount;
    private Long totalOrdersToday;
    private double totalAmountToday;
    private Long totalOrdersThisWeek;
    private double totalAmountThisWeek;
    private Long totalOrdersThisMonth;
    private double totalAmountThisMonth;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalOrdersToday() {
        return totalOrdersToday;
    }

    public void setTotalOrdersToday(Long totalOrdersToday) {
        this.totalOrdersToday = totalOrdersToday;
    }

    public double getTotalAmountToday() {
        return totalAmountToday;
    }

    public void setTotalAmountToday(double totalAmountToday) {
        this.totalAmountToday = totalAmountToday;
    }

    public Long getTotalOrdersThisWeek() {
        return totalOrdersThisWeek;
    }

    public void setTotalOrdersThisWeek(Long totalOrdersThisWeek) {
        this.totalOrdersThisWeek = totalOrdersThisWeek;
    }

    public double getTotalAmountThisWeek() {
        return totalAmountThisWeek;
    }

    public void setTotalAmountThisWeek(double totalAmountThisWeek) {
        this.totalAmountThisWeek = totalAmountThisWeek;
    }

    public Long getTotalOrdersThisMonth() {
        return totalOrdersThisMonth;
    }

    public void setTotalOrdersThisMonth(Long totalOrdersThisMonth) {
        this.totalOrdersThisMonth = totalOrdersThisMonth;
    }

    public double getTotalAmountThisMonth() {
        return totalAmountThisMonth;
    }

    public void setTotalAmountThisMonth(double totalAmountThisMonth) {
        this.totalAmountThisMonth = totalAmountThisMonth;
    }
}
