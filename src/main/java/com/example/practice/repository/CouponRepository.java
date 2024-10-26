package com.example.practice.repository;
import com.example.practice.entity.Coupon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//CouponRepository.java
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
 Optional<Coupon> findByName(String name);
}
