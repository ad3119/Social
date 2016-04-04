package com.aricent.model;

import org.springframework.data.annotation.Id;


public class UrlRatingRef {
	@Id
	private String url;
	private int totNumRating;
	private float averageRating;
	private int urlId;
	private String lastCommentId;
	
	public int getTotNumRating() {
		return totNumRating;
	}
	public void setTotNumRating(int totNumRating) {
		this.totNumRating = totNumRating;
	}
	public float getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getUrlId() {
		return urlId;
	}
	public void setUrlId(int urlId) {
		this.urlId = urlId;
	}
	public String getLastCommentId() {
		return lastCommentId;
	}
	public void setLastCommentId(String lastCommentId) {
		this.lastCommentId = lastCommentId;
	}
}
