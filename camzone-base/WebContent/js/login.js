/************************LOAD GOOGLE API*******************************************/

(function() {
	var po = document.createElement('script');
	po.type = 'text/javascript';
	po.async = true;
	po.src = 'https://apis.google.com/js/client:plusone.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(po, s);
})();


/************************LOAD GOOGLE API*******************************************/


/************************LOAD FACEBOOK API******************************************/

(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) return;
	js = d.createElement(s);
	js.id = id;
	js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&appId=850966344923496&version=v2.0";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

/************************LOAD FACEBOOK API******************************************/


/************************Enabling JSON for cookie Plugin****************************/
$( document ).ready(function() {
	$.cookie.json = true;
});
/************************Enabling JSON for cookie Plugin****************************/


/************************GLOBAL URLs***********************************************/
var reactionsData;
var reactionList;

var userId;
var login_data = {};
var notificationData;


var homePage = socialPluginConfig.homeURL;
var profilepicBaseURL = socialPluginConfig.profilepicBaseURL;
var baseURL = socialPluginConfig.baseURL;
var loginAuditService = baseURL+"login";
var logoutService = baseURL+"logout";
var mediaType = baseURL+"mediatype";
var media = baseURL+"media";
var registerURL = baseURL+"register";
var validateURL =baseURL+"validate";
var sendpwdLinkURL = baseURL+"sendpwdlink";
var updatepwd = baseURL+"updatepwd";
var activateSocial = baseURL+"activate";
var checkregistereduser = baseURL+"checkregistereduser";
var deactivateSocial = baseURL+"deactivate";
var fetchProfilePic = baseURL+"download_pic";
var allUserActivityURL = baseURL+"alluseractivity";
var UserActivityURL = baseURL+"useractivity";
var SocialPicURL = baseURL+"social_pic";
var AllocatePointsURL = baseURL+"allocatepoint";
var reactionBaseURL = socialPluginConfig.reactionBaseURL;


var reactionListURL = reactionBaseURL;
var notificationURL = socialPluginConfig.notificationURL;


/************************GLOBAL URLs***********************************************/



/************************Points allocation notification*****************************/

var notify = function(){
	
	if($.cookie("login_data")){
		var login_cookie = JSON.parse($.cookie("login_data"));
		if((login_cookie) && (login_cookie.userbase)){	
			var userBaseId = login_cookie.userbase;
			$.ajax({
				type : 'POST',
				contentType : 'text/plain',
				url : notificationURL,
				data : userBaseId,
				success : function(data) {
					if (data.code == "501") {
						console.log("Error is receiving the data");
					} else if (data.code == "500") {
						console.log(data.object);
						notificationData = data.object; 
						if(notificationData ){
							for ( var i = 0; i < data.object.length; i++) {
								$.notify(notificationData [i], "success");
							}							
						}
					}
				}
			});	
		}
		else{
			console.log("No user session exists");
		}
	}
	
};

setInterval(notify,5000);
 /************************Points allocation notification*****************************/



/***************FUNCTIONS TO HANDEL BUTTON CLICK*********************/

$('#submitbtn_login').click(function() {
	/*$('#login_modal').modal('hide');*/
	postLoginData();
	return false;
});

$('#forgot-password').click(function() {
	postEmail();
	return false;
});

$('#reset-password').click(function() {
	postpwd();
	return false;
});

$("#registerForm").submit(function (event) {
	if( ( $("#reg_email").val().trim() ) == '') {
		alert("Enter a valid email id");
	}
	else {
	
	var formData = new FormData($(this)[0]);
	console.log(formData);
	
	 $.ajax({
		    url: registerURL,
		    type: 'POST',
		    data: formData,
		    async: false,
		    cache: false,
		    contentType: false,
		    processData: false,
		    success: function (data) {

				console.log(data);
				if(data.code == "501")
				{
					$("#error-window").removeClass( 'hidden' );
					$("#error-msg").text("An error occured! Please try again");
					//console.log("Error in Fetching data");
				}
				if(data.code == "603")
				{
					$("#error-window").removeClass( 'hidden' );
					$("#error-msg").text("User already exists");
				}
				else if(data.code == "500")
				{
					var now = new Date();
					/* one hour = 1000*3600 , one minute = 1000*60 */
					var time = new Date(now.getTime() + (1000*60*60));
					
					allocatePoints(data.object,"808");
					setTimeout(function(){
					login_data['social'] = "register";
					login_data['username'] = $('#fname').val();
					login_data['expires'] = time.toString();
					login_data['profilepic'] = "";
					//login_data['path'] = '/';
					login_data['email'] = $('#reg_email').val();
					login_data['userbase'] = data.object;
					$.cookie("login_data",JSON.stringify(login_data));
					//window.opener.location.reload();
					//window.close();
					widget.reloadWidgets();
					$("#register_modal").modal('hide');
					$("#login").addClass('hidden');
					RegLoginStatus();
					
					
					/*$("#RloggedIn").removeClass( 'hidden' );
					$("#LinkSocial").removeClass("hidden");*/
					}, 2000);

				}
				else if(data.code == 4001){
					console.log(data);
					alert("Registration Service is not active");
				}
				

			},
			error: function(jqXHR, textStatus, errorThrown){
				alert('addUser error: ' + textStatus);
			}
		  });
	}
	
});

/**********************************************************************/


/************************GAMIFICATION FUNCTIONS************************/

function allocatePoints(userbase,activityType){
    
    $.ajax({
           type: 'POST',
           contentType: 'application/json',
           url: AllocatePointsURL,
           dataType: "json",
           data: JSON.stringify({"userBaseId":userbase,"activityType":activityType}),
           success: function(data){
        	   console.log("inside allocate points " +data);
           }
    });
    
}



/**********************************************************************
 * 
 * Function to login to Facebook and fetch the email and user name
 * in-order to fill the register form.
 * 
 * 
 **********************************************************************/
function doFBFormLogin() {

	FB.getLoginStatus(function(response) {
		if (response.status === 'connected') {
			console.log('Logged in.');
			fillRegistrationForm();
		} else {
			FB.login(function(response) {
				console.log(response);
				fillRegistrationForm();
			}, {
				scope : 'email,user_friends,public_profile'
			});
		}
	});
}

function fillRegistrationForm() {
	FB.api("/me", function(response) {
		if (response && !response.error) {
			console.log("response:" + response);
			console.log(response);
			/* handle the result */
			$("#fname").val(
					response['first_name'] + " "
					+ response['last_name']);
			$("#email").val(response['email']);
			FB.logout(function(response) {
				
				$("#loggedIn").addClass('hidden');
				$("#login").removeClass('hidden');
				FB.Auth.setAuthResponse(null,'unknown');
			});
		} else {
			console.log(response);
		}
	});
}

/**********************************************************************
 * 
 * Function to login to Google plus and fetch the email and username
 * inorder to fill the register form.
 * 
 * 
 **********************************************************************/
