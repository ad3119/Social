function includeRequiredSrc() {
	var headElem = document.getElementsByTagName('head').item(0);
	var cssFile = document.createElement('link');
	cssFile.rel = "stylesheet";
	cssFile.href = "Css/jRating.jquery.css";
	headElem.appendChild(cssFile);
	var cssFile2 = document.createElement('link');
	cssFile2.rel = "stylesheet";
	cssFile2.href = "Css/ratingNreview.css";
	headElem.appendChild(cssFile2);
};

var baseRestUrl = socialPluginConfig.baseURL;
var homePage = socialPluginConfig.homeURL;
var getRatingUrl= baseRestUrl+"getRating";
var writeReviewUrl=baseRestUrl+"updateReview";
var readReviewUrl=baseRestUrl+"readReview";
var loginPage=homePage+"login.html";
var AllocatePointsURL = baseRestUrl+"allocatepoint";

var userRating = 0;	
var userName = "Guest";
/*var userAvatar = "images/jrating/Avatar_empty_x1.png";*/
var socialMediaAttrName = "NONE";
var cmntIndex = 0;
var cmntSetSize = 5;

/*var userBaseId = null;*/

var pageUrl="";
var commmentId="";
	
function initiliseReviewAndRating() {
	
	userReviewObjsforRnr = null;
	//var isUserLoggedIn = false;
	cmntIndex = 0;
	//checkUserSession();
    var url = window.location.href;
    var splitUrl = url.split("#");
    var getCommentId = splitUrl[1].split("/");
    commentId = decodeURI((getCommentId[2]));
    pageUrl = getCommentId[2];
    var rateObj={};
    //includeRequiredSrc();
    var showOverallratingElem = document.getElementById('show-overall-rating');
    if(null != showOverallratingElem)
    {
    	
           console.log("OverallRating div is present");
           showOverallRating(showOverallratingElem);
    }
    else
    {
           console.log("OverallRating div is missing");
    }
    
    var showUserRatingElem = document.getElementById('show-user-rating');
    if(null != showUserRatingElem)
    {
           $("#show-user-rating").empty();
    		console.log("show-user-rating div is present!");
           loadReviews(showUserRatingElem);
    }
    else
    {
           console.log("show-user-rating div is not present");
    }
    
    var showRateBoxElem = document.getElementById('show-rate-comnts-box');
    if(null != showRateBoxElem)
    {
    	
           var needRnRUI = true;
           var limitRatingToOne = showRateBoxElem.getAttribute("data-limitratingtoone");
           if(limitRatingToOne){
                  if("YES"==limitRatingToOne.toUpperCase()) {
                	 if((userReviewObjsforRnr!=null) && (userReviewObjsforRnr.length != null)) {
                		for(var objCnt=0; objCnt < userReviewObjsforRnr.length; ++objCnt) {
                        	if(userReviewObjsforRnr[objCnt] != undefined) {
                               if((userBaseId)&& (userBaseId == userReviewObjsforRnr[objCnt].userBaseId)) {
                                    needRnRUI = false;
                                    $("#show-rate-comnts-box").empty().hide();
                                    break;
                               }
                        	}
                        }
                	 }
                  }
           } 
           
           if(true == needRnRUI) {
        	//  $("#show-rate-comnts-box").empty();
                  addRateAndReviewUI(showRateBoxElem);
                  $(".starRate").jRating({
                        isDisabled : false,
                        decimalLength : 1,
                        showRateInfo : false,
                        sendRequest : false,
                        onClick: function(element, rate) {
                               userRating = rate;
                        },
                        bigStarsPath : 'images/jrating/stars.png', // path of the icon stars.png
                        smallStarsPath : 'images/jrating/small.png' // path of the icon small.png
                  });
           }
    }
    else
    {
           console.log('div element rating-box is not present!');
    }
	
}


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



