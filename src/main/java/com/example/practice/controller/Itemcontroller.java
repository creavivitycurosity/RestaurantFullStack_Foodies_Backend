package com.example.practice.controller;
import org.slf4j.Logger;import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.practice.dto.ItemReviewResponse;

import org.slf4j.LoggerFactory;
import com.example.practice.exceptions.ResourceNotFoundException;
import com.example.practice.dto.Category2DTO;
import com.example.practice.dto.CategoryDTO;
import com.example.practice.dto.ItemReviewsResponse;
import com.example.practice.dto.ReviewRequest;
import com.example.practice.dto.SellerDTO;
import com.example.practice.dto.SellerInfoDTO;

import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.practice.entity.Category;
import com.example.practice.entity.Favorite;
import com.example.practice.entity.Item;
import com.example.practice.entity.ItemReview;
import com.example.practice.entity.OurUsers;

import com.example.practice.entity.OrderItem;
import com.example.practice.repository.CategoryRepository;
import com.example.practice.repository.FavoriteRepository;
import com.example.practice.repository.ItemRepository;
import com.example.practice.repository.ItemReviewRepository;
import com.example.practice.repository.OrderItemRepository;
import com.example.practice.repository.OurUserRepo;
import com.example.practice.res.ItemSearchResult;
@RestController
@RequestMapping("/items")
public class Itemcontroller {
	@Autowired
    private ItemReviewRepository reviewRepository;

	 @Autowired
	 private FavoriteRepository favoriteRepository;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private OurUserRepo ourUsersRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(Itemcontroller.class);

