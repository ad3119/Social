package com.aricent.transaction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;

import com.datastax.driver.core.Row;
import com.aricent.model.Activity;
import com.aricent.model.CommentRequest;
import com.aricent.model.DownvoteReq;
import com.aricent.model.FriendsLikesList;
import com.aricent.model.IdleServices;
import com.aricent.model.LinkedAccountType;
import com.aricent.model.LoggedInUser;
import com.aricent.model.PasswordUpdate;
import com.aricent.model.PortalWidgets;
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
import com.aricent.model.UserReview;
import com.aricent.model.UserRatingCount;

public interface UserTransaction {

	// public int saveUserFriends(UserFriendList userFriendList);

	// public int saveGoogleFriends(GoogleFriendList googleFriendList);

	// public int saveUserBase(UserBase userBase, SocialMediaId socialMediaId);

	// public List<ArrayList<RecommendedProduct>> getProductDetails();

	public String updateUserBase(UserBase userBase);

	public void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation);

	public String registerUserBase(UserBase userBase);

	public int updateSocialMediaType(SocialMediaType socialMediaType);

	public int saveSocialMediaData(SocialMediaData socialMediaData);

	public int activateAccount(LinkedAccountType linkedAccountType);

	public FriendsLikesList fetchUserFriends(String email);

	public SocialMediaData fetchUserLikes(String email);

	public int logout(UserBase userBase);

	public String validateUser(UserBase userBase);

	public UserCount countFriends();

	public UserActivityList fetchAllUserActivity();

	public List<UserReview> fetchUserActivity(String userBase);

	public int updatePassword(PasswordUpdate passwordUpdate);

	public int updateServices(IdleServices idleServices);

	public int sendPwdLink(String email);

	public int validateCode(String code);

	public RegisteredUserFilters newRegisteredUsers(
			RegisteredUserFilters registeredUserFilters);

	public UserCount newSocialUsers(RegisteredUserFilters registeredUserFilters);

	public int checkRegisteredUser(String userBaseId);

	public int deactivateAccount(String userBaseId);

	public String getProfilePath(String userBase);

	public String getUserName(String userBase);

	public UrlRatingRef fetchUrlRating(String url);

	public int updateUserReview(ReviewUpdateReq ruReq);

	public List<UserReview> fetchUserReviews(String url);

	public List<UserReview> fetchUserReviewsProduct();

	public int logActivity(Activity activityLog);

	public UserCount genderGraphUserBase();

	public UserCount ageGraphUserBase();

	public UserCount getlogincount();

	public List getAllProfPics();

	public UserCount getsharingCount();

	public UserCount getcommentsCount();

	public List<UserRatingCount> getEachUrlRating();

	public UserRatingCount getDailyLoginPerWeek();

	public List<String> getNotifications(String userBaseId);

	public UserCount getAgePerWeek(ReportsData dateRange);

	public List<UserRatingCount> getreportsShareRange(ReportsData dateRange);

	public UserRatingCount dailyActvity();

	public List<UserRatingCount> getreportsReactionRange(ReportsData dateRange);

	public int updateUserComment(CommentRequest comreq);

	public List<UserReview> fetchUserComments(String url);

	public int updateUserReply(CommentRequest comreq);

	public List<UserReview> fetchUserReplies(CommentRequest parentCommentDetails);

	public int deleteCommentwithId(String commentId);

	public int upvoteComment(UpvoteReq upvoteReq);

	public int downvoteComment(DownvoteReq downvoteReq);

	public String addToPortal(String widgetId);

	public Integer addNewWidget(String json);

	public String addToPage(String widgetId, JSONArray pages, String align);

	public String deleteFromPage(String widgetId);

	public String deleteFromPortal(String widgetId);

	public List<PortalWidgets> fetchPortalWidgets();

	public List<PortalWidgets> fetchPageWidgets();

	public List<PortalWidgets> fetchWidgets();

	public List<UserRatingCount> getProductUrlRating(String product_id);
}
