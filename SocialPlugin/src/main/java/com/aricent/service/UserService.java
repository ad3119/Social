package com.aricent.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.aricent.constant.GlobalConstants;
import com.aricent.gamification.RuleEngine;
import com.aricent.gamification.model.Badge;
import com.aricent.gamification.model.Rule;
import com.aricent.gamification.model.UserPointDetails;
import com.aricent.model.Activity;
import com.aricent.model.CommentRequest;
import com.aricent.model.DownvoteReq;
import com.aricent.model.Email;
import com.aricent.model.FriendsLikesList;
import com.aricent.model.IdleServices;
import com.aricent.model.LinkedAccountType;
import com.aricent.model.LoggedInUser;
import com.aricent.model.PasswordUpdate;
import com.aricent.model.Recommendations;
import com.aricent.model.RecommendedProduct;
import com.aricent.model.RegisteredUserFilters;
import com.aricent.model.ReportsData;
import com.aricent.model.ReviewUpdateReq;
import com.aricent.model.SocialMediaData;
import com.aricent.model.SocialMediaType;
import com.aricent.model.UpvoteReq;
import com.aricent.model.UrlRatingRef;
import com.aricent.model.UserActivityList;
import com.aricent.model.UserBase;
import com.aricent.model.UserCount;
import com.aricent.model.UserRatingCount;
import com.aricent.model.UserReview;
import com.aricent.transaction.RestResponse;
import com.aricent.transaction.UserTransaction;
import com.aricent.transaction.impl.UserTransactionImpl;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Component
@Path("/user")
public class UserService {

	@Autowired
	UserTransaction oUserTransaction;
	@Autowired
	RuleEngine oRuleEngineImpl;
	// @Autowired
	// RuleEngine oRuleEngineImpl;

	private static final Logger logger = Logger.getLogger(UserService.class);

	/*
	 * @GET
	 * 
	 * @Path("/productdetails")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 * 
	 * @Consumes(MediaType.TEXT_PLAIN) public RestResponse getProductDetails() {
	 * 
	 * List<ArrayList<RecommendedProduct>> result =
	 * oUserTransaction.getProductDetails(); RestResponse resp; if (result !=
	 * null) {
	 * 
	 * resp = new RestResponse(result,500, null); return resp; } else { resp =
	 * new RestResponse(null, 501, null); return resp; }
	 * 
	 * }
	 */

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse saveUserBase(UserBase userBase) {
		String result;
		RestResponse resp;
		result = oUserTransaction.updateUserBase(userBase);
		if (result != null)
			resp = new RestResponse(result, GlobalConstants.OPRERATION_SUCCESS,
					null);
		else
			resp = new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		return resp;
	}

	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public RestResponse registerUserBase(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("email") FormDataBodyPart email1,
			@FormDataParam("pass_confirmation") FormDataBodyPart password1,
			@FormDataParam("pass") FormDataBodyPart password2,
			@FormDataParam("fname") FormDataBodyPart firstname,
			@FormDataParam("lname") FormDataBodyPart lastname,
			@FormDataParam("birthyear") FormDataBodyPart yearofbirth,
			@FormDataParam("gender") FormDataBodyPart gender) {

		String result;
		RestResponse resp;

		logger.info(email1.getName() + ": " + email1.getValue());
		logger.info(firstname.getName() + ": " + firstname.getValue());
		logger.info(lastname.getName() + ": " + lastname.getValue());
		logger.info(yearofbirth.getName() + ": " + yearofbirth.getValue());
		logger.info(gender.getName() + ": " + gender.getValue());

		// oUserTransaction.callRegister(uploadedInputStream,fileDetail,
		// email1,password1,firstname,lastname,yearofbirth,gender);
		String uploadedFileLocation = GlobalConstants.profilePicUrl.concat(fileDetail.getFileName());

		// Upload to server
		oUserTransaction.writeToFile(uploadedInputStream, uploadedFileLocation);

		UserBase userBase = new UserBase();
		userBase.setName(firstname.getValue() + " " + lastname.getValue());
		userBase.setPassword(password1.getValue());

		List<String> emailIdList = new ArrayList<String>();
		// Email emailId = new Email();
		// emailId.setEmailId(email1.getValue());
		emailIdList.add(email1.getValue());
		userBase.setEmailIdList(emailIdList);

		userBase.setYearOfBirth(yearofbirth.getValue());
		userBase.setGender(gender.getValue());
		userBase.setNameOfFile("uploaded/" + fileDetail.getFileName());

		result = oUserTransaction.registerUserBase(userBase);
		if (result
				.equals(Integer.toString(GlobalConstants.USER_ALREADY_EXISTS))) {
			resp = new RestResponse(null,
					Integer.valueOf(GlobalConstants.USER_ALREADY_EXISTS), null);
		} else if (result.equals(Integer
				.toString(GlobalConstants.OPRERATION_FAILURE))) {
			resp = new RestResponse(null,
					Integer.valueOf(GlobalConstants.OPRERATION_FAILURE), null);
		} else if (result.equals(Integer
				.toString(GlobalConstants.ERROR_REGISTER_DISABLED))) {
			resp = new RestResponse(null,
					Integer.valueOf(GlobalConstants.ERROR_REGISTER_DISABLED),
					null);
		} else
			resp = new RestResponse(result, GlobalConstants.OPRERATION_SUCCESS,
					null);
		return resp;
	}

