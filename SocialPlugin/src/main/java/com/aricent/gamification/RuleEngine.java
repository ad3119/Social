package com.aricent.gamification;

import java.util.List;

import com.aricent.gamification.model.Badge;
import com.aricent.gamification.model.Rule;
import com.aricent.gamification.model.UserPointDetails;
import com.aricent.gamification.model.UserPoints;
import com.aricent.model.Activity;

/**
 * @author BGH34973 - Himalaya Gupta
 * 
 */
public interface RuleEngine {

	/**
	 * Allocate point based on the userAction
	 * 
	 * @param userBaseId
	 *            - userBaseId of the user
	 * @param userAction
	 *            - Ex. - GamificationConstants.ACTION_LOGIN
	 */
	public void allocatePoint(Activity activityLog);

	/**
	 * Allocate Badges to the user based on the points earned
	 * 
	 * @param userBaseId
	 *            - userBaseId of the user
	 */
	public void allocateBadges(String userBaseId);

	/**
	 * Get the points earned for a user
	 * 
	 * @param userBaseId
	 *            - userBaseId of the user
	 * @return - The total points earned by an user.
	 */
	public UserPointDetails getUserPoints(String userBaseId);

	/**
	 * Get the badges earned by the user
	 * 
	 * @param userBaseId
	 *            - userBaseId of the user
	 * @return The list of badges earned by user
	 */
	public List<Badge> getUserBadges(String userBaseId);

	/**
	 * Get the leader board content. Gives the list of top ten leaders based on
	 * the points.
	 */
	public List<UserPointDetails> getLeaderBoard();

	/**
	 * Get the list of available badges
	 */
	public List<Badge> getBadges();

	/**
	 * Create a new rule
	 * 
	 * @param rule
	 *            - An Rule object
	 * @return Returns if rule created successfully
	 */
	public int createRule(Rule rule);

	/**
	 * Get all the available rules
	 * 
	 * @return the list of rules created
	 */
	public List<Rule> getRules();

	public int createBadge(Badge badge);

	public int editRule(Rule rule);

	public int deleteRule(String ruleId);

	public int editBadge(Badge badge);

	public int deleteBadge(String badgeId);

}