function showOverallRating(showOverallratingElem) {
	showOverallratingElem.innerHTML = '\
		<strong class="ratingText">Overall Rating : </strong><div class="OverallRating" \
		data-average="0" data-id="display-rating" id="Overall-Rating"></div>\
		<div class="numReviews" id="num-Reviews"></div>';
	
	
	$.ajax({
				type : "POST",
				url : getRatingUrl,
				contentType : "text/plain",
				data : pageUrl,
				success : function(data) {
					$('.OverallRating').attr("data-average",
							data.object.averageRating);
					console.log("UrlId = " + data.object.urlId);
					//$(document).ready(function() {
						$('.OverallRating').jRating({
							isDisabled : true,
							decimalLength : 1,
							async : false,
							bigStarsPath : 'images/jrating/stars.png', // path of the
							// icon
							// stars.png
							smallStarsPath : 'images/jrating/small.png' // path of the
						// icon
						// small.png
						//});
					});

					var totRate = document.getElementById('num-Reviews')
					if (null != totRate) {
						if (0 != data.object.totNumRating) {
							if (data.object.totNumRating > 1) {
								totRate.innerHTML = '<em>'
										+ data.object.totNumRating
										+ ' Reviews</em>';
							} else {
								totRate.innerHTML = '<em>'
										+ data.object.totNumRating
										+ ' Review</em>';
							}
						} else {
							totRate.innerHTML = '<em>'
									+ data.object.totNumRating
									+ ' Review, be the first to review.</em>';
						}
					}
				}
			});
}

function loadReviews(showUserRatingElem) {
	
	
	//alert(thisUrl);
	$.ajax({
		type : "POST",
		url : readReviewUrl,
		contentType : "application/json",
		data : pageUrl,
		async : false,
		success : function(data) {
			if (500 == data.code) {
				console.log('Number of review received = '
								+ data.object.length);
				if (0 < data.object.length) {
					//$("#show-user-rating").empty());
					userReviewObjsforRnr = data.object;
					addReviewCommentsUI(showUserRatingElem, false);
				//	$(document).ready(function() {
						$('.userRating').jRating({
							isDisabled : true,
							decimalLength : 1,
							type : 'small',
							bigStarsPath : 'images/jrating/stars.png', // path of the
																// icon
																// stars.png
							smallStarsPath : 'images/jrating/small.png' // path of the
																// icon
																// small.png
						});
				//	});
					if(commentId){
						//document.getElementById(commentId). ;
						$("#"+commentId).focus();
					}
				}
			} else {
				
				$("#show-user-rating").empty();
				console.log('readReviewUrl response code = ' + data.code);
			}
		}
	});
}

function addRateAndReviewUI (addElem){
	$('#show-rate-comnts-box').empty();
	
	
	addElem.innerHTML += '<img class="reviewAvatar" alt="" src="'+userAvatar+'"> \
	<div class="userDetailBox"> \
	<span>'+userName+'</span> \
	<div class= "starRate" data-average="0" data-id="display-rating" id="star-rate"></div> \
	</div> \
	<div class="titleBox" id="titleBoxDiv"> \
	<input type="text" class="titleInput" name="title_box" id="titleBox" placeholder="Enter a title for your review."></input> \
	</div> \
	<div class="reviewBox" id="reviewBoxDiv"> \
	<textarea type="text" class="reviewInput" name="review_box" id="reviewBox" placeholder="Write your review."></textarea> \
	</div> \
	<div class="postBox"> \
	<div class="postErrorBox"></div> \
	<input type="button" class="postButton" name="post_button" id ="postbutton" value="Post" onclick="postFunc()"></input> \
	</div> ';
	
};


/**
 * @param addElem
 * @param loadMore
 */