function doGooglelogin() {
	var config = {
			'client_id' : '264266712427-jt2585f5d9bd18vtac1imtdorrvivi5m.apps.googleusercontent.com',
			'scope' : 'email https://www.googleapis.com/auth/plus.login'
	};
	gapi.auth.authorize(config, function() {
		console.log('login complete');
		gapi.client.load('plus', 'v1', function() {

			gapi.client.plus.people.get({
				userId : 'me'
			}).execute(handleEmailResponse);

			function handleEmailResponse(resp) {
				var primaryEmail = '';
				for ( var i = 0; i < resp.emails.length; i++) {
					if (resp.emails[i].type === 'account')
						primaryEmail = resp.emails[i].value;
				}
				$("#email").val(primaryEmail);
				$("#fname").val(resp.displayName);
			}
			win = window.open("http://accounts.google.com/logout",
					"something", "width=550,height=570");
			setTimeout("win.close();", 1000);
		});
	});
}




/*************************************************************
 * 
 * 
 * FUNCTION TO POST THE REGISTERED USER's DATA
 * 
 * 
 * ***********************************************************/
function postLoginData(){
	
	/*var Email = {};
	var primaryEmail = [];
	Email['emailId'] = $('#email').val();
	primaryEmail.push(Email);*/
	var emailIdList = [];
	emailIdList.push($('#email').val());
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: validateURL,
		dataType: "json",
		data: JSON.stringify({"userBaseId":"","emailIdList": emailIdList/*primaryEmail*/,"password": $('#pwd').val()}),
		success: function(data){
			console.log(data);
			if(data.code == "500") //user logged in successfully
			{
				var str = $('#email').val();
				var res = str.split("@");
				var now = new Date();
				/* one hour = 1000*3600 , one minute = 1000*60 */
				var time = new Date(now.getTime() + (1000*60*60));
				console.log(data.object);
				console.log("calling allocatePoints");
				allocatePoints(data.object,"801");
				
				setTimeout(function(){
					console.log("Called allocatePoints");
					login_data['username'] = res[0];
					login_data['social'] = "register";
					login_data['expires'] = time.toString();
					login_data['profilepic'] = "";
					login_data['email'] = $('#email').val();
					login_data['userbase'] = data.object;
					
					$.cookie("login_data",JSON.stringify(login_data));
					//window.opener.location.reload();
					//window.close();
					$('#email').val('');
					$('#pwd').val('');
					$('#login_modal').modal('hide');
					$("#login").addClass('hidden');
					
					
					//fetchUserProfilePic(login_data['userbase']);
					RegLoginStatus();
					window.location.reload();
					//widget.reloadWidgets()
				}, 2000);
			}
			if(data.code == "601") //if user does not exist
			{
				//alert("User Does Not Exist");
				$('#email').val('');
				$("#error-window").removeClass( 'hidden' );
				$("#error-msg-login").html("<span class='text-warning'>User does not exist</span>");
				$("#error-msg-login").fadeIn();
				$("#error-msg-login").fadeOut("slow");
				/*$("#error-window").addClass( 'hidden' );*/
			}
			if(data.code == "602") //incorrect password
			{
				//alert("Incorrect Password");
				//$("#error-window").removeClass( 'hidden' );
				$("#error-msg-login").html("<span class='text-warning'>Incorrect password</span>");
				$("#error-msg-login").fadeIn();
				$("#error-msg-login").fadeOut("slow");
			}
			else if(data.code == "501")
			{
				console.log("Error in Fetching data");
			}

		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('LoginUser error: ' + textStatus);
		}
	});

}


/*************************************************************
 * 
 * 
 * FUNCTION TO POST USER's EMAIL TO RESET THE PASSOWRD
 * 
 * 
 * ***********************************************************/

function postEmail(){
	var Email = $('#email').val();
	console.log(Email);
	$.ajax({
		type: 'POST',
		contentType: 'text/plain',
		url: sendpwdLinkURL,
		data: Email,
		success: function(data){
			if(data.code == "501"){
				alert("Error is receiving the data");
			}
			else if(data.code == "500"){
				console.log(data);
				window.opener.location.reload();
				window.close();

			}
		}
	});
}

/*************************************************************
 * 
 * 
 * FUNCTION TO POST USER's NEW PASSOWRD
 * 
 * 
 * ***********************************************************/

function postpwd(){
	var Email = "dummy.100@gmail.com";
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: updatepwd,
		dataType: "json",
		data: JSON.stringify({"email": Email,"newPassword": $('#pwd').val()}),
		success: function(data){
			console.log(data);
			if(data.code == "501"){
				alert("Error is receiving the data");
			}
			if(data.code == "601"){
				alert("User does not exist");
			}
			else if(data.code == "500"){
				console.log(data);
				window.opener.location.replace(homePage);
				window.close();



			}
		}
	});
}


/*************************************************************
 * 
 * 
 * 		Helper function to serialize all the form 
 * 		fields into a JSON string
 * 
 * ************************************************************/
function formToJSON() {
	var Email = {};
	var primaryEmail = [];
	Email['emailId'] = $('#email').val();
	primaryEmail.push(Email);
	return JSON.stringify({
		"name": $('#fname').val(), 
		"emailIdList": primaryEmail,
		"password": $('#pwd').val(),
		"dob": $('#birthyear').val(),
		"gender": $('#gender').val(),
	});
}


/*************************************************************
 * 
 * 			function to deactivate the linked social
 * 			account
 * 
 * ************************************************************/

function deactivate() {

	var object = JSON.parse($.cookie("login_data"));
	var userBaseId = object.userbase;
	if (userBaseId) {
		$.ajax({
					type : 'POST',
					contentType : 'text/plain',
					url : deactivateSocial,
					data : userBaseId,
					success : function(data) {
						// console.log(data);
						if (data.code == "501") {
							$("#error-msg").removeClass('hidden');
							$("#error").text("Error occured during the operation! please try again");
						} 
						else if (data.code == "500") {
							$("#Glinked").addClass('hidden');
							$("#fblinked").addClass('hidden');
							$("#LinkSocial").removeClass('hidden');
							widget.reloadWidgets();
						}
					}
				});
	}
}



/******************************************************************
 * 
 * 
 * 					FACEBOOK ACTIVATE FUNCTION
 * 
 * 
 * ****************************************************************/

function activateFB() {
	var access_token = "";
	FB.getLoginStatus(function(response) {
		if (response.status === 'connected') {
			getFBuserDetails(response['authResponse']['accessToken']);
			$("#LinkSocial").addClass("hidden");
			$("#fblinked").removeClass("hidden");
		} else {
			console.log("User not logged in! Initiating FB login...");
			FB.login(function(response) {
				console.log("After FB login...");
				// console.log(response);
				access_token = response.authResponse.accessToken;
				getFBuserDetails(access_token);
				$("#LinkSocial").addClass("hidden");
				$("#fblinked").removeClass("hidden");
			}, 
			{
				scope : 'email,user_friends,public_profile'
			});
		}
	});

}


/******************************************************************
 * 
 * 
 * 				FUNCTION TO INSERT USER LIKES
 * 
 * 
 * ****************************************************************/

