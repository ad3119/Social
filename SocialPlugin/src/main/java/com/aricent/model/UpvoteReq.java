package com.aricent.model;

public class UpvoteReq {

	private String commentId;
	private String upvoteBy;
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getUpvoteBy() {
		return upvoteBy;
	}
	public void setUpvoteBy(String upvoteBy) {
		this.upvoteBy = upvoteBy;
	}
	
	
}