function addReviewCommentsUI(addElem, loadMore) {
	
	fullPageUrl = window.location.href;
	var cmntIndex = 0
	var numIter = 0;
	var modURObjs = [];
	if (true == loadMore) {
		var loadMoreElem = document.getElementById("loadreviews");
		if (null != loadMoreElem) {
			loadMoreElem.remove();
		}
	} else {
		if (commentId) {
			var modObjCnt=1;
			for (var objCnt = 0; objCnt < userReviewObjsforRnr.length; ++objCnt) {
				if (userReviewObjsforRnr[objCnt].commentId == commentId) {
					modURObjs[0] = userReviewObjsforRnr[objCnt];
				} else {
					modURObjs[modObjCnt] = userReviewObjsforRnr[objCnt];
					++modObjCnt;
				}
			}
			userReviewObjsforRnr = modURObjs;
		}
	}
	
	
	for ( var objCnt = cmntIndex; (numIter < cmntSetSize)
			&& (objCnt <= userReviewObjsforRnr.length); ++objCnt, ++numIter) {
		if(userReviewObjsforRnr[objCnt] != undefined) {
		var localDateTime = new Date(userReviewObjsforRnr[objCnt].dateTime);
		addElem.innerHTML += '\
			<div class="reviewContainer" id = '
				+ userReviewObjsforRnr[objCnt].commentId
				+ '><img class="reviewAvatar" alt="" src="'
				+ userReviewObjsforRnr[objCnt].userAvatar
				+ '"></img><div class="userDetailBox"> \
				<span class=userName>'
				+ userReviewObjsforRnr[objCnt].userName
				+ '</span> \
				<div class= "userRating" data-average="'
				+ userReviewObjsforRnr[objCnt].userRating
				+ '" data-id="user-rating" id="user-rate"></div> \
				<div class= "dateTime"><p>'
				// + userReviewObjsforRnr[objCnt].dateTime
				+ localDateTime.toLocaleTimeString()
				+ ' '
				+ localDateTime.toLocaleDateString()
				+ '</p></div></div><div class="reviewComment"> \
				<h4 style="margin: 0px;">'
				+ userReviewObjsforRnr[objCnt].commentTitle
				+ '</h4><p style="margin: 0px">'
				+ userReviewObjsforRnr[objCnt].userComment + '</p></div></div>';
		
		if ((userBaseId) && (userBaseId == userReviewObjsforRnr[objCnt].userBaseId)) {
			var reviewContr = document
					.getElementById(userReviewObjsforRnr[objCnt].commentId);
			reviewContr.innerHTML += '<div class="commentShare" id="comment-share" > \
									<a> \
									<div class="hovershare"> \
									<img src="images/sharing/share.png" width="15"/> \
									<div class="sharetool"> \
									<div class="fb-share-button" data-href="'+fullPageUrl+'" data-layout="button"></div> <p></p>\
									<div class="g-plus" data-action="share" data-href="'+fullPageUrl+'#'+userReviewObjsforRnr[objCnt].commentId+'" data-annotation="none"></div> \
									</div> \
									</div> \
									</a>\
									</div>';
		}
	}

	}
	cmntIndex = objCnt;

	if (objCnt < userReviewObjsforRnr.length) {
		addElem.innerHTML += '<div id="loadreviews" class="loadMore"> <a href="javascript:;" onClick="loadMoreReviews();">\
			<em>Load more review(s),&nbsp;&nbsp;'
				+ (userReviewObjsforRnr.length - objCnt)
				+ ' more reviews</a></em></div>';
	}

}

function loadMoreReviews() {
	$(document).ready(function() {
		var showUserRatingElem = document.getElementById('show-user-rating');
		addReviewCommentsUI(showUserRatingElem, true);
		$('.userRating').jRating({
			isDisabled : true,
			decimalLength : 1,
			type : 'small',
			bigStarsPath : 'images/jrating/stars.png', // path of the icon stars.png
			smallStarsPath : 'images/jrating/small.png' // path of the icon small.png
		});
	});
}


