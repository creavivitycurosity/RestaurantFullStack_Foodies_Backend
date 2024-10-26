package com.example.practice.controller;
import java.time.LocalDateTime;import org.slf4j.Logger;import java.time.DayOfWeek;
import com.example.practice.dto.AddressDTO;

import com.example.practice.entity.UserAddress;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import java.util.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.temporal.ChronoUnit;
import com.example.practice.dto.OrderRequestDTO;
import com.example.practice.dto.OrderResponseDTO;
import com.example.practice.dto.OrderedItemDTO2;
import com.example.practice.dto.ProductDTO;
import com.example.practice.dto.CouponRequestDTO;
import com.example.practice.dto.CustomDateResponse;
import com.example.practice.dto.OrderDTO2;
import com.example.practice.dto.OrderItemResponseDTO;
import com.example.practice.dto.OrderItemResponseDTO2;
import com.example.practice.dto.ReqRes;
import com.example.practice.dto.SellerSummaryDTO;
import com.example.practice.dto.SellerData;
import com.example.practice.entity.Coupon;
import com.example.practice.entity.Item;
import com.example.practice.service.AuthService;
import com.example.practice.service.JWTUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import com.example.practice.service.JWTUtils;
import com.example.practice.entity.OrderItem;
import com.example.practice.entity.Orders;
import com.example.practice.entity.OurUsers;
import com.example.practice.repository.ItemRepository;
import com.example.practice.repository.OrderItemRepository;
import com.example.practice.repository.OrderRepository;
import com.example.practice.repository.CouponRepository;

import com.example.practice.repository.OurUserRepo;
import com.example.practice.repository.UserAddressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import com.example.practice.exceptions.ResourceNotFoundException;

import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
@RestController
@RequestMapping("/orders")
public class OrderController {
	@Autowired
    private JWTUtils jwtUtils;
	
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private OurUserRepo userRepository;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserAddressRepository userAddressRepository;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    
    @GetMapping("/admin/sellers")
    public ResponseEntity<List<SellerData>> getAllSellersData() {
        List<OurUsers> sellers = userRepository.findByRole("SELLER");
        List<SellerData> sellerDataList = new ArrayList<>();

        for (OurUsers seller : sellers) {
            SellerData sellerData = new SellerData();
            sellerData.setSellerName(seller.getName());
            sellerData.setRestaurantName(seller.getRestaurantName());
            sellerData.setTotalIncome(getTotalIncome(seller.getId()));
            sellerData.setTotalOrders(getTotalOrders(seller.getId()));
            sellerData.setMonthlyIncome(getMonthlyIncome(seller.getId()));
            sellerData.setMonthlyOrders(getMonthlyOrders(seller.getId()));
            sellerDataList.add(sellerData);
        }

        return ResponseEntity.ok(sellerDataList);
    }
    
    
    
    
    private double getTotalIncome(Integer sellerId) {
        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
        double totalIncome = orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();
        return totalIncome;
    }

    private long getTotalOrders(Integer sellerId) {
        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
        Map<Orders, List<OrderItem>> groupedOrders = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder));
        return groupedOrders.size();
    }

    private double getMonthlyIncome(Integer sellerId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<OrderItem> monthlyOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                .collect(Collectors.toList());
        double monthlyIncome = monthlyOrderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();
        return monthlyIncome;
    }

    private long getMonthlyOrders(Integer sellerId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<OrderItem> monthlyOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                .collect(Collectors.toList());
        Map<Orders, List<OrderItem>> groupedOrders = monthlyOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder));
        return groupedOrders.size();
    }
    
    
    @GetMapping("/orders/sellersSummary")
    public ResponseEntity<List<SellerSummaryDTO>> getSellersSummary() {
        // Get all sellers
        List<OurUsers> sellers = userRepository.findByRole("SELLER");

        List<SellerSummaryDTO> summaryList = new ArrayList<>();

        for (OurUsers seller : sellers) {
            List<OrderItem> orderItems = orderItemRepository.findBySellerId(seller.getId());

            // Total income and total orders
            double totalIncome = orderItems.stream()
                    .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                    .sum();
            long totalOrders = orderItems.stream().map(OrderItem::getOrder).distinct().count();

            // Monthly income and monthly orders
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            double monthlyIncome = orderItems.stream()
                    .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                    .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                    .sum();
            long monthlyOrders = orderItems.stream()
                    .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                    .map(OrderItem::getOrder)
                    .distinct()
                    .count();

            SellerSummaryDTO summary = new SellerSummaryDTO(seller.getName(), seller.getRestaurantName(), totalIncome, totalOrders, monthlyIncome, monthlyOrders);
            summaryList.add(summary);
        }

        return ResponseEntity.ok(summaryList);
    }

    
    
    
    @GetMapping("/orders/sellersSummary2")
    public ResponseEntity<List<SellerSummaryDTO>> getSellersSummary2() {
        List<OurUsers> sellers = userRepository.findByRole("SELLER");

        List<SellerSummaryDTO> summaryList = sellers.stream()
                .map(seller -> {
                    List<OrderItem> orderItems = orderItemRepository.findBySellerId(seller.getId());

                    double totalIncome = orderItems.stream()
                            .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                            .sum();
                    long totalOrders = orderItems.stream().map(OrderItem::getOrder).distinct().count();

                    LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
                    double monthlyIncome = orderItems.stream()
                            .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                            .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                            .sum();
                    long monthlyOrders = orderItems.stream()
                            .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                            .map(OrderItem::getOrder)
                            .distinct()
                            .count();

                    return new SellerSummaryDTO(seller.getName(), seller.getRestaurantName(), totalIncome, totalOrders, monthlyIncome, monthlyOrders);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaryList);
    }
    
    @GetMapping("/orders/sellerDateRangeOrders2/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getOrdersByDateRange(
        @PathVariable String sellerEmail,
        @PathVariable Integer sellerId,
        @RequestParam("startDate") String startDateStr,
        @RequestParam("endDate") String endDateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Parse start and end dates
        LocalDateTime startDate = OffsetDateTime.parse(startDateStr, formatter).toLocalDateTime();
        LocalDateTime endDate = OffsetDateTime.parse(endDateStr, formatter).toLocalDateTime();

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Find all order items by seller in the given date range
        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt() != null &&
                                     orderItem.getOrder().getCreatedAt().isAfter(startDate) &&
                                     orderItem.getOrder().getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());

        // Group order items by their parent order and calculate the totals
        Map<Orders, List<OrderItem>> groupedOrders = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder));

        double totalAmount = groupedOrders.values().stream()
                .mapToDouble(orderItemList -> orderItemList.stream()
                        .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                        .sum())
                .sum();

        long totalOrders = groupedOrders.size(); // Distinct orders

        return ResponseEntity.ok(Map.of(
            "totalOrders", (double) totalOrders,
            "totalAmount", totalAmount
        ));
    }
    
    @GetMapping("/orders/seller/allOrders3/{sellerEmail}")
    public ResponseEntity<Long> getAllOrders3(@PathVariable String sellerEmail) {
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        List<Orders> orders = orderRepository.findByOrderItems_Seller(seller);

        // Get distinct orders
        long totalOrders = orders.stream()
                .distinct()
                .count();

        return ResponseEntity.ok(totalOrders);
    }

    @GetMapping("/orders/sellerTotalAmount3/{sellerEmail}/{sellerId}")
    public ResponseEntity<Double> getTotalAmount(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);

        // Group order items by their order and sum the total amount
        Map<Orders, List<OrderItem>> groupedOrders = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder));

        double totalAmount = groupedOrders.values().stream()
                .mapToDouble(orderItemList -> orderItemList.stream()
                        .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                        .sum())
                .sum();

        return ResponseEntity.ok(totalAmount);
    }


    @GetMapping("/orders/sellerweeklyOrders3/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getWeeklyOrders(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Filter items ordered in the last week
        List<OrderItem> weeklyOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneWeekAgo))
                .collect(Collectors.toList());

        // Group by orders
        Map<Orders, List<OrderItem>> groupedOrders = weeklyOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder));

        double weeklyAmount = groupedOrders.values().stream()
                .mapToDouble(orderItemList -> orderItemList.stream()
                        .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                        .sum())
                .sum();

        long totalOrders = groupedOrders.size(); // Distinct orders

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", weeklyAmount
        ));
    }

    
    @GetMapping("/orders/sellermonthlyOrders3/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getMonthlyOrders(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Filter items ordered in the last month
        List<OrderItem> monthlyOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                .collect(Collectors.toList());

        // Group by orders
        Map<Orders, List<OrderItem>> groupedOrders = monthlyOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder));

        double monthlyAmount = groupedOrders.values().stream()
                .mapToDouble(orderItemList -> orderItemList.stream()
                        .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                        .sum())
                .sum();

        long totalOrders = groupedOrders.size(); // Distinct orders

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", monthlyAmount
        ));
    }

    
    @GetMapping("/orders/sellertodayOrders3/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getTodayOrders(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Filter items ordered today
        List<OrderItem> todayOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(startOfDay))
                .collect(Collectors.toList());

        // Group by orders
        Map<Orders, List<OrderItem>> groupedOrders = todayOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrder));

        double todayAmount = groupedOrders.values().stream()
                .mapToDouble(orderItemList -> orderItemList.stream()
                        .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                        .sum())
                .sum();

        long totalOrders = groupedOrders.size(); // Distinct orders

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", todayAmount
        ));
    }

