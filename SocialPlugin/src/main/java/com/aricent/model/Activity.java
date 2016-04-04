package com.aricent.model;

import java.util.Date;

public class Activity {

	private String userBaseId;
	private String socialMediaId;
	private String activityType;
	private Date activityTimeStamp;
	
	public String getUserBaseId() {
		return userBaseId;
	}
	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}
	public String getSocialMediaId() {
		return socialMediaId;
	}
	public void setSocialMediaId(String socialMediaId) {
		this.socialMediaId = socialMediaId;
	}
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	public Date getActivityTimeStamp() {
		return activityTimeStamp;
	}
	public void setActivityTimeStamp(Date activityTimeStamp) {
		this.activityTimeStamp = activityTimeStamp;
	}
	
	
}
