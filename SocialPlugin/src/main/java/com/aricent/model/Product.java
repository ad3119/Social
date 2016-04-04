package com.aricent.model;

import java.util.List;
import java.util.Date;



public class Product implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5287951213294232267L;
	private String id;
	private String productName;
	private float productRating;
	private transient String description;
	private String category;
	private String price;
	//private transient List<Specs> specs;
	private Date productCreated;
	private Date productLastUpdated;
	//private List<ProductUser> productUserList;
	private Integer totalNumberOfRatings;
	
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getid() {
		return id;
	}
	
	public void setid(String id) {
		this.id = id;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public Date getProductCreated() {
		return productCreated;
	}
	public void setProductCreated(Date productCreated) {
		this.productCreated = productCreated;
	}
	public Date getProductLastUpdated() {
		return productLastUpdated;
	}
	public void setProductLastUpdated(Date productLastUpdated) {
		this.productLastUpdated = productLastUpdated;
	}
	public float getProductRating() {
		return productRating;
	}
	public void setProductRating(float productRating) {
		this.productRating = productRating;
	}
	
	public Integer getTotalNumberOfRatings() {
		return totalNumberOfRatings;
	}

	public void setTotalNumberOfRatings(Integer totalNumberOfRatings) {
		this.totalNumberOfRatings = totalNumberOfRatings;
	}
	
}