//    @PostMapping("/orders")
//    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest) {
//        Orders order = new Orders();
//        order.setUser(userRepository.findByEmail(orderRequest.getUser().getEmail())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
//        order.setPaymentId(orderRequest.getPaymentId());
//        order.setPayerId(orderRequest.getPayerId());
//        order.setStatus("NEW");
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (ProductDTO productDTO : orderRequest.getProduct()) {
//            OrderItem orderItem = new OrderItem();
//            Item item = itemRepository.findById(productDTO.getId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
//            orderItem.setItem(item);
//            orderItem.setQuantity(productDTO.getQuantity());
//            orderItem.setName(item.getName());
//            orderItem.setPrice(item.getPrice());
//            orderItem.setSeller(userRepository.findById(productDTO.getSellerId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found")));
//            orderItem.setOrder(order);
//
//            orderItems.add(orderItem);
//        }
//
//        order.setOrderItems(orderItems);
//        orderRepository.save(order);
//
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest) {
        // Log the incoming request for debugging

        // Fetch the user who placed the order
        Orders order = new Orders();
        order.setUser(userRepository.findByEmail(orderRequest.getUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        order.setPaymentId(orderRequest.getPaymentId());
        order.setPayerId(orderRequest.getPayerId());
        order.setStatus("pending");
        order.setCreatedAt(LocalDateTime.now()); // Set the creation time
        
        // Fetch the selected address and set it to the order
        UserAddress selectedAddress = userAddressRepository.findById(orderRequest.getOrderAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        order.setOrderAddress(selectedAddress);

        List<OrderItem> orderItems = new ArrayList<>();
        for (ProductDTO productDTO : orderRequest.getProduct()) {

            // Fetch the item details
            Item item = itemRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setName(item.getName());
            orderItem.setPrice(item.getPrice());
            orderItem.setStatus("pending"); // Set status to pending

            // Log the seller details
            String sellerEmail = productDTO.getSellerName();

            // Fetch the seller details
            OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
            orderItem.setSeller(seller);
            orderItem.setOrder(order);

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        return ResponseEntity.ok().build();
    }
    
    
    @GetMapping("/orders/adminTotalOrdersAndAmount4")
    public ResponseEntity<Map<String, Double>> getTotalOrdersAndAmount() {
        List<Orders> allOrders = orderRepository.findAll(); // Fetch all orders

        long totalOrders = allOrders.size(); // Total number of orders

        double totalAmount = allOrders.stream()  // Calculate total amount across all orders
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", totalAmount
        ));
    }
    
    @GetMapping("/orders/adminWeeklyOrdersAndAmount4")
    public ResponseEntity<Map<String, Double>> getWeeklyOrdersAndAmount() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        List<Orders> weeklyOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt().isAfter(oneWeekAgo))
                .collect(Collectors.toList());

        long totalOrders = weeklyOrders.size(); // Total number of weekly orders

        double totalAmount = weeklyOrders.stream()  // Calculate total amount for the week
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", totalAmount
        ));
    }

    
    @GetMapping("/orders/adminMonthlyOrdersAndAmount4")
    public ResponseEntity<Map<String, Double>> getMonthlyOrdersAndAmount() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        List<Orders> monthlyOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt().isAfter(oneMonthAgo))
                .collect(Collectors.toList());

        long totalOrders = monthlyOrders.size(); // Total number of monthly orders

        double totalAmount = monthlyOrders.stream()  // Calculate total amount for the month
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", totalAmount
        ));
    }

    
    @GetMapping("/orders/adminTodayOrdersAndAmount4")
    public ResponseEntity<Map<String, Double>> getTodayOrdersAndAmount() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

        List<Orders> todayOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt().isAfter(startOfDay))
                .collect(Collectors.toList());

        long totalOrders = todayOrders.size(); // Total number of today's orders

        double totalAmount = todayOrders.stream()  // Calculate total amount for today
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", totalAmount
        ));
    }

    
    @GetMapping("/orders/adminCustomDateRangeOrdersAndAmount4")
    public ResponseEntity<Map<String, Double>> getOrdersByDateRange(
        @RequestParam("startDate") String startDateStr,
        @RequestParam("endDate") String endDateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Parse start and end dates
        LocalDateTime startDate = OffsetDateTime.parse(startDateStr, formatter).toLocalDateTime();
        LocalDateTime endDate = OffsetDateTime.parse(endDateStr, formatter).toLocalDateTime();

        List<Orders> ordersInRange = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt().isAfter(startDate) && order.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());

        long totalOrders = ordersInRange.size(); // Total number of orders in the date range

        double totalAmount = ordersInRange.stream()  // Calculate total amount for the date range
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", totalAmount
        ));
    }
    
    @GetMapping("/orders/adminCustomDateRangeOrdersAndAmount5")
    public ResponseEntity<Map<String, Double>> getOrdersByDateRange5(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Parse start and end dates
        LocalDateTime startDate = OffsetDateTime.parse(startDateStr, formatter).toLocalDateTime();
        LocalDateTime endDate = OffsetDateTime.parse(endDateStr, formatter).toLocalDateTime();

        // Find all orders in the given date range
        List<Orders> orders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null &&
                                 order.getCreatedAt().isAfter(startDate) &&
                                 order.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());

        // Calculate the totals
        double totalAmount = orders.stream()
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        long totalOrders = orders.size(); // Total number of orders

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", totalAmount
        ));
    }

    @GetMapping("/orders/adminCustomDateRangeOrdersAndAmount6")
    public ResponseEntity<Map<String, Double>> getOrdersByDateRange6(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Parse start and end dates
        LocalDateTime startDate = OffsetDateTime.parse(startDateStr, formatter).toLocalDateTime();
        LocalDateTime endDate = OffsetDateTime.parse(endDateStr, formatter).toLocalDateTime();

        // Fetch all orders within the specified date range
        List<Orders> ordersInRange = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null &&
                        order.getCreatedAt().isAfter(startDate) &&
                        order.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());

        // Grouping orders to calculate total amount
        double totalAmount = ordersInRange.stream()
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        long totalOrders = ordersInRange.size(); // Total distinct orders in the date range

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", totalAmount
        ));
    }


