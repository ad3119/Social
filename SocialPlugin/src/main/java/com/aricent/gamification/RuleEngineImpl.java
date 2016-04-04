package com.aricent.gamification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlTemplate;
import org.springframework.data.domain.Sort.Direction;
import org.apache.commons.lang3.time.DateUtils;

import com.aricent.constant.GamificationConstants;
import com.aricent.constant.GlobalConstants;
import com.aricent.gamification.model.Badge;
import com.aricent.gamification.model.Rule;
import com.aricent.gamification.model.UserBadges;
import com.aricent.gamification.model.UserPointDetails;
import com.aricent.gamification.model.UserPoints;
import com.aricent.model.Activity;
import com.aricent.model.SocialMediaType;
import com.aricent.model.UserBase;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Ordering;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;

/**
 * @author BGH34973 - Himalaya Gupta
 * 
 *         A Rule Engine Implementation
 * 
 */
public class RuleEngineImpl implements RuleEngine {

	private static final Logger logger = Logger.getLogger(RuleEngineImpl.class);

	@Autowired
	private CqlTemplate cassandraTemplate;
	public static final String USER_KEYSPACE = "user_tables";
	private static final String RULE_COLLECTION = "rules";
	private static final String USER_POINTS_COLLECTION = "user_points";
	private static final String BADGE_COLLECTION = "badges";
	private static final String USER_BADGE_COLLECTION = "user_badges";
	private static final String ACTIVITY_LOG_COLLECTION = "activitylog";
	private static final String USER_BASE_COLLECTION = "baselist";
	private static final String SOCIAL_MEDIA_TYPE_COLLECTION = "usersocialmediatype";

	public void init() {
		logger.info("Initializing RuleEngine");

		try {
			cassandraTemplate
					.execute("CREATE TABLE IF NOT EXISTS user_tables.user_points ("
							+ "userBaseId text,"
							+ "totalPoints int,"
							+ "userAction int,"
							+ "userActionPeriod int,"
							+ "allocationDate timestamp,"
							+ "isNotified int,"
							+ "id text," + "PRIMARY KEY(userBaseId,id)" + ");");
			cassandraTemplate
					.execute("CREATE TABLE IF NOT EXISTS user_tables.rules ("
							+ "ruleId text PRIMARY KEY," + "ruleName text,"
							+ "ruleDesc text," + "activityType text,"
							+ "ruleOperator text," + "actionValue text,"
							+ "applyPeriod text," + "priority text,"
							+ "updateDate timestamp," + "status text" + ");");

			logger.info("Created rules.");

			cassandraTemplate
					.execute("CREATE TABLE IF NOT EXISTS user_tables.badges ("
							+ "badgeId text PRIMARY KEY," + "badgeName text,"
							+ "activeBadgeUrl text," + "disabledBadgeUrl text,"
							+ "activationPoints text," + ");");

			logger.info("Created badges.");

			cassandraTemplate
					.execute("CREATE TABLE IF NOT EXISTS user_tables.user_badges ("
							+ "userBaseId text PRIMARY KEY,"
							+ "userBadgeList list<text>" + ");");

			logger.info("Created user_badges.");

		} catch (Exception e) {
			logger.info("Initializing Failed");
			logger.info(e.getMessage());
		}

	}

