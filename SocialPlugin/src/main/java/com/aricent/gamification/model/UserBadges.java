package com.aricent.gamification.model;

import java.util.List;

public class UserBadges {

	private String userBaseId;
	private List<String> userBadgeList;

	public String getUserBaseId() {
		return userBaseId;
	}

	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}

	public List<String> getUserBadgeList() {
		return userBadgeList;
	}

	public void setUserBadgeList(List<String> userBadgeList) {
		this.userBadgeList = userBadgeList;
	}

}
