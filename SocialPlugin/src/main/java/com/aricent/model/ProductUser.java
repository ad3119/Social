/**
 * 
 */
package com.aricent.model;

import java.util.Comparator;
import java.util.Date;

import org.springframework.data.domain.Sort.Order;

/**
 * @author bgh39679
 *
 */
public class ProductUser implements Comparable<ProductUser>{

	private String userBase;
	private String product;
	private Integer viewedCount;
	private Boolean addedToCart;
	private Integer quantity;
	private String price;
	private String	productId;
	private Date viewedTime;

	public ProductUser() {
		super();
	}
	public String getUserBase() {
		return userBase;
	}
	public void setUserBase(String userBase) {
		this.userBase = userBase;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public Integer getViewedCount() {
		return viewedCount;
	}
	public void setViewedCount(Integer viewedCount) {
		this.viewedCount = viewedCount;
	}
	public Boolean getAddedToCart() {
		return addedToCart;
	}
	public void setAddedToCart(Boolean addedToCart) {
		this.addedToCart = addedToCart;
	}

	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Date getViewedTime() {
		return viewedTime;
	}
	public void setViewedTime(Date viewedTime) {
		this.viewedTime = viewedTime;
	}

	public static class OrderByViewedcount implements Comparator<ProductUser> {
		
		@Override
		public int compare(ProductUser prodUser1, ProductUser prodUser2) {
			return prodUser2.viewedCount > prodUser1.viewedCount ? 1 : (prodUser2.viewedCount < prodUser1.viewedCount ? -1 : 0);
		}
	}
	public static class OrderByViewedtime implements Comparator<ProductUser> {
		
		@Override
		public int compare(ProductUser prodUser1, ProductUser prodUser2) {
			Date comparetime = prodUser1.viewedTime;
			return prodUser2.viewedTime.compareTo(comparetime);
		}
	}
	@Override
	public int compareTo(ProductUser o) {
		return o.viewedCount > this.viewedCount ? 1 : (o.viewedCount < this.viewedCount ? -1 : 0);
	}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
}
