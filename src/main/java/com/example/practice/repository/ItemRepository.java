package com.example.practice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.practice.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	 @Query("SELECT i FROM Item i WHERE i.name LIKE :query%")
	    List<Item> findByNameStartingWith(String query);
	 
	   // Search by tags
	    @Query("SELECT i FROM Item i JOIN i.tags t WHERE t LIKE %:query%")
	    List<Item> findByTagContaining(String query); 
	 
//	 @Query("SELECT i FROM Item i WHERE i.name LIKE :query% OR :query MEMBER OF i.tags")
//	    List<Item> findByNameOrTagsStartingWith(String query);
	 
	 @Query("SELECT i FROM Item i JOIN i.tags t WHERE i.name LIKE :query% OR t LIKE :query%")
	    List<Item> findByNameOrTagStartingWith(@Param("query") String query);
//	    List<Item> findByCategory(String category);
	    
	    List<Item> findByCategory_Id(Long categoryId);
	    List<Item> findByFeaturedTrue();

	    List<Item> findBySellerName(String sellerName); // This method should work after renaming the field
	    List<Item> findByrestaurantName(String restaurantName); // This method should work after renaming the field

	    @Query("SELECT COUNT(DISTINCT i.sellerName) FROM Item i")
	    Long countDistinctSellers();
	    
	    @Query("SELECT DISTINCT i.sellerName FROM Item i WHERE i.sellerName IS NOT NULL")
	    List<String> findAllDistinctSellerNames();
	    
	    @Query("SELECT DISTINCT i.sellerName FROM Item i WHERE i.sellerName IS NOT NULL")
	    List<String> findDistinctSellerNames();
	    @Query("SELECT DISTINCT i.restaurantName FROM Item i WHERE i.restaurantName IS NOT NULL")
	    List<String> findDistinctRestaurantNames();
		List<Item> findByName(String name);

		List<Item> findByNameStartingWithIgnoreCase(String name);

		List<Item> findByIsVeg(boolean b);


}