//    @GetMapping("/orders/seller/{sellerId}")
//    public ResponseEntity<List<OrderItem>> getOrdersBySeller(@PathVariable Long sellerId) {
//        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
//        return ResponseEntity.ok(orderItems);
//    }
    
//    @GetMapping("/orders/seller/{sellerEmail}")
//    public ResponseEntity<List<OrderResponseDTO>> getOrdersBySellerEmail(@PathVariable String sellerEmail) {
//        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
//              .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
//
//        List<Orders> orders = orderRepository.findByOrderItems_Seller(seller);
//
//        if (!orders.isEmpty()) {
//            List<OrderResponseDTO> orderResponseDTOs = orders.stream()
//                  .map(order -> {
//                       OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
//                       orderResponseDTO.setId(order.getId());
//                       orderResponseDTO.setCreatedAt(order.getCreatedAt());
//                       orderResponseDTO.setStatus(order.getStatus());
//                       orderResponseDTO.setTotalAmount(order.getTotalAmount());
//
//                       List<OrderItemResponseDTO> orderItemResponseDTOs = order.getOrderItems().stream()
//                             .map(orderItem -> {
//                                  OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO();
//                                  orderItemResponseDTO.setId(orderItem.getId());
//                                  orderItemResponseDTO.setName(orderItem.getName());
//                                  orderItemResponseDTO.setPrice(orderItem.getPrice());
//                                  orderItemResponseDTO.setQuantity(orderItem.getQuantity());
//                                  return orderItemResponseDTO;
//                              })
//                             .collect(Collectors.toList());
//
//                       orderResponseDTO.setOrderItems(orderItemResponseDTOs);
//                       return orderResponseDTO;
//                   })
//                  .collect(Collectors.toList());
//
//            return ResponseEntity.ok(orderResponseDTOs);
//        } else {
//            return ResponseEntity.noContent().build();
//        }
//    } 
    
    @GetMapping("/orders/seller/{sellerEmail}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersBySellerEmail(@PathVariable String sellerEmail) {
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
              .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        List<Orders> orders = orderRepository.findByOrderItems_Seller(seller);

        if (!orders.isEmpty()) {
            List<OrderResponseDTO> orderResponseDTOs = orders.stream()
                  .map(order -> {
                       OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
                       orderResponseDTO.setId(order.getId());
                       orderResponseDTO.setCreatedAt(order.getCreatedAt());
                       orderResponseDTO.setStatus(order.getStatus());
                       orderResponseDTO.setTotalAmount(order.getTotalAmount());
                       orderResponseDTO.setUserEmail(order.getUser().getEmail()); // Set the userEmail property

                       
                       
                       
                       // Map the order address to AddressDTO
                       if (order.getOrderAddress() != null) {
                           AddressDTO addressDTO = new AddressDTO();
                           addressDTO.setStreet(order.getOrderAddress().getStreet());
                           addressDTO.setCity(order.getOrderAddress().getCity());
                           addressDTO.setState(order.getOrderAddress().getState());
                           addressDTO.setCountry(order.getOrderAddress().getCountry());
                           addressDTO.setPincode(order.getOrderAddress().getPincode());
                           addressDTO.setArea(order.getOrderAddress().getArea());
                           addressDTO.setNearbyLocation(order.getOrderAddress().getNearbyLocation());

                           // Set the AddressDTO in the OrderResponseDTO
                           orderResponseDTO.setOrderAddress(addressDTO);
                       }
                       
                       
                       List<OrderItemResponseDTO> orderItemResponseDTOs = order.getOrderItems().stream()
                             .filter(orderItem -> orderItem.getSeller().equals(seller)) // Add this filter
                             .map(orderItem -> {
                                  OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO();
                                  orderItemResponseDTO.setId(orderItem.getId());
                                  orderItemResponseDTO.setItemId(orderItem.getItem().getId());  // Set the actual item ID here
                                  orderItemResponseDTO.setName(orderItem.getName());
                                  orderItemResponseDTO.setPrice(orderItem.getPrice());
                                  orderItemResponseDTO.setQuantity(orderItem.getQuantity());
                                  orderItemResponseDTO.setStatus(orderItem.getStatus());

                                  return orderItemResponseDTO;
                              })
                             .collect(Collectors.toList());

                       orderResponseDTO.setOrderItems(orderItemResponseDTOs);
                       return orderResponseDTO;
                  })
                  .collect(Collectors.toList());

            return ResponseEntity.ok(orderResponseDTOs);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    @GetMapping("/refund-status/{orderItemId}")
    public ResponseEntity<String> getRefundStatus(@PathVariable Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        
        return ResponseEntity.ok(orderItem.getRefundStatus());
    }

    
    @PutMapping("/seller/update-refund-status/{orderItemId}")
    public ResponseEntity<String> updateRefundStatus(
            @PathVariable Long orderItemId,
            @RequestBody String newRefundStatus) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));

        orderItem.setRefundStatus(newRefundStatus);
        orderItemRepository.save(orderItem);

        return ResponseEntity.ok("Refund status updated successfully");
    }

    
    @PutMapping("seller/{orderId}/status")
    public ResponseEntity<String> updateOrderStatusSeller(@PathVariable Long orderId, @RequestBody Map<String, String> statusMap, @RequestHeader("Authorization") String token) {
        try {
            // Extract the JWT token without "Bearer " prefix
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractUsername(jwtToken);
            System.out.println("Extracted email: " + email);

            // Retrieve the user from the database
            OurUsers user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Retrieved user: " + user.getEmail());

            // Retrieve the order from the database
            Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            System.out.println("Retrieved order: " + order.getId() + " placed by: " + order.getUser().getEmail());

            // Check if the user is a seller and the owner of the order
//            if (!user.getRole().equals("SELLER") || !order.getSeller().getId().equals(user.getId())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this order");
//            }

            // Update the order status
            String status = statusMap.get("status");
            order.setStatus(status);
            orderRepository.save(order);

            return ResponseEntity.ok("Order status updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order status");
        }
    }
    
    @PutMapping("seller/{orderId}/status2")
    public ResponseEntity<String> updateOrderStatusSeller2(@PathVariable Long orderId, @RequestBody Map<String, String> statusMap, @RequestHeader("Authorization") String token) {
        try {
            // Extract the JWT token without "Bearer " prefix
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractUsername(jwtToken);
            System.out.println("Extracted email: " + email);

            // Retrieve the user from the database
            OurUsers user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Retrieved user: " + user.getEmail());

            // Retrieve the order from the database
            Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            System.out.println("Retrieved order: " + order.getId() + " placed by: " + order.getUser().getEmail());

            // Check if the user is a seller and the owner of the order
            // Uncomment and adjust as needed
            // if (!user.getRole().equals("SELLER") || !order.getSeller().getId().equals(user.getId())) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this order");
            // }

            // Update the order status
            String status = statusMap.get("status");
            System.out.println("Received status: " + status); // Debugging line

            if ("cancelled".equals(status)) {
                order.setStatus("cancelled by seller");
                orderRepository.save(order);
                LOGGER.info("Order status changed to: {}", status);
                System.out.println("hi hello cancel " + status); // Debugging line
            } else {
                LOGGER.info("Order status changed to: {}", status);
                System.out.println("hi hello " + status); // Debugging line
                order.setStatus(status);
                orderRepository.save(order);  
            }

            return ResponseEntity.ok("Order status updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order status");
        }
    }
    @PutMapping("user/{orderId}/item/{itemId}/status")
    public ResponseEntity<String> updateUserOrderItemStatus(@PathVariable Long orderId, @PathVariable Long itemId, @RequestHeader("Authorization") String token) {
        try {
            // Extract the JWT token without "Bearer " prefix
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractUsername(jwtToken);
            System.out.println("Extracted email: " + email);

            // Retrieve the user from the database
            OurUsers user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Retrieved user: " + user.getEmail());

            // Retrieve the order from the database
            Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            System.out.println("Retrieved order: " + order.getId() + " placed by: " + order.getUser().getEmail());

            System.out.println("Order contains the following items:");
            order.getOrderItems().forEach(orderItem -> {
                System.out.println("OrderItem ID: " + orderItem.getId() + ", Item ID: " + orderItem.getItem().getId());
            });
            
            // Retrieve the order item from the database
            OrderItem orderItem = order.getOrderItems().stream()
            	    .filter(item -> item.getItem().getId().longValue() == itemId.longValue())  // Ensuring both are Long
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Order item not found"));
            System.out.println("Retrieved order item: " + orderItem.getId() + " for order: " + order.getId());

            
            String role = jwtUtils.extractRole(jwtToken);

            	orderItem.setStatus("cancelled by user");
                orderItemRepository.save(orderItem);
            	
            
            // Update the order item status
          

            return ResponseEntity.ok("Order item status updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order item status");
        }
    }


    @PutMapping("seller/{orderId}/item/{itemId}/status")
    public ResponseEntity<String> updateOrderItemStatus(@PathVariable Long orderId, @PathVariable Long itemId, @RequestBody Map<String, String> statusMap, @RequestHeader("Authorization") String token) {
        try {
            // Extract the JWT token without "Bearer " prefix
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractUsername(jwtToken);
            System.out.println("Extracted email: " + email);

            // Retrieve the user from the database
            OurUsers user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Retrieved user: " + user.getEmail());

            // Retrieve the order from the database
            Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            System.out.println("Retrieved order: " + order.getId() + " placed by: " + order.getUser().getEmail());

            // Retrieve the order item from the database
            OrderItem orderItem = order.getOrderItems().stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Order item not found"));
            System.out.println("Retrieved order item: " + orderItem.getId() + " for order: " + order.getId());

            // Update the order item status
            String status = statusMap.get("status");
    

            if ("cancelled".equals(status)) {
                orderItem.setStatus("cancelled by seller");
                orderItemRepository.save(orderItem);
                LOGGER.info("Order status changed to: {}", status);
                System.out.println("hi hello cancel " + status); // Debugging line
            } else {
                LOGGER.info("Order status changed to: {}", status);
                System.out.println("hi hello " + status); // Debugging line
                orderItem.setStatus(status);
                orderItemRepository.save(orderItem); 
            }
    
            
            
            
            return ResponseEntity.ok("Order item status updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order item status");
        }
    }

    @GetMapping("/order/{orderId}/item/{itemId}/status")
    public ResponseEntity<String> getOrderItemStatus(@PathVariable Long orderId, @PathVariable Long itemId, @RequestHeader("Authorization") String token) {
        try {
            // Extract the JWT token without "Bearer " prefix
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractUsername(jwtToken);
            System.out.println("Extracted email: " + email);

            // Retrieve the user from the database
            OurUsers user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Retrieved user: " + user.getEmail());

            // Retrieve the order from the database
            Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            System.out.println("Retrieved order: " + order.getId() + " placed by: " + order.getUser().getEmail());

            // Retrieve the order item from the database
            OrderItem orderItem = order.getOrderItems().stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Order item not found"));
            System.out.println("Retrieved order item: " + orderItem.getId() + " for order: " + order.getId());

            return ResponseEntity.ok(orderItem.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving order item status");
        }
    }
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @PostMapping("/placeOrder")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequestDTO orderRequest, Authentication authentication) {
        OurUsers user = userRepository.findByEmail(orderRequest.getUser().getEmail()).orElseThrow();

        List<OrderItem> orderItems = orderRequest.getProduct().stream()
                .map(product -> {
                    Item dbItem = itemRepository.findById(product.getId()).orElseThrow();
                    return new OrderItem(dbItem, product.getQuantity()); // Pass quantity here
                })
                .collect(Collectors.toList());

        Orders order = new Orders(user, orderItems);
        order.setStatus("pending"); // Explicitly set status to pending
        order.setPaymentId(orderRequest.getPaymentId()); // Set paymentId
        order.setPayerId(orderRequest.getPayerId());     // Set payerId
        orderRepository.save(order);

        return ResponseEntity.ok("Order placed successfully");
    }


