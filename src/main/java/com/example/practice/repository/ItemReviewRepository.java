package com.example.practice.repository;

import com.example.practice.entity.ItemReview;
import com.example.practice.entity.Item;
import com.example.practice.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemReviewRepository extends JpaRepository<ItemReview, Long> {
    // Find all reviews for a specific item
    List<ItemReview> findByItem(Item item);

    // Find all reviews made by a specific user
    List<ItemReview> findByUser(OurUsers user);

    // Find a specific review by item and user
    ItemReview findByItemAndUser(Item item, OurUsers user);
    
    void deleteByItem(Item item);

	void deleteByItemId(Long id);

	List<ItemReview> findByItemId(Long id);

}
