package com.example.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.practice.entity.OurUsers;

import java.util.List;
import java.util.Optional;
@Repository
public interface OurUserRepo extends JpaRepository<OurUsers, Integer> {
    Optional<OurUsers> findByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM OurUsers u WHERE u.role = 'seller'")
    Long countSellers();
    
    OurUsers findByRestaurantName(String restaurantName);


	Optional<OurUsers> findById(Long sellerId);
	
    Optional<OurUsers> findByEmailAndRole(String email, String role);

    List<OurUsers> findByEmailStartingWithAndRole(String name, String role);
    List<OurUsers> findByRestaurantNameStartingWithAndRole(String restaurantName, String role);

	List<OurUsers> findByRole(String string);

//	@Query("SELECT u FROM OurUsers u WHERE u.sellerName = :sellerName")
//	Optional<OurUsers> findBySellerName(@Param("sellerName") String sellerName);

}
