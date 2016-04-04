package com.aricent.plugin.reaction.model;

import java.util.Date;

public class UserReaction {

	private String userBaseId;
	private String pageId;
	private String reactId;
	private Date dtReacted;

	public String getUserBaseId() {
		return userBaseId;
	}

	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getReactId() {
		return reactId;
	}

	public void setReactId(String reactId) {
		this.reactId = reactId;
	}

	public Date getDtReacted() {
		return dtReacted;
	}

	public void setDtReacted(Date dtReacted) {
		this.dtReacted = dtReacted;
	}

}