	@Override
	public void allocatePoint(Activity activityLog) {

		List<Row> rows;
		String userBaseId = activityLog.getUserBaseId();
		activityLog.setActivityTimeStamp(new Date());

		// Query to get social Media ID

		Select socialMediaTypeSelect = QueryBuilder.select().from(
				USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
		socialMediaTypeSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
		SocialMediaType socialMediaTypeData = null;
		rows = cassandraTemplate.query(socialMediaTypeSelect).all();
		for (Row r : rows) {
			socialMediaTypeData = new SocialMediaType();
			socialMediaTypeData.setUserBaseId(r.getString("userBaseId"));
			socialMediaTypeData.setSocialMediatypeId(r
					.getString("socialMediatypeId"));
			socialMediaTypeData.setSocialMediaId(r.getString("socialMediaId"));
			socialMediaTypeData.setAccessToken(r.getString("accessToken"));
			socialMediaTypeData.setProfilePicUrl(r.getString("profilePicUrl"));
		}

		if (socialMediaTypeData != null) {
			activityLog
					.setSocialMediaId(socialMediaTypeData.getSocialMediaId());
		} else {
			activityLog.setSocialMediaId(null);
		}

		int actitivtyType = Integer.parseInt(activityLog.getActivityType());
		List<Rule> ruleList = getRulesByType(activityLog.getActivityType());
		List<Activity> lstAtivityLog = null;
		UserPoints point;

		for (Rule rule : ruleList) {

			if (Integer.parseInt(rule.getStatus()) != GamificationConstants.RULE_STATUS_ACTIVE) {
				continue;
			}

			switch (Integer.parseInt(rule.getApplyPeriod())) {

			case GamificationConstants.RULE_PERIOD_EVERY:

				point = new UserPoints(userBaseId, Integer.parseInt(rule
						.getActionValue()), actitivtyType,
						GamificationConstants.RULE_PERIOD_EVERY, new Date());

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						USER_POINTS_COLLECTION);
				insert.value("userBaseId", point.getUserBaseId());
				insert.value("totalPoints", point.getTotalPoints());
				insert.value("userAction", point.getUserAction());
				insert.value("userActionPeriod", point.getUserActionPeriod());
				insert.value("allocationDate", point.getAllocationDate());
				insert.value("isNotified", point.getIsNotified());
				Random rand = new Random();
				int randomNum = rand.nextInt((1000 - 1) + 1) + 1;
				insert.value("id", Integer.toString(randomNum));
				cassandraTemplate.execute(insert);

				debug("Points allocated for the user for actitity type: "
						+ actitivtyType + " with period Every");
				break;

			case GamificationConstants.RULE_PERIOD_DAILY:

				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date dateWithoutTime = sdf.parse(sdf.format(activityLog
							.getActivityTimeStamp()));

					lstAtivityLog = getActivityLog(userBaseId, actitivtyType,
							dateWithoutTime); // Get activitylog for current Day

					if (lstAtivityLog.size() == 0) {

						point = new UserPoints(userBaseId,
								Integer.parseInt(rule.getActionValue()),
								actitivtyType,
								GamificationConstants.RULE_PERIOD_DAILY,
								new Date());

						insert = QueryBuilder.insertInto(USER_KEYSPACE,
								USER_POINTS_COLLECTION);
						insert.value("userBaseId", point.getUserBaseId());
						insert.value("totalPoints", point.getTotalPoints());
						insert.value("userAction", point.getUserAction());
						insert.value("userActionPeriod",
								point.getUserActionPeriod());
						insert.value("allocationDate",
								point.getAllocationDate());
						insert.value("isNotified", point.getIsNotified());
						cassandraTemplate.execute(insert);
						// mongoTemplate.save(point, USER_POINTS_COLLECTION);
						debug("Points allocated for the user for actitity type: "
								+ actitivtyType + " with period Every");
					}

				} catch (ParseException e) {
					logger.error(e);
				}

				break;

			case GamificationConstants.RULE_PERIOD_HOURLY:

				lstAtivityLog = getActivityLog(userBaseId, actitivtyType, null); // Get
																					// activitylog
				Date prevAcitivityDate = lstAtivityLog.get(0)
						.getActivityTimeStamp();

				long currAcitivityHr = DateUtils.getFragmentInHours(
						activityLog.getActivityTimeStamp(),
						Calendar.DAY_OF_YEAR);
				long prevAcitivityHr = DateUtils.getFragmentInHours(
						prevAcitivityDate, Calendar.DAY_OF_YEAR);

				if (currAcitivityHr - prevAcitivityHr > 0) {
					point = new UserPoints(userBaseId, Integer.parseInt(rule
							.getActionValue()), actitivtyType,
							GamificationConstants.RULE_PERIOD_HOURLY,
							new Date());
					insert = QueryBuilder.insertInto(USER_KEYSPACE,
							USER_POINTS_COLLECTION);
					insert.value("userBaseId", point.getUserBaseId());
					insert.value("totalPoints", point.getTotalPoints());
					insert.value("userAction", point.getUserAction());
					insert.value("userActionPeriod",
							point.getUserActionPeriod());
					insert.value("allocationDate", point.getAllocationDate());
					insert.value("isNotified", point.getIsNotified());
					cassandraTemplate.execute(insert);
					// mongoTemplate.save(point, USER_POINTS_COLLECTION);
					debug("Points allocated for the user for actitity type: "
							+ actitivtyType + " with period Every");
				}

				break;

			case GamificationConstants.RULE_PERIOD_MAIDEN:

				lstAtivityLog = getActivityLog(userBaseId, actitivtyType, null); // Get
																					// activitylog

				if (lstAtivityLog.size() == 0) {

					point = new UserPoints(userBaseId, Integer.parseInt(rule
							.getActionValue()), actitivtyType,
							GamificationConstants.RULE_PERIOD_MAIDEN,
							new Date());
					insert = QueryBuilder.insertInto(USER_KEYSPACE,
							USER_POINTS_COLLECTION);
					insert.value("userBaseId", point.getUserBaseId());
					insert.value("totalPoints", point.getTotalPoints());
					insert.value("userAction", point.getUserAction());
					insert.value("userActionPeriod",
							point.getUserActionPeriod());
					insert.value("allocationDate", point.getAllocationDate());
					insert.value("isNotified", point.getIsNotified());
					cassandraTemplate.execute(insert);
					// mongoTemplate.save(point, USER_POINTS_COLLECTION);
					debug("Points allocated for the user for actitity type: "
							+ actitivtyType + " with period Every");
				}

				break;

			default:
				logger.warn("No Rule Matched");
				break;
			}

		}
		Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
				ACTIVITY_LOG_COLLECTION);
		insert.value("userBaseId", activityLog.getUserBaseId());
		insert.value("socialMediaId", activityLog.getSocialMediaId());
		insert.value("activityType", activityLog.getActivityType());
		insert.value("activityTimeStamp", activityLog.getActivityTimeStamp());
		cassandraTemplate.execute(insert);
		// mongoTemplate.save(activityLog, ACTIVITY_LOG_COLLECTION);
		allocateBadges(userBaseId);
	}

	private List<Rule> getRulesByType(String ruleType) {

		List<Rule> ruleList = new ArrayList<Rule>();
		try {

			List<Row> rows;
			Select ruleSelect = QueryBuilder.select().from(USER_KEYSPACE,
					RULE_COLLECTION);
			// Query ruleQuery = new
			// Query(Criteria.where("activityType").is(Integer.toString(ruleType)));
			// ruleList = mongoTemplate.find(ruleQuery, Rule.class,
			// RULE_COLLECTION);
			rows = cassandraTemplate.query(ruleSelect).all();
			for (Row r : rows) {
				if (r.getString("activityType").equals(ruleType)) {
					Rule rule = new Rule();
					rule.setRuleId(r.getString("ruleId"));
					rule.setRuleName(r.getString("ruleName"));
					rule.setRuleDesc(r.getString("ruleDesc"));
					rule.setActivityType(r.getString("activityType"));
					rule.setRuleOperator(r.getString("ruleOperator"));
					rule.setActionValue(r.getString("actionValue"));
					rule.setApplyPeriod(r.getString("applyPeriod"));
					rule.setPriority(r.getString("priority"));
					rule.setUpdateDate(r.getDate("updateDate"));
					rule.setStatus(r.getString("status"));
					ruleList.add(rule);
				}

			}

		} catch (Exception e) {
			logger.error("Failed to retrieve the badge");
			logger.error(e);
		}

		return ruleList;
	}

	private List<Activity> getActivityLog(String userBaseId, int action,
			Date day) {

		// Aggregation aggr = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(day);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date nextDay = calendar.getTime();
		// Date nextDay = DateUtils.addDays(day, 1);
		List<Row> rows;
		List<Activity> activityLog = new ArrayList<Activity>();
		Select activitySelect = QueryBuilder.select().from(USER_KEYSPACE,
				ACTIVITY_LOG_COLLECTION);

		activitySelect.where(QueryBuilder.eq("userBaseId", userBaseId));
		Ordering ordering = QueryBuilder.desc("activityTimeStamp");
		activitySelect.orderBy(ordering);
		activitySelect.limit(20);
		rows = cassandraTemplate.query(activitySelect).all();
		for (Row r : rows) {
			if (day == null) {
				if (r.getString("activityType")
						.equals(Integer.toString(action))) {
					Activity activity = new Activity();
					activity.setUserBaseId(r.getString("userBaseId"));
					activity.setSocialMediaId(r.getString("socialMediaId"));
					activity.setActivityTimeStamp(r
							.getDate("activityTimeStamp"));
					activity.setActivityType(r.getString("activityType"));
					activityLog.add(activity);
				}
			} else {
				if ((r.getString("activityType").equals(Integer
						.toString(action)))
						&& (r.getDate("activityTimeStamp").compareTo(day) > 0)
						&& (r.getDate("activityTimeStamp").compareTo(nextDay) < 0)) {
					Activity activity = new Activity();
					activity.setUserBaseId(r.getString("userBaseId"));
					activity.setSocialMediaId(r.getString("socialMediaId"));
					activity.setActivityTimeStamp(r
							.getDate("activityTimeStamp"));
					activity.setActivityType(r.getString("activityType"));
					activityLog.add(activity);
				}
			}
		}

		/*
		 * aggr = newAggregation(
		 * match(Criteria.where("userBaseId").is(userBaseId)
		 * .and("activityType").is(action)), sort(Direction.DESC,
		 * "activityTimeStamp"), limit(20)); } else { aggr = newAggregation(
		 * match(Criteria.where("userBaseId").is(userBaseId)
		 * .and("activityType")
		 * .is(action).and("activityTimeStamp").gte(day).lte(nextDay)),
		 * sort(Direction.DESC, "activityTimeStamp"), limit(20)); }
		 */

		// AggregationResults<Activity> aggResult =
		// mongoTemplate.aggregate(aggr, ACTIVITY_LOG_COLLECTION,
		// Activity.class);
		// List<Activity> activityLog = aggResult.getMappedResults();

		return activityLog;
	}

	@Override
	public void allocateBadges(String userBaseId) {

		int userPoints = getUserPoints(userBaseId).getTotalPoints();
		boolean isUpdatedRequired = false;

		if (userPoints != 0) {

			List<Badge> badgeList = getBadges();
			List<String> userBadges = getUserBadgeIdList(userBaseId);
			List<String> userBadgeList = null;

			if (badgeList != null) {

				if (userBadges != null) {
					userBadgeList = new ArrayList<String>(userBadges);
				}

				for (Badge badge : badgeList) {

					// Check if user has points to earn this badge
					if (userPoints >= Integer.parseInt(badge
							.getActivationPoints())) {

						// check if user already has this badge
						if (!userBadgeList.contains(badge.getBadgeId())) {
							userBadgeList.add(badge.getBadgeId());
							isUpdatedRequired = true;
						}
					}
				}

				if (isUpdatedRequired) {

					int result = addBadges(userBaseId, userBadgeList);

					if (result == GamificationConstants.ACTION_BADGES_UPDATED) {
						logger.info("Badge Allocated successfully to the user");
					} else {
						logger.error("Badge Allocation failed for the user");
					}
				}
			}
		}
	}

	@Override
	public UserPointDetails getUserPoints(String userBaseId) {

		List<Row> rows;
		int totalPoints = 0;
		UserPointDetails userPointDetails = new UserPointDetails();
		try {

			Select userPointsSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_POINTS_COLLECTION);
			userPointsSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
			rows = cassandraTemplate.query(userPointsSelect).all();
			for (Row r : rows) {
				totalPoints += r.getInt("totalPoints");
			}
			/*
			 * Aggregation aggr = newAggregation(match(Criteria
			 * .where("userBaseId").is(userBaseId)), group("userBaseId")
			 * .sum("totalPoints").as("totalPoints"), sort(Direction.DESC,
			 * "totalPoints"));
			 * 
			 * AggregationResults<UserPoints> aggResult =
			 * mongoTemplate.aggregate(aggr, USER_POINTS_COLLECTION,
			 * UserPoints.class);
			 * 
			 * if (aggResult.getMappedResults().size() != 0) { totalPoints =
			 * aggResult.getMappedResults().get(0) .getTotalPoints(); }
			 */
			userPointDetails.setTotalPoints(totalPoints);

			// Query userBaseQuery = new Query();
			Select userBaseSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			userBaseSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
			// userBaseQuery.addCriteria(Criteria.where("userBaseId").is(userBaseId));
			Row row = cassandraTemplate.query(userBaseSelect).one();
			// UserBase userBase = mongoTemplate.findOne(userBaseQuery,
			// UserBase.class, USER_BASE_COLLECTION);
			if (row != null) {
				userPointDetails.setUserBaseId(userBaseId);
				userPointDetails.setUsername(row.getString("name"));
				if (row.getString("nameOfFile") != null)
					userPointDetails.setProfile_pic_url(row
							.getString("nameOfFile"));
				else {
					Select socialMediaTypeSelect = QueryBuilder.select().from(
							USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
					socialMediaTypeSelect.where(QueryBuilder.eq("userBaseId",
							userBaseId));
					row = cassandraTemplate.query(socialMediaTypeSelect).one();
					// Query socialMediaTypeQuery = new Query();
					// socialMediaTypeQuery.addCriteria(Criteria.where("userBaseId").is(userBaseId));
					// SocialMediaType socialMediaType =
					// mongoTemplate.findOne(socialMediaTypeQuery,
					// SocialMediaType.class, SOCIAL_MEDIA_TYPE_COLLECTION);
					if (row != null)
						userPointDetails.setProfile_pic_url(row
								.getString("profilePicUrl"));
				}

			} else {
				logger.info("getUserPoints: User not found");
				return null;
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve the badges for user");
			logger.error(e);
		}

		return userPointDetails;
	}

	@Override
	public List<Badge> getBadges() {

		List<Badge> badgeList = new ArrayList<Badge>();
		Badge badge;
		List<Row> rows;

		try {
			Select badgeSelect = QueryBuilder.select().from(USER_KEYSPACE,
					BADGE_COLLECTION);
			rows = cassandraTemplate.query(badgeSelect).all();
			for (Row r : rows) {
				badge = new Badge();
				badge.setBadgeId(r.getString("badgeId"));
				badge.setBadgeName(r.getString("badgeName"));
				badge.setActiveBadgeUrl(r.getString("activeBadgeUrl"));
				badge.setDisabledBadgeUrl(r.getString("disabledBadgeUrl"));
				badge.setActivationPoints(r.getString("activationPoints"));
				badgeList.add(badge);
			}
			// badgeList = mongoTemplate.findAll(Badge.class, BADGE_COLLECTION);
		} catch (Exception e) {
			logger.error("Failed to retrieve the badges");
			logger.error(e);
		}
		if (!badgeList.isEmpty())
			return badgeList;
		else
			return null;
	}

	@Override
	public List<Badge> getUserBadges(String userBaseId) {

		List<String> badgeList = null;
		List<Badge> userBadgeList = null;
		// Query userBadgeListQuery;
		Select userbadgeListSelect = QueryBuilder.select().from(USER_KEYSPACE,
				USER_BADGE_COLLECTION);

		// TODO: change the getBadge() impl. -- Himalaya
		try {

			userbadgeListSelect
					.where(QueryBuilder.eq("userBaseId", userBaseId));
			Row row = cassandraTemplate.query(userbadgeListSelect).one();

			if (row != null) {

				badgeList = row.getList("userBadgeList", String.class);
				// badgeList = userBadges.getUserBadgeList();
				userBadgeList = new ArrayList<Badge>();

				for (String badgeId : badgeList) {

					Badge badge = getBadge(badgeId);
					userBadgeList.add(badge);
				}
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve the badges for user");
			logger.error(e);
		}

		return userBadgeList;

	}

	@Override
	public List<UserPointDetails> getLeaderBoard() {

		List<UserPoints> userPointsList = new ArrayList<UserPoints>();
		;
		List<String> userBaseIdList = new ArrayList<String>();
		List<UserPointDetails> userPointDetailsList = new ArrayList<UserPointDetails>();
		Map<String, Integer> userPointsMap = new HashMap<String, Integer>();
		// List<String> userBaseList = new ArrayList<String>();
		UserPointDetails userPointDetails = null;
		int points = 0;
		try {

			Select leaderSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_POINTS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(leaderSelect).all();
			for (Row r : rows) {
				if (!userBaseIdList.contains(r.getString("userBaseId"))) {
					points = 0;
					userBaseIdList.add(r.getString("userBaseId"));
				}
				if (userPointsMap.get(r.getString("userBaseId")) != null)
					points = userPointsMap.get(r.getString("userBaseId"));
				userPointsMap.put(r.getString("userBaseId"),
						points + r.getInt("totalPoints"));

			}

			/*
			 * Aggregation aggr =
			 * newAggregation(group("userBaseId").sum("totalPoints")
			 * .as("totalPoints"), sort(Direction.DESC, "totalPoints"));
			 * 
			 * AggregationResults<Object> aggResult =
			 * mongoTemplate.aggregate(aggr, USER_POINTS_COLLECTION,
			 * Object.class);
			 */
			// List<Object> results = aggResult.getMappedResults();
			UserPoints usrpnt = null;
			String name = null;
			for (Map.Entry<String, Integer> entry : userPointsMap.entrySet()) {
				usrpnt = new UserPoints();
				String userBaseId = entry.getKey();
				for (Row r : rows) {
					if (r.getString("userBaseId").equals(userBaseId)) {
						/*
						 * if(r.getString("_id") == null) continue;
						 */

						name = r.getString("userBaseId");
					}
				}
				usrpnt.setTotalPoints(entry.getValue());
				usrpnt.setUserBaseId(name);
				userPointsList.add(usrpnt);
			}
			/*
			 * for(Object o : results){ LinkedHashMap<?, ?> rslt =
			 * (LinkedHashMap<?, ?>)o;
			 * 
			 * String points = rslt.get("totalPoints").toString();
			 * if(rslt.get("_id")==null){ continue; } //String name =
			 * rslt.get("_id").toString();
			 * 
			 * 
			 * usrpnt.setTotalPoints(Integer.parseInt(points));
			 * usrpnt.setUserBaseId(name);
			 * //logger.info(name);logger.info(name);
			 * userPointsList.add(usrpnt);
			 * 
			 * 
			 * 
			 * }
			 */

			// logger.info(userPointsList.size());
			UserBase userBase = null;
			for (UserPoints userPoints : userPointsList) {
				Select userBaseSelect = QueryBuilder.select().from(
						USER_KEYSPACE, USER_BASE_COLLECTION);
				userBaseSelect.where(QueryBuilder.eq("userBaseId",
						userPoints.getUserBaseId()));
				// Query userBaseQuery = new Query();
				// userBaseQuery.addCriteria(Criteria.where("userBaseId").is(userPoints.getUserBaseId()));
				Row row = cassandraTemplate.query(userBaseSelect).one();
				if (row != null) {
					userBase = new UserBase();
					userBase.setUserBaseId(row.getString("userBaseId"));
					userBase.setName(row.getString("name"));
					userBase.setEmailIdList(row.getList("emailIdList",
							String.class));
					userBase.setPassword(row.getString("password"));
					userBase.setAge(row.getString("age"));
					userBase.setGender(row.getString("gender"));
					userBase.setDob(row.getString("dob"));
					userBase.setUserRegisteredDate(row
							.getDate("userRegisteredDate"));
					userBase.setYearOfBirth(row.getString("yearOfBirth"));
					userBase.setNameOfFile(row.getString("nameOfFile"));
				}
				// UserBase userBase = mongoTemplate.findOne(userBaseQuery,
				// UserBase.class, USER_BASE_COLLECTION);
				if (userBase != null) {
					userPointDetails = new UserPointDetails();
					userPointDetails.setUserBaseId(userBase.getUserBaseId());

					// logger.info("2nd for loopp: "+userPointDetails.getUserBaseId());
					userPointDetails.setUsername(userBase.getName());
					userPointDetails
							.setTotalPoints(userPoints.getTotalPoints());
					if (userBase.getNameOfFile() != null)
						userPointDetails.setProfile_pic_url(userBase
								.getNameOfFile());
					else {
						Select socialMediaTypeSelect = QueryBuilder.select()
								.from(USER_KEYSPACE,
										SOCIAL_MEDIA_TYPE_COLLECTION);
						socialMediaTypeSelect.where(QueryBuilder.eq(
								"userBaseId", userBase.getUserBaseId()));
						// Query socialMediaTypeQuery = new Query();
						// socialMediaTypeQuery.addCriteria(Criteria.where("userBaseId").is(userBase.getUserBaseId()));
						// SocialMediaType socialMediaType =
						// mongoTemplate.findOne(socialMediaTypeQuery,
						// SocialMediaType.class, SOCIAL_MEDIA_TYPE_COLLECTION);
						row = cassandraTemplate.query(socialMediaTypeSelect)
								.one();
						if (row != null) {
							userPointDetails.setProfile_pic_url(row
									.getString("profilePicUrl"));
						}
					}

					userPointDetailsList.add(userPointDetails);
				}
				// if(userPointDetails != null)

			}
		} catch (Exception e) {
			logger.error("Failed to retrieve leaderboard");
			logger.error(e);
		}
		// logger.info("userPointDetailsList : "+userPointDetailsList.size());
		Collections.sort(userPointDetailsList);
		return userPointDetailsList;
	}

	@Override
	public int createRule(Rule rule) {

		Select ruleListSelect;
		List<Row> rows;
		// Query ruleListQuery;
		Update ruleUpdate;
		Integer resp = null;

		try {

			if (rule != null) {

				if (rule.getRuleId() != null && !rule.getRuleId().equals("")) {

					ruleListSelect = QueryBuilder.select().from(USER_KEYSPACE,
							RULE_COLLECTION);
					ruleListSelect.where(QueryBuilder.eq("ruleId",
							rule.getRuleId()));
					rows = cassandraTemplate.query(ruleListSelect).all();

					if (!rows.isEmpty()) {
						ruleUpdate = QueryBuilder.update(USER_KEYSPACE,
								RULE_COLLECTION);
						ruleUpdate.where(QueryBuilder.eq("ruleId",
								rule.getRuleId()));
						// ruleListQuery.addCriteria(Criteria.where("ruleId").is(rule.getRuleId()));

						ruleUpdate.with(QueryBuilder.set("ruleName",
								rule.getRuleName()));
						ruleUpdate.with(QueryBuilder.set("ruleDescc",
								rule.getRuleDesc()));
						ruleUpdate.with(QueryBuilder.set("activityType",
								rule.getActivityType()));
						ruleUpdate.with(QueryBuilder.set("ruleOperator",
								rule.getRuleOperator()));
						ruleUpdate.with(QueryBuilder.set("actionValue",
								rule.getActionValue()));
						ruleUpdate.with(QueryBuilder.set("applyPeriod",
								rule.getApplyPeriod()));
						ruleUpdate.with(QueryBuilder.set("priority",
								rule.getPriority()));
						ruleUpdate.with(QueryBuilder.set("updateDate",
								new Date()));
						ruleUpdate.with(QueryBuilder.set("status",
								rule.getStatus()));

						cassandraTemplate.execute(ruleUpdate);
						resp = GamificationConstants.ACTION_RULE_UPDATED;
					}
				} else {

					rule.setRuleId(UUID.randomUUID().toString());

					Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
							RULE_COLLECTION);
					insert.value("ruleId", rule.getRuleId());
					insert.value("ruleName", rule.getRuleName());
					insert.value("ruleDesc", rule.getRuleDesc());
					insert.value("activityType", rule.getActivityType());
					insert.value("ruleOperator", rule.getRuleOperator());
					insert.value("actionValue", rule.getActionValue());
					insert.value("applyPeriod", rule.getApplyPeriod());
					insert.value("priority", rule.getPriority());
					insert.value("updateDate", rule.getUpdateDate());
					insert.value("status", rule.getStatus());
					cassandraTemplate.execute(insert);
					// mongoTemplate.save(rule, RULE_COLLECTION);
					resp = GamificationConstants.ACTION_RULE_CREATED;

				}

				// mongoTemplate.updateFirst(ruleListQuery, ruleUpdate,
				// RULE_COLLECTION);
				return resp;
			} else {
				return GamificationConstants.ACTION_EMPTY_RULE;
			}

		} catch (Exception e) {
			logger.error("Rule creation/updation Failed");
			logger.error(e);
			return GamificationConstants.ACTION_RULE_CREATE_UPDATE_FAILED;
		}
	}

	@Override
	public List<Rule> getRules() {

		List<Rule> ruleList = new ArrayList<Rule>();
		List<Row> rows;
		try {

			Select ruleSelect = QueryBuilder.select().from(USER_KEYSPACE,
					RULE_COLLECTION);
			rows = cassandraTemplate.query(ruleSelect).all();
			for (Row r : rows) {
				Rule rule = new Rule();
				rule.setRuleId(r.getString("ruleId"));
				rule.setRuleName(r.getString("ruleName"));
				rule.setRuleDesc(r.getString("ruleDesc"));
				rule.setActivityType(r.getString("activityType"));
				rule.setRuleOperator(r.getString("ruleOperator"));
				rule.setActionValue(r.getString("actionValue"));
				rule.setApplyPeriod(r.getString("applyPeriod"));
				rule.setPriority(r.getString("priority"));
				rule.setUpdateDate(r.getDate("updateDate"));
				rule.setStatus(r.getString("status"));
				ruleList.add(rule);
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve the badge");
			logger.error(e);
		}

		return ruleList;
	}

	@Override
	public int editRule(Rule rule) {

		Select ruleListSelect;
		Integer resp;
		List<Row> rows;
		// Query ruleListQuery;
		Update ruleUpdate;

		try {
			if (rule != null) {

				if (rule.getRuleId() != null && !rule.getRuleId().equals("")) {

					ruleListSelect = QueryBuilder.select().from(USER_KEYSPACE,
							RULE_COLLECTION);
					ruleListSelect.where(QueryBuilder.eq("ruleId",
							rule.getRuleId()));
					rows = cassandraTemplate.query(ruleListSelect).all();

					if (!rows.isEmpty()) {
						ruleUpdate = QueryBuilder.update(USER_KEYSPACE,
								RULE_COLLECTION);
						// ruleListQuery.addCriteria(Criteria.where("ruleId").is(rule.getRuleId()));
						ruleUpdate.where(QueryBuilder.eq("ruleId",
								rule.getRuleId()));

						if (rule.getRuleName() != null)
							ruleUpdate.with(QueryBuilder.set("ruleName",
									rule.getRuleName()));
						if (rule.getRuleDesc() != null)
							ruleUpdate.with(QueryBuilder.set("ruleDesc",
									rule.getRuleDesc()));
						if (rule.getActivityType() != null)
							ruleUpdate.with(QueryBuilder.set("activityType",
									rule.getActivityType()));
						if (rule.getRuleOperator() != null)
							ruleUpdate.with(QueryBuilder.set("ruleOperator",
									rule.getRuleOperator()));
						if (rule.getActionValue() != null)
							ruleUpdate.with(QueryBuilder.set("actionValue",
									rule.getActionValue()));
						if (rule.getApplyPeriod() != null)
							ruleUpdate.with(QueryBuilder.set("applyPeriod",
									rule.getApplyPeriod()));
						if (rule.getPriority() != null)
							ruleUpdate.with(QueryBuilder.set("priority",
									rule.getPriority()));
						ruleUpdate.with(QueryBuilder.set("updateDate",
								new Date()));
						if (rule.getStatus() != null)
							ruleUpdate.with(QueryBuilder.set("status",
									rule.getStatus()));

						cassandraTemplate.execute(ruleUpdate);
						resp = GamificationConstants.ACTION_RULE_UPDATED;
					} else
						resp = GamificationConstants.ACTION_RULE_NOT_FOUND;

					// mongoTemplate.updateFirst(ruleListQuery, ruleUpdate,
					// RULE_COLLECTION);

					return resp;

				} else {
					logger.error("editRule : Rule Id is null");
					return GamificationConstants.ACTION_RULE_CREATE_UPDATE_FAILED;
				}

			} else {
				logger.error("editRule : Rule is null");
				return GamificationConstants.ACTION_RULE_CREATE_UPDATE_FAILED;
			}

		} catch (Exception e) {
			logger.error("Rule creation/updation Failed");
			logger.error(e);
			return GamificationConstants.ACTION_RULE_CREATE_UPDATE_FAILED;
		}

	}

	@Override
	public int deleteRule(String ruleId) {
		Delete ruleListDelete;
		// Query ruleListQuery;
		try {
			if (ruleId != null) {
				ruleListDelete = QueryBuilder.delete().from(USER_KEYSPACE,
						RULE_COLLECTION);
				ruleListDelete.where(QueryBuilder.eq("ruleId", ruleId));
				cassandraTemplate.execute(ruleListDelete);

			}
			return GamificationConstants.ACTION_RULE_DELETED;

		} catch (Exception e) {
			logger.error("Rule creation/updation Failed");
			logger.error(e);
			return GamificationConstants.ACTION_RULE_DELETE_FAILED;
		}

	}

	@Override
	public int createBadge(Badge badge) {
		Select badgeListSelect;
		// Query badgeListQuery;
		Update badgeUpdate;

		try {

			if (badge != null) {

				if (badge.getBadgeId() != null
						&& !badge.getBadgeId().equals("")) {

					badgeUpdate = QueryBuilder.update(USER_KEYSPACE,
							BADGE_COLLECTION);
					badgeUpdate.where(QueryBuilder.eq("badgeId",
							badge.getBadgeId()));
					// badgeListQuery.addCriteria(Criteria.where("badgeId").is(badge.getBadgeId()));

					badgeUpdate.with(QueryBuilder.set("badgeName",
							badge.getBadgeName()));
					badgeUpdate.with(QueryBuilder.set("activeBadgeUrl",
							badge.getActiveBadgeUrl()));
					badgeUpdate.with(QueryBuilder.set("disabledBadgeUrl",
							badge.getDisabledBadgeUrl()));
					badgeUpdate.with(QueryBuilder.set("activationPoints",
							badge.getActivationPoints()));

					cassandraTemplate.execute(badgeUpdate);
					// mongoTemplate.updateFirst(badgeListQuery, badgeUpdate,
					// BADGE_COLLECTION);

					return GamificationConstants.ACTION_BADGE_UPDATED;

				} else {

					badge.setBadgeId(UUID.randomUUID().toString());
					Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
							BADGE_COLLECTION);
					insert.value("badgeId", badge.getBadgeId());
					insert.value("badgeName", badge.getBadgeName());
					insert.value("activeBadgeUrl", badge.getActiveBadgeUrl());
					insert.value("disabledBadgeUrl",
							badge.getDisabledBadgeUrl());
					insert.value("activationPoints",
							badge.getActivationPoints());
					cassandraTemplate.execute(insert);

					return GamificationConstants.ACTION_BADGE_CREATED;
				}
			} else {
				return GamificationConstants.ACTION_EMPTY_BADGE;
			}

		} catch (Exception e) {
			logger.error("Rule creation/updation Failed");
			logger.error(e);
			return GamificationConstants.ACTION_BADGE_CREATE_UPDATE_FAILED;
		}
	}

	@Override
	public int editBadge(Badge badge) {
		Select badgeListSelect;
		// Query badgeListQuery;
		Update badgeUpdate;

		try {
			if (badge != null) {

				if (badge.getBadgeId() != null
						&& !badge.getBadgeId().equals("")) {

					badgeListSelect = QueryBuilder.select().from(USER_KEYSPACE,
							BADGE_COLLECTION);
					badgeUpdate = QueryBuilder.update(USER_KEYSPACE,
							BADGE_COLLECTION);
					badgeUpdate.where(QueryBuilder.eq("badgeId",
							badge.getBadgeId()));
					// badgeListQuery.addCriteria(Criteria.where("badgeId").is(badge.getBadgeId()));

					if (badge.getBadgeName() != null)
						badgeUpdate.with(QueryBuilder.set("badgeName",
								badge.getBadgeName()));
					if (badge.getActiveBadgeUrl() != null)
						badgeUpdate.with(QueryBuilder.set("activeBadgeUrl",
								badge.getActiveBadgeUrl()));
					if (badge.getDisabledBadgeUrl() != null)
						badgeUpdate.with(QueryBuilder.set("disabledBadgeUrl",
								badge.getDisabledBadgeUrl()));
					if (badge.getActivationPoints() != null)
						badgeUpdate.with(QueryBuilder.set("activationPoints",
								badge.getActivationPoints()));

					cassandraTemplate.execute(badgeUpdate);
					// mongoTemplate.updateFirst(badgeListQuery, badgeUpdate,
					// BADGE_COLLECTION);

					return GamificationConstants.ACTION_BADGE_UPDATED;

				} else {
					logger.error("editRule : Rule Id is null");
					return GamificationConstants.ACTION_BADGE_CREATE_UPDATE_FAILED;
				}

			} else {
				logger.error("editRule : Rule is null");
				return GamificationConstants.ACTION_BADGE_CREATE_UPDATE_FAILED;
			}

		} catch (Exception e) {
			logger.error("Rule creation/updation Failed");
			logger.error(e);
			return GamificationConstants.ACTION_BADGE_CREATE_UPDATE_FAILED;
		}

	}

	@Override
	public int deleteBadge(String badgeId) {
		Delete badgeListDelete;
		// Query badgeListQuery;
		try {
			if (badgeId != null) {
				badgeListDelete = QueryBuilder.delete().from(USER_KEYSPACE,
						BADGE_COLLECTION);
				badgeListDelete.where(QueryBuilder.eq("badgeId", badgeId));
				cassandraTemplate.execute(badgeListDelete);
				// badgeListQuery.addCriteria(Criteria.where("badgeId").is(badgeId));
				// mongoTemplate.remove(badgeListQuery, BADGE_COLLECTION);

			}
			return GamificationConstants.ACTION_RULE_DELETED;

		} catch (Exception e) {
			logger.error("Rule creation/updation Failed");
			logger.error(e);
			return GamificationConstants.ACTION_RULE_CREATE_UPDATE_FAILED;
		}
	}

	private Badge getBadge(String badgeId) {

		Badge badge = null;

		try {

			Select badgeSelect = QueryBuilder.select().from(USER_KEYSPACE,
					BADGE_COLLECTION);
			badgeSelect.where(QueryBuilder.eq("badgeId", badgeId));
			// Query badgeQuery = new
			// Query(Criteria.where("badgeId").is(badgeId));
			Row row = cassandraTemplate.query(badgeSelect).one();
			if (row != null) {
				badge = new Badge();
				badge.setBadgeId(row.getString("badgeId"));
				badge.setBadgeName(row.getString("badgeName"));
				badge.setActiveBadgeUrl(row.getString("activeBadgeUrl"));
				badge.setDisabledBadgeUrl(row.getString("disabledBadgeUrl"));
				badge.setActivationPoints(row.getString("activationPoints"));
			}
			// badge = mongoTemplate.findOne(badgeQuery, Badge.class,
			// BADGE_COLLECTION);

		} catch (Exception e) {
			logger.error("Failed to retrieve the badge");
			logger.error(e);
		}

		return badge;
	}

	private List<String> getUserBadgeIdList(String userBaseId) {

		List<String> userBadgeList = null;
		Row row;
		try {

			Select userBadgeSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BADGE_COLLECTION);
			userBadgeSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
			row = cassandraTemplate.query(userBadgeSelect).one();

			if (row != null) {
				userBadgeList = row.getList("userBadgeList", String.class);
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve the badge");
			logger.error(e);
		}

		return userBadgeList;
	}

	private int addBadges(String userBaseId, List<String> badgeIds) {

		try {
			Row row;
			UserBadges userBadges = null;
			Select badgeSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BADGE_COLLECTION);
			badgeSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
			row = cassandraTemplate.query(badgeSelect).one();
			if (row != null) {
				userBadges = new UserBadges();
				userBadges.setUserBaseId(row.getString("userBaseId"));
				userBadges.setUserBadgeList(row.getList("userBadgeList",
						String.class));
			}

			// Query badgeQuery = new
			// Query(Criteria.where("userBaseId").is(userBaseId));
			// UserBadges userBadges = mongoTemplate.findOne(badgeQuery,
			// UserBadges.class, USER_BADGE_COLLECTION);

			if (userBadges != null) {

				Update badgeUpdate = QueryBuilder.update(USER_KEYSPACE,
						USER_BADGE_COLLECTION);
				badgeUpdate.where(QueryBuilder.eq("userBaseId", userBaseId));
				badgeUpdate.with(QueryBuilder.set("userBadgeList", badgeIds));
				// badgeUpdate.set("userBadgeList", badgeIds);
				cassandraTemplate.execute(badgeUpdate);
				// mongoTemplate.updateFirst(badgeQuery, badgeUpdate,
				// USER_BADGE_COLLECTION);
				return GamificationConstants.ACTION_BADGES_UPDATED;

			} else {

				userBadges = new UserBadges();
				userBadges.setUserBaseId(userBaseId);
				userBadges.setUserBadgeList(badgeIds);

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						USER_BADGE_COLLECTION);
				insert.value("userBaseId", userBadges.getUserBaseId());
				insert.value("userBadgeList", userBadges.getUserBadgeList());
				cassandraTemplate.execute(insert);
				// mongoTemplate.save(userBadges, USER_BADGE_COLLECTION);
				return GamificationConstants.ACTION_BADGES_UPDATED;
			}
		} catch (Exception e) {
			logger.error("Failed to update the badges for user");
			logger.error(e);
			return GamificationConstants.ACTION_BADGES_UPDATE_FAILED;
		}

	}

	private void debug(String dbgMsg) {
		if (logger.isDebugEnabled()) {
			logger.debug(dbgMsg);
		}
	}
}
