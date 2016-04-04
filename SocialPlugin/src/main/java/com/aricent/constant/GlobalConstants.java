package com.aricent.constant;

public interface GlobalConstants {

	/* DB Operation Constants */
	public static final int DB_QUERY_SUCCESS = 100;
	public static final int DB_CONN_SUCCESS = 101;
	public static final int DB_CONN_FAILURE = 102;
	public static final int DB_QUERY_FAILED = 103;

	public static final int SAVE_SUCCESS = 200;
	public static final int SAVE_FAILED = 201;

	public static final int UPDATE_SUCCESS = 300;
	public static final int UPDATE_FAILED = 301;

	public static final int USER_EXISTS = 401;
	/* Generic Constants */
	public static final int OPRERATION_SUCCESS = 500;
	public static final int OPRERATION_FAILURE = 501;

	public static final int USER_DOES_NOT_EXIST = 601;
	public static final int INCORRECT_PASSWORD = 602;
	public static final int USER_ALREADY_EXISTS = 603;

	public static final int CODE_ALREADY_PRESENT = 701;
	public static final int CODE_NOT_PRESENT = 702;

	/* Activity Type Constants */
	public static final int ACTIVITY_TYPE_LOGIN = 801;
	public static final int ACTIVITY_TYPE_LOGOUT = 802;
	public static final int ACTIVITY_TYPE_ACTIVATE = 803;
	public static final int ACTIVITY_TYPE_REACTION = 804;
	public static final int ACTIVITY_TYPE_REVIEW = 805;
	public static final int ACTIVITY_TYPE_COMMENT = 806;
	public static final int ACTIVITY_TYPE_SHARE = 807;
	public static final int ACTIVITY_TYPE_REGISTER = 808;

	/* SocialMedia Accounts TYpe linked to registered users */
	public static final int SOCIAL_MEDIA_FB_LINKED = 901;
	public static final int SOCIAL_MEDIA_GOOGLE_LINKED = 902;
	public static final int SOCIAL_MEDIA_NOT_LINKED = 903;

	/* Entity Constants */
	public static final int SM_TYPE_FACEBOOK = 1001;
	public static final int SM_ATTR_FACEBOOK_LIKE = 1002;
	public static final int SM_ATTR_FACEBOOK_FRIEND = 1003;

	public static final int SM_TYPE_GOOGLE_PLUS = 2001;
	public static final int SM_TYPE_TWITTER = 2002;

	public static final String DEFAULT_PASSWD = "Pass@123";

	public static final String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

	public static final int ERROR_REGISTER_DISABLED = 4001;

	public static final int REGISTER_MSG = 5001;
	public static final int LOGIN_MSG = 5002;

	/* Reaction Constants */
	public static final int USER_REACTION_UPDATED = 6001;
	public static final int USER_REACTION_UPDATE_FAILED = 6002;
	public static final int REACTION_UPDATED = 6003;
	public static final int REACTION_UPDATE_FAILED = 6004;

	public static final int ACTION_REACTION_CREATED = 111;
	public static final int ACTION_REACTION_UPDATED = 112;
	public static final int ACTION_REACTION_CREATE_UPDATE_FAILED = 113;
	public static final int ACTION_EMPTY_REACTION = 114;
	public static final int ACTION_REACTION_DELETED = 115;
	public static final int ACTION_REACTION_DELETE_FAILED = 116;

	public static final String PLUGIN_REACTION = "8001";

	public static final String profilePicUrlBase = "http://localhost:8080/SocialPlugin/";
	
	public static final String profilePicUrl = "D:\\USERS\\BGH39679\\Documents\\workspace_may\\SocialPluginWithCassandra\\src\\main\\webapp\\uploaded\\";
	
	// Add type of values for reviews comments and replies
	public static final int TYPE_REVIEWS = 1;
	public static final int TYPE_COMMENTS = 2;
	public static final int TYPE_REPLIES = 3;

}
