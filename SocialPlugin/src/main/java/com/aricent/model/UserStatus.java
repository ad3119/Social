package com.aricent.model;

import java.util.Date;

public class UserStatus {

	private String userBaseId;
	private String stateId;
	private Date lastLoginTime;
	private Date currentLoginTime;
	private int cumulativeloginCount;
	public String getUserBaseId() {
		return userBaseId;
	}
	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}
	public String getStateId() {
		return stateId;
	}
	public void setStateId(String stateId) {
		this.stateId = stateId;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Date getCurrentLoginTime() {
		return currentLoginTime;
	}
	public void setCurrentLoginTime(Date currentLoginTime) {
		this.currentLoginTime = currentLoginTime;
	}
	public int getCumulativeloginCount() {
		return cumulativeloginCount;
	}
	public void setCumulativeloginCount(int i) {
		this.cumulativeloginCount = i;
	}

	
}