	@POST
	@Path("/mediatype")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse saveUserBase(SocialMediaType socialMediaType) {
		int result;
		logger.info("In mediatype:" + socialMediaType);
		result = oUserTransaction.updateSocialMediaType(socialMediaType);
		RestResponse resp = new RestResponse(null, result, null);

		/*
		 * Logging Login activity Activity activityLog = new Activity();
		 * activityLog.setUserBaseId(socialMediaType.getUserBaseId());
		 * activityLog.setSocialMediaId(socialMediaType.getSocialMediaId());
		 * activityLog.setActivityType(GlobalConstants.ACTIVITY_TYPE_LOGIN);
		 * activityLog.setActivityTimeStamp(new Date()); int res =
		 * oUserTransaction.logActivity(activityLog);
		 * logger.info("Logging result :" + res);
		 */

		return resp;
	}

	@POST
	@Path("/media")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse saveMediaData(SocialMediaData socialMediaData) {
		// int result = oUserTransaction.saveMediaTypeRef();
		// result = oUserTransaction.saveMediaAttrRef();
		int result = oUserTransaction.saveSocialMediaData(socialMediaData);
		RestResponse resp = new RestResponse(null, result, null);
		return resp;
	}

	@POST
	@Path("/activate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse activateAccount(LinkedAccountType linkedAccountType) {
		int result = oUserTransaction.activateAccount(linkedAccountType);
		RestResponse resp = new RestResponse(null, result, null);

		if (result == GlobalConstants.OPRERATION_SUCCESS) {
			/*
			 * Logging Register activity Activity activityLog = new Activity();
			 * activityLog
			 * .setUserBaseId(linkedAccountType.getSocialMediaType().getUserBaseId
			 * ());
			 * activityLog.setActivityType(GlobalConstants.ACTIVITY_TYPE_ACTIVATE
			 * ); activityLog.setActivityTimeStamp(new Date()); int res =
			 * oUserTransaction.logActivity(activityLog);
			 * logger.info("Logging result :" + res);
			 */
		}

		return resp;
	}

	@GET
	@Path("/{email}/{smdata}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse fetchBUserFriends(@PathParam("smdata") String smdata,
			@PathParam("email") String email) {
		RestResponse resp;
		logger.info("In fetchFriends:Smdata" + smdata);
		logger.info("Email:" + email);
		if (smdata.equals("friends")) {
			FriendsLikesList userFriendList = new FriendsLikesList();
			userFriendList = oUserTransaction.fetchUserFriends(email);
			if (userFriendList != null) {
				return new RestResponse(userFriendList,
						GlobalConstants.OPRERATION_SUCCESS, null);
			} else {
				return new RestResponse(userFriendList,
						GlobalConstants.OPRERATION_FAILURE, null);
			}

		}

		else if (smdata.equals("likes")) {
			SocialMediaData socialMediaUserLikes = oUserTransaction
					.fetchUserLikes(email);
			if (socialMediaUserLikes != null) {
				return new RestResponse(socialMediaUserLikes,
						GlobalConstants.OPRERATION_SUCCESS, null);
			} else {
				return new RestResponse(socialMediaUserLikes,
						GlobalConstants.OPRERATION_FAILURE, null);
			}
		} else {
			return new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		}

	}

	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse logout(UserBase userBase) {
		int result = oUserTransaction.logout(userBase);
		RestResponse resp = new RestResponse(null, result, null);

		return resp;
	}

