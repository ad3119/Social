package com.aricent.transaction.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlTemplate;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.aricent.constant.GlobalConstants;
import com.aricent.gamification.model.UserPoints;
import com.aricent.model.Activity;
import com.aricent.model.CodeForPasswordReset;
import com.aricent.model.CommentRequest;
import com.aricent.model.DownvoteReq;
import com.aricent.model.Email;
import com.aricent.model.FriendsLikes;
import com.aricent.model.FriendsLikesList;
import com.aricent.model.IdleServices;
import com.aricent.model.LinkedAccountType;
import com.aricent.model.LoggedInUser;
import com.aricent.model.MessageModel;
import com.aricent.model.PasswordUpdate;
import com.aricent.model.PortalWidgets;
import com.aricent.model.Recommendations;
import com.aricent.model.RecommendedProduct;
import com.aricent.model.RegisteredUserFilters;
import com.aricent.model.ReportsData;
import com.aricent.model.ReviewUpdateReq;
import com.aricent.model.SeqCounter;
import com.aricent.model.SocialMediaData;
import com.aricent.model.SocialMediaType;
import com.aricent.model.SocialMeidaAttrValue;
import com.aricent.model.UpvoteReq;
import com.aricent.model.UrlRatingRef;
import com.aricent.model.UserActivityList;
import com.aricent.model.UserBase;
import com.aricent.model.UserCount;
import com.aricent.model.UserRatingCount;
import com.aricent.model.UserReview;
import com.aricent.model.UserStatus;
import com.aricent.transaction.UserTransaction;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Ordering;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Truncate;
/* added by Hari */
import com.datastax.driver.core.querybuilder.Update;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.mail.imap.protocol.FetchResponse;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;

@Repository
public class UserTransactionImpl implements UserTransaction {

	/*
	 * Author: 	 * 
	 * TODO: Add Null Checks.
	 */

	@Autowired
	private CqlTemplate cassandraTemplate;
	public static final String USER_KEYSPACE = "user_tables";
	public static final String USER_BASE_COLLECTION = "baselist";
	public static final String USER_STATUS_COLLECTION = "userstatuslist";
	public static final String USER_STATE_COLLECTION = "userStateList";
	public static final String MEDIA_TYPE_REF_COLLECTION = "mediaTypeRef";
	public static final String MEDIA_ATTR_REF_COLLECTION = "mediaAttrRef";
	public static final String SOCIAL_MEDIA_TYPE_COLLECTION = "usersocialmediatype";
	public static final String SOCIAL_MEDIA_DATA_COLLECTION = "socialmediadatalist";
	private static final Logger logger = Logger
			.getLogger(UserTransactionImpl.class);
	public static final String IDLE_SERVICES_COLLECTION = "idleservices";
	public static final String CODE_FOR_PASSWORD_RESET = "codeforpasswordreset";
	public static final String ACTIVITY_LOG = "activitylog";

	// Rating and Review ++
	public static final String URL_RATING_DATA_COLLECTION = "urlratingref";
	public static final String USER_REVIEW_COMMENTS_COLLECTION = "userreviewcomments";
	public static final String SEQ_COUNTERS = "counters";
	// Rating and Review --
	private static final String USER_POINTS_COLLECTION = "user_points";

	public static final String WIDGETS_COLLECTION = "widgetcollection";
	private static final String PRODUCT_COLLECTION = "productcollection";

	public void init() {
		logger.info("Initializing UserTransaction");

		try {
			// initialize UserBase collection

			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.baselist ("
					+ "userBaseId text PRIMARY KEY,"
					+ "name text,"
					+ "emailIdList list<text>,"
					+ "password text,"
					+ "age text,"
					+ "gender text,"
					+ "dob text,"
					+ "nameOfFile text,"
					+ "yearOfBirth text,"
					+ "userRegisteredDate timestamp,"
					+ "recUserId int"
					+ ");");

			logger.info("Completed Initializing UserTransaction");

			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.userstatuslist ("
					+ "userBaseId text PRIMARY KEY,"
					+ "stateId text,"
					+ "lastLoginTime timestamp,"
					+ "currentLoginTime timestamp,"
					+ "cumulativeloginCount int," + ");");
			logger.info("Created userstatuslist");

			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.usersocialmediatype ("
					+ "userBaseId text PRIMARY KEY,"
					+ "socialMediatypeId text,"
					+ "socialMediaId text,"
					+ "accessToken text,"
					+ "profilePicUrl text,"
					+ ");");
			logger.info("Created usersocialmediatype.");

			cassandraTemplate
			.execute("CREATE TYPE IF NOT EXISTS user_tables.socialmeidaattrvalue ("
					+ "AttrId text," + "AttrValue text," + ");");
			logger.info("Created socialmeidaattrvalue");
			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.socialmediadatalist ("
					+ "userBaseId text PRIMARY KEY,"
					+ "socialMediaAttrTypeId text,"
					+ "socialMediaAttrvalueList list<frozen<socialmeidaattrvalue>>,"
					+ ");");
			logger.info("Created socialmediadatalist.");

			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.activitylog ("
					+ "userBaseId text,"
					+ "socialMediaId text,"
					+ "activityType text,"
					+ "activityTimeStamp timestamp,"
					+ "PRIMARY KEY(userBaseId,activityTimeStamp)"
					+ ");");
			logger.info("Created activitylog.");

			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.userreviewcomments ("
					+ "commentId text,"
					+ "urlId int,"
					+ "url text,"
					+ "typeId int,"
					+ "parentCommentId text,"
					+ "userName text,"
					+ "userAvatar text,"
					+ "socialMediaAttrId int,"
					+ "dateTime timestamp,"
					+ "commentTitle text,"
					+ "userComment text,"
					+ "userRating float,"
					+ "userBaseId text,"
					+ "numofreplies int,"
					+ "numUpvotes int,"
					+ "numDownvotes int,"
					+ "upvotesBy list<text>,"
					+ "downvotesBy list<text>,"
					+ "PRIMARY KEY (commentId)" + ");");
			logger.info("Created activitylog.");
			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.idleservices ("
					+ "register text PRIMARY KEY,"
					+ "social text,"
					+ ");");
			logger.info("Created idleservices.");
			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.codeforpasswordreset ("
					+ "code text PRIMARY KEY,"
					+ "time timestamp,"
					+ ");");
			logger.info("Created codeforpasswordreset.");

			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.urlratingref ("
					+ "url text PRIMARY KEY,"
					+ "totNumRating int,"
					+ "averageRating float,"
					+ "urlId int,"
					+ "lastCommentId text" + ");");

			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.counters ("
					+ "id text PRIMARY KEY," + "seq int," + ");");
			logger.info("Created codeforpasswordreset.");
			cassandraTemplate
			.execute("CREATE TYPE IF NOT EXISTS user_tables.specs ("
					+ "name text," + "value text," + ");");
			cassandraTemplate
			.execute("CREATE TYPE IF NOT EXISTS user_tables.productuser ("
					+ "userBase text,"
					+ "product text,"
					+ "viewedCount int,"
					+ "addedToCart boolean,"
					+ "quantity int,"
					+ "price text,"
					+ "productId text," + "viewedTime timestamp" + ");");
			// cassandraTemplate.execute("DROP TABLE user_tables.productcollection");
			cassandraTemplate
			.execute("CREATE TABLE IF NOT EXISTS user_tables.productcollection ("
					+ "serialVersionUID double,"
					+ "id text PRIMARY KEY,"
					+ "productName text,"
					+ "productRating float,"
					+ "description text,"
					+ "category text,"
					+ "price text,"
					+ "specs list<frozen<specs>>,"
					+ "productCreated timestamp,"
					+ "productLastUpdated timestamp,"
					+ "productUserList list<frozen<productuser>>,"
					+ "totalNumberOfRatings int" + ");");
			logger.info("Created productcollection.");
			try {
				cassandraTemplate
				.execute("CREATE TABLE IF NOT EXISTS user_tables.widgetcollection ("
						+ "widgetId text PRIMARY KEY,"

								+ "widgetHeader text,"
								+ "pages list<text>,"
								+ "addedToPage boolean,"
								+ "previewDivContent text,"
								+ "addedToPortal boolean,"
								+ "align text"
								+ ");");
				logger.info("Created widgetCollection.");
			} catch (Exception e) {
				logger.info("Initializing widgetcollectionFailed");
				logger.info(e.getMessage());
			}
			logger.info("Created urlratingref.");
		} catch (Exception e) {
			logger.info("Initializing Failed");
			logger.info(e.getMessage());
		}
	}

	public String generatePassword() {
		// generate random password
		final Random RANDOM = new SecureRandom();
		String pw = "";
		for (int i = 0; i < 8; i++) {
			int index = (int) (RANDOM.nextDouble() * GlobalConstants.letters
					.length());
			pw += GlobalConstants.letters.substring(index, index + 1);
		}
		return pw;
	}

	public String encode(String str) {
		BASE64Encoder encoder = new BASE64Encoder();
		str = new String(encoder.encodeBuffer(str.getBytes()));
		return str;
	}

