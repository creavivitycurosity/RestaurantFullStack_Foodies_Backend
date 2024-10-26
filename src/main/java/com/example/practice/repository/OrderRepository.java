package com.example.practice.repository;

import com.example.practice.entity.Orders;
import com.example.practice.entity.OurUsers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findBypaymentId(String paymentId);
    List<Orders> findByUserId(Integer userId);
    @Modifying
    @Query("UPDATE Orders o SET o.status = :status WHERE o.id = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    @Query("SELECT COUNT(o) FROM Orders o")
    long countTotalOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Orders o")
    double sumTotalAmount();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    long countOrdersBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    double sumAmountBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    long countOrdersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    double sumAmountBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
//    @Query("SELECT o FROM Orders o JOIN o.orderItems i JOIN i.item s WHERE s.seller = :seller")
//    Optional<Orders> findByOrderItems_Item_Seller(@Param("seller") OurUsers seller); 
//    @Query("SELECT o FROM Orders o WHERE o.seller.name = :sellerName")
//    List<Orders> findBySellerName(@Param("sellerName") String sellerName);
    @Query("SELECT o FROM Orders o JOIN o.orderItems i WHERE i.seller = :seller")
    List<Orders> findByOrderItems_Seller(@Param("seller") OurUsers seller);
	List<Orders> findByUser(OurUsers user);
	List<Orders> findByUser(Optional<OurUsers> user);
    
}