	@POST
	@Path("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse validation(UserBase userBase) {
		logger.info("Inside validation");
		String result = oUserTransaction.validateUser(userBase);
		RestResponse resp;
		logger.info(result);
		if (result
				.equals(Integer.toString(GlobalConstants.USER_DOES_NOT_EXIST)))
			resp = new RestResponse(null, GlobalConstants.USER_DOES_NOT_EXIST,
					null);
		else if (result.equals(Integer
				.toString(GlobalConstants.INCORRECT_PASSWORD)))
			resp = new RestResponse(null, GlobalConstants.INCORRECT_PASSWORD,
					null);
		else if (result.equals(Integer
				.toString(GlobalConstants.OPRERATION_FAILURE)))
			resp = new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		else
			resp = new RestResponse(result, GlobalConstants.OPRERATION_SUCCESS,
					null);
		return resp;
	}

	@POST
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse countFriends() {
		UserCount usercount = oUserTransaction.countFriends();
		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/alluseractivity")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse fetchAllUserActivity() {
		UserActivityList allUseractivity = new UserActivityList();
		allUseractivity = oUserTransaction.fetchAllUserActivity();
		if (allUseractivity != null) {
			return new RestResponse(allUseractivity,
					GlobalConstants.OPRERATION_SUCCESS, null);
		} else {
			return new RestResponse(allUseractivity,
					GlobalConstants.OPRERATION_FAILURE, null);
		}
	}

	@POST
	@Path("/useractivity")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse fetchUserActivity(String userBase) {
		List<UserReview> userReviewlist = new ArrayList<UserReview>();
		userReviewlist = oUserTransaction.fetchUserActivity(userBase);
		if (userReviewlist != null) {
			return new RestResponse(userReviewlist,
					GlobalConstants.OPRERATION_SUCCESS, null);
		} else {
			return new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		}
	}

	@POST
	@Path("/updatepwd")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse updatePassword(PasswordUpdate passwordUpdate) {
		int result = oUserTransaction.updatePassword(passwordUpdate);
		RestResponse resp = new RestResponse(null, result, null);
		return resp;
	}

	@POST
	@Path("/updateservices")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse updateServices(IdleServices idleServices) {
		int result = oUserTransaction.updateServices(idleServices);
		RestResponse resp = new RestResponse(null, result, null);
		return resp;
	}

	@POST
	@Path("/sendpwdlink")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse sendPwdLink(String email) {
		int result = oUserTransaction.sendPwdLink(email);
		RestResponse resp = new RestResponse(null, result, null);
		return resp;
	}

	@POST
	@Path("/validatecode")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse validateCode(String code) {
		int result = oUserTransaction.validateCode(code);
		RestResponse resp = new RestResponse(null, result, null);
		return resp;
	}