	public String decode(String str) {
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			str = new String(decoder.decodeBuffer(str));
		} catch (IOException e) {
			logger.error(e);
		}
		return str;
	}

	public String updateUserBase(UserBase userBase) {

		try {

			int result;
			String pw;
			List<Row> rows;
			LoggedInUser loggedInuser = new LoggedInUser();
			List<String> emailList = userBase.getEmailIdList();
			String email = emailList.get(0);
			Select userSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			// userSelect.where(QueryBuilder.in("emailIdList",email));
			UserBase userList = null;

			String msg = "You just logged into your accout";
			rows = cassandraTemplate.query(userSelect).all();

			for (Row r : rows) {

				List<String> emailIds = r.getList("emailIdList", String.class);
				for (String emailId : emailIds) {
					if (emailId.equals(email)) {
						userList = new UserBase();
						userList.setUserBaseId(r.getString("userBaseId"));
						userList.setName(r.getString("name"));
						userList.setEmailIdList(r.getList("emailIdList",
								String.class));
						userList.setPassword(r.getString("password"));
						userList.setAge(r.getString("age"));
						userList.setGender(r.getString("gender"));
						userList.setDob(r.getString("dob"));
						break;
					}
				}

			}

			if (userList != null) {
				logger.info("User already exists");

				// emailNotify(userBase.getEmailIdList().get(0).getEmailId(),
				// userBase.getName(), null, msg);
				UserStatus userStatus = new UserStatus();
				userStatus.setUserBaseId(userList.getUserBaseId());
				result = saveUserStatus(userStatus);
				if (result == GlobalConstants.OPRERATION_SUCCESS) {
					return userList.getUserBaseId();
				} else {
					logger.error("updateUserBase : Failed to update user status.");
					return null;
				}

			} else {

				userBase.setUserBaseId(UUID.randomUUID().toString());
				Random rand = new Random();
				int randomNum = rand.nextInt((1000 - 1) + 1) + 1;
				userBase.setRecUserId(randomNum);

				if (userBase.getPassword() != null
						&& userBase.getPassword().equals("")
						|| userBase.getPassword() == null) {
					/* Added by srihari */
					pw = generatePassword(); // calling a function to generate
					// random password
					String encryptedPwd = encode(pw);
					logger.info("Encrypted password : " + encryptedPwd
							+ "Actual password : " + pw);
					userBase.setPassword(encryptedPwd);
					/* Added by srihari */

				} else {
					/* Added by srihari */
					String encryptedPwd = encode(userBase.getPassword());
					logger.info("Encrypted password : " + encryptedPwd
							+ "Actual password : " + userBase.getPassword());
					userBase.setPassword(encryptedPwd);
					/* Added by srihari */

				}

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						USER_BASE_COLLECTION);
				insert.value("userBaseId", userBase.getUserBaseId());
				insert.value("name", userBase.getName());
				insert.value("emailIdList", userBase.getEmailIdList());
				insert.value("password", userBase.getPassword());
				insert.value("age", userBase.getAge());
				insert.value("gender", userBase.getGender());
				insert.value("dob", userBase.getDob());
				// insert.value("userRegisteredDate", new Date());
				insert.value("yearOfBirth", userBase.getYearOfBirth());
				insert.value("recUserId", userBase.getRecUserId());
				cassandraTemplate.execute(insert);

				emailNotify(userBase.getEmailIdList().get(0),
						userBase.getName(), decode(userBase.getPassword()), msg);

				UserStatus userStatus = new UserStatus();
				userStatus.setUserBaseId(userBase.getUserBaseId());
				result = saveUserStatus(userStatus);
				if (result == GlobalConstants.OPRERATION_SUCCESS) {
					return userBase.getUserBaseId();
				} else {
					logger.error("updateUserBase : Failed to update user status.");
					return null;
				}

			}
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	public int saveUserStatus(UserStatus userStatus) {
		try {

			UserStatus auditEntry = null;
			List<Row> rows;
			// Search for the user ID
			Select userSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_STATUS_COLLECTION);
			userSelect.where(QueryBuilder.eq("userBaseId",
					userStatus.getUserBaseId()));

			rows = cassandraTemplate.query(userSelect).all();
			for (Row r : rows) {

				auditEntry = new UserStatus();
				auditEntry.setStateId(r.getString("stateId"));
				auditEntry.setLastLoginTime(r.getDate("lastLoginTime"));
				auditEntry.setCurrentLoginTime(r.getDate("currentLoginTime"));
				auditEntry.setCumulativeloginCount(r
						.getInt("cumulativeloginCount"));
				break;

			}
			Update update = QueryBuilder.update(USER_KEYSPACE,
					USER_STATUS_COLLECTION);

			// If record is already present, update the table
			if (auditEntry != null) {

				update.with(QueryBuilder.set("lastLoginTime",
						auditEntry.getCurrentLoginTime()));
				update.with(QueryBuilder.set("currentLoginTime", new Date()));
				update.with(QueryBuilder.set("cumulativeloginCount",
						auditEntry.getCumulativeloginCount() + 1));
				update.with(QueryBuilder.set("stateId", "2"));
				// mongoTemplate.updateFirst(statusQuery, update,
				// USER_STATUS_COLLECTION);
				update.where(QueryBuilder.eq("userBaseId",
						userStatus.getUserBaseId()));
				cassandraTemplate.execute(update);

			} else { // If record is not present, save a new record
				auditEntry = new UserStatus();
				auditEntry.setCurrentLoginTime(new Date());
				auditEntry.setLastLoginTime(null);
				auditEntry.setCumulativeloginCount(0);
				auditEntry.setStateId("2");
				auditEntry.setUserBaseId(userStatus.getUserBaseId());

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						USER_STATUS_COLLECTION);
				insert.value("userBaseId", auditEntry.getUserBaseId());
				insert.value("stateId", auditEntry.getStateId());
				insert.value("lastLoginTime", auditEntry.getLastLoginTime());
				insert.value("currentLoginTime",
						auditEntry.getCurrentLoginTime());
				insert.value("cumulativeloginCount",
						auditEntry.getCumulativeloginCount());
				cassandraTemplate.execute(insert);
			}

			return GlobalConstants.OPRERATION_SUCCESS;
		} catch (Exception ex) {

			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;

		}
	}

	@Override
	public String registerUserBase(UserBase userBase) {
		try {

			String email = userBase.getEmailIdList().get(0);
			Select idleServicesSelect = QueryBuilder.select().from(
					USER_KEYSPACE, IDLE_SERVICES_COLLECTION);
			IdleServices idleServices = new IdleServices();
			List<Row> rows = cassandraTemplate.query(idleServicesSelect).all();

			for (Row r : rows) {
				idleServices.setRegister(r.getString("register"));
				idleServices.setSocial(r.getString("social"));
			}

			if (idleServices.getRegister().equals("false")) {
				logger.error("registerUserBase : Register service disabled!!");
				return Integer
						.toString(GlobalConstants.ERROR_REGISTER_DISABLED);
			} else {

				int result;
				List<String> emailList = userBase.getEmailIdList();
				Select userBaseListSelect = QueryBuilder.select().from(
						USER_KEYSPACE, USER_BASE_COLLECTION);

				UserBase userList = null;

				rows = cassandraTemplate.query(userBaseListSelect).all();
				for (Row r : rows) {

					List<String> emailIds = r.getList("emailIdList",
							String.class);
					for (String emailId : emailIds) {
						if (emailId.equals(email)) {
							userList = new UserBase();
							userList.setUserBaseId(r.getString("userBaseId"));
							userList.setName(r.getString("name"));
							userList.setEmailIdList(r.getList("emailIdList",
									String.class));
							userList.setPassword(r.getString("password"));
							userList.setAge(r.getString("age"));
							userList.setGender(r.getString("gender"));
							userList.setDob(r.getString("dob"));
							userList.setNameOfFile(r.getString("nameofFile"));
							userList.setUserRegisteredDate(r
									.getDate("userRegisteredDate"));
							userList.setYearOfBirth(r.getString("yearOfBirth"));

							break;
						}
					}
				}
				if (userList != null) {
					logger.info("User already exists");
					return Integer
							.toString(GlobalConstants.USER_ALREADY_EXISTS);
				} else {

					userBase.setUserBaseId(UUID.randomUUID().toString());
					userBase.setUserRegisteredDate(new Date());

					String encryptedUserPwd = encode(userBase.getPassword());
					logger.info("Encrypted password : " + encryptedUserPwd
							+ "Actual password : " + userBase.getPassword());
					userBase.setPassword(encryptedUserPwd);

					Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
							USER_BASE_COLLECTION);
					insert.value("userBaseId", userBase.getUserBaseId());
					insert.value("name", userBase.getName());
					insert.value("emailIdList", userBase.getEmailIdList());
					insert.value("password", userBase.getPassword());
					insert.value("age", userBase.getAge());
					insert.value("gender", userBase.getGender());
					insert.value("dob", userBase.getDob());
					insert.value("nameOfFile", userBase.getNameOfFile());
					insert.value("userRegisteredDate", new Date());
					insert.value("yearOfBirth", userBase.getYearOfBirth());
					cassandraTemplate.execute(insert);
					UserStatus userStatus = new UserStatus();
					userStatus.setUserBaseId(userBase.getUserBaseId());
					result = saveUserStatus(userStatus);
					if (result == GlobalConstants.OPRERATION_SUCCESS) {
						String msg = "Your email has been registered to BLR FOODY!!";
						emailNotify(userBase.getEmailIdList().get(0),
								userBase.getName(),
								decode(userBase.getPassword()), msg);
						return userBase.getUserBaseId();

					} else {
						logger.error("registerUserBase : Error in updating user status!!");
						return Integer
								.toString(GlobalConstants.OPRERATION_FAILURE);
					}
				}

			}

		} catch (Exception ex) {
			logger.error(ex);
			return Integer.toString(GlobalConstants.OPRERATION_FAILURE);
		}
	}

	@Override
	public int updateSocialMediaType(SocialMediaType socialMediaType) {
		try {

			List<Row> rows;
			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
			socialMediaTypeSelect.where(QueryBuilder.eq("userBaseId",
					socialMediaType.getUserBaseId()));
			Update update = QueryBuilder.update(USER_KEYSPACE,
					SOCIAL_MEDIA_TYPE_COLLECTION);

			SocialMediaType socialMediaTypeData = null;
			rows = cassandraTemplate.query(socialMediaTypeSelect).all();
			for (Row r : rows) {
				socialMediaTypeData = new SocialMediaType();
				socialMediaTypeData.setUserBaseId(r.getString("userBaseId"));
				socialMediaTypeData.setSocialMediatypeId(r
						.getString("socialMediatypeId"));
				socialMediaTypeData.setSocialMediaId(r
						.getString("socialMediaId"));
				socialMediaTypeData.setAccessToken(r.getString("accessToken"));
				socialMediaTypeData.setProfilePicUrl(r
						.getString("profilePicUrl"));
				break;

			}
			if (socialMediaTypeData != null) {
				update.with(QueryBuilder.set("userBaseId",
						socialMediaType.getUserBaseId()));
				update.with(QueryBuilder.set("socialMediatypeId",
						socialMediaType.getSocialMediatypeId()));
				update.with(QueryBuilder.set("socialMediaId",
						socialMediaType.getSocialMediaId()));
				update.with(QueryBuilder.set("accessToken",
						socialMediaType.getAccessToken()));
				update.with(QueryBuilder.set("profilePicUrl",
						socialMediaType.getProfilePicUrl()));
				update.where(QueryBuilder.eq("userBaseId",
						socialMediaType.getUserBaseId()));
				cassandraTemplate.execute(update);
			} else {

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						SOCIAL_MEDIA_TYPE_COLLECTION);
				insert.value("userBaseId", socialMediaType.getUserBaseId());
				insert.value("socialMediatypeId",
						socialMediaType.getSocialMediatypeId());
				insert.value("socialMediaId",
						socialMediaType.getSocialMediaId());
				insert.value("accessToken", socialMediaType.getAccessToken());
				insert.value("profilePicUrl",
						socialMediaType.getProfilePicUrl());
				cassandraTemplate.execute(insert);
			}

			return GlobalConstants.OPRERATION_SUCCESS;
		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}

	}

	@Override
	public int saveSocialMediaData(SocialMediaData socialMediaDataList) {
		try {

			String userBaseId = socialMediaDataList.getUserBaseId();
			List<Row> rows;

			Select socialMediaDataListSelect = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_DATA_COLLECTION);
			socialMediaDataListSelect.where(QueryBuilder.eq("userBaseId",
					userBaseId));

			Update update = QueryBuilder.update(USER_KEYSPACE,
					SOCIAL_MEDIA_DATA_COLLECTION);

			SocialMediaData mediaDataList = null;

			rows = cassandraTemplate.query(socialMediaDataListSelect).all();
			for (Row r : rows) {
				if (r.getString("socialMediaAttrTypeId").equals(
						socialMediaDataList.getSocialMediaAttrTypeId())) {
					mediaDataList = new SocialMediaData();
					mediaDataList.setUserBaseId(r.getString("userBaseId"));
					mediaDataList.setSocialMediaAttrTypeId(r
							.getString("socialMediaAttrTypeId"));
					mediaDataList.setSocialMediaAttrvalueList(r.getList(
							"socialMediaAttrvalueList",
							SocialMeidaAttrValue.class));
					break;
				}

			}
			// If record with same social media id and attribute type is already
			// present, update attribute value list
			if (mediaDataList != null) {
				// Update media data
				update.with(QueryBuilder.set("socialMediaAttrvalueList",
						socialMediaDataList.getSocialMediaAttrvalueList()));
				update.where(QueryBuilder.eq("userBaseId", userBaseId))
				.and(QueryBuilder.eq("socialMediaAttrTypeId",
						socialMediaDataList.getSocialMediaAttrTypeId()));
				cassandraTemplate.execute(update);
			} else {
				// Save media data
			}
			return GlobalConstants.OPRERATION_SUCCESS;
		} catch (Exception ex) {

			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;

		}
	}

	@Override
	public int activateAccount(LinkedAccountType linkedAccountType) {
		try {
			List<Row> rows;
			// Get email id passed from web page
			String registeredUserBaseId = linkedAccountType
					.getSocialMediaType().getUserBaseId();

			// Retrieve the User Base Id using email ID from UserBase table
			Select userBaseListSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_BASE_COLLECTION);
			userBaseListSelect.where(QueryBuilder.eq("userBaseId",
					registeredUserBaseId));

			UserBase userList = null;

			rows = cassandraTemplate.query(userBaseListSelect).all();
			for (Row r : rows) {
				userList = new UserBase();
				userList.setUserBaseId(r.getString("userBaseId"));
				userList.setName(r.getString("name"));
				userList.setEmailIdList(r.getList("emailIdList", String.class));
				userList.setPassword(r.getString("password"));
				userList.setAge(r.getString("age"));
				userList.setGender(r.getString("gender"));
				userList.setDob(r.getString("dob"));
				break;
			}
			int result = 0;
			int flag = 0;
			if (userList != null) {
				// Add the new email id
				String emailid = linkedAccountType.getSocialEmail();
				for (String el : userList.getEmailIdList()) {
					if (el.equals(emailid)) {
						flag = 1;
					}

				}

				if (flag == 0) {
					List<String> emailList = new ArrayList<String>();
					emailList = userList.getEmailIdList();
					emailList.add(emailid);

					Update update = QueryBuilder.update(USER_KEYSPACE,
							USER_BASE_COLLECTION);
					update.with(QueryBuilder.set("emailIdList", emailList));
					update.where(QueryBuilder.eq("userBaseId",
							registeredUserBaseId));
					cassandraTemplate.execute(update);
				}
				logger.info("Account activated!!!");

				// Update SocialMediaType
				result = updateSocialMediaType(linkedAccountType
						.getSocialMediaType());
			} else {
				logger.info("Email is not registered!!!");
				logger.error("activateAccount : Email is not registered!!!");
				return GlobalConstants.USER_DOES_NOT_EXIST;
			}

			if (result == GlobalConstants.OPRERATION_SUCCESS) {
				return GlobalConstants.OPRERATION_SUCCESS;
			} else {
				logger.error("activateAccount : Error in updating socialMediaTYpe!!");
				return GlobalConstants.OPRERATION_FAILURE;
			}

		} catch (Exception ex) {

			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;

		}
	}

	@Override
	public FriendsLikesList fetchUserFriends(String email) {
		try {

			List<Row> rows;
			UserBase userList = null;
			UserBase userBase = null;
			UserStatus userStatus = null;
			SocialMediaType socialMediaTypeData = null;
			Select userBaseListSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_BASE_COLLECTION);

			rows = cassandraTemplate.query(userBaseListSelect).all();

			for (Row r : rows) {
				List<String> emailIds = r.getList("emailIdList", String.class);
				for (String emailId : emailIds) {
					if (emailId.equals(email)) {
						userList = new UserBase();
						userList.setUserBaseId(r.getString("userBaseId"));
						userList.setName(r.getString("name"));
						userList.setEmailIdList(r.getList("emailIdList",
								String.class));
						userList.setPassword(r.getString("password"));
						userList.setAge(r.getString("age"));
						userList.setGender(r.getString("gender"));
						userList.setDob(r.getString("dob"));
						break;
					}
				}

			}

			SocialMediaData socialMediaData = null;
			Select socialMediaDataSelect = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_DATA_COLLECTION);
			socialMediaDataSelect.where(QueryBuilder.eq("userBaseId",
					userList.getUserBaseId()));

			rows = cassandraTemplate.query(socialMediaDataSelect).all();
			for (Row r : rows) {

				if (r.getString("socialMediaAttrTypeId").equals("2")) {
					socialMediaData = new SocialMediaData();
					socialMediaData.setUserBaseId(r.getString("userBaseId"));
					socialMediaData.setSocialMediaAttrTypeId(r
							.getString("socialMediaAttrTypeId"));
					socialMediaData.setSocialMediaAttrvalueList(r.getList(
							"socialMediaAttrvalueList",
							SocialMeidaAttrValue.class));
					break;
				}
			}

			FriendsLikesList friendsLikesList = null;

			List<FriendsLikes> lstOutputFLL = new ArrayList<FriendsLikes>();

			if (socialMediaData != null) {
				// UserFriendListOutput flst;
				FriendsLikes usrFLL;
				for (SocialMeidaAttrValue uf : socialMediaData
						.getSocialMediaAttrvalueList()) {
					friendsLikesList = new FriendsLikesList();

					usrFLL = new FriendsLikes();
					usrFLL.setName(uf.getAttrValue());

					String sm_id = uf.getAttrId();
					String userBaseId = "";
					Select socialMediaTypeSelect = QueryBuilder.select().from(
							USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);

					socialMediaTypeSelect.where(QueryBuilder.eq(
							"socialMediaId", sm_id));
					rows = cassandraTemplate.query(socialMediaTypeSelect).all();
					for (Row r : rows) {
						socialMediaTypeData = new SocialMediaType();
						socialMediaTypeData.setUserBaseId(r
								.getString("userBaseId"));
						socialMediaTypeData.setSocialMediatypeId(r
								.getString("socialMediatypeId"));
						socialMediaTypeData.setSocialMediaId(r
								.getString("socialMediaId"));
						socialMediaTypeData.setAccessToken(r
								.getString("accessToken"));
						socialMediaTypeData.setProfilePicUrl(r
								.getString("profilePicUrl"));
						break;

					}
					if (socialMediaTypeData != null) {
						userBaseId = socialMediaTypeData.getUserBaseId();
						Select userStatusSelect = QueryBuilder.select().from(
								USER_KEYSPACE, USER_STATUS_COLLECTION);
						userStatusSelect.where(QueryBuilder.eq("userBaseId",
								userBaseId));
						rows = cassandraTemplate.query(userStatusSelect).all();

						for (Row r : rows) {
							userStatus = new UserStatus();
							userStatus.setStateId(r.getString("stateId"));
							userStatus.setLastLoginTime(r
									.getDate("lastLoginTime"));
							userStatus.setCurrentLoginTime(r
									.getDate("currentLoginTime"));
							userStatus.setCumulativeloginCount(r
									.getInt("cumulativeloginCount"));
							break;
						}

					}

					Select userBaseSelect = QueryBuilder.select().from(
							USER_KEYSPACE, USER_BASE_COLLECTION);

					userBaseSelect.where(QueryBuilder.eq("userBaseId",
							userBaseId));
					rows = cassandraTemplate.query(userBaseSelect).all();
					for (Row r : rows) {
						userBase = new UserBase();
						userBase.setUserBaseId(r.getString("userBaseId"));
						userBase.setName(r.getString("name"));
						userBase.setEmailIdList(r.getList("emailIdList",
								String.class));
						userBase.setPassword(r.getString("password"));
						userBase.setAge(r.getString("age"));
						userBase.setGender(r.getString("gender"));
						userBase.setDob(r.getString("dob"));
						break;
					}

					if (userBase != null) {
						String FrndName = userBase.getName();
						String Frndemail = userBase.getEmailIdList().get(0);
						SocialMediaData frndLikesObject = fetchUserLikes(Frndemail);
						usrFLL.setEmail(Frndemail);
						usrFLL.setSocialMediaAttrvalueList(frndLikesObject
								.getSocialMediaAttrvalueList());
						usrFLL.setName(FrndName);
						usrFLL.setStateId(userStatus.getStateId());
						usrFLL.setProfile_pic_url(socialMediaTypeData
								.getProfilePicUrl());
					} else {
						usrFLL.setName(uf.getAttrValue());
					}
					lstOutputFLL.add(usrFLL);
				}

				friendsLikesList.setFriendsLikesList(lstOutputFLL);
			}
			if (friendsLikesList != null)
				return friendsLikesList;
			else
				return null;
		} catch (Exception ex) {

			logger.error(ex);
			return null;

		}
	}

	@Override
	public SocialMediaData fetchUserLikes(String email) {
		try {

			List<Row> rows;
			Select userBaseListSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_BASE_COLLECTION);

			rows = cassandraTemplate.query(userBaseListSelect).all();
			UserBase userList = null;
			for (Row r : rows) {

				userList = new UserBase();
				List<String> emailIds = r.getList("emailIdList", String.class);
				for (String emailId : emailIds) {
					if (emailId.equals(email)) {
						userList = new UserBase();
						userList.setUserBaseId(r.getString("userBaseId"));
						userList.setName(r.getString("name"));
						userList.setEmailIdList(r.getList("emailIdList",
								String.class));
						userList.setPassword(r.getString("password"));
						userList.setAge(r.getString("age"));
						userList.setGender(r.getString("gender"));
						userList.setDob(r.getString("dob"));
						break;
					}
				}
			}

			SocialMediaData socialMediaData = null;
			/*
			 * from here we need to write a new code to send the object
			 * containing friends likes
			 */
			if (userList != null) {
				Select socialMediaSelect = QueryBuilder.select().from(
						USER_KEYSPACE, SOCIAL_MEDIA_DATA_COLLECTION);
				socialMediaSelect.where(QueryBuilder.eq("userBaseId",
						userList.getUserBaseId()));
				rows = cassandraTemplate.query(socialMediaSelect).all();
				for (Row r : rows) {

					if (r.getString("socialMediaAttrTypeId").equals("1")) {
						socialMediaData = new SocialMediaData();
						socialMediaData
						.setUserBaseId(r.getString("userBaseId"));
						socialMediaData.setSocialMediaAttrTypeId(r
								.getString("socialMediaAttrTypeId"));
						socialMediaData.setSocialMediaAttrvalueList(r.getList(
								"socialMediaAttrvalueList",
								SocialMeidaAttrValue.class));
						break;
					}
				}

			}

			return socialMediaData;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}

	}

	@Override
	public int logout(UserBase userBase) {
		try {

			List<Row> rows;
			Select userBaseSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			if (userBase.getUserBaseId() != null) {
				userBaseSelect.where(QueryBuilder.eq("userBaseId",
						userBase.getUserBaseId()));
			}

			rows = cassandraTemplate.query(userBaseSelect).all();
			UserBase retrievedUserBase = null;
			for (Row r : rows) {

				retrievedUserBase = new UserBase();
				retrievedUserBase = new UserBase();
				retrievedUserBase.setUserBaseId(r.getString("userBaseId"));
				retrievedUserBase.setName(r.getString("name"));
				retrievedUserBase.setEmailIdList(r.getList("emailIdList",
						String.class));
				retrievedUserBase.setPassword(r.getString("password"));
				retrievedUserBase.setAge(r.getString("age"));
				retrievedUserBase.setGender(r.getString("gender"));
				retrievedUserBase.setDob(r.getString("dob"));
				break;

			}

			String userBaseId = retrievedUserBase.getUserBaseId();
			Select userStatusSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_STATUS_COLLECTION);
			userStatusSelect.where(QueryBuilder.eq("userBaseId", userBaseId));

			UserStatus userStatus = null;
			rows = cassandraTemplate.query(userStatusSelect).all();
			for (Row r : rows) {

				userStatus = new UserStatus();
				userStatus.setStateId(r.getString("stateId"));
				userStatus.setLastLoginTime(r.getDate("lastLoginTime"));
				userStatus.setCurrentLoginTime(r.getDate("currentLoginTime"));
				userStatus.setCumulativeloginCount(r
						.getInt("cumulativeloginCount"));
				break;

			}

			if (userStatus != null) {
				Update update = QueryBuilder.update(USER_KEYSPACE,
						USER_STATUS_COLLECTION);

				update.where(QueryBuilder.eq("userBaseId", userBaseId));
				update.with(QueryBuilder.set("stateId", "0"));

				cassandraTemplate.execute(update);

			}

			/* Logging Register activity */
			Activity activityLog = new Activity();
			activityLog.setUserBaseId(retrievedUserBase.getUserBaseId());
			activityLog.setActivityType(String
					.valueOf(GlobalConstants.ACTIVITY_TYPE_LOGOUT));
			activityLog.setActivityTimeStamp(new Date());
			int res = logActivity(activityLog);
			logger.info("Logging result :" + res);

			return GlobalConstants.OPRERATION_SUCCESS;
		} catch (Exception ex) {

			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;

		}
	}

	@Override
	public String validateUser(UserBase userBase) {
		try {

			List<Row> rows;
			String email = userBase.getEmailIdList().get(0);
			String password = userBase.getPassword();
			Select userBaseSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);

			UserBase userBaseList = null;
			rows = cassandraTemplate.query(userBaseSelect).all();
			for (Row r : rows) {
				List<String> emailIds = r.getList("emailIdList", String.class);
				for (String emailId : emailIds) {
					if (emailId.equals(email)) {
						userBaseList = new UserBase();
						userBaseList.setUserBaseId(r.getString("userBaseId"));
						userBaseList.setName(r.getString("name"));
						userBaseList.setEmailIdList(r.getList("emailIdList",
								String.class));
						userBaseList.setPassword(r.getString("password"));
						userBaseList.setAge(r.getString("age"));
						userBaseList.setGender(r.getString("gender"));
						userBaseList.setDob(r.getString("dob"));
						break;
					}
				}
			}

			if (userBaseList == null) {
				return Integer.toString(GlobalConstants.USER_DOES_NOT_EXIST);
			} else {
				/* code added by srihari */
				String dbPassword = userBaseList.getPassword();
				String decodedPwd = decode(dbPassword);
				logger.info("Decoded Password : " + decodedPwd
						+ " User Password : " + password);
				/* code added by srihari */

				if (!decodedPwd.equals(password)) {

					logger.error("validateUser : Incorrect Password!!");
					return Integer.toString(GlobalConstants.INCORRECT_PASSWORD);
				} else {
					return userBaseList.getUserBaseId();
				}
			}

		} catch (Exception ex) {

			logger.error(ex);
			return Integer.toString(GlobalConstants.OPRERATION_FAILURE);

		}
	}

	@Override
	public UserCount countFriends() {
		try {
			List<Row> rows;
			Select socialMediaSelect = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);

			List<SocialMediaType> socialMediaTypeList = new ArrayList<SocialMediaType>();

			int googleUsersCount = 0;
			int facebookUsersCount = 0;
			rows = cassandraTemplate.query(socialMediaSelect).all();
			for (Row r : rows) {
				SocialMediaType socialMediaTypeData = new SocialMediaType();
				socialMediaTypeData.setUserBaseId(r.getString("userBaseId"));
				socialMediaTypeData.setSocialMediatypeId(r
						.getString("socialMediatypeId"));
				socialMediaTypeData.setSocialMediaId(r
						.getString("socialMediaId"));
				socialMediaTypeData.setAccessToken(r.getString("accessToken"));
				socialMediaTypeData.setProfilePicUrl(r
						.getString("profilePicUrl"));
				socialMediaTypeList.add(socialMediaTypeData);
				break;

			}
			if (socialMediaTypeList != null) {
				for (int i = 0; i < socialMediaTypeList.size(); i++) {
					if (socialMediaTypeList.get(i).getSocialMediatypeId()
							.equals("1"))
						facebookUsersCount++;
					else if (socialMediaTypeList.get(i).getSocialMediatypeId()
							.equals("2")) {
						googleUsersCount++;
					}
				}
			}

			UserCount userCount = new UserCount();
			userCount.setFacebookUserCount(facebookUsersCount);
			userCount.setGoogleUserCount(googleUsersCount);

			return userCount;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public UserActivityList fetchAllUserActivity() {
		try {
			List<Row> rows;
			Select allUsersSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);
			List<UserReview> userReviewList = new ArrayList<UserReview>();
			UserActivityList userActivityList = new UserActivityList();
			rows = cassandraTemplate.query(allUsersSelect).all();
			for (Row r : rows) {
				UserReview userReview = new UserReview();
				userReview.setCommentId(r.getString("commentId"));
				userReview.setUrlId(r.getInt("urlId"));
				userReview.setUrl(r.getString("url"));
				userReview.setTypeId(r.getInt("typeId"));
				userReview.setParentCommentId(r.getString("parentCommentId"));
				userReview.setUserName(r.getString("userName"));
				userReview.setUserAvatar(r.getString("userAvatar"));
				userReview.setSocialMediaAttrId(r.getInt("socialMediaAttrId"));
				userReview.setDateTime(r.getDate("dateTime"));
				userReview.setCommentTitle(r.getString("commentTitle"));
				userReview.setUserComment(r.getString("userComment"));
				userReview.setUserRating(r.getFloat("userRating"));
				userReview.setUserBaseId(r.getString("userBaseId"));
				userReview.setNumofreplies(r.getInt("numofreplies"));
				userReview.setNumUpvotes(r.getInt("numUpvotes"));
				userReview.setNumDownvotes(r.getInt("numDownvotes"));
				userReview.setUpvotesBy(r.getList("upvotesBy", String.class));
				userReview.setDownvotesBy(r
						.getList("downvotesBy", String.class));
				userReviewList.add(userReview);
			}
			if (!userReviewList.isEmpty()) {
				userActivityList.setUserReview(userReviewList);
				return userActivityList;
			} else
				return null;

		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public List<UserReview> fetchUserActivity(String userBase) {
		try {
			Select userSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);
			List<UserReview> userReviewList = new ArrayList<UserReview>();
			List<Row> rows;
			rows = cassandraTemplate.query(userSelect).all();
			for (Row r : rows) {
				if (r.getString("userBaseId").equals(userBase)) {
					UserReview userReview = new UserReview();
					userReview.setCommentId(r.getString("commentId"));
					userReview.setUrlId(r.getInt("urlId"));
					userReview.setUrl(r.getString("url"));
					userReview.setTypeId(r.getInt("typeId"));
					userReview.setParentCommentId(r
							.getString("parentCommentId"));
					userReview.setUserName(r.getString("userName"));
					userReview.setUserAvatar(r.getString("userAvatar"));
					userReview.setSocialMediaAttrId(r
							.getInt("socialMediaAttrId"));
					userReview.setDateTime(r.getDate("dateTime"));
					userReview.setCommentTitle(r.getString("commentTitle"));
					userReview.setUserComment(r.getString("userComment"));
					userReview.setUserRating(r.getFloat("userRating"));
					userReview.setUserBaseId(r.getString("userBaseId"));
					userReview.setNumofreplies(r.getInt("numofreplies"));
					userReview.setNumUpvotes(r.getInt("numUpvotes"));
					userReview.setNumDownvotes(r.getInt("numDownvotes"));
					userReview.setUpvotesBy(r
							.getList("upvotesBy", String.class));
					userReview.setDownvotesBy(r.getList("downvotesBy",
							String.class));
					userReviewList.add(userReview);
				}

			}
			if (!userReviewList.isEmpty()) {
				return userReviewList;
			} else
				return null;

		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public int updatePassword(PasswordUpdate passwordUpdate) {
		try {
			Select emailSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			List<Row> rows;
			rows = cassandraTemplate.query(emailSelect).all();
			UserBase userList = null;
			for (Row r : rows) {
				userList = new UserBase();
				List<String> emailIds = r.getList("emailIdList", String.class);
				for (String emailId : emailIds) {
					if (emailId.equals(passwordUpdate.getEmail())) {
						userList = new UserBase();
						userList.setUserBaseId(r.getString("userBaseId"));
						userList.setName(r.getString("name"));
						userList.setEmailIdList(r.getList("emailIdList",
								String.class));
						userList.setPassword(r.getString("password"));
						userList.setAge(r.getString("age"));
						userList.setGender(r.getString("gender"));
						userList.setDob(r.getString("dob"));
						break;
					}
				}

			}
			if (userList != null) {

				String encryptedUserPwd = encode(passwordUpdate
						.getNewPassword());
				logger.info("Encrypted password : " + encryptedUserPwd
						+ "Actual password : "
						+ passwordUpdate.getNewPassword());

				Update update = QueryBuilder.update(USER_KEYSPACE,
						USER_BASE_COLLECTION);
				update.where(QueryBuilder.eq("userBaseId",
						userList.getUserBaseId()));
				update.with(QueryBuilder.set("password", encryptedUserPwd));
				cassandraTemplate.execute(update);
				return GlobalConstants.OPRERATION_SUCCESS;

			} else {
				logger.error("updatePassword : User does not exist.");
				return GlobalConstants.USER_DOES_NOT_EXIST;
			}

		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}

	}

	@Override
	public int updateServices(IdleServices idleServices) {
		try {
			Select idleServiceSelect = QueryBuilder.select().from(
					USER_KEYSPACE, IDLE_SERVICES_COLLECTION);
			IdleServices idleServicesList = null;
			List<Row> rows;
			rows = cassandraTemplate.query(idleServiceSelect).all();
			for (Row r : rows) {
				idleServicesList = new IdleServices();
				idleServicesList.setSocial(r.getString("social"));
				idleServicesList.setRegister(r.getString("register"));
			}
			if (idleServicesList != null) {
				Update update = QueryBuilder.update(USER_KEYSPACE,
						IDLE_SERVICES_COLLECTION);
				update.with(QueryBuilder.set("register",
						idleServices.getRegister()));
				update.with(QueryBuilder.set("social", idleServices.getSocial()));
				cassandraTemplate.execute(update);
				return GlobalConstants.OPRERATION_SUCCESS;
			} else {
				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						IDLE_SERVICES_COLLECTION);
				insert.value("register", idleServices.getRegister());
				insert.value("social", idleServices.getSocial());
				cassandraTemplate.execute(insert);
				return GlobalConstants.OPRERATION_SUCCESS;
			}

		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public int sendPwdLink(String email) {
		Select emailSelect = QueryBuilder.select().from(USER_KEYSPACE,
				USER_BASE_COLLECTION);
		List<Row> rows;
		UserBase userList = null;
		rows = cassandraTemplate.query(emailSelect).all();
		for (Row r : rows) {
			userList = new UserBase();
			List<String> emailIds = r.getList("emailIdList", String.class);
			for (String emailId : emailIds) {
				if (emailId.equals(email)) {
					userList = new UserBase();
					userList.setUserBaseId(r.getString("userBaseId"));
					userList.setName(r.getString("name"));
					userList.setEmailIdList(r.getList("emailIdList",
							String.class));
					userList.setPassword(r.getString("password"));
					userList.setAge(r.getString("age"));
					userList.setGender(r.getString("gender"));
					userList.setDob(r.getString("dob"));
					break;
				}
			}

		}

		if (userList == null) {
			logger.error("sendPwdLink : User does not exist.");
			return GlobalConstants.USER_DOES_NOT_EXIST;
		} else {
			SecureRandom random = new SecureRandom();
			String code = new BigInteger(130, random).toString(32);

			int result = saveCode(code, new Date());
			if (result == GlobalConstants.CODE_ALREADY_PRESENT) {
				logger.error("sendPwdLink : Code already present");
				return GlobalConstants.CODE_ALREADY_PRESENT;
			} else if (result == GlobalConstants.OPRERATION_FAILURE) {
				logger.error("sendPwdLink : save code Operation failed");
				return GlobalConstants.OPRERATION_FAILURE;
			}

			String msg = "Please click on below link to reset your password\n";
			msg += "http://localhost:8080/foodlounge/?resetPassword=";
			msg += code;
			emailNotify(email, userList.getName(), null, msg);
			return GlobalConstants.OPRERATION_SUCCESS;
		}
	}

	private int saveCode(String code, Date date) {
		try {
			List<Row> rows;
			Select codeSelect = QueryBuilder.select().from(USER_KEYSPACE,
					CODE_FOR_PASSWORD_RESET);
			codeSelect.where(QueryBuilder.eq("code", code));
			CodeForPasswordReset codeList = null;
			rows = cassandraTemplate.query(codeSelect).all();
			for (Row r : rows) {
				codeList = new CodeForPasswordReset();
				codeList.setCode(r.getString("code"));
				Calendar cal = Calendar.getInstance();
				cal.setTime(r.getDate("time"));
				codeList.setTime(cal);
			}
			CodeForPasswordReset codeForPasswordReset = new CodeForPasswordReset();
			codeForPasswordReset.setCode(code);
			codeForPasswordReset.setTime(new GregorianCalendar());
			if (codeList == null) {
				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						CODE_FOR_PASSWORD_RESET);
				insert.value("code", codeForPasswordReset.getCode());
				Date codeTime = codeForPasswordReset.getTime().getTime();
				insert.value("time", codeTime);
				cassandraTemplate.execute(insert);
				return GlobalConstants.OPRERATION_SUCCESS;
			} else {
				logger.info("Code already exists");
				logger.error("saveCode : Same code already present in DB. Please try again!!!");
				return GlobalConstants.CODE_ALREADY_PRESENT;
			}

		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public int validateCode(String code) {
		try {
			List<Row> rows;
			Select codeSelect = QueryBuilder.select().from(USER_KEYSPACE,
					CODE_FOR_PASSWORD_RESET);
			codeSelect.where(QueryBuilder.eq("code", code));
			CodeForPasswordReset codeList = null;
			rows = cassandraTemplate.query(codeSelect).all();
			for (Row r : rows) {
				codeList = new CodeForPasswordReset();
				codeList.setCode(r.getString("code"));
				Calendar cal = Calendar.getInstance();
				cal.setTime(r.getDate("time"));
				codeList.setTime(cal);
			}
			if (codeList == null) {
				logger.info("Code not present");
				logger.error("validateCode : Wrong URL!!!Code is not present in DB!!");
				return GlobalConstants.CODE_NOT_PRESENT;
			} else {
				int resp = GlobalConstants.OPRERATION_FAILURE;
				Calendar codeTime = codeList.getTime();
				int day, month, year;
				int second, minute, hour;

				Date date = new Date(); // your date
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				day = cal.get(Calendar.DAY_OF_MONTH);
				month = cal.get(Calendar.MONTH);
				year = cal.get(Calendar.YEAR);

				second = cal.get(Calendar.SECOND);
				minute = cal.get(Calendar.MINUTE);
				hour = cal.get(Calendar.HOUR);

				logger.info(day + "/" + month + "/" + year + " " + hour + ":"
						+ minute + ":" + second + "\n");
				logger.info(codeTime.get(Calendar.DAY_OF_MONTH) + "/"
						+ codeTime.get(Calendar.MONTH) + "/"
						+ codeTime.get(Calendar.YEAR) + "/"
						+ codeTime.get(Calendar.HOUR) + "/"
						+ codeTime.get(Calendar.MINUTE) + "/"
						+ codeTime.get(Calendar.SECOND));
				if (year != codeTime.get(Calendar.YEAR)) {
					logger.error("validateCode : Year not same");
					resp = GlobalConstants.OPRERATION_FAILURE;
				} else if (month != codeTime.get(Calendar.MONTH)) {
					logger.error("validateCode : Month not same");
					resp = GlobalConstants.OPRERATION_FAILURE;
				} else if (day != codeTime.get(Calendar.DAY_OF_MONTH)) {
					logger.error("validateCode : Day not same");
					resp = GlobalConstants.OPRERATION_FAILURE;
				} else if (hour > codeTime.get(Calendar.HOUR) + 1) {
					logger.error("validateCode : Exceeds 1 hour");
					resp = GlobalConstants.OPRERATION_FAILURE;
				} else if (hour == codeTime.get(Calendar.HOUR) + 1) {
					if (minute > codeTime.get(Calendar.MINUTE)) {
						logger.error("validateCode : Exceeds 1 hour");
						resp = GlobalConstants.OPRERATION_FAILURE;
					} else if (minute == codeTime.get(Calendar.MINUTE)) {
						if (second > codeTime.get(Calendar.SECOND)) {
							logger.error("validateCode : Exceeds 1 hour");
							resp = GlobalConstants.OPRERATION_FAILURE;
						}
					} else {
						resp = GlobalConstants.OPRERATION_SUCCESS;
					}
				} else
					resp = GlobalConstants.OPRERATION_SUCCESS;

				return resp;

			}

		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public RegisteredUserFilters newRegisteredUsers(
			RegisteredUserFilters registeredUserFilters) {
		List<Row> rows;
		List<UserBase> usersList = new ArrayList<UserBase>();
		long userBaseCount = 0;
		Select userBaseListSelect = QueryBuilder.select().from(USER_KEYSPACE,
				USER_BASE_COLLECTION);
		rows = cassandraTemplate.query(userBaseListSelect).all();
		for (Row r : rows) {
			if ((registeredUserFilters.getContextId() != null)
					&& (r.getDate("userRegisteredDate")
							.compareTo(registeredUserFilters.getEndDate())) < 0
							&& (r.getDate("userRegisteredDate")
									.compareTo(registeredUserFilters.getStartDate())) > 0)
				userBaseCount++;
		}
		registeredUserFilters.setRegisteredUserCount((int) userBaseCount);
		return registeredUserFilters;
	}

	@Override
	public UserCount newSocialUsers(RegisteredUserFilters registeredUserFilters) {
		try {
			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
			List<Row> rows;

			int googleUsersCount = 0;
			int facebookUsersCount = 0;
			rows = cassandraTemplate.query(socialMediaTypeSelect).all();
			for (Row r : rows) {
				if ((registeredUserFilters.getContextId() != null)
						&& (r.getDate("userRegisteredDate") != null)
						&& (r.getDate("userRegisteredDate")
								.compareTo(registeredUserFilters.getEndDate())) < 0
								&& (r.getDate("userRegisteredDate")
										.compareTo(registeredUserFilters.getStartDate())) > 0) {
					if (r.getString("socialMediatypeId").equalsIgnoreCase("1"))
						facebookUsersCount++;
					if (r.getString("socialMediatypeId").equalsIgnoreCase("2"))
						googleUsersCount++;
				}
			}
			UserCount userCount = new UserCount();
			userCount.setFacebookUserCount(facebookUsersCount);
			userCount.setGoogleUserCount(googleUsersCount);
			return userCount;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}

	}

	@Override
	public int checkRegisteredUser(String userBaseId) {
		try {

			List<Row> rows;
			UserBase userBase = null;
			Select registerSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			registerSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
			rows = cassandraTemplate.query(registerSelect).all();
			for (Row r : rows) {
				userBase = new UserBase();
				userBase.setUserBaseId(r.getString("userBaseId"));
				userBase.setName(r.getString("name"));
				userBase.setEmailIdList(r.getList("emailIdList", String.class));
				userBase.setPassword(r.getString("password"));
				userBase.setAge(r.getString("age"));
				userBase.setGender(r.getString("gender"));
				userBase.setDob(r.getString("dob"));
				userBase.setUserRegisteredDate(r.getDate("userRegisteredDate"));
				break;
			}

			int resp = GlobalConstants.OPRERATION_FAILURE;
			if (userBase.getUserRegisteredDate() != null) { // Check if it is a
				// registered user

				Select socialMediaTypeSelect = QueryBuilder.select().from(
						USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
				SocialMediaType socialMediaType = null;

				socialMediaTypeSelect.where(QueryBuilder.eq("userBaseId",
						userBaseId));
				rows = cassandraTemplate.query(socialMediaTypeSelect).all();
				for (Row r : rows) {
					socialMediaType = new SocialMediaType();
					socialMediaType.setUserBaseId(r.getString("userBaseId"));
					socialMediaType.setSocialMediatypeId(r
							.getString("socialMediatypeId"));
					socialMediaType.setSocialMediaId(r
							.getString("socialMediaId"));
					socialMediaType.setAccessToken(r.getString("accessToken"));
					socialMediaType.setProfilePicUrl(r
							.getString("profilePicUrl"));
					break;
				}

				if (socialMediaType != null) {
					if (socialMediaType.getSocialMediatypeId().equals("1")) {
						logger.info("checkRegisteredUser : Facebook linked to account");
						resp = GlobalConstants.SOCIAL_MEDIA_FB_LINKED;

					}

					else if (socialMediaType.getSocialMediatypeId().equals("2")) {
						logger.info("checkRegisteredUser : Google Plus account linked.");
						resp = GlobalConstants.SOCIAL_MEDIA_GOOGLE_LINKED;
					}
				} else {
					logger.info("checkRegisteredUser : No social media account linked.");
					resp = GlobalConstants.SOCIAL_MEDIA_NOT_LINKED;
				}
			}
			return resp;
		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public int deactivateAccount(String userBaseId) {
		try {

			Delete socialMediaTypeDelete = QueryBuilder.delete().from(
					USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
			socialMediaTypeDelete.where(QueryBuilder.eq("userBaseId",
					userBaseId));
			cassandraTemplate.execute(socialMediaTypeDelete);

			Delete socialMediaDataDelete = QueryBuilder.delete().from(
					USER_KEYSPACE, SOCIAL_MEDIA_DATA_COLLECTION);
			socialMediaDataDelete.where(QueryBuilder.eq("userBaseId",
					userBaseId));
			cassandraTemplate.execute(socialMediaDataDelete);

			return GlobalConstants.OPRERATION_SUCCESS;

		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public String getProfilePath(String userBase) {

		Row row;
		Select profilePicSelect = QueryBuilder.select().from(USER_KEYSPACE,
				USER_BASE_COLLECTION);
		profilePicSelect.where(QueryBuilder.eq("userBaseId", userBase));
		row = cassandraTemplate.query(profilePicSelect).one();

		if (row != null) {
			return row.getString("nameOfFile");
		} else {
			return Integer.toString(GlobalConstants.OPRERATION_FAILURE);
		}
	}

	@Override
	public String getUserName(String userBaseId) {
		try {
			Row row;
			Select userSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			userSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
			UserBase userBase = null;
			row = cassandraTemplate.query(userSelect).one();
			if (row != null) {
				return row.getString("name");
			} else
				return null;

		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}

	}

	// Rating and Review ++
	@Override
	public UrlRatingRef fetchUrlRating(String url) {
		try {
			Select urlRatingSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlRatingSelect.where(QueryBuilder.eq("url", url));

			UrlRatingRef urlRating = null;
			Row row = cassandraTemplate.query(urlRatingSelect).one();
			if (row != null) {
				urlRating = new UrlRatingRef();
				urlRating.setAverageRating(row.getFloat("averageRating"));
				urlRating.setTotNumRating(row.getInt("totNumRating"));
				urlRating.setUrlId(row.getInt("urlId"));
				urlRating.setUrl(row.getString("url"));
				urlRating.setLastCommentId(row.getString("lastCommentId"));
			} else {
				urlRating = new UrlRatingRef();
				urlRating.setAverageRating(0);
				urlRating.setTotNumRating(0);
				urlRating.setUrlId(0);
				urlRating.setUrl(url);
				urlRating.setLastCommentId("0");
			}
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			productSelect.where(QueryBuilder.eq("id", url));
			List<Row> rows = cassandraTemplate.query(productSelect).all();
			if (rows != null) {
				Update update = QueryBuilder.update(USER_KEYSPACE,
						PRODUCT_COLLECTION);
				update.with(QueryBuilder.set("totalNumberOfRatings",
						urlRating.getTotNumRating()));
				update.with(QueryBuilder.set("productRating",
						urlRating.getAverageRating()));
				update.where(QueryBuilder.eq("id", url));
				cassandraTemplate.execute(update);
			}

			return urlRating;

		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public int updateUserReview(ReviewUpdateReq ruReq) {
		// find the url id from urlRatingRef
		try {

			logger.info("In updateUserReview");

			logger.info("ruReq.getUrl() = " + ruReq.getUrl());

			Select urlRatingSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlRatingSelect.where(QueryBuilder.eq("url", ruReq.getUrl()));

			UrlRatingRef urlRating = new UrlRatingRef();
			Row row = cassandraTemplate.query(urlRatingSelect).one();

			// if no data, update
			if (null == row) {
				urlRating = new UrlRatingRef();
				urlRating.setUrl(ruReq.getUrl());
				urlRating.setAverageRating(0);
				urlRating.setTotNumRating(0);
				urlRating.setLastCommentId("0");
				urlRating.setUrlId(getNextSequence("urlId"));
				logger.info("Inserting new URL data in URL_RATING_DATA_COLLECTION");

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						URL_RATING_DATA_COLLECTION);
				insert.value("url", urlRating.getUrl());
				insert.value("totNumRating", urlRating.getTotNumRating());
				insert.value("averageRating", urlRating.getAverageRating());
				insert.value("urlId", urlRating.getUrlId());
				insert.value("lastCommentId", urlRating.getLastCommentId());
				cassandraTemplate.execute(insert);
			} else {
				urlRating = new UrlRatingRef();
				urlRating.setUrl(row.getString("url"));
				urlRating.setAverageRating(row.getFloat("averageRating"));
				urlRating.setTotNumRating(0);
				urlRating.setLastCommentId("0");
				urlRating.setUrlId(row.getInt("urlId"));
			}

			// get the url id from new record
			int urlId = urlRating.getUrlId();
			int numRating = urlRating.getTotNumRating();
			float currAvg = urlRating.getAverageRating();

			// insert review comments and rating
			UserReview usrReview = new UserReview();
			Random rand = new Random();
			int randomNum = rand.nextInt((1000 - 1) + 1) + 1;
			usrReview.setCommentId(Integer.toString(randomNum));
			usrReview.setCommentTitle(ruReq.getCommentTitle());
			usrReview.setDateTime(new Date());
			usrReview.setParentCommentId("");
			usrReview.setSocialMediaAttrId(0);
			usrReview.setTypeId(1);
			usrReview.setUrlId(urlId);
			usrReview.setUserAvatar(ruReq.getUserAvatar());
			usrReview.setUserComment(ruReq.getUserComment());
			usrReview.setUserName(ruReq.getUserName());
			usrReview.setUserRating(ruReq.getUserRating());
			usrReview.setUserBaseId(ruReq.getUserBaseId());
			// calculate average rating

			float avgRating = ((currAvg * numRating) + ruReq.getUserRating())
					/ (++numRating);

			urlRatingSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlRatingSelect.where(QueryBuilder.eq("url", ruReq.getUrl()));
			row = cassandraTemplate.query(urlRatingSelect).one();
			// increment rating count
			Update urlRatingUpdate = QueryBuilder.update(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlRatingUpdate.where(QueryBuilder.eq("url", ruReq.getUrl()));
			urlRatingUpdate.with(QueryBuilder.set("averageRating", avgRating));
			urlRatingUpdate.with(QueryBuilder.set("totNumRating",
					row.getInt("totNumRating") + 1));
			// urlRatingUpdate.inc("totNumRating", 1);
			// find and update url rating.
			cassandraTemplate.execute(urlRatingUpdate);
			logger.info("Updated URL_RATING_DATA_COLLECTION with latest values");

			// Insert data to userReviewComments

			Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);

			insert.value("commentId", usrReview.getCommentId());
			insert.value("urlId", usrReview.getUrlId());
			insert.value("url", usrReview.getUrl());
			insert.value("typeId", usrReview.getTypeId());
			insert.value("parentCommentId", usrReview.getParentCommentId());
			insert.value("userName", usrReview.getUserName());
			insert.value("userAvatar", usrReview.getUserAvatar());
			insert.value("socialMediaAttrId", usrReview.getSocialMediaAttrId());
			insert.value("dateTime", usrReview.getDateTime());
			insert.value("commentTitle", usrReview.getCommentTitle());
			insert.value("userComment", usrReview.getUserComment());
			insert.value("userRating", usrReview.getUserRating());
			insert.value("userBaseId", usrReview.getUserBaseId());
			insert.value("numofreplies", usrReview.getNumofreplies());
			insert.value("numUpvotes", usrReview.getNumUpvotes());
			insert.value("numDownvotes", usrReview.getNumDownvotes());
			insert.value("upvotesBy", usrReview.getUpvotesBy());
			insert.value("downvotesBy", usrReview.getDownvotesBy());
			cassandraTemplate.execute(insert);
			logger.info("Inserted new record in USER_REVIEW_COMMENTS_COLLECTION");

			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
			socialMediaTypeSelect.where(QueryBuilder.eq("userBaseId",
					usrReview.getUserBaseId()));
			row = cassandraTemplate.query(socialMediaTypeSelect).one();

			Activity activity = new Activity();
			activity.setUserBaseId(usrReview.getUserBaseId());
			if (row != null)
				activity.setSocialMediaId(row.getString("socialMediaId"));
			activity.setActivityType(Integer
					.toString(GlobalConstants.ACTIVITY_TYPE_REVIEW));
			activity.setActivityTimeStamp(new Date());
			logActivity(activity);
			return GlobalConstants.OPRERATION_SUCCESS;
		} catch (Exception ex) {
			logger.error(ex);
			logger.info("Caught in updateUserReview exception");
			return GlobalConstants.OPRERATION_FAILURE;
		}

	}

	public List<UserReview> fetchUserReviews(String url) {
		System.out.print("URL for product :" + url);
		try {
			List<UserReview> userReviewList = new ArrayList<UserReview>();
			UserReview userReview;
			Select urlSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlSelect.where(QueryBuilder.eq("url", url));
			UrlRatingRef urlRating = new UrlRatingRef();
			Row row = cassandraTemplate.query(urlSelect).one();
			if (null == row) {
				return null;
			}

			// Fetch the comments from userReviewComments
			Select userReviewSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(userReviewSelect).all();
			for (Row r : rows) {
				if ((r.getInt("urlId") == row.getInt("urlId"))
						&& (r.getInt("typeId") == 1)) {
					userReview = new UserReview();
					userReview.setCommentId(r.getString("commentId"));
					userReview.setUrl(r.getString("url"));
					userReview.setCommentTitle(r.getString("commentTitle"));
					userReview.setDateTime(r.getDate("dateTime"));
					userReview.setParentCommentId(r
							.getString("parentCommentId"));
					// TODO: get the social media attribute id Based on the
					// value
					userReview.setSocialMediaAttrId(r
							.getInt("socialMediaAttrId"));
					userReview.setTypeId(r.getInt("typeId"));
					userReview.setUrlId(r.getInt("urlId"));
					userReview.setUserAvatar(r.getString("userAvatar"));
					userReview.setUserComment(r.getString("userComment"));
					userReview.setUserName(r.getString("userName"));
					userReview.setUserRating(r.getFloat("userRating"));
					userReview.setUserBaseId(r.getString("userBaseId"));
					userReview.setNumofreplies(r.getInt("numofreplies"));
					userReview.setNumUpvotes(r.getInt("numUpvotes"));
					userReview.setNumDownvotes(r.getInt("numDownvotes"));
					userReview.setUpvotesBy(r
							.getList("upvotesBy", String.class));
					userReview.setDownvotesBy(r.getList("downvotesBy",
							String.class));
					userReviewList.add(userReview);
				}
			}
			Collections.sort(userReviewList);
			return userReviewList;
		} catch (Exception e) {
			logger.error(e);
			return null;

		}

	}

	@Override
	public List<UserReview> fetchUserReviewsProduct() {
		try {

			List<UserReview> userReviewList = new ArrayList<UserReview>();
			// get the url id for urlRatingRef.
			List<UserReview> list = fetchUserReviews("/product/Nikon D5300");
			List<UserReview> list1 = fetchUserReviews("/product/Nikon D810");
			List<UserReview> list2 = fetchUserReviews("/product/Nikon D750");
			List<UserReview> list3 = fetchUserReviews("Camera");

			if (list != null)
				userReviewList.addAll(list);
			if (list1 != null)
				userReviewList.addAll(list1);
			if (list2 != null)
				userReviewList.addAll(list2);
			if (list3 != null)
				userReviewList.addAll(list3);

			logger.info("userReviewList ::" + userReviewList.size());
			return userReviewList;
		} catch (Exception ex) {
			if (null != ex)
				logger.error(ex);
			return null;
		}
	}

	public void emailNotify(String email, String name, String password,
			String msg) {
		DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
		defaultClientConfig.getClasses().add(JacksonJsonProvider.class);
		Client client = Client.create(defaultClientConfig);

		WebResource service = client
				.resource("http://localhost:8080/SocialPlugin/rest/Notify/welcome");
		MessageModel form = new MessageModel();
		form.setEmailId(email);
		form.setPasswd(password);
		form.setName(name);
		form.setMsgStr(msg);
		ClientResponse response = service.type("application/json").post(
				ClientResponse.class, form);
		logger.info("Response status : " + response.getClientResponseStatus());
		logger.error("emailNotify : " + response.getClientResponseStatus());
		logger.info("Response " + response.getEntity(String.class));

	}

	@Override
	public void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {
		try {
			OutputStream out;
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			logger.error(e);
		}

	}

	@Override
	public int logActivity(Activity activityLog) {
		try {
			if (activityLog != null) {
				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						ACTIVITY_LOG);
				insert.value("userBaseId", activityLog.getUserBaseId());
				insert.value("socialMediaId", activityLog.getSocialMediaId());
				insert.value("activityType", activityLog.getActivityType());
				insert.value("activityTimeStamp",
						activityLog.getActivityTimeStamp());
				cassandraTemplate.execute(insert);
			}
			return GlobalConstants.OPRERATION_SUCCESS;
		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}

	}

	/* added: Fetching values for gender graph */
	@Override
	public UserCount genderGraphUserBase() {
		try {
			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_BASE_COLLECTION);
			List<UserBase> socialMediaTypeList = new ArrayList<UserBase>();

			int maleCount = 0;
			int femaleCount = 0;
			List<Row> rows = cassandraTemplate.query(socialMediaTypeSelect)
					.all();
			for (Row r : rows) {
				UserBase user = new UserBase();
				user.setUserBaseId(r.getString("userBaseId"));
				user.setName(r.getString("name"));
				user.setEmailIdList(r.getList("emailIdList", String.class));
				user.setPassword(r.getString("password"));
				user.setAge(r.getString("age"));
				user.setGender(r.getString("gender"));
				user.setDob(r.getString("dob"));
				user.setUserRegisteredDate(r.getDate("userRegisteredDate"));
				user.setNameOfFile(r.getString("nameOfFile"));
				user.setYearOfBirth(r.getString("yearOfBirth"));
				socialMediaTypeList.add(user);
			}

			if (!socialMediaTypeList.isEmpty()) {
				for (int i = 0; i < socialMediaTypeList.size(); i++) {
					if (socialMediaTypeList.get(i).getGender() != null) {
						if (socialMediaTypeList.get(i).getGender().equals("1")
								|| socialMediaTypeList.get(i).getGender()
								.equals("male")) {
							logger.info("maleCount" + maleCount++);
						} else if (socialMediaTypeList.get(i).getGender()
								.equals("2")
								|| socialMediaTypeList.get(i).getGender()
								.equals("female")) {
							logger.info("femaleCount" + femaleCount++);
						}
					}
				}
			}

			UserCount userCount = new UserCount();
			userCount.setMaleCount(maleCount);
			userCount.setFemaleCount(femaleCount);

			return userCount;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	/* added:Fetching values for age graph */
	@Override
	public UserCount ageGraphUserBase() {
		try {
			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_BASE_COLLECTION);
			List<UserBase> socialMediaTypeList = new ArrayList<UserBase>();
			int ageLessthanThirteen = 0;
			int ageBetThirteenAndSeventeen = 0;
			int ageBetEighteenAndTwentyfour = 0;
			int ageBetTwentyfiveAndThirtyfour = 0;
			int ageBetThirtyfiveAndFortynine = 0;
			int ageAboveFifty = 0;

			List<Row> rows = cassandraTemplate.query(socialMediaTypeSelect)
					.all();
			for (Row r : rows) {
				UserBase user = new UserBase();
				user.setUserBaseId(r.getString("userBaseId"));
				user.setName(r.getString("name"));
				user.setEmailIdList(r.getList("emailIdList", String.class));
				user.setPassword(r.getString("password"));
				user.setAge(r.getString("age"));
				user.setGender(r.getString("gender"));
				user.setDob(r.getString("dob"));
				user.setUserRegisteredDate(r.getDate("userRegisteredDate"));
				user.setNameOfFile(r.getString("nameOfFile"));
				user.setYearOfBirth(r.getString("yearOfBirth"));
				socialMediaTypeList.add(user);
			}

			if (socialMediaTypeList != null) {
				for (int i = 0; i < socialMediaTypeList.size(); i++) {
					if (socialMediaTypeList.get(i).getYearOfBirth() != null) {

						int birthyear = Integer.parseInt(socialMediaTypeList
								.get(i).getYearOfBirth());

						logger.info(socialMediaTypeList.get(i).getYearOfBirth());

						if (birthyear >= 2001) {
							ageLessthanThirteen++;

						} else if ((birthyear >= 1997) && (birthyear <= 2001)) {
							ageBetThirteenAndSeventeen++;

						} else if ((birthyear >= 1990) && (birthyear <= 1996)) {
							ageBetEighteenAndTwentyfour++;

						} else if ((birthyear >= 1980) && (birthyear <= 1989)) {
							ageBetTwentyfiveAndThirtyfour++;

						} else if ((birthyear >= 1965) && (birthyear <= 1979)) {
							ageBetThirtyfiveAndFortynine++;

						} else if (birthyear <= 1964) {
							ageAboveFifty++;

						}

					}
				}
			}
			UserCount userCount = new UserCount();
			userCount.setAgeLessthanThirteen(ageLessthanThirteen);
			userCount.setAgeBetThirteenAndSeventeen(ageBetThirteenAndSeventeen);
			userCount
			.setAgeBetEighteenAndTwentyfour(ageBetEighteenAndTwentyfour);
			userCount
			.setAgeBetTwentyfiveAndThirtyfour(ageBetTwentyfiveAndThirtyfour);
			userCount
			.setAgeBetThirtyfiveAndFortynine(ageBetThirtyfiveAndFortynine);
			userCount.setAgeAboveFifty(ageAboveFifty);

			return userCount;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}

	}

	/* added for getting socila reg, user reg, daily login count for admin page */
	@Override
	public UserCount getlogincount() {
		try {
			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, ACTIVITY_LOG);
			Select socialMediaTypeSelectdate = QueryBuilder.select().from(
					USER_KEYSPACE, ACTIVITY_LOG);
			Select socialMediaTypeSelectId = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
			List<Activity> socialMediaTypeList = new ArrayList<Activity>();
			List<Activity> socialMediaTypeListdate = new ArrayList<Activity>();
			List<SocialMediaType> socialMediaTypeListId = new ArrayList<SocialMediaType>();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			Date currDate = sdf.parse(sdf.format(new Date()));
			Date nextDate = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					1)));
			int dailyloginCount = 0;
			int dailyregisteredCount = 0;
			int SocialLoginCount = 0;
			List<Row> socialMediaTypeRows = cassandraTemplate.query(
					socialMediaTypeSelect).all();
			List<Row> socialMediaTypeDateRows = cassandraTemplate.query(
					socialMediaTypeSelectdate).all();
			List<Row> socialMediaTypeIdRows = cassandraTemplate.query(
					socialMediaTypeSelectId).all();
			for (Row r : socialMediaTypeRows) {
				Activity activity = new Activity();
				activity.setUserBaseId(r.getString("userBaseId"));
				activity.setSocialMediaId(r.getString("socialMediaId"));
				activity.setActivityTimeStamp(r.getDate("activityTimeStamp"));
				activity.setActivityType(r.getString("activityType"));
				socialMediaTypeList.add(activity);

			}
			if (!socialMediaTypeList.isEmpty()) {
				for (int i = 0; i < socialMediaTypeList.size(); i++) {

					if (socialMediaTypeList
							.get(i)
							.getActivityType()
							.equals(Integer
									.toString(GlobalConstants.ACTIVITY_TYPE_REGISTER))) {
						dailyregisteredCount++;
					}

				}

			}
			for (Row r : socialMediaTypeDateRows) {
				if ((r.getDate("activityTimeStamp").compareTo(currDate) > 0)
						&& (r.getDate("activityTimeStamp").compareTo(nextDate) < 0)) {
					Activity activity = new Activity();
					activity.setUserBaseId(r.getString("userBaseId"));
					activity.setSocialMediaId(r.getString("socialMediaId"));
					activity.setActivityTimeStamp(r
							.getDate("activityTimeStamp"));
					activity.setActivityType(r.getString("activityType"));
					socialMediaTypeListdate.add(activity);
				}
			}
			String activityId;
			if (!socialMediaTypeListdate.isEmpty()) {
				for (int i = 0; i < socialMediaTypeListdate.size(); i++) {
					activityId = socialMediaTypeListdate.get(i)
							.getActivityType();

					if (activityId.equals(Integer
							.toString(GlobalConstants.ACTIVITY_TYPE_LOGIN))) {
						dailyloginCount++;
					}

				}
			}
			for (Row r : socialMediaTypeIdRows) {
				SocialMediaType socialMediaTypeData = new SocialMediaType();
				socialMediaTypeData.setUserBaseId(r.getString("userBaseId"));
				socialMediaTypeData.setSocialMediatypeId(r
						.getString("socialMediatypeId"));
				socialMediaTypeData.setSocialMediaId(r
						.getString("socialMediaId"));
				socialMediaTypeData.setAccessToken(r.getString("accessToken"));
				socialMediaTypeData.setProfilePicUrl(r
						.getString("profilePicUrl"));
				socialMediaTypeListId.add(socialMediaTypeData);
			}
			if (!socialMediaTypeListId.isEmpty()) {
				for (int i = 0; i < socialMediaTypeListId.size(); i++) {

					if (socialMediaTypeListId.get(i).getSocialMediaId() != null) {
						SocialLoginCount++;
					}

				}
			}

			UserCount userCount = new UserCount();
			userCount.setDailyloginCount(dailyloginCount);
			userCount.setDailyregisteredCount(dailyregisteredCount);
			userCount.setSocialLoginCount(SocialLoginCount);
			return userCount;
		}

		catch (Exception ex) {
			logger.error(ex);
			return null;
		}

	}

	/* added:displaying all prof pics in admin page */
	@Override
	public List getAllProfPics() {
		try {
			Select profilePicSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			List<String> profPics = new ArrayList<String>();
			List<Row> rows = cassandraTemplate.query(profilePicSelect).all();

			for (Row r : rows) {
				if (r.getString("nameOfFile") != null) {
					profPics.add(r.getString("nameOfFile"));
				} else {
					Select socialMediaTypeSelect = QueryBuilder.select().from(
							USER_KEYSPACE, SOCIAL_MEDIA_TYPE_COLLECTION);
					socialMediaTypeSelect.where(QueryBuilder.eq("userBaseId",
							r.getString("userBaseId")));
					Row row = cassandraTemplate.query(socialMediaTypeSelect)
							.one();
					if (row != null) {
						profPics.add(row.getString("profilePicUrl"));
					}
				}
			}

			return profPics;

		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public UserCount getsharingCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserCount getcommentsCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserRatingCount> getEachUrlRating() {
		try {

			List<UserRatingCount> lstRatings = new ArrayList<UserRatingCount>();
			UserRatingCount userRatingCount = null;

			// TODO: Refactor the code in better way. writing WA for now
			// //Himalaya
			int rating_1_Count = 0;
			int rating_2_Count = 0;
			int rating_3_Count = 0;
			int rating_4_Count = 0;
			int rating_5_Count = 0;
			List<Float> userRatingList = new ArrayList<Float>();
			Map<Float, Integer> userRatingMap = new HashMap<Float, Integer>();

			Select userRatingCountSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(userRatingCountSelect)
					.all();
			for (Row r : rows) {
				if (!userRatingList.contains(r.getFloat("userRating"))) {
					userRatingList.add(r.getFloat("userRating"));
					int points = userRatingMap.get(r.getFloat("userRating"));
					userRatingMap.put(r.getFloat("userRating"), points + 1);
				}

			}

			Iterator it = userRatingMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Double rating = (Double) pair.getKey();
				int count = (Integer) pair.getValue();

				if (Math.round(rating) == 1) {
					rating_1_Count += count;
					continue;
				}
				if (Math.round(rating) == 2) {
					rating_2_Count += count;
					continue;
				}
				if (Math.round(rating) == 3) {
					rating_3_Count += count;
					continue;
				}
				if (Math.round(rating) == 4) {
					rating_4_Count += count;
					continue;
				}
				if (Math.round(rating) == 5) {
					rating_5_Count += count;
				}
			}

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(1);
			userRatingCount.setRateValue(rating_1_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(2);
			userRatingCount.setRateValue(rating_2_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(3);
			userRatingCount.setRateValue(rating_3_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(4);
			userRatingCount.setRateValue(rating_4_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(5);
			userRatingCount.setRateValue(rating_5_Count);
			lstRatings.add(userRatingCount);

			return lstRatings;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public UserRatingCount getDailyLoginPerWeek() {
		try {

			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, ACTIVITY_LOG);
			List<Activity> socialMediaTypeListdate = new ArrayList<Activity>();
			UserRatingCount loginRatings = new UserRatingCount();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			Date currDate = sdf.parse(sdf.format(new Date()));
			Date newcurrDate = sdf.parse(sdf.format(DateUtils.addDays(
					new Date(), 1)));
			Date weekDate = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					-6)));

			String date1 = sdf.format(currDate);

			Date secondDay = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					-1)));
			String date2 = sdf.format(secondDay);
			Date thirdDay = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					-2)));
			String date3 = sdf.format(thirdDay);
			Date fourthDay = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					-3)));
			String date4 = sdf.format(fourthDay);
			Date fifthDay = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					-4)));
			String date5 = sdf.format(fifthDay);
			Date sixthDay = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					-5)));
			String date6 = sdf.format(sixthDay);
			Date seventhDay = sdf.parse(sdf.format(DateUtils.addDays(
					new Date(), -6)));
			String date7 = sdf.format(seventhDay);
			Date eighthDay = sdf.parse(sdf.format(DateUtils.addDays(new Date(),
					-7)));
			String date8 = sdf.format(eighthDay);
			int Day1 = 0;
			int Day2 = 0;
			int Day3 = 0;
			int Day4 = 0;
			int Day5 = 0;
			int Day6 = 0;
			int Day7 = 0;

			List<Row> rows = cassandraTemplate.query(socialMediaTypeSelect)
					.all();

			for (Row r : rows) {
				if ((r.getDate("activityTimeStamp").compareTo(newcurrDate) < 0)
						&& (r.getDate("activityTimeStamp").compareTo(eighthDay) > 0)) {
					Activity activity = new Activity();
					activity.setUserBaseId(r.getString("userBaseId"));
					activity.setSocialMediaId(r.getString("socialMediaId"));
					activity.setActivityTimeStamp(r
							.getDate("activityTimeStamp"));
					activity.setActivityType(r.getString("activityType"));
					socialMediaTypeListdate.add(activity);

				}

			}
			String loginType = Integer
					.toString(GlobalConstants.ACTIVITY_TYPE_LOGIN);

			if (socialMediaTypeListdate != null) {
				for (int i = 0; i < socialMediaTypeListdate.size(); i++) {
					String type = socialMediaTypeListdate.get(i)
							.getActivityType();
					Date date = socialMediaTypeListdate.get(i)
							.getActivityTimeStamp();
					Date nowDate = sdf.parse(sdf.format(date));
					if (type.equals(loginType)) {
						if ((nowDate.compareTo(currDate) == 0)) {
							Day1++;
						} else if ((nowDate.compareTo(secondDay) == 0)) {
							Day2++;
						} else if ((nowDate.compareTo(thirdDay) == 0)) {
							Day3++;
						} else if ((nowDate.compareTo(fourthDay) == 0)) {
							Day4++;
						} else if ((nowDate.compareTo(fifthDay) == 0)) {
							Day5++;
						} else if ((nowDate.compareTo(sixthDay) == 0)) {
							Day6++;
						} else if ((nowDate.compareTo(seventhDay) == 0)) {
							Day7++;
						}
					}

				}
			}
			loginRatings.setDay1(Day1);
			loginRatings.setDay2(Day2);
			loginRatings.setDay3(Day3);
			loginRatings.setDay4(Day4);
			loginRatings.setDay5(Day5);
			loginRatings.setDay6(Day6);
			loginRatings.setDay7(Day7);
			loginRatings.setCurrDate(date1);
			loginRatings.setSecondDay(date2);
			loginRatings.setThirdDay(date3);
			loginRatings.setFourthDay(date4);
			loginRatings.setFifthDay(date5);
			loginRatings.setSixthDay(date6);
			loginRatings.setSeventhDay(date7);

			return loginRatings;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}

	}

	@Override
	public List<String> getNotifications(String userBaseId) {

		List<String> lstMessages = null;
		boolean isNewNotification = false;

		try {

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			final Date today = sdf.parse(sdf.format(new Date()));
			final Date nextDay = sdf.parse(sdf.format(DateUtils.addDays(
					new Date(), 1)));
			List<Object> activityList = new ArrayList<Object>();

			Select activitySelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_POINTS_COLLECTION);
			activitySelect.where(QueryBuilder.eq("userBaseId", userBaseId));

			List<Row> rows = cassandraTemplate.query(activitySelect).all();
			for (Row r : rows) {
				if ((r.getDate("allocationDate").compareTo(today) > 0)
						&& (r.getDate("allocationDate").compareTo(nextDay) < 0)
						&& (r.getInt("isNotified") == 0)) {
					UserPoints userpoints = new UserPoints();
					userpoints.setUserBaseId(r.getString("userBaseId"));
					userpoints.setTotalPoints(r.getInt("totalPoints"));
					userpoints
					.setUserActionPeriod(r.getInt("userActionPeriod"));
					userpoints.setUserAction(r.getInt("userAction"));
					userpoints.setAllocationDate(r.getDate("allocationDate"));
					userpoints.setIsNotified(r.getInt("isNotified"));
					userpoints.setId(r.getString("id"));
					activityList.add((Object) userpoints);
				}
			}

			if (activityList != null && activityList.size() > 0) {

				lstMessages = new ArrayList<String>();

				for (Object usrpnt : activityList) {
					isNewNotification = true;
					UserPoints userPoint = (UserPoints) usrpnt;

					String msg = "";
					switch (userPoint.getUserAction()) {

					case GlobalConstants.ACTIVITY_TYPE_LOGIN:
						msg = "You earned " + userPoint.getTotalPoints()
						+ " points by Login";
						break;
					case GlobalConstants.ACTIVITY_TYPE_ACTIVATE:
						msg = "You earned " + userPoint.getTotalPoints()
						+ " points by Activating Social Account";
						break;
					case GlobalConstants.ACTIVITY_TYPE_COMMENT:
						msg = "You earned " + userPoint.getTotalPoints()
						+ " points by Commenting";
						break;
					case GlobalConstants.ACTIVITY_TYPE_REACTION:
						msg = "You earned " + userPoint.getTotalPoints()
						+ " points by Reacting";
						break;
					case GlobalConstants.ACTIVITY_TYPE_REGISTER:
						msg = "You earned " + userPoint.getTotalPoints()
						+ " points by Registering";
						break;
					case GlobalConstants.ACTIVITY_TYPE_REVIEW:
						msg = "You earned " + userPoint.getTotalPoints()
						+ " points by writing a Review";
						break;
					case GlobalConstants.ACTIVITY_TYPE_SHARE:
						msg = "You earned " + userPoint.getTotalPoints()
						+ " points by Sharing";
						break;
					}
					lstMessages.add(msg);

				}

				if (isNewNotification) {
					// setNotified to 1
					for (Object usrpnt : activityList) {
						UserPoints userPoint = (UserPoints) usrpnt;
						Update notifyUpdate = QueryBuilder.update(
								USER_KEYSPACE, USER_POINTS_COLLECTION);
						notifyUpdate.where(
								QueryBuilder.eq("userBaseId", userBaseId)).and(
										QueryBuilder.eq("id", userPoint.getId()));
						notifyUpdate.with(QueryBuilder.set("isNotified", 1));
						cassandraTemplate.execute(notifyUpdate);
						logger.info("Notification Success");
					}

				}
			}

		} catch (Exception ex) {
			logger.error(ex);
		}

		return lstMessages;
	}

	@Override
	public UserCount getAgePerWeek(ReportsData dateRange) {
		try {
			Select socialMediaTypeSelect = QueryBuilder.select().from(
					USER_KEYSPACE, SOCIAL_MEDIA_DATA_COLLECTION);
			List<SocialMediaType> socialMediaTypeList = new ArrayList<SocialMediaType>();
			List<Row> rows = cassandraTemplate.query(socialMediaTypeSelect)
					.all();
			for (Row r : rows) {
				SocialMediaType socialMediaType = new SocialMediaType();
				socialMediaType.setUserBaseId(r.getString("userBaseId"));
				socialMediaType.setSocialMediatypeId(r
						.getString("socialMediatypeId"));
				socialMediaType.setSocialMediaId(r.getString("socialMediaId"));
				socialMediaType.setAccessToken(r.getString("accessToken"));
				socialMediaType.setProfilePicUrl(r.getString("profilePicUrl"));
				socialMediaTypeList.add(socialMediaType);
			}
			Select userBaseSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_BASE_COLLECTION);
			List<UserBase> userBaseAgeList = new ArrayList<UserBase>();
			rows = cassandraTemplate.query(userBaseSelect).all();
			for (Row r : rows) {
				UserBase userBase = new UserBase();
				userBase.setUserBaseId(r.getString("userBaseId"));
				userBase.setName(r.getString("name"));
				userBase.setEmailIdList(r.getList("emailIdList", String.class));
				userBase.setPassword(r.getString("password"));
				userBase.setAge(r.getString("age"));
				userBase.setGender(r.getString("gender"));
				userBase.setDob(r.getString("dob"));
				userBase.setUserRegisteredDate(r.getDate("userRegisteredDate"));
				userBase.setNameOfFile(r.getString("nameOfFile"));
				userBase.setYearOfBirth(r.getString("yearOfBirth"));
				userBaseAgeList.add(userBase);
			}
			String userId;

			String startDate = dateRange.getStartDate();
			Date eighthDay;
			eighthDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			.parse(startDate);
			String endDate = dateRange.getEndDate();
			Date currDate;
			currDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			.parse(endDate);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date newcurrDate = sdf.parse(sdf.format(DateUtils.addDays(currDate,
					1)));
			Date lastDay = sdf.parse(sdf.format(DateUtils
					.addDays(eighthDay, -1)));

			int ageLessthanThirteen = 0;
			int ageBetThirteenAndSeventeen = 0;
			int ageBetEighteenAndTwentyfour = 0;
			int ageBetTwentyfiveAndThirtyfour = 0;
			int ageBetThirtyfiveAndFortynine = 0;
			int ageAboveFifty = 0;
			int maleCount = 0;
			int femaleCount = 0;
			int facebookUserCount = 0;
			int googleUserCount = 0;
			Select socialMediaTypeSelectDate = QueryBuilder.select().from(
					USER_KEYSPACE, ACTIVITY_LOG);
			List<Activity> socialMediaTypeListdate = new ArrayList<Activity>();
			String actType;

			int birthyear = 0;
			String birthYear;
			rows = cassandraTemplate.query(socialMediaTypeSelectDate).all();
			for (Row r : rows) {
				Activity activity = new Activity();
				activity.setUserBaseId(r.getString("userBaseId"));
				activity.setSocialMediaId(r.getString("socialMediaId"));
				activity.setActivityType(r.getString("activityType"));
				activity.setActivityTimeStamp(r.getDate("activityTimeStamp"));
				socialMediaTypeListdate.add(activity);
			}

			if (!socialMediaTypeListdate.isEmpty()) {
				for (int i = 0; i < socialMediaTypeListdate.size(); i++) {

					userId = socialMediaTypeListdate.get(i).getUserBaseId();
					actType = socialMediaTypeListdate.get(i).getActivityType();

					if (actType.equals(Integer
							.toString(GlobalConstants.ACTIVITY_TYPE_LOGIN))
							|| (actType
									.equals(Integer
											.toString(GlobalConstants.ACTIVITY_TYPE_REGISTER)))) {
						for (int j = 0; j < userBaseAgeList.size(); j++) {
							if (userBaseAgeList.get(j).getUserBaseId()
									.equals(userId)) {
								birthYear = userBaseAgeList.get(j)
										.getYearOfBirth();
								if (userBaseAgeList.get(j).getGender() != null) {
									if (userBaseAgeList.get(j).getGender()
											.equals("1")
											|| (userBaseAgeList.get(j)
													.getGender().equals("male"))) {
										logger.info("maleCount" + maleCount++);
									} else if (userBaseAgeList.get(j)
											.getGender().equals("2")
											|| (userBaseAgeList.get(j)
													.getGender()
													.equals("female"))) {
										logger.info("femaleCount"
												+ femaleCount++);
									}
								}

								if (birthYear != null) {
									birthyear = Integer.parseInt(birthYear);
									if (birthyear >= 2001)
										ageLessthanThirteen++;
									else if ((birthyear >= 1997)
											&& (birthyear <= 2001)) {
										ageBetThirteenAndSeventeen++;

									} else if ((birthyear >= 1990)
											&& (birthyear <= 1996)) {
										ageBetEighteenAndTwentyfour++;

									} else if ((birthyear >= 1980)
											&& (birthyear <= 1989)) {
										ageBetTwentyfiveAndThirtyfour++;

									} else if ((birthyear >= 1965)
											&& (birthyear <= 1979)) {
										ageBetThirtyfiveAndFortynine++;

									} else if (birthyear <= 1964) {
										ageAboveFifty++;
									}
								}
							}

						}
					}

				}
			}
			UserCount userCount = new UserCount();
			userCount.setAgeLessthanThirteen(ageLessthanThirteen);
			userCount.setAgeBetThirteenAndSeventeen(ageBetThirteenAndSeventeen);
			userCount
			.setAgeBetEighteenAndTwentyfour(ageBetEighteenAndTwentyfour);
			userCount
			.setAgeBetTwentyfiveAndThirtyfour(ageBetTwentyfiveAndThirtyfour);
			userCount
			.setAgeBetThirtyfiveAndFortynine(ageBetThirtyfiveAndFortynine);
			userCount.setAgeAboveFifty(ageAboveFifty);
			userCount.setMaleCount(maleCount);
			userCount.setFemaleCount(femaleCount);

			return userCount;

		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}

	}

	@Override
	public List<UserRatingCount> getreportsShareRange(ReportsData dateRange) {

		List<UserRatingCount> ratingCount = new ArrayList<UserRatingCount>();
		try {

			String startDate = dateRange.getStartDate();
			String endDate = dateRange.getEndDate();
			Date startDay;
			startDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			.parse(startDate);

			Date endDay;
			endDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			.parse(endDate);

			SimpleDateFormat sdf = new SimpleDateFormat(
					"dd.MM.yyyy HH:mm:ss.SSSZ");
			Date current = sdf.parse(sdf.format(new Date()));

			Date toDate = sdf.parse(sdf.format(DateUtils.addDays(endDay, 1)));
			Date fromDate = sdf.parse(sdf.format(DateUtils
					.addDays(startDay, -1)));

			List<UserRatingCount> lstRatings = new ArrayList<UserRatingCount>();
			UserRatingCount userRatingCount = null;

			int count;
			List<Integer> userRatingList = new ArrayList<Integer>();
			Map<Integer, Integer> userRatingMap = new HashMap<Integer, Integer>();
			Select activitySelect = QueryBuilder.select().from(USER_KEYSPACE,
					ACTIVITY_LOG);
			List<Row> rows = cassandraTemplate.query(activitySelect).all();
			for (Row r : rows) {
				count = 0;
				if (r.getString("activityType").equals(
						Integer.toString(GlobalConstants.ACTIVITY_TYPE_REVIEW))
						&& (r.getDate("activityTimeStamp").compareTo(fromDate) > 0)
						&& (r.getDate("activityTimeStamp").compareTo(toDate) < 0)) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(r.getDate("activityTimeStamp"));
					int day = cal.get(Calendar.DAY_OF_MONTH);
					if (!userRatingList.contains(day)) {
						userRatingList.add(day);
						if (userRatingMap.get(day) != null)
							count = userRatingMap.get(day);
						userRatingMap.put(day, count + 1);
					}
				}
			}

			Iterator it = userRatingMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				int dayOfMonth = (Integer) pair.getKey();
				count = (Integer) pair.getValue();

				UserRatingCount ucnt = new UserRatingCount();
				ucnt.setCount(count);
				ucnt.setDayOfMonth(dayOfMonth);
				ratingCount.add(ucnt);
			}

			return ratingCount;
		}

		catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public UserRatingCount dailyActvity() {

		try {
			Select socialMediaTypeSelectDate = QueryBuilder.select().from(
					USER_KEYSPACE, ACTIVITY_LOG);
			List<Activity> socialMediaTypeListdate = new ArrayList<Activity>();

			int dailyRegCount = 0;
			int dailyLoginCount = 0;
			int dailyRateCount = 0;
			int dailyReactionCount = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String actType;
			Date currDate = sdf.parse(sdf.format(new Date()));
			Date newcurrDate = sdf.parse(sdf.format(DateUtils.addDays(currDate,
					1)));
			Date lastDay = sdf
					.parse(sdf.format(DateUtils.addDays(currDate, -1)));

			List<Row> rows = cassandraTemplate.query(socialMediaTypeSelectDate)
					.all();
			for (Row r : rows) {
				Activity activity = new Activity();
				activity.setUserBaseId(r.getString("userBaseId"));
				activity.setSocialMediaId(r.getString("socialMediaId"));
				activity.setActivityType(r.getString("activityType"));
				activity.setActivityTimeStamp(r.getDate("activityTimeStamp"));
				socialMediaTypeListdate.add(activity);
			}

			if (socialMediaTypeListdate != null) {
				for (int i = 0; i < socialMediaTypeListdate.size(); i++) {
					actType = socialMediaTypeListdate.get(i).getActivityType();

					Date date = socialMediaTypeListdate.get(i)
							.getActivityTimeStamp();
					Date nowDate = sdf.parse(sdf.format(date));
					if ((nowDate.compareTo(currDate) == 0)) {
						if (actType
								.equals(Integer
										.toString(GlobalConstants.ACTIVITY_TYPE_REGISTER))) {
							dailyRegCount++;
						}

						else if (actType.equals(Integer
								.toString(GlobalConstants.ACTIVITY_TYPE_LOGIN))) {
							dailyLoginCount++;

						} else if (actType
								.equals(Integer
										.toString(GlobalConstants.ACTIVITY_TYPE_REVIEW))) {
							dailyRateCount++;

						} else if (actType
								.equals(Integer
										.toString(GlobalConstants.ACTIVITY_TYPE_REACTION))) {
							dailyReactionCount++;

						}
					}

				}
			}
			UserRatingCount userCount = new UserRatingCount();
			userCount.setDailyLoginCount(dailyLoginCount);
			userCount.setDailyRegCount(dailyRegCount);
			userCount.setDailyRateCount(dailyRateCount);
			userCount.setDailyReactionCount(dailyReactionCount);
			return userCount;

		}

		catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public List<UserRatingCount> getreportsReactionRange(ReportsData dateRange) {

		List<UserRatingCount> ratingCount = new ArrayList<UserRatingCount>();
		try {

			String startDate = dateRange.getStartDate();
			String endDate = dateRange.getEndDate();
			Date startDay;
			startDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			.parse(startDate);

			Date endDay;
			endDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			.parse(endDate);

			SimpleDateFormat sdf = new SimpleDateFormat(
					"dd.MM.yyyy HH:mm:ss.SSSZ");
			Date current = sdf.parse(sdf.format(new Date()));

			Date toDate = sdf.parse(sdf.format(DateUtils.addDays(endDay, 1)));
			Date fromDate = sdf.parse(sdf.format(DateUtils
					.addDays(startDay, -1)));
			int count;
			List<UserRatingCount> lstRatings = new ArrayList<UserRatingCount>();
			UserRatingCount userRatingCount = null;

			List<Integer> userRatingList = new ArrayList<Integer>();
			Map<Integer, Integer> userRatingMap = new HashMap<Integer, Integer>();
			Select activitySelect = QueryBuilder.select().from(USER_KEYSPACE,
					ACTIVITY_LOG);
			List<Row> rows = cassandraTemplate.query(activitySelect).all();
			for (Row r : rows) {
				count = 0;
				if (r.getString("activityType").equals(
						GlobalConstants.ACTIVITY_TYPE_REACTION + "")
						&& (r.getDate("activityTimeStamp").compareTo(fromDate) > 0)
						&& (r.getDate("activityTimeStamp").compareTo(toDate) < 0)) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(r.getDate("activityTimeStamp"));
					int day = cal.get(Calendar.DAY_OF_MONTH);
					if (!userRatingList.contains(day)) {
						userRatingList.add(day);
						if (userRatingMap.get(day) != null)
							count = userRatingMap.get(day);
						userRatingMap.put(day, count + 1);
					}
				}
			}

			Iterator it = userRatingMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				int dayOfMonth = (Integer) pair.getKey();
				count = (Integer) pair.getValue();

				UserRatingCount ucnt = new UserRatingCount();
				ucnt.setCount(count);
				ucnt.setDayOfMonth(dayOfMonth);
				ratingCount.add(ucnt);
			}

			return ratingCount;
		}

		catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public int updateUserComment(CommentRequest comreq) {
		logger.info("Eneterd updateUserComment ");
		// find the url id from urlRatingRef
		try {

			logger.info("comreq.getUrl() = " + comreq.getCommenturl());

			Select urlCommentSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlCommentSelect.where(QueryBuilder.eq("url",
					comreq.getCommenturl()));

			UrlRatingRef urlRating = null;
			Row row = cassandraTemplate.query(urlCommentSelect).one();
			// if no data, update
			if (null == row) {
				urlRating = new UrlRatingRef();
				urlRating.setUrl(comreq.getCommenturl());
				urlRating.setUrlId(getNextSequence("urlId"));
				logger.info("Inserting new URL data in URL_RATING_DATA_COLLECTION");

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						URL_RATING_DATA_COLLECTION);
				insert.value("url", urlRating.getUrl());
				insert.value("totNumRating", urlRating.getTotNumRating());
				insert.value("averageRating", urlRating.getAverageRating());
				insert.value("urlId", urlRating.getUrlId());
				insert.value("lastCommentId", urlRating.getLastCommentId());
				cassandraTemplate.execute(insert);
			} else {
				urlRating = new UrlRatingRef();
				urlRating.setUrl(row.getString("url"));
				urlRating.setTotNumRating(row.getInt("totNumRating"));
				urlRating.setAverageRating(row.getFloat("averageRating"));
				urlRating.setUrlId(row.getInt("urlId"));
				urlRating.setLastCommentId(row.getString("lastCommentId"));

			}

			// get the url id from new record
			int urlId = urlRating.getUrlId();

			// insert review comments and rating
			UserReview usrReview = new UserReview();
			Random rand = new Random();
			int randomNum = rand.nextInt((1000 - 1) + 1) + 1;
			usrReview.setCommentId(Integer.toString(randomNum));
			usrReview.setDateTime(new Date());
			usrReview.setParentCommentId("");
			// TODO: get the social media attribute id Based on the value
			usrReview.setSocialMediaAttrId(0);
			usrReview.setTypeId(2);
			usrReview.setUrlId(urlId);
			usrReview.setUserAvatar(comreq.getComuserAvatar());
			usrReview.setUserComment(comreq.getCommentstring());
			usrReview.setUserName(comreq.getComuserName());
			usrReview.setUserBaseId(comreq.getUserBaseId());
			usrReview.setNumUpvotes(0);
			usrReview.setUpvotesBy(null);
			usrReview.setNumDownvotes(0);
			usrReview.setDownvotesBy(null);

			// Insert data to userReviewComments

			Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);

			insert.value("commentId", usrReview.getCommentId());
			insert.value("urlId", usrReview.getUrlId());
			insert.value("url", usrReview.getUrl());
			insert.value("typeId", usrReview.getTypeId());
			insert.value("parentCommentId", usrReview.getParentCommentId());
			insert.value("userName", usrReview.getUserName());
			insert.value("userAvatar", usrReview.getUserAvatar());
			insert.value("socialMediaAttrId", usrReview.getSocialMediaAttrId());
			insert.value("dateTime", usrReview.getDateTime());
			insert.value("commentTitle", usrReview.getCommentTitle());
			insert.value("userComment", usrReview.getUserComment());
			insert.value("userRating", usrReview.getUserRating());
			insert.value("userBaseId", usrReview.getUserBaseId());
			insert.value("numofreplies", usrReview.getNumofreplies());
			insert.value("numUpvotes", usrReview.getNumUpvotes());
			insert.value("numDownvotes", usrReview.getNumDownvotes());
			insert.value("upvotesBy", usrReview.getUpvotesBy());
			insert.value("downvotesBy", usrReview.getDownvotesBy());
			cassandraTemplate.execute(insert);
			logger.info("Inserted new record in USER_REVIEW_COMMENTS_COLLECTION");
			return GlobalConstants.OPRERATION_SUCCESS;

		} catch (Exception ex) {
			logger.error(ex);
			logger.info("Caught in submitcomment exception");
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	// Post Service implementation for fetching only comments .Add criteria
	// type=2
	@Override
	public List<UserReview> fetchUserComments(String url) {
		try {
			List<UserReview> userReviewList = new ArrayList<UserReview>();
			// get the url id for urlRatingRef.
			Select urlSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlSelect.where(QueryBuilder.eq("url", url));
			UrlRatingRef urlRating = null;
			Row row = cassandraTemplate.query(urlSelect).one();
			if (null == row) {
				return null;
			} else {
				urlRating = new UrlRatingRef();
				urlRating.setAverageRating(row.getFloat("averageRating"));
				urlRating.setTotNumRating(row.getInt("totNumRating"));
				urlRating.setUrlId(row.getInt("urlId"));
				urlRating.setUrl(row.getString("url"));
				urlRating.setLastCommentId(row.getString("lastCommentId"));
			}

			// Fetch the comments from userReviewComments
			Select userReviewSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(userReviewSelect).all();
			for (Row r : rows) {
				if (r.getInt("urlId") == urlRating.getUrlId()
						&& r.getInt("typeId") == GlobalConstants.TYPE_COMMENTS) {
					UserReview userReview = new UserReview();
					userReview.setCommentId(r.getString("commentId"));
					userReview.setUrlId(r.getInt("urlId"));
					userReview.setUrl(r.getString("url"));
					userReview.setTypeId(r.getInt("typeId"));
					userReview.setParentCommentId(r
							.getString("parentCommentId"));
					userReview.setUserName(r.getString("userName"));
					userReview.setUserAvatar(r.getString("userAvatar"));
					userReview.setSocialMediaAttrId(r
							.getInt("socialMediaAttrId"));
					userReview.setDateTime(r.getDate("dateTime"));
					userReview.setCommentTitle(r.getString("commentTitle"));
					userReview.setUserComment(r.getString("userComment"));
					userReview.setUserRating(r.getFloat("userRating"));
					userReview.setUserBaseId(r.getString("userBaseId"));
					userReview.setNumofreplies(r.getInt("numofreplies"));
					userReview.setNumUpvotes(r.getInt("numUpvotes"));
					userReview.setNumDownvotes(r.getInt("numDownvotes"));
					userReview.setUpvotesBy(r
							.getList("upvotesBy", String.class));
					userReview.setDownvotesBy(r.getList("downvotesBy",
							String.class));
					userReviewList.add(userReview);
				}

			}

			return userReviewList;
		} catch (Exception ex) {
			if (null != ex)
				logger.error(ex);
			return null;
		}
	}

	@Override
	public int updateUserReply(CommentRequest comreq) {
		logger.info("Entered updateUserReply ");
		// find the url id from urlRatingRef
		try {

			Select urlCommentSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlCommentSelect.where(QueryBuilder.eq("url",
					comreq.getCommenturl()));
			UrlRatingRef urlRating = null;
			Row row = cassandraTemplate.query(urlCommentSelect).one();
			// if no data, update
			if (null == row) {
				logger.info("No URL data in URL_RATING_DATA_COLLECTION,Parent Comment did not insert CHECK Y??");
				return GlobalConstants.OPRERATION_FAILURE;
			} else {
				urlRating = new UrlRatingRef();
				urlRating.setAverageRating(row.getFloat("averageRating"));
				urlRating.setTotNumRating(row.getInt("totNumRating"));
				urlRating.setUrlId(row.getInt("urlId"));
				urlRating.setUrl(row.getString("url"));
				urlRating.setLastCommentId(row.getString("lastCommentId"));
			}

			// get the url id from new record
			int urlId = urlRating.getUrlId();

			// insert reply for the comments
			UserReview userReply = new UserReview();
			Random rand = new Random();
			int randomNum = rand.nextInt((1000 - 1) + 1) + 1;
			userReply.setCommentId(Integer.toString(randomNum));
			userReply.setUrl(comreq.getCommenturl());
			userReply.setDateTime(new Date());
			userReply.setParentCommentId(comreq.getParCommentId());
			userReply.setSocialMediaAttrId(0);
			userReply.setTypeId(3);
			userReply.setUrlId(urlId);
			userReply.setUserAvatar(comreq.getComuserAvatar());
			userReply.setUserComment(comreq.getCommentstring());
			userReply.setUserName(comreq.getComuserName());
			userReply.setUserBaseId(comreq.getUserBaseId());
			userReply.setNumofreplies(0);
			userReply.setNumUpvotes(0);
			userReply.setNumDownvotes(0);

			// Insert data to userReviewComments

			Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);

			insert.value("commentId", userReply.getCommentId());
			insert.value("urlId", userReply.getUrlId());
			insert.value("url", userReply.getUrl());
			insert.value("typeId", userReply.getTypeId());
			insert.value("parentCommentId", userReply.getParentCommentId());
			insert.value("userName", userReply.getUserName());
			insert.value("userAvatar", userReply.getUserAvatar());
			insert.value("socialMediaAttrId", userReply.getSocialMediaAttrId());
			insert.value("dateTime", userReply.getDateTime());
			insert.value("commentTitle", userReply.getCommentTitle());
			insert.value("userComment", userReply.getUserComment());
			insert.value("userRating", userReply.getUserRating());
			insert.value("userBaseId", userReply.getUserBaseId());
			insert.value("numofreplies", userReply.getNumofreplies());
			insert.value("numUpvotes", userReply.getNumUpvotes());
			insert.value("numDownvotes", userReply.getNumDownvotes());
			insert.value("upvotesBy", userReply.getUpvotesBy());
			insert.value("downvotesBy", userReply.getDownvotesBy());
			cassandraTemplate.execute(insert);

			logger.info("Inserted reply comment in USER_REVIEW_COMMENTS_COLLECTION");
			// Fetch parent Comment document to update
			Select parentCommentSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			parentCommentSelect.where(QueryBuilder.eq("commentId",
					userReply.getParentCommentId()));

			UserReview parentUserComment = new UserReview();
			row = cassandraTemplate.query(parentCommentSelect).one();
			if (row != null) {
				parentUserComment.setCommentId(row.getString("commentId"));
				parentUserComment.setUrlId(row.getInt("urlId"));
				parentUserComment.setUrl(row.getString("url"));
				parentUserComment.setTypeId(row.getInt("typeId"));
				parentUserComment.setParentCommentId(row
						.getString("parentCommentId"));
				parentUserComment.setUserName(row.getString("userName"));
				parentUserComment.setUserAvatar(row.getString("userAvatar"));
				parentUserComment.setSocialMediaAttrId(row
						.getInt("socialMediaAttrId"));
				parentUserComment.setDateTime(row.getDate("dateTime"));
				parentUserComment
				.setCommentTitle(row.getString("commentTitle"));
				parentUserComment.setUserComment(row.getString("userComment"));
				parentUserComment.setUserRating(row.getFloat("userRating"));
				parentUserComment.setUserBaseId(row.getString("userBaseId"));
				parentUserComment.setNumofreplies(row.getInt("numofreplies"));
				parentUserComment.setNumUpvotes(row.getInt("numUpvotes"));
				parentUserComment.setNumDownvotes(row.getInt("numDownvotes"));
				parentUserComment.setUpvotesBy(row.getList("upvotesBy",
						String.class));
				parentUserComment.setDownvotesBy(row.getList("downvotesBy",
						String.class));
			}

			int lnumreplies = parentUserComment.getNumofreplies();
			logger.info("No of existing replies to this comment is"
					+ parentUserComment.getNumofreplies());
			lnumreplies++;
			// Increment number of replies and update in the parent comment
			// document
			parentUserComment.setNumofreplies(lnumreplies);

			/*insert = QueryBuilder.insertInto(USER_KEYSPACE,
					USER_REVIEW_COMMENTS_COLLECTION);

			insert.value("commentId", parentUserComment.getCommentId());
			insert.value("urlId", parentUserComment.getUrlId());
			insert.value("url", parentUserComment.getUrl());
			insert.value("typeId", parentUserComment.getTypeId());
			insert.value("parentCommentId",
					parentUserComment.getParentCommentId());
			insert.value("userName", parentUserComment.getUserName());
			insert.value("userAvatar", parentUserComment.getUserAvatar());
			insert.value("socialMediaAttrId",
					parentUserComment.getSocialMediaAttrId());
			insert.value("dateTime", parentUserComment.getDateTime());
			insert.value("commentTitle", parentUserComment.getCommentTitle());
			insert.value("userComment", parentUserComment.getUserComment());
			insert.value("userRating", parentUserComment.getUserRating());
			insert.value("userBaseId", parentUserComment.getUserBaseId());
			insert.value("numofreplies", parentUserComment.getNumofreplies());
			insert.value("numUpvotes", parentUserComment.getNumUpvotes());
			insert.value("numDownvotes", parentUserComment.getNumDownvotes());
			insert.value("upvotesBy", parentUserComment.getUpvotesBy());
			insert.value("downvotesBy", parentUserComment.getDownvotesBy());
			cassandraTemplate.execute(insert);*/
			Update update = QueryBuilder.update(USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			update.where(QueryBuilder.eq("commentId", userReply.getParentCommentId()));
			update.with(QueryBuilder.set("numofreplies", lnumreplies));
			cassandraTemplate.execute(update);
			return GlobalConstants.OPRERATION_SUCCESS;

		} catch (Exception ex) {
			logger.error(ex);
			logger.info("Caught in submitcomment exception");
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	// Post service implementaton for fetching replies for a comment
	@Override
	public List<UserReview> fetchUserReplies(CommentRequest parentCommentDetails) {
		try {
			List<UserReview> userRepliesList = new ArrayList<UserReview>();
			Select urlSelect = QueryBuilder.select().from(USER_KEYSPACE,
					URL_RATING_DATA_COLLECTION);
			urlSelect.where(QueryBuilder.eq("url",
					parentCommentDetails.getCommenturl()));
			// First get the URL di pertaining to given URL from rating ref
			// table
			UrlRatingRef urlRating = null;
			Row row = cassandraTemplate.query(urlSelect).one();
			if (null == row) {
				return null;
			} else {
				urlRating = new UrlRatingRef();
				urlRating.setAverageRating(row.getFloat("averageRating"));
				urlRating.setTotNumRating(row.getInt("totNumRating"));
				urlRating.setUrlId(row.getInt("urlId"));
				urlRating.setUrl(row.getString("url"));
				urlRating.setLastCommentId(row.getString("lastCommentId"));
			}
			// Fetch the replies from user review comments by parent comment id

			Select userRepliesSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(userRepliesSelect).all();
			for (Row r : rows) {
				if (r.getInt("urlId") == urlRating.getUrlId()
						&& r.getString("parentCommentId").equals(
								parentCommentDetails.getParCommentId())) {
					UserReview userReview = new UserReview();
					userReview.setCommentId(r.getString("commentId"));
					userReview.setUrlId(r.getInt("urlId"));
					userReview.setUrl(r.getString("url"));
					userReview.setTypeId(r.getInt("typeId"));
					userReview.setParentCommentId(r
							.getString("parentCommentId"));
					userReview.setUserName(r.getString("userName"));
					userReview.setUserAvatar(r.getString("userAvatar"));
					userReview.setSocialMediaAttrId(r
							.getInt("socialMediaAttrId"));
					userReview.setDateTime(r.getDate("dateTime"));
					userReview.setCommentTitle(r.getString("commentTitle"));
					userReview.setUserComment(r.getString("userComment"));
					userReview.setUserRating(r.getFloat("userRating"));
					userReview.setUserBaseId(r.getString("userBaseId"));
					userReview.setNumofreplies(r.getInt("numofreplies"));
					userReview.setNumUpvotes(r.getInt("numUpvotes"));
					userReview.setNumDownvotes(r.getInt("numDownvotes"));
					userReview.setUpvotesBy(r
							.getList("upvotesBy", String.class));
					userReview.setDownvotesBy(r.getList("downvotesBy",
							String.class));
					userRepliesList.add(userReview);
				}
			}
			for (int i = 0; i < userRepliesList.size(); ++i) {
				logger.info("fetched replies"
						+ userRepliesList.get(i).getUserComment());
			}
			return userRepliesList;
		} catch (Exception ex) {
			if (null != ex)
				logger.error(ex);
			return null;
		}

	}

	@Override
	public int deleteCommentwithId(String commentId) {
		try {
			UserReview lcommentRow; // For parent comment object whihc is
			// deleted
			List<UserReview> deletedrepliesList = new ArrayList<UserReview>();

			Select userCommentSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			userCommentSelect.where(QueryBuilder.eq("commentId", commentId));
			Row row = cassandraTemplate.query(userCommentSelect).one();
			String commentIdPrimary = null;
			if (row != null) {
				lcommentRow = new UserReview();
				lcommentRow.setCommentId(row.getString("commentId"));
				lcommentRow.setUrlId(row.getInt("urlId"));
				lcommentRow.setUrl(row.getString("url"));
				lcommentRow.setTypeId(row.getInt("typeId"));
				lcommentRow
				.setParentCommentId(row.getString("parentCommentId"));
				lcommentRow.setUserName(row.getString("userName"));
				lcommentRow.setUserAvatar(row.getString("userAvatar"));
				lcommentRow.setSocialMediaAttrId(row
						.getInt("socialMediaAttrId"));
				lcommentRow.setDateTime(row.getDate("dateTime"));
				lcommentRow.setCommentTitle(row.getString("commentTitle"));
				lcommentRow.setUserComment(row.getString("userComment"));
				lcommentRow.setUserRating(row.getFloat("userRating"));
				lcommentRow.setUserBaseId(row.getString("userBaseId"));
				lcommentRow.setNumofreplies(row.getInt("numofreplies"));
				lcommentRow.setNumUpvotes(row.getInt("numUpvotes"));
				lcommentRow.setNumDownvotes(row.getInt("numDownvotes"));
				lcommentRow
				.setUpvotesBy(row.getList("upvotesBy", String.class));
				lcommentRow.setDownvotesBy(row.getList("downvotesBy",
						String.class));

				// To decrement numofReplies in parentcomment
				if (lcommentRow.getParentCommentId() != null
						&& !(lcommentRow.getParentCommentId().equals(""))) {

					Select parentCommentSelect = QueryBuilder.select().from(
							USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
					parentCommentSelect.where(QueryBuilder.eq("commentId",
							lcommentRow.getParentCommentId()));
					row = cassandraTemplate.query(parentCommentSelect).one();

					Update parComUpdate = QueryBuilder.update(USER_KEYSPACE,
							USER_REVIEW_COMMENTS_COLLECTION);
					parComUpdate.where(QueryBuilder.eq("commentId",
							lcommentRow.getParentCommentId()));
					parComUpdate.with(QueryBuilder.set("numofreplies",
							row.getInt("numofreplies") - 1));
					cassandraTemplate.execute(parComUpdate);
					// mongoTemplate.updateFirst(parentCommentQuery,
					// parComUpdate, USER_REVIEW_COMMENTS_COLLECTION);
				}
				// parentComment row found,so delete it and find its childs too
				// first child and then parent

				Select userRepliesSelect = QueryBuilder.select().from(
						USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
				List<Row> rows = cassandraTemplate.query(userRepliesSelect)
						.all();
				for (Row r : rows) {
					if (r.getString("parentCommentId").equals(commentId))
						commentIdPrimary = r.getString("commentId");
				}

				Delete deleteComment = QueryBuilder.delete().from(
						USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
				if(commentIdPrimary != null) {
					deleteComment.where(QueryBuilder.eq("commentId",
							commentIdPrimary));
					cassandraTemplate.execute(deleteComment);
				}
				deleteComment = QueryBuilder.delete().from(
						USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
				deleteComment.where(QueryBuilder.eq("commentId", commentId));
				cassandraTemplate.execute(deleteComment);
				return GlobalConstants.OPRERATION_SUCCESS;
			} else {
				logger.info("Parent Comment Not found,so no deletes");
				return GlobalConstants.OPRERATION_FAILURE;
			}

		} catch (Exception ex) {
			if (null != ex)
				logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public int upvoteComment(UpvoteReq upvoteReq) {
		try {

			List<String> upvotesBy = new ArrayList<String>();

			Select userCommentUpvoteSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			userCommentUpvoteSelect.where(QueryBuilder.eq("commentId",
					upvoteReq.getCommentId()));
			Row row = cassandraTemplate.query(userCommentUpvoteSelect).one();

			if (row != null) {
				Update update = QueryBuilder.update(USER_KEYSPACE,
						USER_REVIEW_COMMENTS_COLLECTION);
				update.where(QueryBuilder.eq("commentId",
						upvoteReq.getCommentId()));
				update.with(QueryBuilder.set("numUpvotes",
						row.getInt("numUpvotes") + 1));
				if (!row.getList("upvotesBy", String.class).isEmpty())
					upvotesBy = row.getList("upvotesBy", String.class);
				upvotesBy.add(upvoteReq.getUpvoteBy());
				update.with(QueryBuilder.set("upvotesBy", upvotesBy));
				cassandraTemplate.execute(update);
				return GlobalConstants.OPRERATION_SUCCESS;
			} else {
				logger.info("Parent Comment Not found,so no deletes");
				return GlobalConstants.OPRERATION_FAILURE;
			}

		} catch (Exception ex) {
			if (null != ex)
				logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public int downvoteComment(DownvoteReq downvoteReq) {
		try {
			List<String> downvotesBy = new ArrayList<String>();

			Select userCommentDownvoteSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			userCommentDownvoteSelect.where(QueryBuilder.eq("commentId",
					downvoteReq.getCommentId()));
			Row row = cassandraTemplate.query(userCommentDownvoteSelect).one();

			if (row != null) {
				Update update = QueryBuilder.update(USER_KEYSPACE,
						USER_REVIEW_COMMENTS_COLLECTION);
				update.where(QueryBuilder.eq("commentId",
						downvoteReq.getCommentId()));
				update.with(QueryBuilder.set("numDownvotes",
						row.getInt("numDownvotes") + 1));
				if (row.getList("downvotesBy", String.class) != null)
					downvotesBy = row.getList("downvotesBy", String.class);

				downvotesBy.add(downvoteReq.getDownvoteBy());
				update.with(QueryBuilder.set("downvotesBy", downvotesBy));
				cassandraTemplate.execute(update);
				return GlobalConstants.OPRERATION_SUCCESS;
			} else {
				logger.info("Parent Comment Not found,so no deletes");
				return GlobalConstants.OPRERATION_FAILURE;
			}

		} catch (Exception ex) {
			if (null != ex)
				logger.error(ex);
			return GlobalConstants.OPRERATION_FAILURE;
		}
	}

	@Override
	public String addToPortal(String widgetId) {
		String success = "Y";
		try {

			Update update = QueryBuilder.update(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			update.where(QueryBuilder.eq("widgetId", widgetId));
			update.with(QueryBuilder.set("addedToPortal", true));
			cassandraTemplate.execute(update);
			return success;

		} catch (Exception e) {
			success = "N";
			return success;
		}

	}

	@Override
	public Integer addNewWidget(String json) {

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(json);
			Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			insert.value("widgetId", jsonObj.get("widgetId"));
			insert.value("widgetHeader", jsonObj.get("widgetHeader"));
			insert.value("pages", jsonObj.get("pages"));
			insert.value("addedToPage", jsonObj.get("addedToPage"));
			insert.value("previewDivContent", jsonObj.get("previewDivContent"));
			insert.value("addedToPortal", jsonObj.get("addedToPortal"));
			insert.value("align", jsonObj.get("align"));
			cassandraTemplate.execute(insert);
			return GlobalConstants.OPRERATION_SUCCESS;
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public String addToPage(String widgetId, JSONArray pages, String align) {
		String success = "Y";
		try {

			List<String> pageList = new ArrayList();
			for (int i = 0; i < pages.length(); i++) {
				pageList.add(pages.getString(i));
			}

			Update update = QueryBuilder.update(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			update.where(QueryBuilder.eq("widgetId", widgetId));
			update.with(QueryBuilder.set("addedToPage", true));
			update.with(QueryBuilder.set("pages", pageList));
			update.with(QueryBuilder.set("align", align));
			cassandraTemplate.execute(update);
			return success;
		} catch (Exception e) {
			success = "N";
			return success;
		}

	}

	@Override
	public String deleteFromPage(String widgetId) {
		String success = "Y";
		try {
			Update update = QueryBuilder.update(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			update.where(QueryBuilder.eq("widgetId", widgetId));
			update.with(QueryBuilder.set("addedToPage", false));
			update.with(QueryBuilder.set("pages", "[]"));
			cassandraTemplate.execute(update);
			return success;
		} catch (Exception e) {
			success = "N";
			return success;
		}

	}

	@Override
	public String deleteFromPortal(String widgetId) {
		String success = "Y";
		try {
			logger.info("Inside delete from portal : transaction");
			Update update = QueryBuilder.update(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			update.where(QueryBuilder.eq("widgetId", widgetId));
			update.with(QueryBuilder.set("addedToPortal", false));
			update.with(QueryBuilder.set("addedToPage", false));
			cassandraTemplate.execute(update);

		} catch (Exception e) {
			success = "N";

		}
		return success;
	}

	@Override
	public List<PortalWidgets> fetchPortalWidgets() {
		try {
			logger.info("Inside UserTransactionImpl :");
			List<PortalWidgets> widgetList = new ArrayList();

			Select widgetSelect = QueryBuilder.select().from(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(widgetSelect).all();
			for (Row r : rows) {
				if (r.getBool("addedToPortal") == true) {
					PortalWidgets widget = new PortalWidgets();
					widget.setWidgetId(r.getString("widgetId"));
					widget.setWidgetHeader(r.getString("widgetHeader"));
					widget.setPagesList(r.getList("pages", String.class));
					widget.setAddedToPage(r.getBool("addedToPage"));
					widget.setPreviewDivContent(r
							.getString("previewDivContent"));
					widget.setAddedToPortal(r.getBool("addedToPortal"));
					widget.setAlign(r.getString("align"));
					widgetList.add(widget);
				}
			}
			return widgetList;
		} catch (Exception e) {
			logger.error(e);
			logger.info("Error in fetching all Widgets" + e.getMessage());

			return null;
		}
	}

	@Override
	public List<PortalWidgets> fetchPageWidgets() {
		try {
			logger.info("Inside UserTransactionImpl :");
			List<PortalWidgets> widgetList = new ArrayList();

			Select widgetSelect = QueryBuilder.select().from(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(widgetSelect).all();
			for (Row r : rows) {
				if (r.getBool("addedToPortal") == true) {
					PortalWidgets widget = new PortalWidgets();
					widget.setWidgetId(r.getString("widgetId"));
					widget.setWidgetHeader(r.getString("widgetHeader"));
					widget.setPagesList(r.getList("pages", String.class));
					widget.setAddedToPage(r.getBool("addedToPage"));
					widget.setPreviewDivContent(r
							.getString("previewDivContent"));
					widget.setAddedToPortal(r.getBool("addedToPortal"));
					widget.setAlign(r.getString("align"));
					widgetList.add(widget);
				}
			}
			return widgetList;
		} catch (Exception e) {
			logger.error(e);
			logger.info("Error in fetching all Widgets" + e.getMessage());

			return null;
		}
	}

	@Override
	public List<PortalWidgets> fetchWidgets() {
		try {
			List<PortalWidgets> widgetList = new ArrayList();

			Select widgetSelect = QueryBuilder.select().from(USER_KEYSPACE,
					WIDGETS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(widgetSelect).all();
			for (Row r : rows) {
				PortalWidgets widget = new PortalWidgets();
				widget.setWidgetId(r.getString("widgetId"));
				widget.setWidgetHeader(r.getString("widgetHeader"));
				widget.setPagesList(r.getList("pages", String.class));
				widget.setAddedToPage(r.getBool("addedToPage"));
				widget.setPreviewDivContent(r.getString("previewDivContent"));
				widget.setAddedToPortal(r.getBool("addedToPortal"));
				widget.setAlign(r.getString("align"));
				widgetList.add(widget);
			}
			return widgetList;
		} catch (Exception e) {
			logger.error(e);
			logger.info("Error in fetching all Widgets" + e.getMessage());

			return null;
		}
	}

	public int getNextSequence(String name) {
		logger.info("In getNextSequence");
		Select seqSelect = QueryBuilder.select().from(USER_KEYSPACE,
				SEQ_COUNTERS);
		seqSelect.where(QueryBuilder.eq("id", name));
		Row row = cassandraTemplate.query(seqSelect).one();

		Update seqUpdate = QueryBuilder.update(USER_KEYSPACE, SEQ_COUNTERS);
		seqUpdate.where(QueryBuilder.eq("id", name));
		if (row != null) {
			seqUpdate.with(QueryBuilder.set("seq", row.getInt("seq") + 1));
			cassandraTemplate.execute(seqUpdate);
		} else {
			Insert insert = QueryBuilder
					.insertInto(USER_KEYSPACE, SEQ_COUNTERS);
			insert.value("id", name);
			insert.value("seq", 1);
			cassandraTemplate.execute(insert);
		}
		row = cassandraTemplate.query(seqSelect).one();
		return row.getInt("seq");
	}

	@Override
	public List<UserRatingCount> getProductUrlRating(String url_id) {
		try {

			List<UserRatingCount> lstRatings = new ArrayList<UserRatingCount>();
			UserRatingCount userRatingCount = null;

			int rating_1_Count = 0;
			int rating_2_Count = 0;
			int rating_3_Count = 0;
			int rating_4_Count = 0;
			int rating_5_Count = 0;
			List<Float> userRatingList = new ArrayList<Float>();
			Map<Float, Integer> userRatingMap = new HashMap<Float, Integer>();

			Select userRatingCountSelect = QueryBuilder.select().from(
					USER_KEYSPACE, USER_REVIEW_COMMENTS_COLLECTION);
			List<Row> rows = cassandraTemplate.query(userRatingCountSelect)
					.all();
			for (Row r : rows) {
				if (r.getString("url").equals(url_id)) {
					if (!userRatingList.contains(r.getFloat("userRating"))) {
						userRatingList.add(r.getFloat("userRating"));
						int points = userRatingMap
								.get(r.getFloat("userRating"));
						userRatingMap.put(r.getFloat("userRating"), points + 1);
					}
				}
			}

			Iterator it = userRatingMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Double rating = (Double) pair.getKey();
				int count = (Integer) pair.getValue();

				if (Math.round(rating) == 1) {
					rating_1_Count += count;
					continue;
				}
				if (Math.round(rating) == 2) {
					rating_2_Count += count;
					continue;
				}
				if (Math.round(rating) == 3) {
					rating_3_Count += count;
					continue;
				}
				if (Math.round(rating) == 4) {
					rating_4_Count += count;
					continue;
				}
				if (Math.round(rating) == 5) {
					rating_5_Count += count;
				}
			}

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(1);
			userRatingCount.setRateValue(rating_1_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(2);
			userRatingCount.setRateValue(rating_2_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(3);
			userRatingCount.setRateValue(rating_3_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(4);
			userRatingCount.setRateValue(rating_4_Count);
			lstRatings.add(userRatingCount);

			userRatingCount = new UserRatingCount();
			userRatingCount.setRateCount(5);
			userRatingCount.setRateValue(rating_5_Count);
			lstRatings.add(userRatingCount);

			return lstRatings;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

}