    @GetMapping("/allitems")
    public List<Item> getAllItems() {
        return (List<Item>)itemRepository.findAll();
    }
    // API to get items based on category (Veg/Non-Veg)
    @GetMapping("/category2/{isVeg}")
    public ResponseEntity<List<Item>> getItemsByCategory2(@PathVariable boolean isVeg) {
        List<Item> items = itemRepository.findByIsVeg(isVeg);
        return ResponseEntity.ok(items);
    }
    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<Item> imageEntityOptional = itemRepository.findById(id);
        return imageEntityOptional.map(item -> ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.IMAGE_JPEG).body(item.getImage()))
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    
    @GetMapping("/items/veg-nonveg-with-random-images-and-discounts")
    public List<Category2DTO> getVegNonVegWithRandomImagesAndDiscounts() {
        List<Item> vegItems = itemRepository.findByIsVeg(true);
        List<Item> nonVegItems = itemRepository.findByIsVeg(false);

        if (vegItems.isEmpty() || nonVegItems.isEmpty()) {
            return Collections.emptyList();
        }

        Random random = new Random();
        Item randomVegItem = vegItems.get(random.nextInt(vegItems.size()));
        Item randomNonVegItem = nonVegItems.get(random.nextInt(nonVegItems.size()));

        byte[] vegImage = randomVegItem.getImage();
        byte[] nonVegImage = randomNonVegItem.getImage();

        String encodedVegImage = (vegImage != null) ? Base64.getEncoder().encodeToString(vegImage) : null;
        String encodedNonVegImage = (nonVegImage != null) ? Base64.getEncoder().encodeToString(nonVegImage) : null;

        List<Item> vegItemsWithDiscounts = vegItems.stream()
                .filter(item -> item.isDiscountActive())
                .collect(Collectors.toList());

        List<Item> nonVegItemsWithDiscounts = nonVegItems.stream()
                .filter(item -> item.isDiscountActive())
                .collect(Collectors.toList());

        int vegHighDiscount = vegItemsWithDiscounts.stream()
                .mapToInt(item -> item.getDiscountPercentage())
                .max()
                .orElse(0);

        int vegLowDiscount = vegItemsWithDiscounts.stream()
                .mapToInt(item -> item.getDiscountPercentage())
                .min()
                .orElse(0);

        int nonVegHighDiscount = nonVegItemsWithDiscounts.stream()
                .mapToInt(item -> item.getDiscountPercentage())
                .max()
                .orElse(0);

        int nonVegLowDiscount = nonVegItemsWithDiscounts.stream()
                .mapToInt(item -> item.getDiscountPercentage())
                .min()
                .orElse(0);

        Category2DTO vegCategory = new Category2DTO(null, "veg", encodedVegImage, vegHighDiscount, vegLowDiscount);
        Category2DTO nonVegCategory = new Category2DTO(null, "non-veg", encodedNonVegImage, nonVegHighDiscount, nonVegLowDiscount);

        return Arrays.asList(vegCategory, nonVegCategory);
    }
    
    
    @GetMapping("/items/veg-nonveg-with-random-images3")
    public List<CategoryDTO> getVegNonVegWithRandomImages3() {
        List<Item> vegItems = itemRepository.findByIsVeg(true);
        List<Item> nonVegItems = itemRepository.findByIsVeg(false);

        if (vegItems.isEmpty() || nonVegItems.isEmpty()) {
            return Collections.emptyList();
        }

        Random random = new Random();
        Item randomVegItem = vegItems.get(random.nextInt(vegItems.size()));
        Item randomNonVegItem = nonVegItems.get(random.nextInt(nonVegItems.size()));

        byte[] vegImage = randomVegItem.getImage();
        byte[] nonVegImage = randomNonVegItem.getImage();

        String encodedVegImage = (vegImage != null) ? Base64.getEncoder().encodeToString(vegImage) : null;
        String encodedNonVegImage = (nonVegImage != null) ? Base64.getEncoder().encodeToString(nonVegImage) : null;

        CategoryDTO vegCategory = new CategoryDTO(null, "veg", encodedVegImage);
        CategoryDTO nonVegCategory = new CategoryDTO(null, "non-veg", encodedNonVegImage);

        return Arrays.asList(vegCategory, nonVegCategory);
    }
    @GetMapping("/items/veg-nonveg-with-random-images2")
    public List<CategoryDTO> getVegNonVegWithRandomImages2() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<Item>> typeItemsMap = new HashMap<>();
        typeItemsMap.put("veg", new ArrayList<>());
        typeItemsMap.put("non-veg", new ArrayList<>());

        // Group items by veg and non-veg
        for (Item item : items) {
            if (item.isVeg()) {
                typeItemsMap.get("veg").add(item);
            } else {
                typeItemsMap.get("non-veg").add(item);
            }
        }

        Random random = new Random();
        List<CategoryDTO> vegNonVegCategories = new ArrayList<>();

        // Process each type (veg, non-veg) to get a random image
        for (Map.Entry<String, List<Item>> entry : typeItemsMap.entrySet()) {
            List<Item> itemsInType = entry.getValue();
            if (!itemsInType.isEmpty()) {
                Item randomItem = itemsInType.get(random.nextInt(itemsInType.size()));
                byte[] image = randomItem.getImage();
                String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
                String typeName = entry.getKey(); // "veg" or "non-veg"

                // For veg/non-veg, categoryId can be null or -1 since these aren't real categories
                vegNonVegCategories.add(new CategoryDTO(null, typeName, encodedImage));  // Using `null` for categoryId
            }
        }

        return vegNonVegCategories;
    }

    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<Map<String, Boolean>> toggleAvailability(@PathVariable Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
        item.setAvailable(!item.isAvailable());  // Toggle availability
        itemRepository.save(item);  // Save updated item
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", item.isAvailable());
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<ItemSearchResult>> searchItems(@RequestParam("query") String query) {
//        List<ItemSearchResult> itemResults = itemRepository.findByNameStartingWith(query)
//            .stream()
//            .map(item -> new ItemSearchResult(item.getId(), item.getName(),item.getSellerName(),item.getImage()))
//            .collect(Collectors.toList());
//        return ResponseEntity.ok(itemResults);
//    }
    
    
    
    
    @GetMapping("/search")
    public ResponseEntity<List<ItemSearchResult>> searchItems(@RequestParam("query") String query) {
        logger.info("Received search query: {}", query);

        // Fetch items where name starts with the query
        List<ItemSearchResult> itemResults = itemRepository.findByNameStartingWith(query)
                .stream()
                .map(item -> {
                    logger.debug("Found item: {} from seller: {}", item.getName(), item.getSellerName());
                    return new ItemSearchResult(
                            item.getId(),
                            item.getName(),
                            item.getSellerName(),
                            item.getImage(),
                            false,
                            item.getRestaurantName(),
                            item.getPrice(),                  // Assuming getPrice() exists in your Item entity
                            item.isDiscountActive(),         // Assuming isDiscountActive() exists in your Item entity
                            item.getDiscountPercentage(),    // Assuming getDiscountPercentage() exists in your Item entity
                            item.getPreviousAmount()         // Assuming getPreviousAmount() exists in your Item entity
                  
                            
                    );
                })
                .collect(Collectors.toList());

        logger.info("Item results found: {}", itemResults.size());

        // Fetch users (sellers) whose restaurant name starts with the query and role is "SELLER"
        List<ItemSearchResult> sellerResults = ourUsersRepository.findByRestaurantNameStartingWithAndRole(query, "SELLER")
                .stream()
                .map(user -> {
                    logger.debug("Found seller: {} with restaurant: {}", user.getEmail(), user.getRestaurantName());
                    return new ItemSearchResult(
                            null,
                            user.getRestaurantName(),
                            user.getEmail(),
                            user.getImage(),
                            true,
                            user.getRestaurantName()
                    );
                })
                .collect(Collectors.toList());

        logger.info("Seller results found: {}", sellerResults.size());

        // Combine item and seller results
        itemResults.addAll(sellerResults);
        logger.info("Total results returned: {}", itemResults.size());

        return ResponseEntity.ok(itemResults);
    }
    
    @GetMapping("/searchss")
    public ResponseEntity<List<ItemSearchResult>> searchItemsa(@RequestParam("query") String query) {
        logger.info("Received search query: {}", query);

        // Set to track unique item IDs
        Set<Long> itemIds = new HashSet<>();

        // Fetch items where name starts with the query
        List<ItemSearchResult> itemResults = itemRepository.findByNameStartingWith(query)
                .stream()
                .filter(item -> itemIds.add(item.getId()))  // Add item ID to set and ensure it's unique
                .map(item -> {
                    logger.debug("Found item by name: {} from seller: {}", item.getName(), item.getSellerName());
                    return new ItemSearchResult(
                            item.getId(),
                            item.getName(),
                            item.getSellerName(),
                            item.getImage(),
                            false,
                            item.getRestaurantName(),
                            item.getPrice(),
                            item.isDiscountActive(),
                            item.getDiscountPercentage(),
                            item.getPreviousAmount()
                    );
                })
                .collect(Collectors.toList());

        // Fetch items by tags
        List<ItemSearchResult> tagResults = itemRepository.findByTagContaining(query)
                .stream()
                .filter(item -> itemIds.add(item.getId()))  // Ensure item ID is unique before adding
                .map(item -> {
                    logger.debug("Found item by tag: {} from seller: {}", item.getName(), item.getSellerName());
                    return new ItemSearchResult(
                            item.getId(),
                            item.getName(),
                            item.getSellerName(),
                            item.getImage(),
                            false,
                            item.getRestaurantName(),
                            item.getPrice(),
                            item.isDiscountActive(),
                            item.getDiscountPercentage(),
                            item.getPreviousAmount()
                    );
                })
                .collect(Collectors.toList());

        // Add unique tag search results to itemResults
        itemResults.addAll(tagResults);

        logger.info("Total unique item results found: {}", itemResults.size());

        // Fetch sellers whose restaurant name starts with the query and role is "SELLER"
        List<ItemSearchResult> sellerResults = ourUsersRepository.findByRestaurantNameStartingWithAndRole(query, "SELLER")
                .stream()
                .map(user -> {
                    logger.debug("Found seller: {} with restaurant: {}", user.getEmail(), user.getRestaurantName());
                    return new ItemSearchResult(
                            null,
                            user.getRestaurantName(),
                            user.getEmail(),
                            user.getImage(),
                            true,
                            user.getRestaurantName()
                    );
                })
                .collect(Collectors.toList());

        logger.info("Total seller results found: {}", sellerResults.size());

        // Combine item and seller results
        itemResults.addAll(sellerResults);

        logger.info("Total results returned: {}", itemResults.size());

        return ResponseEntity.ok(itemResults);
    }

    
    
    @GetMapping("/searching")
    public ResponseEntity<List<ItemSearchResult>> searchingItems(@RequestParam("query") String query) {
        logger.info("Received search query: {}", query);

        // Set to track unique item IDs
        Set<Long> itemIds = new HashSet<>();

        // Fetch items where name starts with the query
        List<ItemSearchResult> itemResults = itemRepository.findByNameStartingWith(query)
                .stream()
                .filter(item -> itemIds.add(item.getId()))  // Add item ID to set and ensure it's unique
                .map(item -> {
                    logger.debug("Found item by name: {} from seller: {}", item.getName(), item.getSellerName());
                    return new ItemSearchResult(
                            item.getId(),
                            item.getName(),
                            item.getSellerName(),
                            item.getImage(),
                            false,
                            item.getRestaurantName(),
                            item.getPrice(),
                            item.isDiscountActive(),
                            item.getDiscountPercentage(),
                            item.getPreviousAmount()
                    );
                })
                .collect(Collectors.toList());

        // Fetch items by tags
        List<ItemSearchResult> tagResults = itemRepository.findByTagContaining(query)
                .stream()
                .filter(item -> itemIds.add(item.getId()))  // Ensure item ID is unique before adding
                .map(item -> {
                    logger.debug("Found item by tag: {} from seller: {}", item.getName(), item.getSellerName());
                    return new ItemSearchResult(
                            item.getId(),
                            item.getName(),
                            item.getSellerName(),
                            item.getImage(),
                            false,
                            item.getRestaurantName(),
                            item.getPrice(),
                            item.isDiscountActive(),
                            item.getDiscountPercentage(),
                            item.getPreviousAmount()
                    );
                })
                .collect(Collectors.toList());

        // Add unique tag search results to itemResults
        itemResults.addAll(tagResults);

        logger.info("Total unique item results found: {}", itemResults.size());

        // Fetch sellers whose restaurant name starts with the query and role is "SELLER"
        List<ItemSearchResult> sellerResults = ourUsersRepository.findByRestaurantNameStartingWithAndRole(query, "SELLER")
                .stream()
                .map(user -> {
                    logger.debug("Found seller: {} with restaurant: {}", user.getEmail(), user.getRestaurantName());
                    return new ItemSearchResult(
                            null,
                            user.getRestaurantName(),
                            user.getEmail(),
                            user.getImage(),
                            true,
                            user.getRestaurantName()
                    );
                })
                .collect(Collectors.toList());

        logger.info("Total seller results found: {}", sellerResults.size());

        // Combine item and seller results
        itemResults.addAll(sellerResults);

        logger.info("Total results returned: {}", itemResults.size());

        return ResponseEntity.ok(itemResults);
    }
    
    
    @GetMapping("/searchingjava")
    public ResponseEntity<List<ItemSearchResult>> searchingItemsj(@RequestParam("query") String query) {
        logger.info("Received search query: {}", query);

        // Fetch all items
        List<Item> allItems = itemRepository.findAll(); // Fetch all items at once

        // Set to track unique item IDs
        Set<Long> itemIds = new HashSet<>();

        // Use streams to filter items by name or tags
        List<ItemSearchResult> itemResults = allItems.stream()
                .filter(item -> {
                    boolean matchesName = item.getName().toLowerCase().startsWith(query.toLowerCase());
                    boolean matchesTags = item.getTags() != null && item.getTags().stream()
                            .anyMatch(tag -> tag.toLowerCase().startsWith(query.toLowerCase())); // Match if tag starts with query
                    return matchesName || matchesTags; // Match by name or tags
                })
                .filter(item -> itemIds.add(item.getId())) // Ensure uniqueness
                .map(item -> {
                    logger.debug("Found item: {} from seller: {}", item.getName(), item.getSellerName());
                    return new ItemSearchResult(
                            item.getId(),
                            item.getName(),
                            item.getSellerName(),
                            item.getImage(),
                            false,
                            item.getRestaurantName(),
                            item.getPrice(),
                            item.isDiscountActive(),
                            item.getDiscountPercentage(),
                            item.getPreviousAmount()
                    );
                })
                .collect(Collectors.toList());


        logger.info("Total unique item results found: {}", itemResults.size());

        // Fetch sellers whose restaurant name starts with the query and role is "SELLER"
        List<ItemSearchResult> sellerResults = ourUsersRepository.findByRestaurantNameStartingWithAndRole(query, "SELLER")
                .stream()
                .map(user -> {
                    logger.debug("Found seller: {} with restaurant: {}", user.getEmail(), user.getRestaurantName());
                    return new ItemSearchResult(
                            null,
                            user.getRestaurantName(),
                            user.getEmail(),
                            user.getImage(),
                            true,
                            user.getRestaurantName()
                    );
                })
                .collect(Collectors.toList());

        logger.info("Total seller results found: {}", sellerResults.size());

        // Combine item and seller results
        itemResults.addAll(sellerResults);

        logger.info("Total results returned: {}", itemResults.size());

        return ResponseEntity.ok(itemResults);
    }
    
    

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        return itemOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        List<Item> items = itemRepository.findByNameStartingWithIgnoreCase(name);
        return ResponseEntity.ok(items);
    }

//    @PostMapping("/admin/add")
//    public ResponseEntity<Item> addItem(@RequestParam("name") String name,
//                                        @RequestParam("price") int price,
//                                        @RequestParam("category") String category,
//                                        @RequestParam("sellerName") String sellerName,
//                                        @RequestParam("image") MultipartFile image) throws IOException {
//        Item item = new Item();
//        item.setName(name);
//        item.setPrice(price);
//        item.setCategory(category);
//        item.setSellerName(sellerName);
//        item.setImage(image.getBytes());
//        Item savedItem = itemRepository.save(item);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
//    }

 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            itemRepository.delete(itemOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/categories")
    public List<CategoryDTO> getCategories() {
        List<Item> itemsList = itemRepository.findAll();
        Map<Long, Item> categoryImages = new HashMap<>();
        
        for (Item item : itemsList) {
            Long categoryId = item.getCategory().getId();
            // Only put if the categoryId is not already in the map
            categoryImages.putIfAbsent(categoryId, item);
        }

        return categoryImages.values().stream()
            .map(item -> new CategoryDTO(
                item.getCategory().getId(), 
                item.getCategory().getName(), 
                Base64.getEncoder().encodeToString(item.getImage())
            ))
            .collect(Collectors.toList());
    }

    @GetMapping("/categories-with-random-images")
    public List<CategoryDTO> getCategoriesWithRandomImages() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if no items are found
        }

        Map<String, List<Item>> categoryItemsMap = new HashMap<>();
        
        // Group items by category
        for (Item item : items) {
            Category category = item.getCategory();
            if (category != null) {
                String categoryName = category.getName();
                categoryItemsMap.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(item);
            }
        }

        Random random = new Random();
        List<CategoryDTO> categories = new ArrayList<>();

        // Process each category to get a random image
        for (Map.Entry<String, List<Item>> entry : categoryItemsMap.entrySet()) {
            List<Item> itemsInCategory = entry.getValue();
            if (!itemsInCategory.isEmpty()) {
                Item randomItem = itemsInCategory.get(random.nextInt(itemsInCategory.size()));
                byte[] image = randomItem.getImage();
                String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
                categories.add(new CategoryDTO(
                    randomItem.getCategory().getId(),
                    entry.getKey(), // categoryName
                    encodedImage
                ));
            }
        }

        return categories;
    }
    