	@POST
	@Path("/newRegisteredUsers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse newRegisteredUsers(
			RegisteredUserFilters registeredUserFilters) {
		RegisteredUserFilters usercount = oUserTransaction
				.newRegisteredUsers(registeredUserFilters);
		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/newSocialUsers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse newSocialUsers(
			RegisteredUserFilters registeredUserFilters) {
		UserCount usercount = oUserTransaction
				.newSocialUsers(registeredUserFilters);
		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/checkregistereduser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse checkRegisteredUser(String userBaseId) {
		int result = oUserTransaction.checkRegisteredUser(userBaseId);
		RestResponse resp = new RestResponse(null, result, null);
		return resp;
	}

	@POST
	@Path("/deactivate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse deactivateAccount(String userBaseId) {
		int result = oUserTransaction.deactivateAccount(userBaseId);
		RestResponse resp = new RestResponse(null, result, null);
		return resp;
	}

	@POST
	@Path("/download_pic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse downloadPic(String userBase) {
		// UserBase userBase = new UserBase();

		String result;
		result = oUserTransaction.getProfilePath(userBase);
		RestResponse resp;
		if (result == null
				|| result.equals(Integer
						.toString(GlobalConstants.OPRERATION_FAILURE))) {
			resp = new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		} else {
			resp = new RestResponse(result, GlobalConstants.OPRERATION_SUCCESS,
					null);
			String absolutePath = GlobalConstants.profilePicUrlBase
					.concat(result);
			logger.info(absolutePath);
		}

		return resp;
	}

	@POST
	@Path("/username")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getUserName(String userBase) {
		// UserBase userBase = new UserBase();

		String result;
		result = oUserTransaction.getUserName(userBase);
		RestResponse resp;
		if (result.equals(Integer.toString(GlobalConstants.OPRERATION_FAILURE))) {
			resp = new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		} else {
			resp = new RestResponse(result, GlobalConstants.OPRERATION_SUCCESS,
					null);
		}
		return resp;
	}

	@POST
	@Path("/allocatepoint")
	// @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void allocatePoint(Activity activityLog) {
		oRuleEngineImpl.allocatePoint(activityLog);
	}

	@POST
	@Path("/allocateBadges")
	// @Produces(MediaType.)
	@Consumes(MediaType.TEXT_PLAIN)
	public void allocateBadges(String userBaseId) {
		oRuleEngineImpl.allocateBadges(userBaseId);
	}

	@POST
	@Path("/getUserPoints")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getUserPoints(String userBaseId) {
		UserPointDetails result = new UserPointDetails();
		result = oRuleEngineImpl.getUserPoints(userBaseId);
		RestResponse resp = new RestResponse(result,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/getUserBadges")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getUserBadges(String userBaseId) {
		List<Badge> badgeList = new ArrayList<Badge>();
		badgeList = oRuleEngineImpl.getUserBadges(userBaseId);
		return new RestResponse(badgeList, GlobalConstants.OPRERATION_SUCCESS,
				null);
	}

	@GET
	@Path("/getLeaderBoard")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse getLeaderBoard() {
		List<UserPointDetails> userPointsList = new ArrayList<UserPointDetails>();
		userPointsList = oRuleEngineImpl.getLeaderBoard();
		return new RestResponse(userPointsList,
				GlobalConstants.OPRERATION_SUCCESS, null);
	}

	@POST
	@Path("/getBadges")
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getBadges() {
		List<Badge> badgeList = new ArrayList<Badge>();
		badgeList = oRuleEngineImpl.getBadges();
		return new RestResponse(badgeList, GlobalConstants.OPRERATION_SUCCESS,
				null);
	}

	@POST
	@Path("/createRule")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse createRule(Rule rule) {
		int result = oRuleEngineImpl.createRule(rule);
		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/getRules")
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getRules() {
		List<Rule> ruleList = new ArrayList<Rule>();
		ruleList = oRuleEngineImpl.getRules();
		return new RestResponse(ruleList, GlobalConstants.OPRERATION_SUCCESS,
				null);
	}

	@POST
	@Path("/editRule")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse editRule(Rule rule) {
		int result = oRuleEngineImpl.editRule(rule);
		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/deleteRule")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse deleteRule(String ruleId) {
		int result = oRuleEngineImpl.deleteRule(ruleId);
		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/getRating")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getOverallRating(String url) {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		UrlRatingRef ratingCount = oUserTransaction.fetchUrlRating(url);
		logger.info("URL = " + ratingCount.getUrl());
		logger.info("totNumRating = " + ratingCount.getTotNumRating());
		logger.info("averageRating = " + ratingCount.getAverageRating());
		RestResponse resp = new RestResponse(ratingCount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + ratingCount);
		return resp;
	}

	@POST
	@Path("/updateReview")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse updateReview(ReviewUpdateReq ruReq) {
		int responseCode = oUserTransaction.updateUserReview(ruReq);
		RestResponse resp = new RestResponse(null, responseCode, null);
		logger.info("updateRating Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/readReview")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse readReviews(String url) {
		List<UserReview> userReviewList = new ArrayList<UserReview>();
		userReviewList = oUserTransaction.fetchUserReviews(url);
		int responseCode;
		if ((null == userReviewList) || (userReviewList.isEmpty()))
			responseCode = GlobalConstants.OPRERATION_FAILURE;
		else
			responseCode = GlobalConstants.OPRERATION_SUCCESS;
		RestResponse resp = new RestResponse(userReviewList, responseCode, null);
		logger.info("readReviews Response code = " + responseCode);
		return resp;
	}

	@GET
	@Path("/readAllReviews")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse readAllReviews() {
		List<UserReview> userReviewList = new ArrayList<UserReview>();
		userReviewList = oUserTransaction.fetchUserReviewsProduct();
		int responseCode;
		if ((null == userReviewList) || (userReviewList.isEmpty()))
			responseCode = GlobalConstants.OPRERATION_FAILURE;
		else
			responseCode = GlobalConstants.OPRERATION_SUCCESS;
		RestResponse resp = new RestResponse(userReviewList, responseCode, null);
		logger.info("readReviews Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/createBadge")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse createBadge(Badge badge) {
		int result = oRuleEngineImpl.createBadge(badge);
		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/editBadge")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse editBadge(Badge badge) {
		int result = oRuleEngineImpl.editBadge(badge);
		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/deleteBadge")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse deleteBadge(String badgeId) {
		int result = oRuleEngineImpl.deleteBadge(badgeId);
		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/logactivity")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse logActivity(Activity activityLog) {
		activityLog.setActivityTimeStamp(new Date());
		int res = oUserTransaction.logActivity(activityLog);
		RestResponse resp = new RestResponse(null, res, null);
		return resp;
	}

	@POST
	@Path("/gender_graph")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse genderGraphUserBase() {
		UserCount usercount = oUserTransaction.genderGraphUserBase();
		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/age_graph")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse ageGraphUserBase() {
		UserCount usercount = oUserTransaction.ageGraphUserBase();

		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	// added for getting social reg, user reg and daily count for admin page

	@POST
	@Path("/loginCount")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getlogincount() {
		UserCount usercount = oUserTransaction.getlogincount();
		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	// added for displaying all prof pics in the admin page
	@POST
	@Path("/getAllProfPics")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getAllProfPics() {
		List profPics = oUserTransaction.getAllProfPics();
		for (int i = 0; i < profPics.size(); i++) {
			logger.info(profPics.get(i));
		}

		RestResponse resp = new RestResponse(profPics,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/sharingGraph")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getsharingGraph() {
		UserCount usercount = oUserTransaction.getsharingCount();
		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/commentsGraph")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getsharingCount() {
		UserCount usercount = oUserTransaction.getcommentsCount();
		RestResponse resp = new RestResponse(usercount,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	// added for rating graph

	@POST
	@Path("/eachUrlRating")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getEachUrlRating() {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		List<UserRatingCount> eachUrlList = oUserTransaction.getEachUrlRating();

		RestResponse resp = new RestResponse(eachUrlList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + eachUrlList);
		return resp;
	}

	@POST
	@Path("/productUrlRating")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductUrlRating(String product_id) {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		List<UserRatingCount> eachUrlList = oUserTransaction
				.getProductUrlRating(product_id);

		RestResponse resp = new RestResponse(eachUrlList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + eachUrlList);
		return resp;
	}

	@POST
	@Path("/dailyLoginsPerWeek")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getDailyLoginsPerWeek() {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		UserRatingCount getDailyLoginsPerWeekList = oUserTransaction
				.getDailyLoginPerWeek();

		RestResponse resp = new RestResponse(getDailyLoginsPerWeekList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + getDailyLoginsPerWeekList);
		return resp;
	}

	@POST
	@Path("/getNotifications")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getUserNotifications(String userBaseId) {
		List<String> msgList = new ArrayList<String>();
		msgList = oUserTransaction.getNotifications(userBaseId);
		return new RestResponse(msgList, GlobalConstants.OPRERATION_SUCCESS,
				null);
	}

	@POST
	@Path("/ageWeek")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getAgePerWeek(ReportsData dateRange) {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		UserCount getAgeWeekList = oUserTransaction.getAgePerWeek(dateRange);

		RestResponse resp = new RestResponse(getAgeWeekList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + getAgeWeekList);
		return resp;
	}

	@POST
	@Path("/reportsRange")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse reports_Share_Range(ReportsData dateRange) {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		List<UserRatingCount> getreportsShareRangeList = oUserTransaction
				.getreportsShareRange(dateRange);

		RestResponse resp = new RestResponse(getreportsShareRangeList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + getreportsShareRangeList);
		return resp;
	}

	@POST
	@Path("/dailyActivitiesGraph")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse dailyActvity() {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		UserRatingCount getdailyActvity = oUserTransaction.dailyActvity();

		RestResponse resp = new RestResponse(getdailyActvity,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + getdailyActvity);
		return resp;
	}

	@POST
	@Path("/reportReactionRange")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse reports_Reaction_Range(ReportsData dateRange) {
		// RatingCount ratingCount =
		// oUserTransaction.fetchRatingCount(RatingCnt.getUrl());
		List<UserRatingCount> getreportsReactionRangeList = oUserTransaction
				.getreportsReactionRange(dateRange);

		RestResponse resp = new RestResponse(getreportsReactionRangeList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		logger.info("Rest resp = " + getreportsReactionRangeList);
		return resp;
	}

	@POST
	@Path("/submitComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse submitComment(CommentRequest comreq) {
		int responseCode = oUserTransaction.updateUserComment(comreq);
		RestResponse resp = new RestResponse(null, responseCode, null);
		logger.info("submitComment Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/readComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse readComments(String url) {
		List<UserReview> userReviewList = new ArrayList<UserReview>();
		userReviewList = oUserTransaction.fetchUserComments(url);
		int responseCode;
		if ((null == userReviewList) || (userReviewList.isEmpty()))
			responseCode = GlobalConstants.OPRERATION_FAILURE;
		else
			responseCode = GlobalConstants.OPRERATION_SUCCESS;
		RestResponse resp = new RestResponse(userReviewList, responseCode, null);
		logger.info("readComments Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/submitReply")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse submitreply(CommentRequest comreq) {
		int responseCode = oUserTransaction.updateUserReply(comreq);
		RestResponse resp = new RestResponse(null, responseCode, null);
		logger.info("submitReply Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/readReplies")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse readReplies(CommentRequest parentCommentDetails) {
		List<UserReview> userRepliesList = new ArrayList<UserReview>();
		userRepliesList = oUserTransaction
				.fetchUserReplies(parentCommentDetails);
		int responseCode;
		if ((null == userRepliesList) || (userRepliesList.isEmpty()))
			responseCode = GlobalConstants.OPRERATION_FAILURE;
		else
			responseCode = GlobalConstants.OPRERATION_SUCCESS;
		RestResponse resp = new RestResponse(userRepliesList, responseCode,
				null);
		logger.info("readReplies Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/deleteComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse deleteComment(String commentId) {
		int responseCode;
		int numofdeleteddocuments = oUserTransaction
				.deleteCommentwithId(commentId);
		if (numofdeleteddocuments > 0)
			responseCode = GlobalConstants.OPRERATION_SUCCESS;
		else
			responseCode = GlobalConstants.OPRERATION_FAILURE;
		RestResponse resp = new RestResponse(numofdeleteddocuments,
				responseCode, null);
		logger.info("deleteComment Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/upvoteComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse upvoteComment(UpvoteReq upvoteReq) {
		int responseCode;
		int numofupvotes = oUserTransaction.upvoteComment(upvoteReq);
		if (numofupvotes > 0)
			responseCode = GlobalConstants.OPRERATION_SUCCESS;
		else
			responseCode = GlobalConstants.OPRERATION_FAILURE;
		RestResponse resp = new RestResponse(numofupvotes, responseCode, null);
		logger.info("upvoteComment Response code = " + responseCode);
		return resp;
	}

	@POST
	@Path("/downvoteComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse downvoteComment(DownvoteReq downvoteReq) {
		int responseCode;
		int numofdownvotes = oUserTransaction.downvoteComment(downvoteReq);
		if (numofdownvotes > 0)
			responseCode = GlobalConstants.OPRERATION_SUCCESS;
		else
			responseCode = GlobalConstants.OPRERATION_FAILURE;
		RestResponse resp = new RestResponse(numofdownvotes, responseCode, null);
		logger.info("deleteComment Response code = " + responseCode);
		return resp;
	}

}