//    
//    @GetMapping("/myOrders")
//    public ResponseEntity<List<Orders>> getMyOrders(@RequestHeader("Authorization") String token) {
//        String email = jwtUtils.extractUsername(token.substring(7)); // Remove "Bearer " from the token
//        OurUsers user = userRepository.findByEmail(email).orElseThrow();
//        List<Orders> orders = orderRepository.findByUserId(user.getId());
//        return ResponseEntity.ok(orders);
//    }
    
 // OrderController.java
    @GetMapping("/seller-id/{sellerEmail}")
    public ResponseEntity<Integer> getSellerId(@PathVariable String sellerEmail) {
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return ResponseEntity.ok(seller.getId());
    }

    @GetMapping("/allOrders2")
    public ResponseEntity<List<Orders>> getAllOrders() {
        List<Orders> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("seller/allOrders/{sellerEmail}")
    public ResponseEntity<List<Orders>> getAllOrdersss(@PathVariable String sellerEmail) {
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        List<Orders> orders = orderRepository.findByOrderItems_Seller(seller);
        return ResponseEntity.ok(orders);
    }
    
    
    @GetMapping("/myOrders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(@RequestHeader("Authorization") String token) {
        String email = jwtUtils.extractUsername(token.substring(7)); // Remove "Bearer " from the token
        OurUsers user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Orders> orders = orderRepository.findByUserId(user.getId());

        List<OrderResponseDTO> orderResponseDTOs = orders.stream().map(order -> {
            OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
            orderResponseDTO.setId(order.getId());
            orderResponseDTO.setCreatedAt(order.getCreatedAt());
            orderResponseDTO.setStatus(order.getStatus());
            orderResponseDTO.setTotalAmount(order.getTotalAmount());
            orderResponseDTO.setUserEmail(order.getUser().getEmail());

            List<OrderItemResponseDTO> orderItemResponseDTOs = order.getOrderItems().stream().map(orderItem -> {
                OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO();
                if (orderItem.getItem()!= null) {

                orderItemResponseDTO.setId(orderItem.getItem().getId()); // Get the actual item ID
                }
                orderItemResponseDTO.setName(orderItem.getName());
                orderItemResponseDTO.setPrice(orderItem.getPrice());
                orderItemResponseDTO.setQuantity(orderItem.getQuantity());
                orderItemResponseDTO.setStatus(orderItem.getStatus());
                return orderItemResponseDTO;
            }).collect(Collectors.toList());

            orderResponseDTO.setOrderItems(orderItemResponseDTOs);
            return orderResponseDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(orderResponseDTOs);
    }

    @GetMapping("/totalAmount")
    public ResponseEntity<Double> getTotalAmount() {
        double totalAmount = orderRepository.findAll().stream()
                .mapToDouble(Orders::getTotalAmount)
                .sum();
        return ResponseEntity.ok(totalAmount);
    }
    
    @GetMapping("seller/allOrders2/{sellerEmail}")
    public ResponseEntity<Long> getAllOrdersss2(@PathVariable String sellerEmail) {
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        List<Orders> orders = orderRepository.findByOrderItems_Seller(seller);

        // Get distinct orders to avoid counting duplicates
        long totalOrders = orders.stream()
                .distinct()
                .count();

        return ResponseEntity.ok(totalOrders);
    }
    
    @GetMapping("sellermonthlyOrders/monthlyOrders2/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getMonthlyOrdersss2(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Get all OrderItems for the Seller within the last month
        List<OrderItem> monthlyOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt() != null && orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                .collect(Collectors.toList());

        // Calculate total monthly amount
        double monthlyAmount = monthlyOrderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();

        // Get distinct orders
        long totalOrders = monthlyOrderItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .count();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", monthlyAmount
        ));
    }
    
    @GetMapping("sellerweeklyOrders/weeklyOrders2/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getWeeklyOrdersss2(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Get all OrderItems for the Seller within the last week
        List<OrderItem> weeklyOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt() != null && orderItem.getOrder().getCreatedAt().isAfter(oneWeekAgo))
                .collect(Collectors.toList());

        // Calculate total weekly amount
        double weeklyAmount = weeklyOrderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();

        // Get distinct orders (to avoid counting the same order multiple times)
        long totalOrders = weeklyOrderItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .count();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", weeklyAmount
        ));
    }



    