//    @GetMapping("/categories-with-random-images2")
//    public List<CategoryDTO> getCategoriesWithRandomImages2(@RequestParam String email) {
//        List<Item> items = itemRepository.findAll();
//        Optional<OurUsers> optionalUser = ourUsersRepository.findByEmail(email);
//
//        if (items.isEmpty()) {
//            return Collections.emptyList(); // Return an empty list if no items are found
//        }
//        if (optionalUser.isEmpty() || !optionalUser.get().getRole().equals("SELLER")) {
//            return Collections.emptyList(); // Return an empty list if no user found or user is not a seller
//        }    
//        OurUsers user = optionalUser.get();
//
//        Map<String, List<Item>> categoryItemsMap = new HashMap<>();
//        
//        // Group items by category
//        for (Item item : items) {
//            Category category = item.getCategory();
//            if (category != null) {
//                String categoryName = category.getName();
//                categoryItemsMap.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(item);
//            }
//        }
//
//        Random random = new Random();
//        List<CategoryDTO> categories = new ArrayList<>();
//
//        // Process each category to get a random image
////        for (Map.Entry<String, List<Item>> entry : categoryItemsMap.entrySet()) {
////            List<Item> itemsInCategory = entry.getValue();
////            if (!itemsInCategory.isEmpty()) {
////                Item randomItem = itemsInCategory.get(random.nextInt(itemsInCategory.size()));
////                byte[] image = randomItem.getImage();
////                String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
////                categories.add(new CategoryDTO(
////                    randomItem.getCategory().getId(),
////                    entry.getKey(), // categoryName
////                    encodedImage
////                ));
////            }
////        }
//        byte[] image = user.getImage();
//        String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
//
//        // Build the CategoryDTO with the seller's image
//        List<CategoryDTO> categories = List.of(new CategoryDTO(
//            user.getId(),  // Use the user's ID or any identifier as needed
//            user.getcategoryName(), // Assuming you want to use the restaurant name as the category name
//            encodedImage
//        ));
//
//        return categories;
//    }
//    

    @GetMapping("/get/category")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @GetMapping("/get/categories/sellers/{sellerEmail}")
    public List<Category> getCategoriesForSellers(@PathVariable String sellerEmail) {
        // Fetch all items for the seller based on the sellerName field
        List<Item> items = itemRepository.findByrestaurantName(sellerEmail);

        // Log the items to confirm they are being retrieved
        System.out.println("Fetched items: " + items);

        // Use streams to collect unique categories from the items
        List<Category> uniqueCategories = items.stream()
            .map(Item::getCategory) // Get the category of each item
            .filter(Objects::nonNull) // Ensure category is not null
            .distinct() // Keep only unique categories
            .collect(Collectors.toList());

        // Log the unique categories to check the result
        System.out.println("Unique categories for seller: " + uniqueCategories);
        System.out.println(" seller: " + sellerEmail);

        return uniqueCategories;
    }


    @GetMapping("/category/{categoryId}")
    public List<Item> getItemsByCategory(@PathVariable Long categoryId) {
        List<Item> items = itemRepository.findByCategory_Id(categoryId);
        System.out.println("Items: " + items);
        return items;
    }

    @PostMapping("/admin/add-category")
    public ResponseEntity<Category> addCategory(@RequestParam("name") String name) {
        Category category = new Category(name);
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
    
    @PostMapping("/admin/addnew")
    public ResponseEntity<Item> addItem(
            @RequestParam("name") String name,
            @RequestParam("price") int price,
            @RequestParam("category") Long categoryId,
            @RequestParam("sellerName") String sellerName,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setRestaurantName(sellerName);
        item.setCategory(category);
        item.setImage(image.getBytes());
        Item savedItem = itemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }
    
    @PutMapping("/admin/edit/{id}")
    public ResponseEntity<Item> editItem(@PathVariable Long id,
                                          @RequestParam("name") String name,
                                          @RequestParam("price") int price,
                                          @RequestParam("category") Long categoryId,
                                          @RequestParam("sellerName") String sellerName,
                                          @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            
            // Update item details
            item.setName(name);
            item.setPrice(price);
            item.setRestaurantName(sellerName);
            
            // Update category
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            item.setCategory(category);
            
            // Update image if provided
            if (image != null && !image.isEmpty()) {
                item.setImage(image.getBytes());
            }
            
            // Save the updated item
            Item updatedItem = itemRepository.save(item);
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/admin/feature/{id}")
    public ResponseEntity<Item> toggleFeatured(@PathVariable Long id, @RequestParam("featured") boolean featured) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setFeatured(featured);
            Item updatedItem = itemRepository.save(item);
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Item>> getFeaturedItems() {
        List<Item> featuredItems = itemRepository.findByFeaturedTrue();
        return ResponseEntity.ok(featuredItems);
    }
    
    // seller ..........................
    @PutMapping("/update-discount/{id}")
    public ResponseEntity<Item> updateDiscount(
            @PathVariable Long id,
            @RequestParam int discountPercentage) {
        
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        
        if (item.getPreviousAmount() == 0) {
            item.setPreviousAmount(item.getPrice());  // Set the original price if not set
        }
        
        item.setDiscountPercentage(discountPercentage);
        item.setDiscountActive(true);
        item.setPrice(item.getPreviousAmount() * (100 - discountPercentage) / 100);  // Apply discount
        
        Item updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(updatedItem);
    }

    // Remove discount from an item
    @PutMapping("/remove-discount/{id}")
    public ResponseEntity<Item> removeDiscount(@PathVariable Long id) {
        
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        
        item.setPrice(item.getPreviousAmount());  // Reset price to original
        item.setDiscountPercentage(0);  // Clear discount percentage
        item.setDiscountActive(false);  // Deactivate discount
        
        Item updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/seller")
    public List<Item> getItemsBySeller(Authentication authentication) {
        String sellerName = authentication.getName();
        return itemRepository.findBySellerName(sellerName);
    }
    
    @GetMapping("/seller/{sellerEmail}")
    public List<Item> getItemsBySeller(@PathVariable String sellerEmail) {
        return itemRepository.findBySellerName(sellerEmail);
    }
    
    @GetMapping("/seller/name/{restarauntName}")
    public List<Item> getItemsByrestarauntName(@PathVariable String restarauntName) {
        return itemRepository.findByrestaurantName(restarauntName);
    }
    @PostMapping("/add-seller-item")
    public Item addsellerItem(@RequestBody Item item, Authentication authentication) {
    	
        String sellerName = authentication.getName();
        item.setSellerName(sellerName);
        return itemRepository.save(item);
    }

    @PutMapping("/update-seller-item/{id}")
    public Item updatesellerItem(@PathVariable Long id, @RequestBody Item updatedItem, Authentication authentication) {
        String sellerName = authentication.getName();
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getRestaurantName().equals(sellerName)) {
            throw new RuntimeException("Unauthorized");
        }
        item.setName(updatedItem.getName());
        item.setPrice(updatedItem.getPrice());
        item.setImage(updatedItem.getImage());
        item.setCategory(updatedItem.getCategory());
        return itemRepository.save(item);
    }

    @DeleteMapping("/delete-seller-item/{id}")
    @Transactional // Add this annotation
    public void deletesellerItem(@PathVariable Long id, Authentication authentication) {
        String sellerName = authentication.getName();
        
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (!item.getSellerName().equals(sellerName)) {
            throw new RuntimeException("Unauthorized");
        }
        // Delete associated reviews first
        List<ItemReview> reviews = reviewRepository.findByItem(item);
        System.out.println("reviews"+reviews);
        if (!reviews.isEmpty()) {
            // Delete associated reviews if there are any
            reviewRepository.deleteByItem(item);
        }
        
        
        List<Favorite> favorites = favoriteRepository.findByItem(item);
        if (!favorites.isEmpty()) {
            favoriteRepository.deleteByItem(item);
        }
        System.out.println("favorites"+favorites);

        itemRepository.delete(item);
    }
    
    @DeleteMapping("/delete-seller-items/{id}")
    @Transactional // Ensure all operations are part of a single transaction
    public void deletesellerItems(@PathVariable Long id, Authentication authentication) {
        String sellerName = authentication.getName();

        // Fetch the item, ensuring it exists
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Verify the seller's authorization
        if (!item.getSellerName().equals(sellerName)) {
            throw new RuntimeException("Unauthorized");
        }

        // Delete associated reviews if there are any
        List<ItemReview> reviews = reviewRepository.findByItemId(item.getId());
        System.out.println("Reviews before deletion: " + reviews);
        
        // Instead of relying on the item reference, delete reviews using the item ID
        if (!reviews.isEmpty()) {
            reviewRepository.deleteByItemId(item.getId());
        }

        // Delete associated favorites if there are any
        List<Favorite> favorites = favoriteRepository.findByItemId(item.getId());
        System.out.println("Favorites before deletion: " + favorites);
        
        if (!favorites.isEmpty()) {
            favoriteRepository.deleteByItemId(item.getId());
        }
        
        // First, delete any related OrderItems
        List<OrderItem> orderItems = orderItemRepository.findByItemId(item.getId());
        
        if (!orderItems.isEmpty()) {

            orderItemRepository.deleteByItemId(item.getId());
        }  
        
        // Finally, delete the item itself
        itemRepository.delete(item);
        System.out.println("Item with ID " + id + " deleted successfully.");
    }


    
    @GetMapping("seller-item/{id}")
    public Item getItemById(@PathVariable Long id, Authentication authentication) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getSellerName().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }
        return item;
    }
    
    
    @PostMapping("/seller/addnew")
    public ResponseEntity<Item> addItemseller(
            @RequestParam("name") String name,
            @RequestParam("price") int price,
            @RequestParam("category") Long categoryId,
            @RequestParam("sellerName") String sellerName,
            @RequestParam("image") MultipartFile image,
            @RequestParam("quantity") int quantity,
            @RequestParam("description") String description ,
            @RequestParam("tags") List<String> tags,  // Accepting tags
            @RequestParam("isVeg") boolean isVeg  // New field for veg or non-veg status

// New description field
    ) throws IOException {
        OurUsers seller = ourUsersRepository.findByEmailAndRole(sellerName, "SELLER")
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setSellerName(sellerName);
        item.setCategory(category);
        item.setRestaurantName(seller.getRestaurantName());
        item.setImage(image.getBytes());
        item.setQuantity(quantity);
        item.setDescription(description);  // Set the description
        item.setTags(tags);  // Saving the tags
        item.setVeg(isVeg);  // Set the veg or non-veg status

        Item savedItem = itemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }
    
    @PutMapping("/seller/edit/{id}")
    public ResponseEntity<Item> editItemSeller(@PathVariable Long id,
                                               @RequestParam("name") String name,
                                               @RequestParam("price") int price,
                                               @RequestParam("category") Long categoryId,
                                               @RequestParam("sellerName") String sellerName,
                                               @RequestParam(value = "image", required = false) MultipartFile image,
                                               @RequestParam("quantity") int quantity,
                                               @RequestParam("description") String description,
                                               @RequestParam("tags") String tagsJson,
                                               @RequestParam("isVeg") boolean isVeg  // New field for veg or non-veg status

                                               
    		) throws IOException { // Add description
    	                                       
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();

            // Update item details
            item.setName(name);
            item.setPrice(price);
            item.setSellerName(sellerName);
            item.setQuantity(quantity);
            item.setDescription(description);  // Update description
            item.setVeg(isVeg);  // Set the veg or non-veg status

            // Update category
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            item.setCategory(category);

            // Update image if provided
            if (image != null && !image.isEmpty()) {
                item.setImage(image.getBytes());
            }
            
            // Update tags
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
            item.setTags(tags);

            // Save the updated item
            Item updatedItem = itemRepository.save(item);
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/items/veg-nonveg-with-random-images")
    public List<CategoryDTO> getVegNonVegWithRandomImages() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<Item>> typeItemsMap = new HashMap<>();
        typeItemsMap.put("veg", new ArrayList<>());
        typeItemsMap.put("non-veg", new ArrayList<>());

        // Group items by veg and non-veg
        for (Item item : items) {
            if (item.isVeg()) {
                typeItemsMap.get("veg").add(item);
            } else {
                typeItemsMap.get("non-veg").add(item);
            }
        }

        Random random = new Random();
        List<CategoryDTO> vegNonVegCategories = new ArrayList<>();

        // Process each type (veg, non-veg) to get a random image
        for (Map.Entry<String, List<Item>> entry : typeItemsMap.entrySet()) {
            List<Item> itemsInType = entry.getValue();
            if (!itemsInType.isEmpty()) {
                Item randomItem = itemsInType.get(random.nextInt(itemsInType.size()));
                byte[] image = randomItem.getImage();
                String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
                String typeName = entry.getKey(); // "veg" or "non-veg"
                
                // For veg/non-veg, categoryId can be null or -1 since these aren't real categories
                vegNonVegCategories.add(new CategoryDTO(null, typeName, encodedImage));  // Using `null` for categoryId
            }
        }

        return vegNonVegCategories;
    }


    @PutMapping("/seller/edit/{id}/quantity")
    public ResponseEntity<Item> editItemQuantity(@PathVariable Long id, @RequestParam("quantity") int quantity) {
    	  // Check if the provided quantity is negative and set it to 0 if it is
//        if (quantity < 0) {
//            quantity = 0;
//        }
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setQuantity(quantity);
            Item updatedItem = itemRepository.save(item);
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/total-unique-sellers")
    public ResponseEntity<Long> getSellers() {
        Long totalSellers = itemRepository.countDistinctSellers();
        return ResponseEntity.ok(totalSellers);
    }
    
    @GetMapping("/total-sellers")
    public ResponseEntity<Long> getTotalSellers() {
        Long totalSellers = ourUsersRepository.countSellers();
        return ResponseEntity.ok(totalSellers);
    }
    
//    @GetMapping("/sellers-info")   // working but sellers with 0 items are not displaying their names
//    public ResponseEntity<List<SellerInfoDTO>> getSellersInfo() {
//        List<Item> items = itemRepository.findAll();
//        
//        if (items == null) {
//            return ResponseEntity.noContent().build();
//        }
//        
//        // Filter out items with null seller names
//        List<SellerInfoDTO> sellersInfo = items.stream()
//            .filter(item -> item.getSellerName() != null) // Filter out null seller names
//            .collect(Collectors.groupingBy(Item::getSellerName, Collectors.counting()))
//            .entrySet().stream()
//            .map(entry -> new SellerInfoDTO(entry.getKey(), entry.getValue()))
//            .collect(Collectors.toList());
//        
//        return ResponseEntity.ok().body(sellersInfo);
//    }
    
    
//    @GetMapping("/sellers-info")
//    public ResponseEntity<List<SellerInfoDTO>> getSellersInfo() {
//        // Retrieve all items from the database
//        List<Item> items = itemRepository.findAll();
//        
//        // Query for all distinct seller names
//        List<String> allSellerNames = itemRepository.findDistinctSellerNames();
//        
//        if (items == null || items.isEmpty()) {
//            // If no items, create a list with zero items for each seller
//            List<SellerInfoDTO> sellersInfo = allSellerNames.stream()
//                .map(sellerName -> new SellerInfoDTO(sellerName, 0))
//                .collect(Collectors.toList());
//            return ResponseEntity.ok().body(sellersInfo);
//        }
//        
//        // Process items to count number of items per seller
//        List<SellerInfoDTO> sellersInfo = items.stream()
//            .filter(item -> item.getSellerName() != null) // Filter out null seller names
//            .collect(Collectors.groupingBy(Item::getSellerName, Collectors.counting()))
//            .entrySet().stream()
//            .map(entry -> new SellerInfoDTO(entry.getKey(), entry.getValue().intValue())) // Convert Long to int
//            .collect(Collectors.toList());
//
//        // Add sellers with zero items (if needed)
//        Set<String> sellerNamesWithItems = sellersInfo.stream()
//            .map(SellerInfoDTO::getSellerName)
//            .collect(Collectors.toSet());
//
//        allSellerNames.stream()
//            .filter(sellerName -> !sellerNamesWithItems.contains(sellerName))
//            .forEach(sellerName -> sellersInfo.add(new SellerInfoDTO(sellerName, 0)));
//
//        return ResponseEntity.ok().body(sellersInfo);
//    }

    @GetMapping("/sellers-info")
    public ResponseEntity<List<SellerInfoDTO>> getSellersInfo() {
        // Retrieve all items from the database
        List<Item> items = itemRepository.findAll();
        
        // Query for all distinct seller names
        List<String> allSellerNames = itemRepository.findDistinctSellerNames();

        // Query for all distinct restaurant names
        List<String> allRestaurantNames = itemRepository.findDistinctRestaurantNames();
        
        if (items == null || items.isEmpty()) {
            // If no items, create a list with zero items for each seller
            List<SellerInfoDTO> sellersInfo = allSellerNames.stream()
                .map(sellerName -> new SellerInfoDTO(sellerName, 0, null))
                .collect(Collectors.toList());
            return ResponseEntity.ok().body(sellersInfo);
        }
        
        // Process items to count number of items per seller and restaurant
        Map<String, SellerInfoDTO> sellersInfoMap = items.stream()
            .filter(item -> item.getSellerName() != null) // Filter out null seller names
            .collect(Collectors.groupingBy(
                Item::getSellerName,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    itemList -> {
                        String restaurantName = itemList.stream()
                            .map(Item::getRestaurantName)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);
                        return new SellerInfoDTO(
                            itemList.get(0).getSellerName(),
                            itemList.size(),
                            restaurantName
                        );
                    }
                )
            ));

        // Add sellers with zero items (if needed)
        Set<String> sellerNamesWithItems = sellersInfoMap.keySet();

        List<SellerInfoDTO> sellersInfo = allSellerNames.stream()
            .filter(sellerName -> !sellerNamesWithItems.contains(sellerName))
            .map(sellerName -> new SellerInfoDTO(sellerName, 0, null))
            .collect(Collectors.toList());

        sellersInfo.addAll(sellersInfoMap.values());

        return ResponseEntity.ok().body(sellersInfo);
    }

    
    @GetMapping("/seller-categories-with-random-images")
    public List<SellerDTO> getSellersWithRandomImages() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if no items are found
        }

        Map<String, List<Item>> sellerItemsMap = new HashMap<>();

        // Group items by seller name
        for (Item item : items) {
            String sellerName = item.getRestaurantName();
            if (sellerName != null) {
                sellerItemsMap.computeIfAbsent(sellerName, k -> new ArrayList<>()).add(item);
            }
        }

        Random random = new Random();
        List<SellerDTO> sellers = new ArrayList<>();

        // Process each seller to get a random image
        for (Map.Entry<String, List<Item>> entry : sellerItemsMap.entrySet()) {
            List<Item> itemsBySeller = entry.getValue();
            if (!itemsBySeller.isEmpty()) {
                Item randomItem = itemsBySeller.get(random.nextInt(itemsBySeller.size()));
                byte[] image = randomItem.getImage();
                String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
                sellers.add(new SellerDTO(
                    randomItem.getRestaurantName(), // sellerName
                    encodedImage
                ));
            }
        }

        return sellers;
    }

    @GetMapping("/seller-categories-with-imagess")
    public List<SellerDTO> getSellersWithImagess() {
        List<OurUsers> sellers = ourUsersRepository.findByRole("seller");
        if (sellers.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if no sellers are found
        }

        List<SellerDTO> sellerDTOs = new ArrayList<>();

        // Process each seller to get their image
        for (OurUsers seller : sellers) {
            byte[] image = seller.getImage();
            String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
            sellerDTOs.add(new SellerDTO(
                seller.getRestaurantName(), // sellerName
                encodedImage
            ));
        }

        return sellerDTOs;
    }
    
    @GetMapping("/seller-categories-with-random-imagesss")
    public List<SellerDTO> getSellersWithImagesss() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if no items are found
        }

        Map<String, List<Item>> sellerItemsMap = new HashMap<>();

        // Group items by seller's restaurant name
        for (Item item : items) {
            String sellerName = item.getRestaurantName();
            if (sellerName != null) {
                sellerItemsMap.computeIfAbsent(sellerName, k -> new ArrayList<>()).add(item);
            }
        }

        List<SellerDTO> sellers = new ArrayList<>();

        // Process each seller and get the corresponding seller's image from OurUsers entity
        for (Map.Entry<String, List<Item>> entry : sellerItemsMap.entrySet()) {
            String restaurantName = entry.getKey();

            // Fetch the seller (OurUsers) based on the restaurantName
            OurUsers seller = ourUsersRepository.findByRestaurantName(restaurantName);
            if (seller != null) {
                byte[] image = seller.getImage(); // Get the seller's image
                String encodedImage = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
                
                // Add seller's details along with the image to the DTO list
                sellers.add(new SellerDTO(
                    restaurantName, // sellerName
                    encodedImage    // Seller's actual image
                ));
            }
        }

        return sellers;
    }
    
    @GetMapping("reviews/item/{itemId}")
    public List<ItemReview> getReviewsForItem(@PathVariable Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return reviewRepository.findByItem(item.get());
        }
        throw new RuntimeException("Item not found with ID: " + itemId);
    }

    // Add or update a review
