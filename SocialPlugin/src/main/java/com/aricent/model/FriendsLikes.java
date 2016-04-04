package com.aricent.model;

import java.util.List;

public class FriendsLikes {
	private String name;
	private List<SocialMeidaAttrValue> socialMediaAttrvalueList;
	private String email;
	private String stateId;
	private String profile_pic_url;
	
	
	public String getProfile_pic_url() {
		return profile_pic_url;
	}
	public void setProfile_pic_url(String profile_pic_url) {
		this.profile_pic_url = profile_pic_url;
	}
	public String getStateId() {
		return stateId;
	}
	public void setStateId(String stateId) {
		this.stateId = stateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SocialMeidaAttrValue> getSocialMediaAttrvalueList() {
		return socialMediaAttrvalueList;
	}
	public void setSocialMediaAttrvalueList(
			List<SocialMeidaAttrValue> socialMediaAttrvalueList) {
		this.socialMediaAttrvalueList = socialMediaAttrvalueList;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