function insertUserLikes(userBaseId){
/*	alert("Inside User Likes")
*/	console.log("inside insertUserLikes() : "+ userBaseId );

	var userLikesValue =  ["I have rated the bakers cake 4/5",
			"I rated the honey comb 2/5","I dislike this recipe! - Gateau a la Royale",
			"I recommend this recipe! - Carbonara Gnocchi ","I recommend this recipe! - Mushroom Mystique ",
			"I rated BLR Foody Site as 5/5","Commented on: BLR Foody","I rated the ice cream 3/5","I disliked this recipe! - Veg Noodles"];
	var userLikesObj = {};
	var userLikes=[];
	for ( var i = 0; i < (Math.floor(Math.random() * (6 - 3)) + 3); i++) {
		var likes = {};
		likes["AttrId"]=i;
		likes["AttrValue"]=userLikesValue[Math.floor(Math.random()*userLikesValue.length)];
		userLikes.push(likes);
	}
	userLikesObj["userBaseId"] = userBaseId;
	userLikesObj["socialMediaAttrTypeId"] = '1';
	userLikesObj["socialMediaAttrvalueList"] = userLikes;
	$.ajax({
		type: "POST",
		url: media,
		contentType: "application/json",
		data: JSON.stringify(userLikesObj),
		success: function(data){
			console.log(data);
			if(data.code == "501")
			{
				console.log("Error in sending data");
			}
			else{									
				console.log("User likes added successfully!");
				//window.opener.location.reload();
				//window.close();
				widget.reloadWidgets();
			}
		}
	});
}


/******************************************************************
 * 
 * 
 * 					FACEBOOK FUNCTION TO FETCH USER DETAILS
 * 					TO ACTIVATE SOCIAL ACCOUNT
 * 
 * 
 * ****************************************************************/


function getFBuserDetails(access_token) {
	var activatedata={};
	var socialMediaType = {};
	var userBaseId = '';
	var object = JSON.parse($.cookie("login_data"));
	userBaseId = object.userbase; 
	FB.api(
			"/me?fields=id,name,picture,email,friends",
			function(response) {
				if (response && !response.error) {
					//console.log("getPersonalProfileFB() response:");
					activatedata['socialEmail'] = response['email'];

					socialMediaType['userBaseId'] = userBaseId;
					socialMediaType['socialMediatypeId'] = '1';
					socialMediaType['socialMediaId'] = response['id'];
					socialMediaType['accessToken'] = access_token;
					socialMediaType['profilePicUrl'] = response['picture']['data']['url'];

					activatedata['socialMediaType']= socialMediaType;
					$.ajax({
						type: "POST",
						url: activateSocial,
						contentType: "application/json",
						data: JSON.stringify(activatedata),
						success: function(data) 
						{
							if(data.code == "501")
							{
								console.log("Error in sending data");
							}
							else
							{
								FB.api(
										"/me/friends",
										function(response) {
											if (response && !response.error) {
												var friends = response.data;
												var i;

												var friendListObj = {};
												var friendList=[];

												for (i = 0; i < friends.length; ++i) {
													var friend = {};
													friend["AttrId"]=friends[i].id;
													friend["AttrValue"]=friends[i].name;
													friendList.push(friend);
												}
												friendListObj["userBaseId"] = userBaseId;
												friendListObj["socialMediaAttrTypeId"] = '2';
												friendListObj["socialMediaAttrvalueList"] = friendList;
												$.ajax({
													type: "POST",
													url: media,
													contentType: "application/json",
													data: JSON.stringify(friendListObj),
													success: function(data){
														if(data.code == "501")
														{
															console.log("Error in sending data");
														}
														else
														{ 	
															insertUserLikes(userBaseId);
															allocatePoints(userBaseId, "803");
															setTimeout(function(){
																console.log("Called allocatePoints");
																//window.opener.location.reload();
																//window.close();
																//widget.reloadWidgets();
																window.location.reload();	
																
															}, 2000);
															
															/*FB.logout(function(response) {
															console.log("FB user logged out!");
															});*/
														}
														
													}
												});
											} else {
												console.log("getFriendListFB() Error Response:");
												console.log(response);
											}
										}
								);
							}
						}
					});
				}
				else{
					console.log("FB user not logged in!!");
				}
			}
	);
	

}


/******************************************************************
 * 
 * 
 * 					GOOGLE ACTIVATE FUNCTION
 * 
 * 
 * ****************************************************************/

function activateGoogle() {
	var config = {
		'client_id' : '264266712427-jt2585f5d9bd18vtac1imtdorrvivi5m.apps.googleusercontent.com',
		'scope' : 'email https://www.googleapis.com/auth/plus.login'
	};
	gapi.auth.authorize(config, function() {
		console.log('login complete');
		gapi.auth.checkSessionState(config, function(stateMatched) {
			if (stateMatched == false) {
				getGoogleuserDetails();/* Personal profile information */
				$("#LinkSocial").addClass("hidden");
				$("#Glinked").removeClass("hidden");
			} else {
				console.log("G+ user not logged in");
			}
		});

	});

}


/******************************************************************
 * 
 * 
 * 					FACEBOOK FUNCTION TO FETCH USER DETAILS
 * 					TO ACTIVATE SOCIAL ACCOUNT
 * 
 * 
 * ****************************************************************/

function getGoogleuserDetails() {
	var activatedata={};
	var socialMediaType = {};
	var userBaseId = '';
	var object = JSON.parse($.cookie("login_data"));
	userBaseId = object.userbase; 
	gapi.client.load('plus', 'v1', function() {

		gapi.client.plus.people.get({
			userId: 'me',
			collection: 'public'
		}).execute(handleEmailResponse);
		function handleEmailResponse(resp) {
			var email='';
			for (var i = 0; i < resp.emails.length; i++) {
				if (resp.emails[i].type === 'account'){
					email = resp.emails[i].value;
				}
			}
			activatedata['socialEmail'] = email;

			var a = gapi.auth.getToken();
			socialMediaType['userBaseId'] = userBaseId;
			socialMediaType['socialMediatypeId'] = '2';
			socialMediaType['socialMediaId'] = resp.id;
			socialMediaType['accessToken'] = a.access_token;
			socialMediaType['profilePicUrl'] = resp.image.url; 

			activatedata['socialMediaType']= socialMediaType;
			//console.log(JSON.stringify(activatedata));
			$.ajax({
				type: "POST",
				url: activateSocial,
				contentType: "application/json",
				data: JSON.stringify(activatedata),
				success: function(data) {
					if(data.code == "501")
					{
						console.log("Error in sending data");
					}
					else
					{
						console.log("postLoginData() Response:");
						//console.log(data);
						var request = gapi.client.plus.people.list({
							'userId' : 'me',
							'collection' : 'visible'
						});
						request.execute(function(resp) {
							var numItems = resp.items.length;
							var friendListObj = {};
							var friendList=[];
							for ( var i = 0; i < numItems; i++) {
								var friend = {};
								friend["AttrId"]=resp.items[i].id;
								friend["AttrValue"]=resp.items[i].displayName;
								friendList.push(friend);
								console.log(resp.items[i].displayName);
							}
							friendListObj["userBaseId"] = userBaseId;
							friendListObj["socialMediaAttrTypeId"] = '2';
							friendListObj["socialMediaAttrvalueList"] = friendList;
							console.log(JSON.stringify(friendListObj));
							$.ajax({
								type: "POST",
								url: media,
								contentType: "application/json",
								data: JSON.stringify(friendListObj),
								success: function(data){
									console.log(data);
									if(data.code == "501")
									{
										console.log("Error in sending data");
									}
									else{
										insertUserLikes(userBaseId);
										allocatePoints(userBaseId, "803");
										setTimeout(function(){
											console.log("Called allocatePoints");
										//	window.opener.location.reload();
											//window.close();
											widget.reloadWidgets();
										}, 2000);
									}
								}
							});
							
						});
					}
				}
			});
		}
	});
	//win = window.open("http://accounts.google.com/logout","something", "width=550,height=570");
	//setTimeout("win.close();", 1000);
}