//    @PostMapping("/item/{itemId}")
//    public ItemReview addOrUpdateReview(@PathVariable Long itemId, @RequestBody ReviewRequest reviewRequest) {
//        // Fetch the item based on ID
//        Optional<Item> item = itemRepository.findById(itemId);
//        if (item.isEmpty()) {
//            throw new RuntimeException("Item not found with ID: " + itemId);
//        }
//
//        // Fetch the user based on the email sent in the request
//        Optional<OurUsers> user = ourUsersRepository.findByEmail(reviewRequest.getEmail());
//        if (user.isEmpty()) {
//            throw new RuntimeException("User not found with email: " + reviewRequest.getEmail());
//        }
//
//        // Check if the user already reviewed the item
//        Optional<ItemReview> existingReview = Optional.ofNullable(reviewRepository.findByItemAndUser(item.get(), user.get()));
//
//        if (existingReview.isPresent()) {
//            // Update the existing review
//            ItemReview review = existingReview.get();
//            review.setComment(reviewRequest.getComment());
//            review.setRating(reviewRequest.getRating());
//            return reviewRepository.save(review);
//        }
//
//        // Save a new review
//        ItemReview newReview = new ItemReview();
//        newReview.setItem(item.get());
//        newReview.setUser(user.get());
//        newReview.setComment(reviewRequest.getComment());
//        newReview.setRating(reviewRequest.getRating());
//        return reviewRepository.save(newReview);
//    }
    
    
    
    @PostMapping("/item/{itemId}")
    public ItemReview addOrUpdateReview(@PathVariable Long itemId, @RequestBody ReviewRequest reviewRequest) {
        // Fetch the item based on ID
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }

        // Fetch the user based on the email sent in the request
        Optional<OurUsers> user = ourUsersRepository.findByEmail(reviewRequest.getEmail());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with email: " + reviewRequest.getEmail());
        }

        // Check if the user already reviewed the item
        ItemReview existingReview = reviewRepository.findByItemAndUser(item.get(), user.get());

        if (existingReview != null) {
            // Update the existing review
            existingReview.setComment(reviewRequest.getComment());
            existingReview.setRating(reviewRequest.getRating());
            existingReview.setLastUpdatedDate(LocalDateTime.now()); // Update the last updated date
            return reviewRepository.save(existingReview);
        } else {
            // Save a new review
            ItemReview newReview = new ItemReview();
            newReview.setItem(item.get());
            newReview.setUser(user.get());
            newReview.setComment(reviewRequest.getComment());
            newReview.setRating(reviewRequest.getRating());
            newReview.setReviewDate(LocalDateTime.now()); // Set the review creation date
            newReview.setLastUpdatedDate(LocalDateTime.now()); // Initialize lastUpdatedDate to the same value as reviewDate
            return reviewRepository.save(newReview);
        }
    }

    
    
    
    // Get all reviews for a specific item along with the average rating
    @GetMapping("new/item/{itemId}")
    public ItemReviewsResponse getReviewsForItems(@PathVariable Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }

        List<ItemReview> reviews = reviewRepository.findByItem(item.get());
        double averageRating = calculateAverageRating(reviews);

        return new ItemReviewsResponse(reviews, averageRating);
    }
    
    
    @GetMapping("/highest-rated-reviews")
    public List<ItemReviewResponse> getHighestRatedReviews() {
        List<Item> items = itemRepository.findAll();
        List<ItemReviewResponse> responseList = new ArrayList<>();

        for (Item item : items) {
            List<ItemReview> reviews = reviewRepository.findByItem(item);
            if (!reviews.isEmpty()) {
                ItemReview highestRatedReview = reviews.stream()
                    .max(Comparator.comparing(ItemReview::getRating))
                    .orElse(null);

                if (highestRatedReview != null) {
                    responseList.add(new ItemReviewResponse(item, highestRatedReview));
                }
            }
        }

        return responseList;
    }
    
    
    @GetMapping("/latest-highest-rated-reviews")
    public List<ItemReviewResponse> getHighestRatedReviewss() {
        List<Item> items = itemRepository.findAll();
        List<ItemReviewResponse> responseList = new ArrayList<>();

        for (Item item : items) {
            // Fetch all reviews for the item
            List<ItemReview> reviews = reviewRepository.findByItem(item);
            
            if (!reviews.isEmpty()) {
                // Find the review with the highest rating and latest updated date, handling null values
                ItemReview highestRatedLatestReview = reviews.stream()
                    .filter(review -> review.getLastUpdatedDate() != null)  // Filter out null dates
                    .sorted(Comparator.comparing(ItemReview::getLastUpdatedDate, Comparator.nullsLast(Comparator.reverseOrder())))  // Handle null values gracefully
                    .max(Comparator.comparing(ItemReview::getRating))  // Get the highest-rated
                    .orElse(null);

                if (highestRatedLatestReview != null) {
                    // Construct ItemReviewResponse and add it to the list
                    responseList.add(new ItemReviewResponse(item, highestRatedLatestReview));
                }
            }
        }

        return responseList;
    }


