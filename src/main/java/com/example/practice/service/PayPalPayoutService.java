//package com.example.practice.service;
//import com.paypal.core.PayPalEnvironment;
//import com.paypal.core.PayPalHttpClient;
//import com.paypal.core.exception.PayPalException;
//import com.paypal.payouts.Currency;
//import com.paypal.payouts.PayoutsBatchRequest;
//import com.paypal.payouts.PayoutsBatchResponse;
//import com.paypal.payouts.PayoutsItemRequest;
//import com.paypal.payouts.PayoutsPostRequest;
//import com.paypal.payouts.PayoutsSenderBatchHeader;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
////PayPalPayoutService.java
//@Service
//public class PayPalPayoutService {
//
// // Assuming PayPal client configuration is set up elsewhere
// private final PayPalClient payPalClient;
//
// public PayPalPayoutService(PayPalClient payPalClient) {
//     this.payPalClient = payPalClient;
// }
//
// public void distributeFunds(List<Payout> payouts) throws PayPalRESTException {
//     // Construct a payout request
//     PayoutBatchRequest payoutRequest = new PayoutBatchRequest();
//
//     List<PayoutItem> payoutItems = new ArrayList<>();
//     for (Payout payout : payouts) {
//         PayoutItem payoutItem = new PayoutItem();
//         payoutItem.setRecipientType("EMAIL");
//         payoutItem.setReceiver(payout.getSeller().getEmail());
//         payoutItem.setAmount(new Currency("USD", String.valueOf(payout.getAmount())));
//         payoutItem.setNote(payout.getNote());
//         payoutItems.add(payoutItem);
//     }
//
//     payoutRequest.setItems(payoutItems);
//     payoutRequest.setSenderBatchHeader(new PayoutSenderBatchHeader("Batch Header"));
//
//     // Execute the payout
//     PayoutBatch payoutBatch = payPalClient.payouts().create(payoutRequest);
//     System.out.println("Payout batch status: " + payoutBatch.getBatchHeader().getBatchStatus());
// }
//}
//
//package com.example.practice.service;
//
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.PayPalRESTException;
//import com.paypal.payouts.*;
//import com.paypal.api.payments.Payout;
//import com.paypal.api.payments.PayoutItem;
//import com.paypal.api.payments.*;
//import com.paypal.api.payments.PayoutBatch;
//import com.paypal.api.payments.Currency;
//import java.util.ArrayList;
//import java.util.List;
//import com.example.practice.entity.*;
//import org.springframework.stereotype.Service;
//
//
//@Service
//public class PayPalPayoutService {
//
//    private final String clientId = "YOUR_PAYPAL_CLIENT_ID";
//    private final String clientSecret = "YOUR_PAYPAL_CLIENT_SECRET";
//    private final String mode = "sandbox"; // or "live"
//
//    private APIContext getAPIContext() {
//        return new APIContext(clientId, clientSecret, mode);
//    }
//
//    public void sendPayouts(List<Payout> payouts) throws PayPalRESTException {
//        APIContext apiContext = getAPIContext();
//
//        PayoutRequest payoutRequest = new PayoutRequest();
//        payoutRequest.setSenderBatchHeader(new SenderBatchHeader().emailSubject("You have a payment"));
//        payoutRequest.setItems(payouts);
//
//        PayoutBatch payoutBatch = Payout.create(apiContext, payoutRequest);
//        // Handle response and errors
//    }
//
//    public List<PayoutItem> createPayouts(List<OrderItem> orderItems) {
//        // Create payout items based on orderItems
//        List<PayoutItem> payoutItems = new ArrayList<>();
//        for (OrderItem item : orderItems) {
//            if ("seller".equals(item.getSeller().getRole())) {
//                double amount = item.getPrice() * item.getQuantity() * 0.95; // Seller's amount (after 5% commission)
//                PayoutItem payoutItem = new PayoutItem()
//                        .setRecipientType("EMAIL")
//                        .setReceiver(item.getSeller().getEmail())
//                        .setAmount(new Currency().setValue(String.valueOf(amount)).setCurrency("USD"))
//                        .setNote("Payment for your items");
//                payoutItems.add(payoutItem);
//            }
//        }
//        return payoutItems;
//    }
//}
//
//package com.example.practice.service;
//
//import com.example.practice.entity.OrderItem;
//import com.paypal.api.payments.Currency;
//import com.paypal.payouts.*;
//import com.paypal.payouts.PayoutItem;
//import com.paypal.payouts.SenderBatchHeader;
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.PayPalRESTException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class PayPalPayoutService {
//
//    private final String clientId = "YOUR_PAYPAL_CLIENT_ID";
//    private final String clientSecret = "YOUR_PAYPAL_CLIENT_SECRET";
//    private final String mode = "sandbox"; // or "live"
//
//    private APIContext getAPIContext() {
//        return new APIContext(clientId, clientSecret, mode);
//    }
//
//    public void sendPayouts(List<PayoutItem> payoutItems) throws PayPalRESTException {
//        APIContext apiContext = getAPIContext();
//
//        PayoutRequest payoutRequest = new PayoutRequest();
//        payoutRequest.setSenderBatchHeader(new SenderBatchHeader().emailSubject("You have a payment"));
//        payoutRequest.setItems(payoutItems);
//
//        PayoutBatch payoutBatch = Payout.create(apiContext, payoutRequest);
//        // Handle response and errors
//    }
//
//    public List<PayoutItem> createPayouts(List<OrderItem> orderItems) {
//        // Create payout items based on orderItems
//        List<PayoutItem> payoutItems = new ArrayList<>();
//        for (OrderItem item : orderItems) {
//            User seller = item.getSeller();
//            double amount = item.getPrice() * item.getQuantity() * 0.95; // Seller's amount (after 5% commission)
//            PayoutItem payoutItem = new PayoutItem()
//                    .setRecipientType("EMAIL")
//                    .setReceiver(seller.getEmail())
//                    .setAmount(new Currency().setValue(String.valueOf(amount)).setCurrency("USD"))
//                    .setNote("Payment for your items");
//            payoutItems.add(payoutItem);
//
//            // Create a payout item for the admin commission
//            double commissionAmount = item.getPrice() * item.getQuantity() * 0.05;
//            PayoutItem commissionPayoutItem = new PayoutItem()
//                    .setRecipientType("EMAIL")
//                    .setReceiver("admin@example.com") // Replace with the admin's email
//                    .setAmount(new Currency().setValue(String.valueOf(commissionAmount)).setCurrency("USD"))
//                    .setNote("Commission for your sale");
//            payoutItems.add(commissionPayoutItem);
//        }
//        return payoutItems;
//    }
//}