package com.example.practice.res;
//
//public class ItemSearchResult {
//   
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	private Long id;
//    private String name;
//    private String sellerName;
//	public String getSellerName() {
//		return sellerName;
//	}
//	public void setSellerName(String sellerName) {
//		this.sellerName = sellerName;
//	}
//    private byte[] image;
//	public byte[] getImage() {
//		return image;
//	}
//	public void setImage(byte[] image) {
//		this.image = image;
//	}
//	public ItemSearchResult(Long id, String name, String sellerName, byte[] image) {
//		super();
//		this.id = id;
//		this.name = name;
//		this.sellerName = sellerName;
//		this.image = image;
//	}
//
//}
public class ItemSearchResult {
    private Long itemId;
    private String name;
    private String sellerName;
	private byte[] image;
    private boolean isSeller;
    private String restaurantName;
    private int price;

    public ItemSearchResult(Long itemId, String name, String sellerName, byte[] image, boolean isSeller,
			String restaurantName, int price, boolean discountActive, int discountPercentage, int previousAmount) {
		super();
		this.itemId = itemId;
		this.name = name;
		this.sellerName = sellerName;
		this.image = image;
		this.isSeller = isSeller;
		this.restaurantName = restaurantName;
		this.price = price;
		this.discountActive = discountActive;
		this.discountPercentage = discountPercentage;
		this.previousAmount = previousAmount;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isDiscountActive() {
		return discountActive;
	}

	public void setDiscountActive(boolean discountActive) {
		this.discountActive = discountActive;
	}

	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public int getPreviousAmount() {
		return previousAmount;
	}

	public void setPreviousAmount(int previousAmount) {
		this.previousAmount = previousAmount;
	}

	private boolean discountActive;
    private int discountPercentage;
    private int previousAmount;  // New field to store the original price

    
    public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public boolean isSeller() {
		return isSeller;
	}

	public void setSeller(boolean isSeller) {
		this.isSeller = isSeller;
	}

	public ItemSearchResult(Long itemId, String name, String sellerName, byte[] image, boolean isSeller,
			String restaurantName) {
		super();
		this.itemId = itemId;
		this.name = name;
		this.sellerName = sellerName;
		this.image = image;
		this.isSeller = isSeller;
		this.restaurantName = restaurantName;
	}



  
    // Getters and setters
}