//    
//    @GetMapping("/latest-highest-rated-reviews")
//    public List<ItemReviewResponse> getlatestHighestRatedReviews() {
//        List<Item> items = itemRepository.findAll();
//        List<ItemReviewResponse> responseList = new ArrayList<>();
//
//        for (Item item : items) {
//            // Fetch all reviews for the item
//            List<ItemReview> reviews = reviewRepository.findByItem(item);
//            
//            if (!reviews.isEmpty()) {
//                // Find the highest-rated review that is also the most recent
//                ItemReview latestHighestRatedReview = reviews.stream()
//                    .max(Comparator.comparing(ItemReview::getRating)
//                            .thenComparing(ItemReview::getLastUpdatedDate, Comparator.nullsLast(Comparator.naturalOrder())))
//                    .orElse(null);
//
//                if (latestHighestRatedReview != null) {
//                    responseList.add(new ItemReviewResponse(item, latestHighestRatedReview));
//                }
//            }
//        }
//
//        return responseList;
//    }

    
    @GetMapping("/latest-highest-rated-review")
    public List<ItemReviewResponse> getHighestRatedReview() {
    	  List<Item> items = itemRepository.findAll();
    	    List<ItemReviewResponse> responseList = new ArrayList<>();

    	    for (Item item : items) {
    	        // Fetch all reviews for the item
    	        List<ItemReview> reviews = reviewRepository.findByItem(item);
    	        
    	        if (!reviews.isEmpty()) {
    	            // Find the review with the highest rating and latest updated date, handling null values
    	            ItemReview highestRatedLatestReview = reviews.stream()
    	                .filter(review -> review.getLastUpdatedDate() != null) // Filter out null dates
    	                .sorted(Comparator.comparing(ItemReview::getLastUpdatedDate, Comparator.nullsLast(Comparator.reverseOrder()))) // Handle null values gracefully
    	                .max(Comparator.comparing(ItemReview::getRating)) // Get the highest-rated
    	                .orElse(null);

    	            if (highestRatedLatestReview != null) {
    	                responseList.add(new ItemReviewResponse(item, highestRatedLatestReview));
    	            }
    	        }
    	    }

    	    return responseList;
    }

    
    
    // Method to calculate average rating from a list of reviews
    private double calculateAverageRating(List<ItemReview> reviews) {
        if (reviews.isEmpty()) {
            return 0.0; // Avoid division by zero if there are no reviews
        }
        double totalRating = reviews.stream().mapToDouble(ItemReview::getRating).sum();
        return totalRating / reviews.size();
    } 
    
    
    
    
    @GetMapping
    public List<Favorite> getFavorites(@RequestParam String email) {
        OurUsers user = ourUsersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return favoriteRepository.findByUser(user);
    }
    
    @PostMapping("ad/{itemId}")
    public Favorite addFavorite(@PathVariable Long itemId, @RequestBody Map<String, String> payload) {
        String userEmail = payload.get("email");
        OurUsers user = ourUsersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        Favorite favorite = new Favorite(user, item);
        return favoriteRepository.save(favorite);
    }

    // Remove a favorite item for the user
    @DeleteMapping("del/{itemId}")
    public void removeFavorite(@PathVariable Long itemId, @RequestBody Map<String, String> payload) {
        String userEmail = payload.get("email");
        OurUsers user = ourUsersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Favorite favorite = favoriteRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        favoriteRepository.delete(favorite);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    // Get all favorites by the user
//    @GetMapping
//    public List<Favorite> getFavorites(Principal principal) {
//        String userEmail = principal.getName();
//        OurUsers user = ourUsersRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        return favoriteRepository.findByUser(user);
//    }
//
//    // Add a favorite item for the user
//    @PostMapping("/{itemId}")
//    public Favorite addFavorite(@PathVariable Long itemId, Principal principal) {
//        String userEmail = principal.getName();
//        OurUsers user = ourUsersRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new RuntimeException("Item not found"));
//        Favorite favorite = new Favorite(user, item);
//        return favoriteRepository.save(favorite);
//    }
//
//    // Remove a favorite by its ID
//    @DeleteMapping("/{favoriteId}")
//    public void removeFavorite(@PathVariable Long favoriteId) {
//        favoriteRepository.deleteById(favoriteId);
//    }
//    
    
    
    
    
    
    
//    @PutMapping("/admin/edit/{id}")
//    public ResponseEntity<Item> editItem(@PathVariable Long id,
//                                         @RequestParam("name") String name,
//                                         @RequestParam("price") int price,
//                                         @RequestParam("category") Long categoryId,
//                                         @RequestParam("sellerName") String sellerName,
//                                         @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
//        Optional<Item> itemOptional = itemRepository.findById(id);
//        if (itemOptional.isPresent()) {
//            Item item = itemOptional.get();
//            item.setName(name);
//            item.setPrice(price);
//            item.setCategory(category);
//            item.setSellerName(sellerName);
//            
//            if (image != null && !image.isEmpty()) {
//                item.setImage(image.getBytes());
//            }
//            Item updatedItem = itemRepository.save(item);
//            return ResponseEntity.ok(updatedItem);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

}