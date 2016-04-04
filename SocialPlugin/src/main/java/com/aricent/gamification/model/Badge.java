package com.aricent.gamification.model;

/**
 * @author BGH34973 - Himalaya Gupta
 * 
 *         The Badge Entity
 * 
 */
public class Badge {

	private String badgeId;
	private String badgeName;
	private String activeBadgeUrl;
	private String disabledBadgeUrl;
	private String activationPoints;

	public String getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}

	public String getBadgeName() {
		return badgeName;
	}

	public void setBadgeName(String badgeName) {
		this.badgeName = badgeName;
	}

	public String getActiveBadgeUrl() {
		return activeBadgeUrl;
	}

	public void setActiveBadgeUrl(String activeBadgeUrl) {
		this.activeBadgeUrl = activeBadgeUrl;
	}

	public String getDisabledBadgeUrl() {
		return disabledBadgeUrl;
	}

	public void setDisabledBadgeUrl(String disabledBadgeUrl) {
		this.disabledBadgeUrl = disabledBadgeUrl;
	}

	public String getActivationPoints() {
		return activationPoints;
	}

	public void setActivationPoints(String activationPoints) {
		this.activationPoints = activationPoints;
	}

}
