package com.aricent.spark.models;

import java.io.Serializable;

public class UserProductRating implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer ratingid;
	private Integer userid;
	private Integer productid;
	private Double rating;

	public static UserProductRating newInstance(Integer ratingid, Integer userid, Integer productid, Double rating) {
		UserProductRating upr = new UserProductRating();
		upr.setRatingid(ratingid);
		upr.setUserid(userid);
		upr.setProductid(productid);
		upr.setRating(rating);
		return upr;
	}

	public Integer getRatingid() {
		return ratingid;
	}

	public void setRatingid(Integer ratingid) {
		this.ratingid = ratingid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getProductid() {
		return productid;
	}

	public void setProductid(Integer productid) {
		this.productid = productid;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}
}
