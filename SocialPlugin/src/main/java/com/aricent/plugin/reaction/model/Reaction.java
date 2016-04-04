package com.aricent.plugin.reaction.model;

import java.util.Date;

/**
 * @author BGH34973 - Himalaya Gupta
 * 
 */
public class Reaction {

	private String reactId;
	private String reactName;
	private String reactUrl;
	private Date dtUpdated;

	public Date getDtUpdated() {
		return dtUpdated;
	}

	public void setDtUpdated(Date dtUpdated) {
		this.dtUpdated = dtUpdated;
	}

	public String getReactId() {
		return reactId;
	}

	public void setReactId(String reactId) {
		this.reactId = reactId;
	}

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
}
