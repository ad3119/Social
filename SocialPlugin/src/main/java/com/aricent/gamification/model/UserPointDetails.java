package com.aricent.gamification.model;

import com.aricent.model.ProductUser;

public class UserPointDetails implements Comparable<UserPointDetails> {

	private String userBaseId;
	private int totalPoints;
	private String username;
	private String profile_pic_url;

	public String getUserBaseId() {
		return userBaseId;
	}

	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProfile_pic_url() {
		return profile_pic_url;
	}

	public void setProfile_pic_url(String profile_pic_url) {
		this.profile_pic_url = profile_pic_url;
	}

	@Override
	public int compareTo(UserPointDetails usrpoints) {
		return usrpoints.totalPoints > this.totalPoints ? 1
				: (usrpoints.totalPoints < this.totalPoints ? -1 : 0);
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