//    @GetMapping("seller/totalAmount/{sellerEmail}")
//    public ResponseEntity<Double> getTotalAmount(@PathVariable String sellerEmail) {
//        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
//                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
//        List<Orders> orders = (List<Orders>) orderRepository.findByOrderItems_Seller(seller);
//        double totalAmount = orders.stream()
//                .mapToDouble(Orders::getTotalAmount)
//                .sum();
//        return ResponseEntity.ok(totalAmount);
//    }
    
    @GetMapping("sellerTotalAmount/totalAmount/{sellerEmail}/{sellerId}")
    public ResponseEntity<Double> getTotalAmountss(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);

        double totalAmount = orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();

        return ResponseEntity.ok(totalAmount);
    }
    
    @GetMapping("/allUsers")
    public ResponseEntity<List<OurUsers>> getAllUsers() {
        List<OurUsers> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    

    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> statusMap, @RequestHeader("Authorization") String token) {
        try {
            // Extract the JWT token without "Bearer " prefix
            String jwtToken = token.substring(7);
            String email = jwtUtils.extractUsername(jwtToken);
            System.out.println("Extracted email: " + email);

            // Retrieve the user from the database
            OurUsers user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Retrieved user: " + user.getEmail());

            // Retrieve the order from the database
            Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            System.out.println("Retrieved order: " + order.getId() + " placed by: " + order.getUser().getEmail());

            // Check if the user is an admin or the owner of the order
            if (!user.getRole().equals("ADMIN") && !order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this order");
            }

            // Update the order status
            String status = statusMap.get("status");
            order.setStatus(status);
            orderRepository.save(order);

            return ResponseEntity.ok("Order status updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order status");
        }
    }

    
    @GetMapping("/allOrders")
    public ResponseEntity<List<Orders>> getAllOrders(@RequestHeader("Authorization") String token) {
        List<Orders> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
    
    
    @GetMapping("/weeklyOrders")
    public ResponseEntity<Map<String, Double>> getWeeklyOrders() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        List<Orders> weeklyOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().isAfter(oneWeekAgo))
                .collect(Collectors.toList());

        double weeklyAmount = weeklyOrders.stream()
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) weeklyOrders.size(),
                "totalAmount", weeklyAmount
        ));
    }
    @GetMapping("sellerweeklyOrders/weeklyOrders/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getWeeklyOrdersss(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        Logger logger = LoggerFactory.getLogger(this.getClass());


        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);


        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));


        List<OrderItem> weeklyOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt() != null && orderItem.getOrder().getCreatedAt().isAfter(oneWeekAgo))
                .collect(Collectors.toList());


        double weeklyAmount = weeklyOrderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();


        long totalOrders = weeklyOrderItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .count();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", weeklyAmount
        ));
    }
    @GetMapping("/monthlyOrders")
    public ResponseEntity<Map<String, Double>> getMonthlyOrders() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);

        List<Orders> monthlyOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().isAfter(oneMonthAgo))
                .collect(Collectors.toList());

        double monthlyAmount = monthlyOrders.stream()
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) monthlyOrders.size(),
                "totalAmount", monthlyAmount
        ));
    }


    @GetMapping("sellermonthlyOrders/monthlyOrders/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getMonthlyOrdersss(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        Logger logger = LoggerFactory.getLogger(this.getClass());


        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);


        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));


        List<OrderItem> monthlyOrderItems =  orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt() != null && orderItem.getOrder().getCreatedAt().isAfter(oneMonthAgo))
                .collect(Collectors.toList());


        double monthlyAmount = monthlyOrderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();


        long totalOrders = monthlyOrderItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .count();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", monthlyAmount
        ));
    }
    
    @GetMapping("/todayOrders")
    public ResponseEntity<Map<String, Double>> getTodayOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

        List<Orders> todayOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().isAfter(startOfDay))
                .collect(Collectors.toList());

        double todayAmount = todayOrders.stream()
                .mapToDouble(Orders::getTotalAmount)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) todayOrders.size(),
                "totalAmount", todayAmount
        ));
    }
    
    @GetMapping("sellertodayOrders/todayOrders/{sellerEmail}/{sellerId}")
    public ResponseEntity<Map<String, Double>> getTodayOrdersss(@PathVariable String sellerEmail, @PathVariable Integer sellerId) {
        Logger logger = LoggerFactory.getLogger(this.getClass());


        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();


        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));


        List<OrderItem> todayOrderItems = orderItemRepository.findBySellerId(sellerId).stream()
                .filter(orderItem -> orderItem.getOrder().getCreatedAt() != null && orderItem.getOrder().getCreatedAt().isAfter(startOfDay))
                .collect(Collectors.toList());


        double todayAmount = todayOrderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();

        logger.info("Today's amount: {}", todayAmount);

        long totalOrders = todayOrderItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .count();

        return ResponseEntity.ok(Map.of(
                "totalOrders", (double) totalOrders,
                "totalAmount", todayAmount
        ));
    }


    @GetMapping("/customOrders")
    public ResponseEntity<Map<String, Object>> getCustomOrders(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDate validStartDate = parseDate(startDate);
        LocalDate validEndDate = parseDate(endDate);

        if (validStartDate == null && validEndDate == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Both startDate and endDate are invalid"));
        }

        if (validStartDate == null) {
            validStartDate = validEndDate.minusYears(1); // Default to one year before the valid endDate
        }

        if (validEndDate == null) {
            validEndDate = validStartDate.plusYears(1); // Default to one year after the valid startDate
        }

        long totalOrders = orderRepository.countOrdersBetween(validStartDate, validEndDate);
        double totalAmount = orderRepository.sumAmountBetween(validStartDate, validEndDate);

        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", totalOrders);
        response.put("totalAmount", totalAmount);

        return ResponseEntity.ok(response);
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    
    
    @GetMapping("/count")
    public ResponseEntity<CustomDateResponse> getCustomOrders(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        long count = orderRepository.countOrdersBetween(startDateTime, endDateTime);
        double sum = orderRepository.sumAmountBetween(startDateTime, endDateTime);

        CustomDateResponse response = new CustomDateResponse(count, sum);
        return ResponseEntity.ok(response);
    }

    
//  .......  

    @GetMapping("/seller-item-detail/{sellerEmail}/{sellerid}")
    public ResponseEntity<List<OrderItemResponseDTO2>> getSellerItemsa(@PathVariable String sellerEmail, @PathVariable Integer sellerid) {
        LOGGER.info("Fetching seller ID for email: {}", sellerEmail);

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        LOGGER.info("Seller ID: {}", sellerid);

        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerid);
        List<Item> items = orderItems.stream()
                .map(OrderItem::getItem)
                .distinct()
                .collect(Collectors.toList());

        LOGGER.info("Found items: {}", items);

        List<OrderItemResponseDTO2> itemResponseDTOs = items.stream()
                .map(item -> {
                    OrderItemResponseDTO2 itemResponseDTO = new OrderItemResponseDTO2();
                    itemResponseDTO.setId(item.getId());
                    itemResponseDTO.setName(item.getName());
                    itemResponseDTO.setPrice(item.getPrice());

                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

                    // Calculate the start of the week (Monday)
                    LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();

                    // Calculate the start of the month (1st day of the month)
                    LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

                    LOGGER.info("Calculating stats for item: {}", item.getName());

                    long totalOrders = orderItemRepository.countByItem(item);
                    double totalAmount = orderItemRepository.sumAmountByItem(item);

                    long totalOrdersToday = orderItemRepository.countByItemAndCreatedAtAfter(item, startOfDay);
                    double totalAmountToday = orderItemRepository.sumAmountByItemAndCreatedAtAfter(item, startOfDay);

                    long totalOrdersThisWeek = orderItemRepository.countByItemAndCreatedAtAfter(item, startOfWeek);
                    double totalAmountThisWeek = orderItemRepository.sumAmountByItemAndCreatedAtAfter(item, startOfWeek);

                    long totalOrdersThisMonth = orderItemRepository.countByItemAndCreatedAtAfter(item, startOfMonth);
                    double totalAmountThisMonth = orderItemRepository.sumAmountByItemAndCreatedAtAfter(item, startOfMonth);

                    itemResponseDTO.setTotalOrders(totalOrders);
                    itemResponseDTO.setTotalAmount(totalAmount);
                    itemResponseDTO.setTotalOrdersToday(totalOrdersToday);
                    itemResponseDTO.setTotalAmountToday(totalAmountToday);
                    itemResponseDTO.setTotalOrdersThisWeek(totalOrdersThisWeek);
                    itemResponseDTO.setTotalAmountThisWeek(totalAmountThisWeek);
                    itemResponseDTO.setTotalOrdersThisMonth(totalOrdersThisMonth);
                    itemResponseDTO.setTotalAmountThisMonth(totalAmountThisMonth);

                    LOGGER.info("Item details: {}", itemResponseDTO);

                    return itemResponseDTO;
                })
                .collect(Collectors.toList());

        LOGGER.info("Returning item response DTOs: {}", itemResponseDTOs);

        return ResponseEntity.ok(itemResponseDTOs);
    }    
    
    
    @GetMapping("/seller-item-detailss/{sellerEmail}/{sellerid}")
    public ResponseEntity<List<OrderItemResponseDTO2>> getSellerItemsaa(@PathVariable String sellerEmail, @PathVariable Integer sellerid) {
        LOGGER.info("Fetching seller ID for email: {}", sellerEmail);

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        LOGGER.info("Seller ID: {}", sellerid);

        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerid);
        List<Item> items = orderItems.stream()
                .map(OrderItem::getItem)
                .distinct()
                .collect(Collectors.toList());

        LOGGER.info("Found items: {}", items);

        List<OrderItemResponseDTO2> itemResponseDTOs = items.stream()
                .map(item -> {
                    OrderItemResponseDTO2 itemResponseDTO = new OrderItemResponseDTO2();
                    itemResponseDTO.setId(item.getId());
                    itemResponseDTO.setName(item.getName());
                    itemResponseDTO.setPrice(item.getPrice());

                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

                    // Calculate the start of the week (Monday)
                    LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
                    // Calculate the end of the week (Sunday)
                    LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).toLocalDate().atTime(LocalTime.MAX);

                    // Calculate the start of the month (1st day of the month)
                    LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

                    LOGGER.info("Calculating stats for item: {}", item.getName());

                    long totalOrders = orderItemRepository.countByItem(item);
                    double totalAmount = orderItemRepository.sumAmountByItem(item);

                    long totalOrdersToday = orderItemRepository.countByItemAndCreatedAtAfter(item, startOfDay);
                    double totalAmountToday = orderItemRepository.sumAmountByItemAndCreatedAtAfter(item, startOfDay);

                    long totalOrdersThisWeek = orderItemRepository.countByItemAndCreatedAtBetween(item, startOfWeek, endOfWeek);
                    double totalAmountThisWeek = orderItemRepository.sumAmountByItemAndCreatedAtBetween(item, startOfWeek, endOfWeek);

                    long totalOrdersThisMonth = orderItemRepository.countByItemAndCreatedAtAfter(item, startOfMonth);
                    double totalAmountThisMonth = orderItemRepository.sumAmountByItemAndCreatedAtAfter(item, startOfMonth);

                    itemResponseDTO.setTotalOrders(totalOrders);
                    itemResponseDTO.setTotalAmount(totalAmount);
                    itemResponseDTO.setTotalOrdersToday(totalOrdersToday);
                    itemResponseDTO.setTotalAmountToday(totalAmountToday);
                    itemResponseDTO.setTotalOrdersThisWeek(totalOrdersThisWeek);
                    itemResponseDTO.setTotalAmountThisWeek(totalAmountThisWeek);
                    itemResponseDTO.setTotalOrdersThisMonth(totalOrdersThisMonth);
                    itemResponseDTO.setTotalAmountThisMonth(totalAmountThisMonth);

                    LOGGER.info("Item details: {}", itemResponseDTO);

                    return itemResponseDTO;
                })
                .collect(Collectors.toList());

        LOGGER.info("Returning item response DTOs: {}", itemResponseDTOs);

        return ResponseEntity.ok(itemResponseDTOs);
    }
   
    
    
    @GetMapping("/seller-item-detail-date-range/{sellerEmail}/{sellerId}")
    public ResponseEntity<List<OrderItemResponseDTO2>> getSellerItemsByDateRanges(
            @PathVariable String sellerEmail,
            @PathVariable Integer sellerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
        List<Item> items = orderItems.stream()
                .map(OrderItem::getItem)
                .distinct()
                .collect(Collectors.toList());

        List<OrderItemResponseDTO2> itemResponseDTOs = items.stream()
                .map(item -> {
                    OrderItemResponseDTO2 itemResponseDTO = new OrderItemResponseDTO2();
                    itemResponseDTO.setId(item.getId());
                    itemResponseDTO.setName(item.getName());
                    itemResponseDTO.setPrice(item.getPrice());

                    long totalOrders = orderItemRepository.countByItemAndCreatedAtBetween(item, startDate, endDate);
                    double totalAmount = orderItemRepository.sumAmountByItemAndCreatedAtBetween(item, startDate, endDate);

                    itemResponseDTO.setTotalOrders(totalOrders);
                    itemResponseDTO.setTotalAmount(totalAmount);

                    return itemResponseDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemResponseDTOs);
    }
    
//    ...........
    
    
    
    
    
    @GetMapping("/seller-item-details/{sellerEmail}/{sellerid}")
    public ResponseEntity<List<OrderItemResponseDTO2>> getSellerItemss(@PathVariable String sellerEmail, @PathVariable Integer sellerid) {
        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerid);
        List<Item> items = orderItems.stream()
                .map(OrderItem::getItem)
                .distinct()
                .collect(Collectors.toList());

        long totalOrders = orderItems.size();
        double totalAmount = orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getQuantity() * orderItem.getPrice())
                .sum();

        List<OrderItemResponseDTO2> itemResponseDTOs = items.stream()
                .map(item -> {
                    OrderItemResponseDTO2 itemResponseDTO = new OrderItemResponseDTO2();
                    itemResponseDTO.setId(item.getId());
                    itemResponseDTO.setName(item.getName());
                    itemResponseDTO.setPrice(item.getPrice());
                    itemResponseDTO.setTotalOrders(totalOrders);
                    itemResponseDTO.setTotalAmount(totalAmount);
                    return itemResponseDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemResponseDTOs);
    }
    
    @PostMapping("/apply")
    public ResponseEntity<?> applyCoupon(@RequestBody CouponRequestDTO request) {
        String couponName = request.getCouponName();
        String userEmail = request.getUserEmail();

        Optional<Coupon> couponOptional = couponRepository.findByName(couponName);
        if (couponOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Coupon not found");
        }

        Coupon coupon = couponOptional.get();

        // Check if coupon is expired
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body("Coupon is expired");
        }

        Optional<OurUsers> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        OurUsers user = userOptional.get();

        // Check if the user has already used the coupon
        if (coupon.getUsedByUsers().contains(user)) {
            return ResponseEntity.badRequest().body("Coupon already used by this user");
        }

        // Add user to usedByUsers set and save coupon
        coupon.getUsedByUsers().add(user);
        couponRepository.save(coupon);

        return ResponseEntity.ok("Coupon applied successfully. Discount: " + coupon.getAmount());
    }

    // Create coupon
    @PostMapping("/create")
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        couponRepository.save(coupon);
        return ResponseEntity.ok("Coupon created successfully");
    }

    // Delete coupon
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return ResponseEntity.ok("Coupon deleted successfully");
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Coupon>> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponRepository.findById(id));
    }

    @GetMapping("/byname/{couponName}")
    public ResponseEntity<Optional<Coupon>> getCouponByCouponName(@PathVariable String couponName) {
        return ResponseEntity.ok(couponRepository.findByName(couponName));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponRepository.findAll());
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    @GetMapping("/seller-item-orders")
//    public ResponseEntity<List<OrderItemResponseDTO2>> getSellerItemOrders(
//            @RequestParam("sellerEmail") String sellerEmail,
//            @RequestParam("sellerId") Integer sellerId,
//            @RequestParam("itemId") Long itemId,
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        OurUsers seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER")
//                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
//
//        List<OrderItem> orderItems = orderItemRepository.findBySellerIdAndItemId(sellerId, itemId);
//
//        List<OrderItemResponseDTO2> itemResponseDTOs = orderItems.stream()
//                .filter(orderItem -> orderItem.getOrder().getCreatedAt().isAfter(startDate.atStartOfDay())
//                        && orderItem.getOrder().getCreatedAt().isBefore(endDate.plusDays(1).atStartOfDay()))
//                .map(orderItem -> {
//                    OrderItemResponseDTO2 itemResponseDTO = new OrderItemResponseDTO2();
//                    itemResponseDTO.setId(orderItem.getId());
//                    itemResponseDTO.setName(orderItem.getName());
//                    itemResponseDTO.setPrice(orderItem.getPrice());
//                    itemResponseDTO.setQuantity(orderItem.getQuantity());
//
//                    LocalDateTime orderDate = orderItem.getOrder().getCreatedAt();
//                    itemResponseDTO.setOrderDate(orderDate.toLocalDate());
//
//                    int totalOrders = orderItemRepository.countByItemIdAndCreatedAtBetween(itemId, orderDate.toLocalDate().atStartOfDay(), orderDate.toLocalDate().plusDays(1).atStartOfDay());
//                    double totalAmount = orderItemRepository.sumAmountByItemIdAndCreatedAtBetween(itemId, orderDate.toLocalDate().atStartOfDay(), orderDate.toLocalDate().plusDays(1).atStartOfDay());
//
//                    itemResponseDTO.setTotalOrders(totalOrders);
//                    itemResponseDTO.setTotalAmount(totalAmount);
//
//                    return itemResponseDTO;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(itemResponseDTOs);
//    }
    //seller ...............
    
    
