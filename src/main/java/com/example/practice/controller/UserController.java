package com.example.practice.controller;

import com.example.practice.dto.AddressDTO;
import com.example.practice.dto.UsersDTO;
import com.example.practice.entity.Orders;
import com.example.practice.entity.OurUsers;
import com.example.practice.entity.UserAddress;
import com.example.practice.repository.OrderRepository;
import com.example.practice.repository.OurUserRepo;
import com.example.practice.repository.UserAddressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private OurUserRepo userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;
    
    @GetMapping
    public List<OurUsers> getAllUsers() {
        return userRepository.findAll();
    }
//    @GetMapping("/me")
//    public ResponseEntity<OurUsers> getCurrentUser(@RequestParam String email) {
//        OurUsers user = userRepository.findByEmail(email)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//        return ResponseEntity.ok(user);
//    }
//    
//    
    
    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        Long userCount = userRepository.count();
        return ResponseEntity.ok(userCount);
    }
    
    
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(
        @RequestParam("image") MultipartFile image,
        @RequestParam("email") String email) {
        
        Optional<OurUsers> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            OurUsers user = optionalUser.get();
            try {
                user.setImage(image.getBytes());
                userRepository.save(user);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image.");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    

    @PutMapping("/{id}/status")
    public OurUsers toggleUserStatus(@PathVariable Integer id, @RequestParam boolean enabled) {
        Optional<OurUsers> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            OurUsers user = optionalUser.get();
            user.setEnabled(enabled);
            userRepository.save(user);
            return user;
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    @GetMapping("/orders/{email}")
    public ResponseEntity<List<Orders>> getOrdersByEmail(@PathVariable String email) {
        Optional<OurUsers> optionalUser = userRepository.findByEmail(email);
        if (optionalUser == null) {
            return ResponseEntity.notFound().build();
        }
        OurUsers user = optionalUser.get();

        
        List<Orders> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/image")
    public ResponseEntity<byte[]> getSellerImage(@RequestParam String email) {
        try {
            // Find the user by email and ensure they have the role "SELLER"
            OurUsers seller = userRepository.findByEmailAndRole(email, "SELLER")
                    .orElseThrow(() -> new IllegalArgumentException("Seller not found with the given email"));

            // Retrieve the image bytes from the seller
            byte[] image = seller.getImage();

            // Set headers for the image response
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg");

            // Return the image in the response
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Return a 404 status if the seller or image is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<UsersDTO> getCurrentUser(@RequestParam String email) {
        OurUsers user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setId(user.getId());
        usersDTO.setEmail(user.getEmail());
        usersDTO.setName(user.getName());
        usersDTO.setRestaurantName(user.getRestaurantName());
        usersDTO.setPassword(user.getPassword());
        usersDTO.setRole(user.getRole());
        usersDTO.setEnabled(user.isEnabled());
        usersDTO.setImage(user.getImage());

        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (UserAddress userAddress : user.getAddresses()) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setId(userAddress.getId());
            addressDTO.setArea(userAddress.getArea());
            addressDTO.setStreet(userAddress.getStreet());
            addressDTO.setCity(userAddress.getCity());
            addressDTO.setState(userAddress.getState());
            addressDTO.setCountry(userAddress.getCountry());
            addressDTO.setPincode(userAddress.getPincode());
            addressDTO.setNearbyLocation(userAddress.getNearbyLocation());
            addressDTOs.add(addressDTO);
        }
        
        usersDTO.setAddresses(addressDTOs); // Now setting List<AddressDTO>

        return ResponseEntity.ok(usersDTO);
    }

    
    @GetMapping("/me2")
    public ResponseEntity<UsersDTO> getCurrentUsers(@RequestParam String email) {
        OurUsers user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setId(user.getId());
        usersDTO.setEmail(user.getEmail());
        usersDTO.setName(user.getName());
        usersDTO.setRestaurantName(user.getRestaurantName());
        usersDTO.setPassword(user.getPassword());
        usersDTO.setRole(user.getRole());
        usersDTO.setEnabled(user.isEnabled());
        usersDTO.setImage(user.getImage());

        List<AddressDTO> addressDTOs = user.getAddresses().stream()
                .map(userAddress -> {
                    AddressDTO addressDTO = new AddressDTO();
                    addressDTO.setId(userAddress.getId());
                    addressDTO.setArea(userAddress.getArea());
                    addressDTO.setStreet(userAddress.getStreet());
                    addressDTO.setCity(userAddress.getCity());
                    addressDTO.setState(userAddress.getState());
                    addressDTO.setCountry(userAddress.getCountry());
                    addressDTO.setPincode(userAddress.getPincode());
                    addressDTO.setNearbyLocation(userAddress.getNearbyLocation());
                    return addressDTO;
                })
                .collect(Collectors.toList());

        usersDTO.setAddresses(addressDTOs);
        return ResponseEntity.ok(usersDTO);
    }

    @GetMapping("/api/users/{userId}/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressesForUser  (@PathVariable Integer userId) {
        OurUsers user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (UserAddress userAddress : user.getAddresses()) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setId(userAddress.getId());
            addressDTO.setArea(userAddress.getArea());
            addressDTO.setStreet(userAddress.getStreet());
            addressDTO.setCity(userAddress.getCity());
            addressDTO.setState(userAddress.getState());
            addressDTO.setCountry(userAddress.getCountry());
            addressDTO.setPincode(userAddress.getPincode());
            addressDTO.setNearbyLocation(userAddress.getNearbyLocation());
            addressDTOs.add(addressDTO);
        }

        return ResponseEntity.ok(addressDTOs);
    }

    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressDTO> addAddress (@PathVariable Integer userId, @RequestBody AddressDTO addressDTO) {
        OurUsers user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        UserAddress userAddress = new UserAddress();
        userAddress.setArea(addressDTO.getArea());
        userAddress.setStreet(addressDTO.getStreet());
        userAddress.setCity(addressDTO.getCity());
        userAddress.setState(addressDTO.getState());
        userAddress.setCountry(addressDTO.getCountry());
        userAddress.setPincode(addressDTO.getPincode());
        userAddress.setNearbyLocation(addressDTO.getNearbyLocation());
        userAddress.setUser(user);

        userAddressRepository.save(userAddress);

        return ResponseEntity.ok(addressDTO);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress (@PathVariable Integer addressId, @RequestBody AddressDTO addressDTO) {
        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        userAddress.setArea(addressDTO.getArea());
        userAddress.setStreet(addressDTO.getStreet());
        userAddress.setCity(addressDTO.getCity());
        userAddress.setState(addressDTO.getState());
        userAddress.setCountry(addressDTO.getCountry());
        userAddress.setPincode(addressDTO.getPincode());
        userAddress.setNearbyLocation(addressDTO.getNearbyLocation());

        userAddressRepository.save(userAddress);

        return ResponseEntity.ok(addressDTO);
    }
    
    
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress (@PathVariable Integer addressId) {
        userAddressRepository.deleteById(addressId);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("user/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress (@PathVariable Integer addressId) {
        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(userAddress.getId());
        addressDTO.setArea(userAddress.getArea());
        addressDTO.setStreet(userAddress.getStreet());
        addressDTO.setCity(userAddress.getCity());
        addressDTO.setState(userAddress.getState());
        addressDTO.setCountry(userAddress.getCountry());
        addressDTO.setPincode(userAddress.getPincode());
        addressDTO.setNearbyLocation(userAddress.getNearbyLocation());

        return ResponseEntity.ok(addressDTO);
    }
    
//    @PostMapping("/{userId}/addresses")
//    public ResponseEntity<UserAddress> addAddress(@PathVariable Integer userId, @RequestBody UserAddress address) {
//        // Find user by ID
//        Optional<OurUsers> userOptional = userRepository.findById(userId);
//        if (userOptional.isPresent()) {
//            OurUsers user = userOptional.get();
//            address.setUser(user);  // Set the user for the address
//            userAddressRepository.save(address);  // Save address
//            return ResponseEntity.ok(address);
//        } else {
//            return ResponseEntity.notFound().build();  // User not found
//        }
//    }
//    
//    
// // Update Address
//    @PutMapping("/addresses/{addressId}")
//    public ResponseEntity<UserAddress> updateAddress(@PathVariable Integer addressId, @RequestBody UserAddress updatedAddress) {
//        // Find the address by ID
//        Optional<UserAddress> addressOptional = userAddressRepository.findById(addressId);
//        if (addressOptional.isPresent()) {
//            UserAddress existingAddress = addressOptional.get();
//
//            // Update fields
//            existingAddress.setArea(updatedAddress.getArea());
//            existingAddress.setStreet(updatedAddress.getStreet());
//            existingAddress.setCity(updatedAddress.getCity());
//            existingAddress.setState(updatedAddress.getState());
//            existingAddress.setCountry(updatedAddress.getCountry());
//            existingAddress.setPincode(updatedAddress.getPincode());
//            existingAddress.setNearbyLocation(updatedAddress.getNearbyLocation());
//
//            // Save updated address
//            userAddressRepository.save(existingAddress);
//            return ResponseEntity.ok(existingAddress);
//        } else {
//            return ResponseEntity.notFound().build();  // Address not found
//        }
//    }
//
//    
// // Delete Address
//    @DeleteMapping("/addresses/{addressId}")
//    public ResponseEntity<Void> deleteAddress(@PathVariable Integer addressId) {
//        // Check if address exists
//        Optional<UserAddress> addressOptional = userAddressRepository.findById(addressId);
//        if (addressOptional.isPresent()) {
//            // Delete the address
//            userAddressRepository.deleteById(addressId);
//            return ResponseEntity.noContent().build();  // Success: No Content response
//        } else {
//            return ResponseEntity.notFound().build();  // Address not found
//        }
//    }
//    
// // Get all addresses for a user
//    @GetMapping("/api/users/{userId}/addresses")
//    public ResponseEntity<List<UserAddress>> getAddressesForUser(@PathVariable Integer userId) {
//        // Find the user by ID
//        Optional<OurUsers> userOptional = userRepository.findById(userId);
//        if (userOptional.isPresent()) {
//            OurUsers user = userOptional.get();
//            
//            // Get all addresses linked to the user
//            List<UserAddress> addresses = user.getAddresses();
//            
//            return ResponseEntity.ok(addresses);  // Return the list of addresses
//        } else {
//            return ResponseEntity.notFound().build();  // User not found
//        }
//    }
//

    
}
