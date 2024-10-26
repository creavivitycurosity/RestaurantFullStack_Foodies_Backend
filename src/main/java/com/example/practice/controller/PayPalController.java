package com.example.practice.controller;

import com.example.practice.dto.OrderRequestDTO;
import com.example.practice.dto.ProductDTO;
import com.example.practice.entity.Item;
import com.example.practice.entity.OrderItem;
import com.example.practice.entity.Orders;
import com.example.practice.entity.OurUsers;
import com.example.practice.repository.ItemRepository;
import com.example.practice.repository.OrderRepository;
import com.example.practice.repository.OurUserRepo;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/paypal")
public class PayPalController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OurUserRepo userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PayPalHttpClient payPalClient;
    @PostMapping("/execute-payment")
    @Transactional
    public ResponseEntity<String> executePayment(@RequestBody OrderRequestDTO paymentDetails, Authentication authentication) {
        try {
        	System.out.println("Entering executePayment method");
        	// other code

            String paymentId = paymentDetails.getPaymentId();
            Orders existingOrder = orderRepository.findBypaymentId(paymentId).orElse(null);

            if (existingOrder != null) {
                if (existingOrder.getStatus().equalsIgnoreCase("completed")) {
                    return ResponseEntity.ok("Order has already been processed.");
                } else if (existingOrder.getStatus().equalsIgnoreCase("pending")) {
                    return ResponseEntity.ok("Order is pending.");
                }
            }

            // Fetch the order details from PayPal to check its capture status
            com.paypal.orders.Order orderDetails = fetchOrderDetails(paymentId);

            // Check if the order is already captured
            boolean isAlreadyCaptured = orderDetails.purchaseUnits().stream()
                .flatMap(pu -> pu.payments().captures().stream())
                .anyMatch(capture -> capture.status().equalsIgnoreCase("COMPLETED"));

            if (isAlreadyCaptured) {
                return ResponseEntity.ok("Order has already been captured.");
            }

            OrdersCaptureRequest request = new OrdersCaptureRequest(paymentId);
            request.requestBody(new com.paypal.orders.OrderRequest());

            HttpResponse<com.paypal.orders.Order> response = payPalClient.execute(request);
//            System.out.println("Payment ID: " + paymentDetails.getPaymentId());
//            System.out.println("User: " + paymentDetails.getUser());
//            System.out.println("PayPal response status: " + response.statusCode());
//            System.out.println("PayPal response body: " + response.result());

//            if (response.statusCode() == 201) {
//                com.paypal.orders.Order order = response.result();
//                if (order.status().equalsIgnoreCase("COMPLETED")) {
//                    OurUsers user = userRepository.findByEmail(paymentDetails.getUser().getEmail()).orElseThrow();
//
//                    List<OrderItem> orderItems = paymentDetails.getProduct().stream()
//                        .map(product -> {
//                            Item dbItem = itemRepository.findById(product.getId()).orElseThrow();
//                            return new OrderItem(dbItem, product.getQuantity());
//                        })
//                        .collect(Collectors.toList());
//
//                    Orders newOrder = new Orders(user, orderItems);
//                    newOrder.setStatus("completed");
//                    newOrder.setPaymentId(paymentDetails.getPaymentId());
//                    newOrder.setPayerId(paymentDetails.getPayerId());
//                    System.out.println("Saving order: " + newOrder); // Debug log
//                    Orders savedOrder = orderRepository.save(newOrder);
//                    System.out.println("Saved order: " + savedOrder); // Debug log
//
//                    return ResponseEntity.ok("Payment executed and order placed successfully");
//                } else {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment not approved");
//                }
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment not approved");
//            }
         return ResponseEntity.ok("Payment was successfully");

        }
        catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing PayPal payment: " + e.getMessage());
        }
    }

    private com.paypal.orders.Order fetchOrderDetails(String orderId) throws IOException {
        com.paypal.orders.OrdersGetRequest request = new com.paypal.orders.OrdersGetRequest(orderId);
        HttpResponse<com.paypal.orders.Order> response = payPalClient.execute(request);
        return response.result();
    }

}