//    @GetMapping("/seller-orders")
//    public ResponseEntity<List<OrderDTO2>> getSellerOrders(Authentication authentication) {
//        String sellerName = authentication.getName();
//        List<Orders> orders = orderRepository.findBySellerName(sellerName);
//        List<OrderDTO2> orderDTOs = orders.stream()
//                .map(order -> {
//                    OrderDTO2 orderDTO = new OrderDTO2();
//                    orderDTO.setId(order.getId());
//                    orderDTO.setUserName(order.getUser().getEmail());
//                    List<OrderedItemDTO2> orderedItems = order.getOrderItems().stream()
//                            .map(orderItem -> {
//                                OrderedItemDTO2 orderedItemDTO = new OrderedItemDTO2();
//                                orderedItemDTO.setId(orderItem.getId());
//                                orderedItemDTO.setItemName(orderItem.getName());
//                                orderedItemDTO.setPrice(orderItem.getPrice());
//                                orderedItemDTO.setQuantity(orderItem.getQuantity());
//                                return orderedItemDTO;
//                            })
//                            .collect(Collectors.toList());
//                    orderDTO.setOrderedItems(orderedItems);
//                    return orderDTO;
//                })
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(orderDTOs);
//    }
//    
    
    
    
    
    
//  @PutMapping("/{orderId}/status")
//  public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestBody String status, @RequestHeader("Authorization") String token) {
//      String email = jwtUtils.extractUsername(token.substring(7));
//      OurUsers user = userRepository.findByEmail(email).orElseThrow();
//
//      Orders order = orderRepository.findById(orderId).orElseThrow();
//      if (!order.getUser().getId().equals(user.getId())) {
//          return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this order");
//      }
//
//      order.setStatus(status);
//      orderRepository.save(order);
//
//      return ResponseEntity.ok("Order status updated successfully");
//  }
    
