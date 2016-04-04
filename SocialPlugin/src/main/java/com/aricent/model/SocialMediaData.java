package com.aricent.model;

import java.util.List;

public class SocialMediaData {
	private String userBaseId;
	private String socialMediaAttrTypeId;
	private List<SocialMeidaAttrValue> socialMediaAttrvalueList;

	
	public String getUserBaseId() {
		return userBaseId;
	}
	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}
	public String getSocialMediaAttrTypeId() {
		return socialMediaAttrTypeId;
	}
	public void setSocialMediaAttrTypeId(String socialMediaAttrTypeId) {
		this.socialMediaAttrTypeId = socialMediaAttrTypeId;
	}
	public List<SocialMeidaAttrValue> getSocialMediaAttrvalueList() {
		return socialMediaAttrvalueList;
	}
	public void setSocialMediaAttrvalueList(
			List<SocialMeidaAttrValue> socialMediaAttrvalueList) {
		this.socialMediaAttrvalueList = socialMediaAttrvalueList;
	}



}
