package com.aricent.model;

public class SocialMediaType {
	private String userBaseId;
	private String socialMediatypeId;
	private String socialMediaId;
	private String accessToken;
	private String profilePicUrl;

	public String getUserBaseId() {
		return userBaseId;
	}

	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}

	public String getSocialMediatypeId() {
		return socialMediatypeId;
	}

	public void setSocialMediatypeId(String socialMediatypeId) {
		this.socialMediatypeId = socialMediatypeId;
	}

	public String getSocialMediaId() {
		return socialMediaId;
	}

	public void setSocialMediaId(String socialMediaId) {
		this.socialMediaId = socialMediaId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

}
