package com.aricent.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

public class UserReview implements Comparable{
	@Id
	private String commentId;
	private int urlId;
	private String url;
	private int typeId;
	private String parentCommentId;
	private String userName;
	private String userAvatar;
	private int socialMediaAttrId;
	private Date dateTime;
	private String commentTitle;
	private String userComment;
	private float userRating;
	public String userBaseId;
	private int numofreplies;
	private int numUpvotes;
	private int numDownvotes;
	private List<String> upvotesBy;
	private List<String> downvotesBy;
	
	public UserReview() {
		super();
	}
	
	public List<String> getDownvotesBy() {
		return downvotesBy;
	}
	public void setDownvotesBy(List<String> downvotesBy) {
		this.downvotesBy = downvotesBy;
	}
	public int getNumDownvotes() {
		return numDownvotes;
	}
	public void setNumDownvotes(int numDownvotes) {
		this.numDownvotes = numDownvotes;
	}
	
	public List<String> getUpvotesBy() {
		return upvotesBy;
	}
	public void setUpvotesBy(List<String> upvotesBy) {
		this.upvotesBy = upvotesBy;
	}
	public int getNumUpvotes() {
		return numUpvotes;
	}
	public void setNumUpvotes(int numUpvotes) {
		this.numUpvotes = numUpvotes;
	}
	public int getNumofreplies() {
		return numofreplies;
	}
	public void setNumofreplies(int numofreplies) {
		this.numofreplies = numofreplies;
	}
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public int getUrlId() {
		return urlId;
	}
	public void setUrlId(int urlId) {
		this.urlId = urlId;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getParentCommentId() {
		return parentCommentId;
	}
	public void setParentCommentId(String parentCommentId) {
		this.parentCommentId = parentCommentId;
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
	public int getSocialMediaAttrId() {
		return socialMediaAttrId;
	}
	public void setSocialMediaAttrId(int socialMediaAttrId) {
		this.socialMediaAttrId = socialMediaAttrId;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public int compareTo(Object usrReview) {
		Date comparetime=((UserReview)usrReview).getDateTime();
        /* For Ascending order*/
        return comparetime.compareTo(this.dateTime);
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