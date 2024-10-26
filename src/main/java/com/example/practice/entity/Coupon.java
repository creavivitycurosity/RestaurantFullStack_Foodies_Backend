package com.example.practice.entity;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

//Coupon.java
@Entity
public class Coupon {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String name;
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

public double getAmount() {
	return amount;
}

public void setAmount(double amount) {
	this.amount = amount;
}

public LocalDate getExpiryDate() {
	return expiryDate;
}

public void setExpiryDate(LocalDate expiryDate) {
	this.expiryDate = expiryDate;
}

public List<OurUsers> getUsedByUsers() {
	return usedByUsers;
}

public void setUsedByUsers(List<OurUsers> usedByUsers) {
	this.usedByUsers = usedByUsers;
}

private double amount;
 private LocalDate expiryDate;

 @ManyToMany
 private List<OurUsers> usedByUsers;

 // Getters and Setters
}