/**************************************************************
 * 
 * 		FETCH ALL USER ACTIVITY
 * 
 * ************************************************************/

function alluserActivity(){
	
	$.ajax({
		type: "POST",
		url: allUserActivityURL,
		contentType: "application/json",
		success: function(data) 
		{
			
			$("#everyonelistloading").hide();
			$("#friendListloading").hide();
			$("#myactivitylistloading").hide();
			console.log(data);
			if(data.code == "501")
			{
				$("#everyoneactivity").text("Error!!");
			}
			else{
				console.log("All user activity");
				console.log(data);
				var likesContainer = $("div#everyoneactivity");
	        	if(data.object.userReview.length <= 0){
	        		$("#everyoneactivity").text("No user activity!");
	        	}
	        	else{
	        		console.log(data);
	        		for(var i=0; i<data.object.userReview.length;i++)
	        		{
	        			if(data.object.userReview[i].userComment != null)
	        			{
	        				
	    					var divUserActivity = $("<div/>").attr("class","userActivity");
	    	        		var usrImg = $("<img/>").attr("src","images/user.png");
	    	        		var usrName =$("<p style='font-weight:bold'/>");
	    	        		var usractivity =$("<p />");
	    	        		var lnBrk = $("<br/>");
	    	        		var contentTitle = $("<span style='font-weight:bold'/>"); 
	    	        		var content = $("<span/>");
	    	        		var clearDiv = $("<div/>").attr("class","clear");
	    	        		
	    	        		usrImg.attr("src",data.object.userReview[i].userAvatar);
	    					usrImg.attr("width","32");
	    					usrName.text(data.object.userReview[i].userName);
	    					if(data.object.userReview[i].typeId == 1){
	    						usractivity.text(" Reviewed");
	    					}
	    					else{
	    						usractivity.text(" Commented");
	    					}
	    					if(data.object.userReview[i].commentTitle){
	    						contentTitle.text(data.object.userReview[i].commentTitle+" : ");
	    					}
	    					content.text(data.object.userReview[i].userComment);
	    					
	    					divUserActivity.append(usrImg);
	    					divUserActivity.append(usrName);
	    					divUserActivity.append(usractivity);
	    					divUserActivity.append(lnBrk);
	    					divUserActivity.append(contentTitle);
	    					divUserActivity.append(content);
	    					likesContainer.append(divUserActivity);
	    					likesContainer.append(clearDiv);
	    					//insertUserLikes();
	        			}   	
	        		}
	        	}
				//console.log(data);
			}
			
			
			
			if(!$.cookie("login_data")){
				
				$("#friendslikes").text("Please login to view your friends activity");
				$("#myactivity").text("Please login");

			} else if($.cookie("login_data")) {
				$("#friendslikes").text("");
				$("#myactivity").text("");
				refreshUserFriendsActivity();
				//display();
			}
			
		}
		
	});
	
	
	
	
}

function refreshUserFriendsActivity() {
	var object =JSON.parse($.cookie("login_data"))
	displayUserActivity(object.userbase);
	if(object.email){
		$.ajax({
			type: "GET",
			url: baseURL + object.email + "/friends",
			success: function(data){
				$("#friendListloading").hide();
				if(data.code == "501")
				{
					console.log("Error in sending data");
				}
				else
				{
					displayFriendsActivity(data);
				}

			}
		});
	}
}

/**************************************************************
 * 
 * 		ONLOAD FUNCTIONS TO CHECK LOGIN STATUS
 * 
 * ************************************************************/
window.onload = function()
{
	
	//alluserActivity();
	
	
	/*$('.glyphicon-menu-down').on('click', function() {
		$(this).parent().next().toggle('hide');
	});*/
	//intialiseGamification();
	//console.log($.cookie("login_data"));
	if($.cookie("login_data")){
		console.log("im here");
		var object = JSON.parse($.cookie("login_data"));
		console.log("in onload:"+object);
		var now = new Date();
		var current = new Date(now.getTime());
		console.log(object.expires.toString());
		console.log(current.toString());
		console.log((object.expires.toString() > current.toString()));
		console.log((object.expires.toString() <= current.toString()));
		if((object.expires.toString() > current.toString())){
			if(object.social == "google"){
				OnLoadCallback();
			}
			else if(object.social == "facebook"){
				getUserProfileName();
			}
			else if(object.social == "register"){
				RegLoginStatus();
			}
		}
		else if(object.expires.toString() <= current.toString()){
			if(object.social == "google"){
				doGoogleLogout();
			}
			else if(object.social == "facebook"){
				doLogout();
			}
			else if(object.social == "register"){
				RLogout();
			}
		}
	}
	else{
		//$.removeCookie();
		$("#login").removeClass('hidden');
		$("#friendslikes").text("Please login to view your friends activity");
		$("#myactivity").text("Please login");
	
	}
};




/***************************************************************
 * 
 * 
 * 					GOOGLE LOGIN FUNCTION
 * 
 * 
 * *************************************************************/
function Googlelogin() {
	var config = {
			'client_id': '264266712427-jt2585f5d9bd18vtac1imtdorrvivi5m.apps.googleusercontent.com',
			'scope': 'email https://www.googleapis.com/auth/plus.login'
	};
	gapi.auth.authorize(config, function() {
		var a = gapi.auth.getToken();
		console.log('login complete');
		console.log(a);
		console.log(a.access_token);
		gapi.auth.checkSessionState(config, function(stateMatched) 
				{ 
			if (stateMatched == false) {
				
				$('#login_modal').modal('hide');
				var now = new Date();
				/* one hour = 1000*3600 , one minute = 1000*60 */
				var time = new Date(now.getTime() + (1000*60*60));

				console.log(time.toString());
				login_data['expires'] = time.toString();
				login_data['social'] = "google";
				login_data['profilepic'] = "";
				login_data['email'] = "";
				$.cookie("login_data",JSON.stringify(login_data));
				getPersonalProfileGoogle();/*Personal profile information*/
				$('#login').addClass("hidden");
				$('#loggedIn').removeClass('hidden');
			}
			else{
				console.log("G+ user not logged in");
			}
				});

	});
}



/******************************************************************
 * 
 * 
 * 					FACEBOOK LOGIN FUNCTION
 * 
 * 
 * ****************************************************************/
