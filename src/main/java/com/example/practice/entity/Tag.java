//package com.example.practice.entity;
//
//import java.util.List;
//import jakarta.persistence.*;
//
//@Entity
//public class Tag {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String tagName;  // Field for tag name
//
//    // Many-to-many relationship with items
//    @ManyToMany(mappedBy = "tags")  // Items own the relationship
//    private List<Item> items;
//
//    // Getters and setters...
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getTagName() {
//        return tagName;
//    }
//
//    public void setTagName(String tagName) {
//        this.tagName = tagName;
//    }
//
//    public List<Item> getItems() {
//        return items;
//    }
//
//    public void setItems(List<Item> items) {
//        this.items = items;
//    }
//}
