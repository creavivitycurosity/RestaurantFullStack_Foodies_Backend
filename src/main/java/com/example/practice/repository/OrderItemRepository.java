package com.example.practice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.practice.entity.Item;
import com.example.practice.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	List<OrderItem> findBySellerId(Long sellerId);

	List<OrderItem> findBySellerId(Integer sellerId);
	
//	@Query("SELECT COUNT(o) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt = (SELECT MAX(o2.order.createdAt) FROM OrderItem o2 WHERE o2.item = o.item)")
//	Long countByItem(@Param("item") Item item);
//	 @Query("SELECT SUM(o.quantity * o.item.price) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt = (SELECT MAX(o2.order.createdAt) FROM OrderItem o2 WHERE o2.item = o.item)")
//	 Double sumAmountByItem(@Param("item") Item item);
//	 @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt > :createdAt")
//	 Long countByItemAndCreatedAtAfter(@Param("item") Item item, @Param("createdAt") LocalDateTime createdAt);
//	 @Query("SELECT COALESCE(SUM(o.quantity * o.item.price), 0.0) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt > :createdAt")
//	 Double sumAmountByItemAndCreatedAtAfter(@Param("item") Item item, @Param("createdAt") LocalDateTime createdAt);
	 
	 @Query("SELECT o FROM OrderItem o WHERE o.item = :item AND o.order.createdAt > :createdAt")
	 List<OrderItem> findByItemAndCreatedAtAfter(@Param("item") Item item, @Param("createdAt") LocalDateTime createdAt);

	List<OrderItem> findBySellerIdAndItemId(Integer sellerId, Long itemId);
	
	 @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.item.id = :itemId AND oi.order.createdAt BETWEEN :startDate AND :endDate")
	    int countByItemIdAndCreatedAtBetween(@Param("itemId") Long itemId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.item.id = :itemId AND oi.order.createdAt BETWEEN :startDate AND :endDate")	    
	    
	    double sumAmountByItemIdAndCreatedAtBetween(@Param("itemId") Long itemId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
	    
	    
	    
	    
	    
	    
	    
	    
//	    
//	    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.item = :item")
//	    Long countByItem(@Param("item") Item item);
//
//	    @Query("SELECT SUM(o.quantity * o.item.price) FROM OrderItem o WHERE o.item = :item")
//	    Double sumAmountByItem(@Param("item") Item item);
//
	    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt > :createdAt")
	    Long countByItemAndCreatedAtAfter(@Param("item") Item item, @Param("createdAt") LocalDateTime createdAt);

	    @Query("SELECT COALESCE(SUM(o.quantity * o.item.price), 0.0) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt > :createdAt")
	    Double sumAmountByItemAndCreatedAtAfter(@Param("item") Item item, @Param("createdAt") LocalDateTime createdAt);

	    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.item = :item")
	    Long countByItem(@Param("item") Item item);

	    @Query("SELECT SUM(o.quantity * o.item.price) FROM OrderItem o WHERE o.item = :item")
	    Double sumAmountByItem(@Param("item") Item item);

	    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt >= :startDate AND o.order.createdAt <= :endDate")
	    Long countByItemAndCreatedAtBetween(@Param("item") Item item, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	    @Query("SELECT COALESCE(SUM(o.quantity * o.item.price), 0.0) FROM OrderItem o WHERE o.item = :item AND o.order.createdAt >= :startDate AND o.order.createdAt <= :endDate")
	    Double sumAmountByItemAndCreatedAtBetween(@Param("item") Item item, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

		List<OrderItem> findByItemId(Long id);

		void deleteByItemId(Long id);

}