function doFBLogin() {
	var access_token = "";
	FB.getLoginStatus(function(response) {
		if (response.status === 'connected') {
			var now = new Date();
			/* one hour = 1000*3600 , one minute = 1000*60 */
			var time = new Date(now.getTime() + (1000*60*60));
			
			login_data['expires'] = time.toString();
			login_data['social'] = "facebook";
			login_data['profilepic'] = "";
			login_data['email'] = "";
			$.cookie("login_data",JSON.stringify(login_data));
			$('#login_modal').modal('hide');
			$('#login').addClass('hidden');
			$('#loggedIn').removeClass('hidden');
			getPersonalProfileFB(response['authResponse']['accessToken']);
			
			//window.location.reload();
			//getFBuserDetails(response['authResponse']['accessToken']);
			//getUserProfileName(), 10000);
		} else {
			console.log("User not logged in! Initiating FB login...");
			FB.login(
					function(response) {
						console.log("After FB login...");
						access_token = response.authResponse.accessToken;
						var now = new Date();
						/* one hour = 1000*3600 , one minute = 1000*60 */
						var time = new Date(now.getTime() + (1000*60*60));
						console.log(time.toString());
						login_data['expires'] = time.toString();
						login_data['social'] = "facebook";
						login_data['profilepic'] = "";
						login_data['email'] = "";
						$.cookie("login_data",JSON.stringify(login_data));
						
						$('#login_modal').modal('hide');
						$('#login').addClass('hidden');
						$('#loggedIn').removeClass('hidden');
						getPersonalProfileFB(access_token);
						//getFBuserDetails(access_token);
						//getUserProfileName();
						//getUserProfileName();
						/*Personal profile information*/
						//window.location.reload();
					},
					{
						scope: 'email,user_friends,public_profile'
					}
			);
		}
	});
}



/*****************FUNCTIONS TO FETCH THE USER DETAILS AND DUMP INTO DB****************/


/***************************************************************************************
 * 
 * 			Function to fetch the facebook user details and perform a REST service 
 * 			call to dump the data into the database
 * 
 ***************************************************************************************/
function getPersonalProfileFB(access_token) {
	var userBaseId;
	FB.api(
			"/me?fields=id,name,picture,email,friends",
			function(response) {
				if (response && !response.error) {
					var FBdata = {};
					var mediaData = {};
					/*var Email = {};
					var primaryEmail = [];*/
					
					// emailIdList change
					var emailIdList = [];
					emailIdList.push(response['email']);
					
					/*Email['emailId'] = response['email'];*/
					FBmail = response['email'];
					/*primaryEmail.push(Email);*/
					FBdata['userBaseId']= '';
					FBdata['name'] = response['name'];
					name = response['name'];
					FBdata['emailIdList'] = emailIdList/*primaryEmail*/;
					FBdata['password']='';
					FBdata['gender']='';
					FBdata['yearOfBirth']='1995';
					$.ajax({
						type: "POST",
						url: loginAuditService,
						contentType: "application/json",
						data: JSON.stringify(FBdata),
						success: function(data) 
						{
							if(data.code == "501")
							{
								console.log("Error in sending data");
							}
							else
							{
								mediaData['userBaseId'] = data.object;
								mediaData['socialMediatypeId'] = '1';
								mediaData['socialMediaId'] = response['id'];
								mediaData['accessToken'] = access_token;
								mediaData['profilePicUrl'] = response['picture']['data']['url'];
								
								
								allocatePoints(data.object, "801");
								
								var logincookie = JSON.parse($.cookie("login_data"));
								
								logincookie.username = name;
								$("#user").text(name);
								logincookie.userbase = data.object;
								logincookie.email = response['email'];
								if(!logincookie.profilepic) {
									logincookie.profilepic = response['picture']['data']['url'];
									$("#FBprofilepicture").attr("src",logincookie.profilepic);
									$.cookie("login_data",JSON.stringify(logincookie));
								}
								$.ajax({
									type: "POST",
									url: mediaType,
									contentType: "application/json",
									data: JSON.stringify(mediaData),
									success: function(data){
										console.log(data);
										if(data.code == "501")
										{
											console.log("Error in sending data");
										}
									}
								});
								FB.api(
										"/me/friends",
										function(response) {
											if (response && !response.error) {
												var friends = response.data;
												var i;

												var friendListObj = {};
												var friendList=[];

												for (i = 0; i < friends.length; ++i) {
													var friend = {};
													friend["AttrId"]=friends[i].id;
													friend["AttrValue"]=friends[i].name;
													friendList.push(friend);
												}
												userBaseId = data.object;
												friendListObj["userBaseId"] = data.object;
												friendListObj["socialMediaAttrTypeId"] = '2';
												friendListObj["socialMediaAttrvalueList"] = friendList;
												$.ajax({
													type: "POST",
													url: media,
													contentType: "application/json",
													data: JSON.stringify(friendListObj),
													success: function(data){
														if(data.code == "501")
														{
															console.log("Error in sending data");
														}
														else
														{ 	
															window.location.reload();
															insertUserLikes(userBaseId);
															
														}
													}
												});
											} else {
												console.log("getFriendListFB() Error Response:");
												console.log(response);
											}
										}
								);
							}
						}
					});

				} else {
					console.log(response);
				}
			}
	);

}


/***************************************************************************************
 * 
 * 			Function to fetch the Google plus user details and perform a REST service 
 * 			call to dump the data into the database
 * 
 ***************************************************************************************/

function getPersonalProfileGoogle() {
	var googledata = {};
	var mediaData = {};
	var userBaseId;
	var email = '';
	gapi.client.load('plus', 'v1', function() {

		gapi.client.plus.people.get({
			userId: 'me',
			collection: 'public'
		}).execute(handleEmailResponse);
		function handleEmailResponse(resp) {
			/*var Email = {};
			var primaryEmail = [];*/
			var emailIdList = [];
			for (var i = 0; i < resp.emails.length; i++) {
				if (resp.emails[i].type === 'account'){
					/*Email['emailId'] = resp.emails[i].value;*/
					email = resp.emails[i].value;
				}
			}
			name = resp.displayName;
			
			emailIdList.push(email);
			
			/*primaryEmail.push(Email);*/
			googledata['userBaseId']= '';
			googledata['name'] = resp.displayName;
			googledata['emailIdList'] = emailIdList/*primaryEmail;*/;
			googledata['password']=''; 
			googledata['gender']=resp.gender;
			console.log(resp.gender);
			googledata['yearOfBirth']='1996';
			$.ajax({
				type: "POST",
				url: loginAuditService,
				contentType: "application/json",
				data: JSON.stringify(googledata),
				success: function(data) {
					if(data.code == "501")
					{
						console.log("Error in sending data");
					}
					else
					{

						var a = gapi.auth.getToken();
						mediaData['userBaseId'] = data.object;
						mediaData['socialMediatypeId'] = '2';
						mediaData['socialMediaId'] = resp.id;
						mediaData['accessToken'] = a.access_token;
						mediaData['profilePicUrl'] = resp.image.url;
						
						allocatePoints(data.object, "801");
						
						var logincookie = JSON.parse($.cookie("login_data"));
						logincookie.username = name;
						$("#user").text(name);
						logincookie.userbase = data.object;
						logincookie.email = email;
						if(!logincookie.profilepic){
							logincookie.profilepic = resp.image.url;
							$("#FBprofilepicture").attr("src",logincookie.profilepic);
							$.cookie("login_data",JSON.stringify(logincookie));
						}	
						$.ajax({
							type: "POST",
							url: mediaType,
							contentType: "application/json",
							data: JSON.stringify(mediaData),
							success: function(data){
								console.log(data);
								if(data.code == "501")
								{
									console.log("Error in sending data");
								}
							}
						});
						var request = gapi.client.plus.people.list({
							'userId' : 'me',
							'collection' : 'visible'
						});
						request.execute(function(resp) {
							var numItems = resp.items.length;
							var friendListObj = {};
							var friendList=[];
							for ( var i = 0; i < numItems; i++) {
								var friend = {};
								friend["AttrId"]=resp.items[i].id;
								friend["AttrValue"]=resp.items[i].displayName;
								friendList.push(friend);
							}
							userBaseId = data.object;
							friendListObj["userBaseId"] = data.object;
							friendListObj["socialMediaAttrTypeId"] = '2';
							friendListObj["socialMediaAttrvalueList"] = friendList;
							$.ajax({
								type: "POST",
								url: media,
								contentType: "application/json",
								data: JSON.stringify(friendListObj),
								success: function(data){
									console.log(data);
									if(data.code == "501")
									{
										console.log("Error in sending data");
									}
									else{
										insertUserLikes(userBaseId);
									}
								}
							});
							
						});
					}
				}
			});

		}


	});

}

