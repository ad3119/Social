package com.aricent.model;

import java.util.List;

public class Recommendations {

	private Integer userId;
	private List<Integer> products;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public List<Integer> getProducts() {
		return products;
	}
	public void setProducts(List<Integer> products) {
		this.products = products;
	}
	
	
}
