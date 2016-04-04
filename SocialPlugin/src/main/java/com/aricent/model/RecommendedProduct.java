package com.aricent.model;

public class RecommendedProduct {

	private String productName;
	private Integer productId;
	private String imageUrl;
	private String productDesc;

	public RecommendedProduct() {

	}

	public RecommendedProduct(int id, String name, String desc, String url) {
		this.productId = id;
		this.productName = name;
		this.productDesc = desc;
		this.imageUrl = url;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

}
