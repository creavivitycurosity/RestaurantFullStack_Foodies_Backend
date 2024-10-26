package com.example.practice.repository;

import com.example.practice.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
    // Additional query methods if needed
}
