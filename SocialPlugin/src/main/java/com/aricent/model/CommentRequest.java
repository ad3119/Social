package com.aricent.model;

public class CommentRequest {
	private String parCommentId;
	private String commenturl;
	private String comuserName;
	private String comuserAvatar;
	private String comsocialloginName;
	private String commentstring;
	private String userBaseId;
	
	
	public String getUserBaseId() {
		return userBaseId;
	}
	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}
	public String getParCommentId() {
		return parCommentId;
	}
	public void setParCommentId(String parCommentId) {
		this.parCommentId = parCommentId;
	}
	public String getCommenturl() {
		return commenturl;
	}
	public void setCommenturl(String commenturl) {
		this.commenturl = commenturl;
	}
	public String getComuserName() {
		return comuserName;
	}
	public void setComuserName(String comuserName) {
		this.comuserName = comuserName;
	}
	public String getComuserAvatar() {
		return comuserAvatar;
	}
	public void setComuserAvatar(String comuserAvatar) {
		this.comuserAvatar = comuserAvatar;
	}
	public String getComsocialloginName() {
		return comsocialloginName;
	}
	public void setComsocialloginName(String comsocialloginName) {
		this.comsocialloginName = comsocialloginName;
	}
	public String getCommentstring() {
		return commentstring;
	}
	public void setCommentstring(String commentstring) {
		this.commentstring = commentstring;
	}
	
	
}
