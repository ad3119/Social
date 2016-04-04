package com.aricent.model;

public class DownvoteReq {

	private String commentId;
	private String downvoteBy;
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getDownvoteBy() {
		return downvoteBy;
	}
	public void setDownvoteBy(String downvoteBy) {
		this.downvoteBy = downvoteBy;
	}
	
	
}