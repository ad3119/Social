package com.aricent.model;

public class ReviewUpdateReq {
	private String url;
	private String userName;
	private String userAvatar;
	private String socialMediaAttrName;
	private String commentTitle;
	private String userComment;
	private float userRating;
	private String userBaseId;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserAvatar() {
		return userAvatar;
	}
	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}
	public String getSocialMediaAttrName() {
		return socialMediaAttrName;
	}
	public void setSocialMediaAttrName(String socialMediaAttrName) {
		this.socialMediaAttrName = socialMediaAttrName;
	}
	public String getCommentTitle() {
		return commentTitle;
	}
	public void setCommentTitle(String commentTitle) {
		this.commentTitle = commentTitle;
	}
	public String getUserComment() {
		return userComment;
	}
	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}
	public float getUserRating() {
		return userRating;
	}
	public void setUserRating(float userRating) {
		this.userRating = userRating;
	}
	public String getUserBaseId() {
		return userBaseId;
	}
	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}
}