function postFunc() {
	// alert("The user has posted rate = "+userRating);
	var postButtonElem = document.getElementById("postbutton");
	if (validatePostData()) {
		// <a href="login.html" title="Login" class="loginForm">Login</a></p>
		if ((!checkUserSession()) && ("Post" == postButtonElem.value)) {
			console.log("No user logged in!");
			$('.postErrorBox').css("visibility", "visible");
			$('.postErrorBox').html(
					"<strong>Please login to post your review</strong>");
			postButtonElem.value = "Ok";
		}
		else if ("Ok" == postButtonElem.value) {
			var width  = 810;
			var height = 350;
			var left   = (screen.width  - width)/2;
			var top    = (screen.height - height)/2;
			var params = 'width='+width+', height='+height;
			params += ', top='+top+', left='+left;
			params += ', directories=no';
			params += ', location=no';
			params += ', menubar=no';
			params += ', resizable=no';
			params += ', scrollbars=no';
			params += ', status=no';
			params += ', toolbar=no';
			/*
			newwin=window.open(url,'windowname5', params);
			if (window.focus) {newwin.focus()}
			return false;
			*/
			window.open(loginPage,'Login', params);
			$('.postErrorBox').css("visibility", "hidden");
			$('.postErrorBox').html(
					"<strong>Please login to post your review</strong>");
			postButtonElem.value = "Post";
			
		} 
		else {
			var reviewData = {};
			reviewData["url"] = pageUrl;
			reviewData["userName"] = userName;
			reviewData["userAvatar"] = userAvatar;
			reviewData["socialMediaAttrName"] = socialMediaAttrName;
			reviewData["commentTitle"] = document.getElementById("titleBox").value;
			reviewData["userComment"] = document.getElementById("reviewBox").value;
			reviewData["userRating"] = userRating;
			reviewData["userBaseId"] = userBaseId;
			allocatePoints(userBaseId,"805");
			console.log(JSON.stringify(reviewData));

			$.ajax({
				type : "POST",
				url : writeReviewUrl,
				contentType : "application/json",
				// data: JSON.stringify(rateObj),
				data : JSON.stringify(reviewData),
				async : false,
				success : function(data) {
					if (500 != data.code) {
						alert('Review update failed, please try again later');
					}
					else{
						//location.reload();
						initiliseReviewAndRating();
						$('#show-rate-comnts-box').remove();
						
					}
				}
			});
		}
	}

}

function validatePostData() {
	var ret = true;
	
						var ratTitleElem = document.getElementById("titleBox");
						var ratSummElem = document.getElementById("reviewBox");

						if ("" == ratTitleElem.value) {
							$('.titleBox').css("border", "1px solid red");
							ret = false;
						}
						if ("" == ratSummElem.value) {
							$('.reviewBox').css("border", "1px solid red");
							ret = false;
						}

						if (false == ret) {
							$('.postErrorBox').css("visibility", "visible");
							$('.postErrorBox')
									.html(
											"<strong>Please fill the mandatory field(s) in red!</strong>");
						} else {
							$('.titleBox').css("border", "1px solid #D5D5D5");
							$('.reviewBox').css("border", "1px solid #D5D5D5");
							$('.postErrorBox').css("visibility", "hidden");
						}

					return ret;
}

/*function checkUserSession() {
	var retValue = false;
	if($.cookie("login_data")){
		var temp_data = JSON.parse($.cookie("login_data"));
		var logincookie = {};
		if(temp_data.username == undefined){
			logincookie = JSON.parse(temp_data);
		}else{
			logincookie = temp_data;	
		}
		
		userName = logincookie.username;
		if(logincookie.profilepic){
			userAvatar = logincookie.profilepic;
		}
		socialMediaAttrName = logincookie.social;
		alert(logincookie.userbase);
		userBaseId = logincookie.userbase;
		console.log(userBaseId);
		if(userBaseId){
			retValue = true;
		}
	}
	if(!retValue) {
		userBaseId = null;
		console.log("No user session available.");
	}
	
	return retValue;
}*/