/******************************LOGIN STATUS FUNCTIONS**********************************/


/***************************************************************************************
 * 
 * 			
 * 				Function to display user activity 
 * 			
 * 
 ***************************************************************************************/

function displayUserActivity(userBaseId){
	console.log("Inside displayUserActivity()");
	$.ajax({
		type: "POST",
		url: UserActivityURL,
		contentType: "text/plain",
		data: userBaseId,
		success: function(data) 
		{
			console.log("displayUserActivity() : "+ data);
			$("#myactivitylistloading").hide();
			if(data.code == "501")
			{
				$("#myactivity").text("No data to display");
			}
			else{
				console.log(data);
				var likesContainer = $("div#myactivity");
	        	if(data.object == null){
	        		$("#myactivity").text("No data to display");
	        	}
	        	else{
	        		for(var i=0; i<data.object.length;i++)
	        		{
	        			if(data.object[i].userComment != null)
	        			{
	        				
	    					var divUserActivity = $("<div/>").attr("class","userActivity");
	    	        		var usrImg = $("<img/>").attr("src","images/user.png");
	    	        		var usrName =$("<p style='font-weight:bold'/>");
	    	        		var usractivity =$("<p />");
	    	        		var lnBrk = $("<br/>");
	    	        		var contentTitle = $("<span style='font-weight:bold'/>"); 
	    	        		var content = $("<span/>");
	    	        		var clearDiv = $("<div/>").attr("class","clear");
	    	        		
	    	        		usrImg.attr("src",data.object[i].userAvatar);
	    					usrImg.attr("width","32");
	    					usrName.text(data.object[i].userName);
	    					if(data.object[i].typeId == 1){
	    						usractivity.text(" Reviewed");
	    					}
	    					else{
	    						usractivity.text(" Commented");
	    					}
	    					if(data.object[i].commentTitle){
	    						contentTitle.text(data.object[i].commentTitle+" : ");
	    					}
	    					content.text(data.object[i].userComment);
	    					
	    					divUserActivity.append(usrImg);
	    					divUserActivity.append(usrName);
	    					divUserActivity.append(usractivity);
	    					divUserActivity.append(lnBrk);
	    					divUserActivity.append(contentTitle);
	    					divUserActivity.append(content);
	    					likesContainer.append(divUserActivity);
	    					likesContainer.append(clearDiv);
	    					
	        			}   	
	        		}

	        	}
			}
		}
	});
}






/***************************************************************************************
 * 
 * 			
 * 				Function to display user friends activity  
 * 			
 * 
 ***************************************************************************************/

function displayFriendsActivity(data){
	
  	console.log("Inside displayFriendsActivity()");
  	console.log(data);
  	var activity = 0;
	if(data.object.friendsLikesList.length < 0){
		$("#friendslikes").text("None of your friends are active");
	}
	else{
		var likesContainer = $("div#friendslikes");
	
		for(var i=0; i<data.object.friendsLikesList.length;i++)
		{
			if(data.object.friendsLikesList[i].socialMediaAttrvalueList != null)
			{
				for(var j=0; j<data.object.friendsLikesList[i].socialMediaAttrvalueList.length;j++){
				
					var divUserActivity = $("<div/>").attr("class","userActivity");
					var usrImg = $("<img/>").attr("src","images/user.png");
					var usrName =$("<p style='font-weight:bold'/>");
	        		var lnBrk = $("<br/>");
	        		var content = $("<span/>");
					var clearDiv = $("<div/>").attr("class","clear");
					
					usrImg.attr("src",data.object.friendsLikesList[i].profile_pic_url);
					usrImg.attr("width","32");
					usrName.text(data.object.friendsLikesList[i].name);

					content.text(data.object.friendsLikesList[i].socialMediaAttrvalueList[j].AttrValue);
					
					divUserActivity.append(usrImg);
					divUserActivity.append(usrName);
					divUserActivity.append(lnBrk);
					divUserActivity.append(content);
					likesContainer.append(divUserActivity);
					likesContainer.append(clearDiv);
					activity++;
					
				}	
			}
		}
		if(0 == activity){
			$("#friendslikes").text("None of your friends are active");
		}
	}
	
}





/***************************************************************************************
 * 
 * 			Function to check the login status of facebook and display the 
 * 			friends of the user and their likes(which are fetched from the DB).
 * 
 ***************************************************************************************/


function getUserProfileName() {
	
	var login_cookie = '';
	if($.cookie("login_data")){
		login_cookie  = JSON.parse($.cookie("login_data"));
	}
	else {
		console.log("FB User not logged in!");
		$("#login").removeClass( 'hidden' );
		$("#friendlist").hide();
		//RegLoginStatus();
	}
	
//	alert(JSON.stringify(login_cookie));
	var primaryEmail = login_cookie.email;
	$("#loggedIn").removeClass( 'hidden' );
	$("#reactions").removeClass( 'hidden' );
	$("#login").addClass( 'hidden' );
	$("#friendlist").show();
	$("#user").text(login_cookie.username +" ");
	if(primaryEmail){
		$.ajax({
			type: "GET",
			url: baseURL + primaryEmail + "/friends",
			success: function(data){
				$("#friendListloading").hide();
				if(data.code == "501")
				{
					console.log("Error in sending data");
				}
				else
				{
					displayFriendsActivity(data);
				}

			}
		});
	}

	$("#FBprofilepicture").attr("src",login_cookie.profilepic);
	if(!login_cookie.profilepic){
		login_cookie.profilepic = login_cookie.profilepic;
		$.cookie("login_data",JSON.stringify(login_cookie));
	}						
	userBaseId = login_cookie.userbase;
	if(userBaseId){
		displayUserActivity(userBaseId);
	}
}





/***************************************************************************************
 * 
 * 			Function to check the login status of google plus and display the 
 * 			friends of the user and their likes(which are fetched from the DB).
 * 
 ***************************************************************************************/

