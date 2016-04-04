package com.aricent.gamification.model;

import java.util.Date;

/**
 * @author BGH34973 - Himalaya Gupta
 * 
 *         The Rule Entity
 * 
 */
public class Rule {

	private String ruleId;
	private String ruleName;
	private String ruleDesc;
	private String activityType;
	private String ruleOperator;
	private String actionValue;
	private String applyPeriod;
	private String priority;
	private Date updateDate;
	private String status;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleDesc() {
		return ruleDesc;
	}

	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getRuleOperator() {
		return ruleOperator;
	}

	public void setRuleOperator(String ruleOperator) {
		this.ruleOperator = ruleOperator;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
	}

	public String getApplyPeriod() {
		return applyPeriod;
	}

	public void setApplyPeriod(String applyPeriod) {
		this.applyPeriod = applyPeriod;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
