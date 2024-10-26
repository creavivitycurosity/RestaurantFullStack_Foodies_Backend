package com.example.practice.dto;

import java.util.List;

import com.example.practice.entity.ItemReview;

public class ItemReviewsResponse {
    private List<ItemReview> reviews;
    private double averageRating;

    public ItemReviewsResponse(List<ItemReview> reviews, double averageRating) {
        this.reviews = reviews;
        this.averageRating = averageRating;
    }

    public List<ItemReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ItemReview> reviews) {
        this.reviews = reviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}