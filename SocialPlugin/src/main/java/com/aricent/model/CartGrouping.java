package com.aricent.model;

public class CartGrouping {

	private String productId;
	private Integer groupedCount;
	
	
	public Integer getGroupedCount() {
		return groupedCount;
	}
	
	public void setGroupedCount(Integer groupedCount) {
		this.groupedCount = groupedCount;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
}
