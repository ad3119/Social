package com.aricent.plugin.reaction.model;

import org.springframework.data.annotation.Id;

public class CumulativeReaction {

	@Id
	private String reactId;
	private int reactCount;
	private String reactUrl;
	private String reactName;

	public String getReactName() {
		return reactName;
	}

	public void setReactName(String reactName) {
		this.reactName = reactName;
	}

	public String getReactUrl() {
		return reactUrl;
	}

	public void setReactUrl(String reactUrl) {
		this.reactUrl = reactUrl;
	}

	public String getReactId() {
		return reactId;
	}

	public void setReactId(String reactId) {
		this.reactId = reactId;
	}

	public int getReactCount() {
		return reactCount;
	}

	public void setReactCount(int reactCount) {
		this.reactCount = reactCount;
	}
}