function OnLoadCallback() {
	var login_cookie = '';
	if($.cookie("login_data")){
		login_cookie  = JSON.parse($.cookie("login_data"));
	}
	else {
		console.log("G+ User not logged in!");
		$("#login").removeClass( 'hidden' );
		$("#friendlist").hide();
	}
	var primaryEmail = login_cookie.email;
	var userBaseId = '';
	$("#loggedIn").addClass( 'hidden' );
	$("#login").addClass( 'hidden' );
	$("#GoogleloggedIn").removeClass( 'hidden' );
	$("#reactions").removeClass( 'hidden' );
	$("#friendlist").show();
	$("#Guser").text(login_cookie.username +" ");
	if(primaryEmail){
		$.ajax({
			type: "GET",
			url:baseURL + primaryEmail + "/friends",
			success: function(data){
				$("#friendListloading").hide();
				if(data.code == "501")
				{
					console.log("Error in sending data");
				}
				else
				{
					console.log("friends data");
					console.log(data);
					displayFriendsActivity(data);
				}

			}
		});
	}
	$("#Gprofilepicture").attr("src",login_cookie.profilepic);
	if(!login_cookie.profilepic){
		login_cookie.profilepic = login_cookie.profilepic;
		$.cookie("login_data",JSON.stringify(login_cookie));
	}
	userBaseId = login_cookie.userbase;

	if(userBaseId){
		displayUserActivity(userBaseId);
	}
} 


/***************************************************************************************
 * 
 * 			Function to check the social media linked by the user
 * 
 ***************************************************************************************/

function checkSocialLinked(userBaseId){
	
	$.ajax({
		type: 'POST',
		contentType: 'text/plain',
		url: checkregistereduser,
		data: userBaseId,
		success: function(data){
			if(data.code == "501"){
				console.log("Error occured during the operation! please try again");
			}
			else if(data.code == "901"){
				$("#fblinked").removeClass( 'hidden' );
				$("#FBlinkedtext").text(" Linked");
			}
			else if(data.code == "902"){
				$("#Glinked").removeClass( 'hidden' );
				$("#Glinkedtext").text(" Linked");
			}
			else if(data.code == "903"){
				$("#LinkSocial").removeClass( 'hidden' );
				$("#friendslikes").text("None of your friends are active");
				$("#myactivity").text("No data to display");
			}
			
		}
	});
	
	
	
}


/***************************************************************************************
 * 
 * 			Function to fetch the profile pic of the user
 * 
 ***************************************************************************************/

function fetchUserProfilePic(userBaseId){
	var picSrc ="";
	$.ajax({
		type: 'POST',
		contentType: 'text/plain',
		url: fetchProfilePic,
		data: userBaseId,
		success: function(data){
			if(data.code == "501"){
				console.log("Error occured during the operation! please try again");
			}
			else if(data.code == "500"){
				console.log(data);
				console.log($("#Rprofilepicture"));
				if(data.object){
					console.log("Reg profile :"+ data.object);
					picSrc = profilepicBaseURL + data.object;
					$("#Rprofilepicture").attr("src",picSrc);
					userAvatar = picSrc;
					
					// Added to bring up the avatar on review and comment plugin
					//$('#reviewAvatar').attr('src', picSrc);
					
					
					var loginCookie = JSON.parse($.cookie("login_data"));
					if(!loginCookie.profilepic){
						loginCookie.profilepic = picSrc;
						$.cookie("login_data",JSON.stringify(loginCookie));
					}
				}
				else{
					picSrc = "img/Avatar_empty_x1.png";
					$("#Rprofilepicture").attr('src',picSrc);
					var loginCookie = JSON.parse($.cookie("login_data"));
					if(!loginCookie.profilepic){
						loginCookie.profilepic = picSrc;
						$.cookie("login_data",JSON.stringify(loginCookie));
					}
					
				}
			}	
		}
	});
	
}




/***************************************************************************************
 * 
 * 			Function to check the login status of the registered user. 
 * 
 ***************************************************************************************/

function RegLoginStatus() {
	var object = JSON.parse($.cookie("login_data"));

	$("#Ruser").text(object.username +" ");
	$("#RloggedIn").removeClass( 'hidden' );
	$("#comment-share").removeClass('hidden');
	$("#reactions").removeClass('hidden');
	
	
	var RegEmail = object.email;
	var userBaseId = object.userbase;
	
	if(userBaseId){
		checkSocialLinked(userBaseId);
		fetchUserProfilePic(userBaseId);
		displayUserActivity(userBaseId);

	}
	if(RegEmail){
	$.ajax({
        type: "GET",
        url:baseURL + RegEmail + "/friends",
        success: function(data){
        	$("#friendListloading").hide();
        	if(data.code == "501")
			{
				console.log("Error in sending data");
			}
        	else
        	{
        		displayFriendsActivity(data);
        	}
        	
       }
    });
	}
	else{
		$("#friendslikes").innerHTML("<p>No data to display</p>");
		$("#myactivity").innerHTML("<p>No data to display</p>");
	}
}



/*************************FUNCTIONS TO LOGOUT THE USERS********************/

/**************************************************************************
 * 
 * Function to logout the facebook user and update the status in DB
 * 
 **************************************************************************/
function doLogout(){

	var object = JSON.parse($.cookie("login_data"));
	console.log("COOKIE: ");
	console.log(object.userbase.userbaseId);
	var FBdata = {};
	/*var Email = {};
	var primaryEmail = [];
	Email['emailId'] = object.email;
	primaryEmail.push(Email);*/
	var emailIdList = [];
	emailIdList.push(object.email);
	/*FBdata['userBaseId']= '';*/
	FBdata['userBaseId']= object.userbase.userbaseId;
	FBdata['name'] = '';
	FBdata['emailIdList'] = emailIdList/*primaryEmail*/;
	FBdata['password']='';
	console.log(FBdata);
	$.ajax({
		type: "POST",
		url: logoutService,
		contentType: "application/json",
		data: JSON.stringify(FBdata),
		success: function(data) {
			console.log("postData() Response:");
			console.log(data);
			if(data.code == "501")
			{
				console.log("Error in sending data");
			}
			else{
				$("#loggedIn").addClass( 'hidden' );
				$("#login").removeClass( 'hidden' );
				$("#friendlist").hide();
				
				console.log('Coookie Details::'+JSON.stringify($.cookie('login_data')));
				FB.logout(function(response) {
					FB.Auth.setAuthResponse(null,'unknown');
				});
				
				$.removeCookie('login_data', null,   {path:'/CamZone'});
				//userBaseId = null;
				//widget.reloadWidgets();
				window.location.reload();
			}
		}
	});
}

/**************************************************************************
 * 
 * Function to logout the Google Plus user and update the status in DB
 * 
 **************************************************************************/

function doGoogleLogout()
{
	var googledata = {};
	/*var Email = {};
	var primaryEmail = [];*/
	var object = JSON.parse($.cookie("login_data"));
	/*Email['emailId'] = object.email;
	primaryEmail.push(Email);*/
	var emailIdList = [];
	emailIdList.push(object.email);
	/*googledata['userBaseId']= '';*/
	googledata['userBaseId']= object.userbase.userbaseId;
	googledata['name'] = '';
	googledata['emailIdList'] = emailIdList/*primaryEmail*/;
	googledata['password']='';
	$.ajax({
		type: "POST",
		url: logoutService,
		contentType: "application/json",
		data: JSON.stringify(googledata),
		success: function(data){
			//console.log(data);
			if(data.code == "501")
			{
				console.log("Error in sending data");
			}
			else{
				gapi.auth.signOut();
				$("#GoogleloggedIn").addClass('hidden');
				$("#login").removeClass('hidden');
				$("#friendlist").hide();
				$.removeCookie('login_data', null, {path:'/CamZone'});
				//userBaseId = null;
				//widget.reloadWidgets();
				window.location.reload();
			}
		}
	});

}

