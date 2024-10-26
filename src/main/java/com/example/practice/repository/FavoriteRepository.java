package com.example.practice.repository;

import com.example.practice.entity.Favorite;
import com.example.practice.entity.Item;
import com.example.practice.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(OurUsers user);

    Optional<Favorite> findByUserAndItem(OurUsers user, Item item);
    
    void deleteByItem(Item item);

	List<Favorite> findByItem(Item item);

	void deleteByItemId(Long id);

	List<Favorite> findByItemId(Long id);

}
