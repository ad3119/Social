package com.aricent.gamification.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class UserPoints {

	private String userBaseId;
	private int totalPoints;
	private int userAction;
	private int userActionPeriod;
	private Date allocationDate;
	private int isNotified;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIsNotified() {
		return isNotified;
	}

	public void setIsNotified(int isNotified) {
		this.isNotified = isNotified;
	}

	public UserPoints() {

	}

	public UserPoints(String userId, int points, int action, int period,
			Date allocDate) {
		this.userBaseId = userId;
		this.totalPoints = points;
		this.userAction = action;
		this.userActionPeriod = period;
		this.allocationDate = allocDate;
	}

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public int getUserActionPeriod() {
		return userActionPeriod;
	}

	public void setUserActionPeriod(int userActionPeriod) {
		this.userActionPeriod = userActionPeriod;
	}

	public Date getAllocationDate() {
		return allocationDate;
	}

	public void setAllocationDate(Date allocationDate) {
		this.allocationDate = allocationDate;
	}

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

}