/**************************************************************************
 * 
 * Function to logout the Registered user.
 * 
 **************************************************************************/

function RLogout(){
	
	var userdata = {};
	var object = JSON.parse($.cookie("login_data"));
	console.log("In RLogout:"+object);
	/*var Email = {};
	Email['emailId'] = object.email;
	var primaryEmail = [];
	primaryEmail.push(Email);*/
	var emailIdList = [];
	emailIdList.push(object.email);
	/*userdata['userBaseId']= '';*/
	//googledata['userBaseId']= object.userbase,userbaseId;
	userdata['userBaseId'] = object.userbaseid;
	userdata['name'] = '';
	userdata['emailIdList'] = emailIdList/*primaryEmail*/;
	userdata['password']='';
	if(object.email){
	$.ajax({
		type: "POST",
		url: logoutService,
		contentType: "application/json",
		data: JSON.stringify(userdata),
		success: function(data){
			if(data.code == "501")
			{
				console.log("Error in sending data");
			}
			else if(data.code == "500") {
				$("#RloggedIn").addClass( 'hidden' );
				$("#LinkSocial").addClass( 'hidden' );
				$("#fblinked").addClass( 'hidden' );
				$("#Glinked").addClass( 'hidden' );
				$("#login").removeClass('hidden');
				$("#comment-share").addClass('hidden');
				//console.log('Coookie Details::'+JSON.parse($.cookie('login_data')));
				//var dataTemp = JSON.parse($.cookie('login_data'));
				//alert(dataTemp.email);
				$.removeCookie('login_data', null, {path:'/CamZone'});
				//userBaseId = null;
				//widget.reloadWidgets();
				window.location.reload();
			}
		}	
	});
	}
	else{
		$("#RloggedIn").addClass( 'hidden' );
		$("#LinkSocial").addClass( 'hidden' );
		$("#fblinked").addClass( 'hidden' );
		$("#Glinked").addClass( 'hidden' );
		$("#login").removeClass('hidden');
	}
}



/*************************Reactions Code************************/
	
	
function getReactionsList(){
	
	
	var temp = getPageId();
	var temp1 = temp.split("/");
	reactionURL = reactionBaseURL + temp1[2];
	reactionUserURL = reactionURL + "/";
	$("#reactions").empty();
	if(reactionList){
		getPageReaction(reactionList);
	}
	else{
		console.log("reactionList is null, fetching from DB...");
		$.ajax({
			type: 'GET',
			contentType: 'text/plain',
			url: reactionListURL,
			data: '',
			success: function(data){
				if(data.code == "501"){
					console.log("Error in receiving the data");
				}
				else if(data.code == "500"){
					console.log("In getReactionsList:");
					console.log(data);
					reactionList = data;
					getPageReaction(data);
				}
			}
		});		
	}
}

function getPageReaction(reactionsList){
	
	//alert(JSON.stringify(reactionsList));
	console.log("ReactionLists ::"+JSON.stringify(reactionsList))
	$.ajax({
		type: 'GET',
		contentType: 'text/plain',
		url: reactionURL,
		data: '',
		success: function(data){
			if(data.code == "501"){
				console.log("Error in receiving the data");
			}
			else if(data.code == "500"){
				console.log("In getPageReaction");
				console.log(data);
				//alert("Success");
				populateReactData(data,reactionsList);
				reactionsData = data;
				if($.cookie("login_data")){
					var login_cookie = JSON.parse($.cookie("login_data"));
					if((login_cookie) && (login_cookie.userbase)) {	
						userId = login_cookie.userbase;
						getUserReaction(userId);
					}
					else{
						console.log("Page Reaction : No user session exists");
					}
				}
			}
		}
	});
}

function getPageId() {
	var url = window.location.href;
    var splitUrl = url.split("#");
    var getCommentId = splitUrl[1].split("/");
    commentId = decodeURI((getCommentId[2]));
    temp = decodeURI(splitUrl[1]);
    return temp;
}

function react(current) {
	
	var userReaction = {
			"reactId" : current
	};
	var user = null;
	
	console.log(JSON.stringify(userReaction));
	if($.cookie("login_data")){
		var login_cookie = JSON.parse($.cookie("login_data"));
		if((login_cookie) && (login_cookie.userbase)){	
			user = login_cookie.userbase;
		}
	}
	if(user != null) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : reactionUserURL + user,
		data : JSON.stringify(userReaction),
		success : function(data) {
			if (data.code == "501") {
				console.log("Error is receiving the data");
			} else if (data.code == "500") {
				allocatePoints(userId,"804");
				setTimeout(function(){
					$("#reactions").empty();
					//getReactionsList();
					widget.reloadWidgets();
					}, 1000);
				
			}
		}
	});
	}
	else {
		alert("Please login to express your reaction!!");
	}
}

function getUserReaction(user) {
	
	$('#userReaction').empty();
	//console.log("getUserReaction() : "+userId);
	$.ajax({
		type : 'GET',
		contentType : 'text/plain',
		url : reactionUserURL + user,
		data : '',
		success : function(data) {
			if (data.code == "501") {
				console.log("Error is receiving the data");
			} else if (data.code == "500") {
				console.log("GetUserReaction");
				console.log(data);
				console.log("reactionsData");
				console.log(reactionsData);

				var cList = $('ul.ulreact');
				for ( var i = 0; i < reactionsData.object.length; i++) {
					if (data.object.reactId === reactionsData.object[i].reactId) {
						/*alert($('#userReaction').length)*/
						$("#userReaction").html(
								"You reacted: "
								+ reactionsData.object[i].reactName)
								.appendTo(cList);
					}
				}
			}
		}
	});
}


function populateReactData(pageReactDatap, reactionsListp) {
	$("#reactions").empty();
	var pageReactData = pageReactDatap.object;
	var reactionsList = reactionsListp.object;
	var ul = $('<ul/>').attr("class", "ulreact");


	for ( var i = 0; i < reactionsList.length; i++) {

		var reactName = reactionsList[i].reactName; 
		var reactId = reactionsList[i].reactId;
		var reactUrl = reactionsList[i].reactUrl;
		var reactCount = 0;

		for ( var j = 0; j < pageReactData.length; j++) {

			if(reactionsList[i].reactId == pageReactData[j].reactId){
				reactCount = pageReactData[j].reactCount;
			}
		}

		var li = $('<li/>').appendTo(ul);
		var rlink = $('<a/>')
		.attr('href', "javascript:;")
		.attr('class',"rCont")
		.attr('title', reactName)
		.attr('onClick', "return react('" + reactId + "')")
		.appendTo(li);
		var img = $('<img/>')
		.attr('width', '30')
		.attr('src',reactUrl)
		.appendTo(rlink);
		var lbl = $('<label/>')
		.attr("id","react-" + reactId)
		.text(reactCount)
		.appendTo(rlink);

	}
	
	$("#reactions").append(ul);
	$("#reactions").append("<div id='userReaction'></div>")
	
}