//    @GetMapping("/myOrders/{mail}")
//    public ResponseEntity<List<Orders>> getMyOrders(@PathVariable String mail) {
//    	OurUsers user = userRepository.findByEmail(mail).orElseThrow();
////        OurUsers user = userRepository.findByEmail(authentication.getName()).orElseThrow();
//        List<Orders> orders = orderRepository.findByUserId(user.getId());
//        System.out.println(mail);
//        return ResponseEntity.ok(orders);
//    }
//    
//    	
    
//  @GetMapping("/myOrders")
//  public ResponseEntity<List<Orders>> getMyOrders(Authentication authentication) {
//  	
//      OurUsers user = userRepository.findByEmail(authentication.getName()).orElseThrow();
//      List<Orders> orders = orderRepository.findByUserId(user.getId());
//      System.out.println(authentication);
//      return ResponseEntity.ok(orders);
//  }
//  
    
    
    
//  @GetMapping("/myOrders")
//  public ResponseEntity<List<Orders>> getMyOrders() {
//	  JWTUtils jwtutils = new JWTUtils("hello");
//	  Claims claims = Jwts.parser().verifyWith(jwtutils.key2).build().parseSignedClaims((AuthService.token2)).getPayload();
////      OurUsers user = userRepository.findByEmail(jwtutils.extractUsername(AuthService.token2)).orElseThrow();
//        OurUsers user = userRepository.findByEmail(claims.getSubject()).orElseThrow();
//      List<Orders> orders = orderRepository.findByUserId(user.getId());
//      return ResponseEntity.ok(orders);
//  }
}