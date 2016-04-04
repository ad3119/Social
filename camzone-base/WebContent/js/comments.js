function includeRequiredSrc() {
	var headElem = document.getElementsByTagName('head').item(0);
	var cssFile = document.createElement('link');
	cssFile.rel = "stylesheet";
	cssFile.href = "css/jRating.jquery.css";
	headElem.appendChild(cssFile);
	var cssFile1 = document.createElement('link');
	cssFile1.rel = "stylesheet";
	cssFile1.href = "css/comments.css";
	headElem.appendChild(cssFile1);
};

var baseURL = socialPluginConfig.baseURL;
var submitCommentUrl=baseURL+"submitComment";
var readCommentUrl=baseURL+"readComment";
var submitReplyUrl=baseURL+"submitReply";
var readRepliesUrl=baseURL+"readReplies";
var deleteCommentUrl=baseURL+"deleteComment";
var upvoteUrl=baseURL+"upvoteComment";
var downvoteUrl=baseURL+"downvoteComment";
var userAvatar = "images/guest.png";
var commentuserAvatar = "images/guest.png";
var anonymoususername ="Guest";
var commentsocialloginName = "TODO";
var cmntIndex = 0;
var userBaseId = null;
var MaxCommentDisplay=5;
var numofrepliesclick=0;
var numofchildrepliesclick=0;
var nr=new Array();

var userLoggedIn = false;

function initialiseCommentPlugin() {
    //includeRequiredSrc();
   	checkUserSession();
   	$('#show-comment-box').empty();
   	var showCommentBoxElem = document.getElementById('show-comment-box');
   	if(null != showCommentBoxElem) {
   		console.log('div element show-comment-box is  present!');
   		addCommentUI(showCommentBoxElem);
   	}
    else {
   		console.log('div element show-comment-box is not present!');
   	}
   	//For kendoUI
   	//$("#commentBox").kendoEditor();
    $(".showCommentBox").css("height", "150px");
   	
   	//to display the comments stored in DB
    displayComments();
}

function getComments() {
	console.log("Getting comments from db");
	$.ajax({
		type: "POST",
		url: readCommentUrl,
		contentType: "application/json",
		data: pageUrl,
		async: false,
		success: function(data) {
			console.log("Comments received:");
			console.log(data);
			if(500 == data.code)
			{
				console.log('Number of parent comments received = '+data.object.length);
				if(0 < data.object.length) {
					userReviewObjs = data.object;	
				}
				else{
					console.log("No comments for this page");
				}
			}
			else {
				console.log('Ajax failure, comments not received');
			}
		}
	});
}

function displayComments() {
	
//	$('#display-user-comments').empty();
	var displayusercommentElem = document.getElementById('display-user-comments');
	if(null != displayusercommentElem)
	{
		console.log("display-user-comments div is present!");
		$.ajax({
			type: "POST",
			url: readCommentUrl,
			contentType: "application/json",
			data: pageUrl,
			success: function(data) {
				
				console.log("Comments received:");
				console.log(data);
				if(500 == data.code)
				{
					console.log('Number of parent comments received = '+data.object.length);
					if(0 < data.object.length)
					{
						userReviewObjs = data.object;
						sortCommentsByDate();
						displayReviewCommentsUI(displayusercommentElem, false);	
					}
				}
				else
				{
					$('#display-user-comments').empty();
					console.log('readCommentUrl response code = '+data.code);
				}
			}
		});
	}
	else
	{
		console.log("display-user-comments is not present");
	}
}


function sortCommentsByLikes() {
	$('#display-user-comments').empty();
	getComments();
	userReviewObjs.sort(SortByLikes);
	console.log("After sorting:");
	console.log(userReviewObjs);
	var displayusercommentElem = document.getElementById('display-user-comments');
	displayReviewCommentsUI(displayusercommentElem, false);	
}

function sortCommentsByDislikes() {
	$('#display-user-comments').empty();
	getComments();
	userReviewObjs.sort(SortByDislikes);
	console.log("After sorting:");
	console.log(userReviewObjs);
	var displayusercommentElem = document.getElementById('display-user-comments');
	displayReviewCommentsUI(displayusercommentElem, false);	
}

function sortCommentsByReplies() {
	$('#display-user-comments').empty();
	getComments();
	userReviewObjs.sort(SortByReplies);
	console.log("After sorting:");
	console.log(userReviewObjs);
	var displayusercommentElem = document.getElementById('display-user-comments');
	displayReviewCommentsUI(displayusercommentElem, false);	
}

function sortCommentsByDate() {
	$('#display-user-comments').empty();
	getComments();
	userReviewObjs.sort(SortByDate);
	console.log("After sorting:");
	console.log(userReviewObjs);
	var displayusercommentElem = document.getElementById('display-user-comments');
	displayReviewCommentsUI(displayusercommentElem, false);
}

function SortByDate(a,b) {
	var aName = a.dateTime;
	var bName = b.dateTime; 
	return ((aName > bName) ? -1 : ((aName < bName) ? 1 : 0));
}

function SortByLikes(a,b) {
	var aName = a.numUpvotes;
	var bName = b.numUpvotes;
	return ((aName > bName) ? -1 : ((aName < bName) ? 1 : 0));
}

function SortByDislikes(a,b) {
	var aName = a.numDownvotes;
	var bName = b.numDownvotes;
	return ((aName > bName) ? -1 : ((aName < bName) ? 1 : 0));
}

function SortByReplies(a,b) {
	var aName = a.numofreplies;
	var bName = b.numofreplies;
	return ((aName > bName) ? -1 : ((aName < bName) ? 1 : 0));
}

//To add default comment box at top for posting original comment
function addCommentUI (addElem){

	addElem.innerHTML +=' \
		<textarea class="form-control" name="comment_box" id="commentBox" autocomplete="off" placeholder="Leave a comment"></textarea> \
		<div class="form-control" style="position: relative; height: 70px"> \
		Post as: \
		<img class="profilePic" alt="" src="'+userAvatar+'" width="32px"></img> \
		<a class="userlink">' + anonymoususername +' </a> \
		<label id="comment-error" style="color:red;"></label> \
		<input type="button" class="btn btn-primary pull-right" name="submit_button" value="Post" onclick="submitCommentFunc()"></input> \
		</div> ';
};

/*****************************************************
 *To invoke post service to submit parent comment to database
 *
 *********************************************************/

function submitCommentFunc() {
	//alert("The user has posted rate = "+userRating);
	var flag = 1;
	
	
	console.log("Before submitting comment : "+anonymoususername);
	var CommentData = {};
	CommentData["commenturl"] = pageUrl;
	CommentData["comuserName"] = anonymoususername;
	CommentData["comuserAvatar"] = userAvatar;
	CommentData["comsocialloginName"] = commentsocialloginName;
	CommentData["commentstring"] = document.getElementById("commentBox").value ;
	CommentData["parCommentId"] = null;
	CommentData["userBaseId"] = userBaseId;
	if(checkUserSession()){
		if(document.getElementById("commentBox").value){
			console.log(JSON.stringify(CommentData));

			$.ajax({
				type: "POST",
				url: submitCommentUrl,
				contentType: "application/json",
				data: JSON.stringify(CommentData),
				success: function(data) {
					if(500 != data.code)
					{
						alert('Sorry!!!!! Comment submission failed');
					}
					else
					{
						//$('.k-editable-area').empty();
						
						$("#holder").fadeIn();
						$("#holder").fadeOut(3000);
						displayComments();
						//$('#commentBox').val('');
						
					}
				}
			});
			$('#commentBox').val('');	
		}
		else{
			
			$('#error_holder').html("<span style='color: red'>Please enter a comment</span>");
			$('#error_holder').show();
			$('#error_holder').fadeOut(3000);
		}
	}
	else{
			$('#error_holder').html("<span style='color: red'>Please log in to comment</span>");
			$('#error_holder').show();
			$('#error_holder').fadeOut(3000);
	}


}

/*****************************************************
 *To load and display the posted comments on page load
 *
 *********************************************************/
function displayReviewCommentsUI (addElem, loadMore){
	if(true == loadMore)
	{
		$(document).ready(function(){
			$('.loadMore').remove();
		});
	}
	$("#display-user-comments").empty();
	//TODO add max display configuration value here
	for(var objCnt=cmntIndex; (objCnt<userReviewObjs.length); ++objCnt)
	{
		var localDateTime = new Date(userReviewObjs[objCnt].dateTime);
		var commentId=userReviewObjs[objCnt].commentId;
		
		var numofexistingreplies = userReviewObjs[objCnt].numofreplies;

		
		upvoteDisabled = false;
		downvoteDisabled = false;
		deleteDisabled = false;

		var onclickdisplayrepliesfunc = "onClickFetchReplies('";
		onclickdisplayrepliesfunc+= userReviewObjs[objCnt].commentId;
		onclickdisplayrepliesfunc+="'";
		onclickdisplayrepliesfunc+=',';
		onclickdisplayrepliesfunc+=numofexistingreplies;
		onclickdisplayrepliesfunc+=",'";
		onclickdisplayrepliesfunc+=commentId;
		onclickdisplayrepliesfunc+="');";
		
		var onClickDeleteFunc= "onClickdeleteComment('";
		onClickDeleteFunc += userReviewObjs[objCnt].commentId;
		onClickDeleteFunc += "');";
		//Delete comment 
		var onClickFunc= "addReplyContainer('";
		onClickFunc += userReviewObjs[objCnt].commentId;
		onClickFunc += "');";
		var onClickUpvote = "onClickUpvote('";
		onClickUpvote += userReviewObjs[objCnt].commentId;
		onClickUpvote +="');";
		var userComment = userReviewObjs[objCnt].userComment;

		if(userReviewObjs[objCnt].upvotesBy != null) {
			for(var i=0;i<userReviewObjs[objCnt].upvotesBy.length;i++) {
				if(userReviewObjs[objCnt].upvotesBy[i] == userBaseId)
					upvoteDisabled = true;
			}
		}
		
		if(userReviewObjs[objCnt].downvotesBy != null) {			
			for(i=0;i<userReviewObjs[objCnt].downvotesBy.length;i++) {
				if(userReviewObjs[objCnt].downvotesBy[i] == userBaseId)
					downvoteDisabled = true;
			}
		}
		
		if(userReviewObjs[objCnt].userBaseId == userBaseId) {
			deleteDisabled = true;
		}
		
		if(userReviewObjs[objCnt].userAvatar){
			commentuserAvatar = userReviewObjs[objCnt].userAvatar;
		}
		else
			commentuserAvatar = "images/guest.png";

		addElem.innerHTML += '\
			<div class="commentdetailContainer" id = "comment_Container"> \
			<img class="commentAvatar" alt="" src="'+commentuserAvatar+'" style="display:inline-block"> \
			<div style="display:inline-block">\
			<span class="commentuserName"><strong>'+userReviewObjs[objCnt].userName+'</strong></span> \
			<div class= "dateTime">\
			<p>'+getCommentTime(localDateTime)+' </p>\
			</div> \
			</div> ';
		
		addElem.innerHTML += '\
			<div id= '+userReviewObjs[objCnt].commentId+'> \
			<div id="userComment_'+commentId+'" style="margin-left: 50px"></div> \
			</div>\
			<div id="repy-error'+commentId+'" style="display: none"><span style="margin-left: 100px; color: red">Please log in to comment</span></div>\
			<div id="reply-error'+commentId+'" style="display: none"><span style="margin-left: 100px; color: red">Please enter a comment</span></div>\
			<div id="actionOnComment_'+commentId+'"><p>';

			
		if(upvoteDisabled == false && downvoteDisabled == false)
			addElem.innerHTML += '\
				<span id="like'+commentId+'">\
				<a id="like_'+commentId+'" onclick="plikeclicked(event)" style="margin-right: 10px" href="javascript:;" >Like</a>\
				</span>\
				<span id="dislike'+commentId+'">\
				<a id="dislike_'+commentId+'" onclick="pdislikeclicked(event)" style="margin-right: 10px" href="javascript:;" >Dislike</a>\
				</span>';
				
		else if(upvoteDisabled == true && downvoteDisabled == false)
				addElem.innerHTML += '<span style="color: blue; margin-right: 10px" id="like'+commentId+'">\
					You like this!\
					</span>\
					';
			
		else if(upvoteDisabled == false && downvoteDisabled == true)
			addElem.innerHTML += '<span style="color: red; margin-right: 10px" id="dislike'+commentId+'">\
				You dislike this!\
				</span>\
				';
			
		else {
			/*This should never occur*/
		}
			
			
		addElem.innerHTML += '\
			<a id="replyLink_'+commentId+'" style="margin-right: 10px" href="javascript:;" onclick="'+onClickFunc+'">Reply</a>';
		/*if(numofexistingreplies > 0)*/
			addElem.innerHTML +='\
				<a id="nr'+commentId+'" style="margin-right: 10px" href="javascript:;" onclick="'+onclickdisplayrepliesfunc+'"><span class="glyphicon glyphicon-chevron-down"></span><span id="rpCount'+commentId+'">'+numofexistingreplies+'</span></a>';
		
			
			if(upvoteDisabled == false && downvoteDisabled == false){
				addElem.innerHTML +='\
				<span id="tup'+commentId+'" style="margin-left: 10px" class="glyphicon glyphicon-thumbs-up pull-right"><span id="like_count'+commentId+'">'+userReviewObjs[objCnt].numUpvotes+'</span></span>\
				<span id="dtup'+commentId+'" style="margin-left: 10px" class="glyphicon glyphicon-thumbs-down pull-right"><span id="dislike_count'+commentId+'">'+userReviewObjs[objCnt].numDownvotes+'</span></span>\
				<a \
				id="deleteLink_'+commentId+'" href="javascript:;" onclick="'+onClickDeleteFunc+'">\
				<span onmouseover="trashpop()" id="trash" data-toggle="popover" data-placement="right" data-content="Delete"\
				class="glyphicon glyphicon-trash"></span>\
				</a>\
				</p></div>\
				';
			}
			
			else if(upvoteDisabled == true && downvoteDisabled == false) {
				addElem.innerHTML +='\
					<span id="tup'+commentId+'" style="margin-left: 10px; color: blue" class="glyphicon glyphicon-thumbs-up pull-right"><span id="like_count'+commentId+'">'+userReviewObjs[objCnt].numUpvotes+'</span></span>\
					<span id="dtup'+commentId+'" style="margin-left: 10px" class="glyphicon glyphicon-thumbs-down pull-right"><span id="dislike_count'+commentId+'">'+userReviewObjs[objCnt].numDownvotes+'</span></span>\
					<a data-toggle="popover" data-placement="right" data-content="Delete"\
					id="deleteLink_'+commentId+'" href="javascript:;" onclick="'+onClickDeleteFunc+'">\
					<span id="trash" data-toggle="popover" data-placement="right" data-content="Delete" \
					class="glyphicon glyphicon-trash"></span>\
					</a>\
					</p></div>\
					';
			}
			
			else if(upvoteDisabled == false && downvoteDisabled == true) {
				addElem.innerHTML +='\
					<span id="tup'+commentId+'" style="margin-left: 10px" class="glyphicon glyphicon-thumbs-up pull-right"><span id="like_count'+commentId+'">'+userReviewObjs[objCnt].numUpvotes+'</span></span>\
					<span id="dtup'+commentId+'" style="margin-left: 10px; color: red" class="glyphicon glyphicon-thumbs-down pull-right"><span id="dislike_count'+commentId+'">'+userReviewObjs[objCnt].numDownvotes+'</span></span>\
					<a data-toggle="popover" data-placement="right" data-content="Delete"\
					id="deleteLink_'+commentId+'" href="javascript:;" onclick="'+onClickDeleteFunc+'">\
					<span id="trash" data-toggle="popover" data-placement="right" data-content="Delete" \
					class="glyphicon glyphicon-trash"></span>\
					</a>\
					</p></div>\
					';
			}
			
			
			else {
				/*This should never occur*/
			}
			
			addElem.innerHTML +='\
				<div id="parent'+commentId+'"> \
				</div> \
				</div> \
				\
				';
			
		var commentHtml = $.parseHTML(userComment);
		$("#userComment_"+commentId).html(commentHtml[0].nodeValue);
	
		if(deleteDisabled == false)
		$('#deleteLink_'+commentId).empty();
		
		nr[commentId] = 0;
	}

	if(objCnt < userReviewObjs.length)
	{
		addElem.innerHTML +='<div class="loadMore" onclick=loadMoreFunc()> \
			<em>Load more comments,'+ (userReviewObjs.length - objCnt) +' more reviews</em></div></div>';
	}
		
}

/*****************************************************************
 * Method:To add the reply box on click event with post button
 * 
 * 
 *****************************************************************/
function addReplyContainer(commentID) {
	$('#replyLink_'+commentID).remove();
	var commentElem=document.getElementById(commentID);
	var replyElem = document.createElement("div");
	var postReply="postReply('"+commentID+"');";
	replyElem.setAttribute("class", "replyContainer");
	replyElem.setAttribute("id","reply_"+commentID);
	replyElem.innerHTML += ' \
		\
		<textarea style="margin-left: 13%; width: 87%" class="" id="replyBox_'+commentID+'"></textarea>\
		\
		<p><span style="font-size: 90%; margin-left: 40px">Reply As: </span><span style="color: blue">'+anonymoususername+'</span>\
		<input type="button" class="btn-primary pull-right" value="Post" onclick="'+postReply+'"/></p>\
		\
		';
		
	$("#"+commentID).after(replyElem);
	/*var rpCount=$("#rpCount"+commentId).text();
	$("#rpCount"+commentId).text(++rpCount);*/
}

function postReply(commentID) {
	var replyText = document.getElementById('replyBox_'+commentID);
	//this is plain java object.Stringify converts it to JSON 
	var replyData = {};
	replyData["commenturl"] = pageUrl;
	replyData["comuserName"] = anonymoususername;
	replyData["comuserAvatar"]= userAvatar;
	replyData["comsocialloginName"]="";
	replyData["commentstring"]=document.getElementById("replyBox_"+commentID).value;
	replyData["parCommentId"] = commentID;
	replyData["userBaseId"]=userBaseId;

	if(checkUserSession()){
		if(document.getElementById("replyBox_"+commentID).value){

			console.log(replyText.value); 
			$.ajax({
				type: "POST",
				url: submitReplyUrl,
				contentType: "application/json",
				data:JSON.stringify(replyData)
			});

			$("#reply_"+commentID).remove();
			var count = $('#rpCount'+commentID).text();
			$('#rpCount'+commentID).text(++count);
			$('#display-user-comments').empty();
			displayComments();
		}
		else{
			$('#reply-error'+commentID).css("display","block");
			$('#reply-error'+commentID).fadeOut(2000);
		}
	}
	else{
		$('#repy-error'+commentID).css("display","block");
		$('#repy-error'+commentID).fadeOut(2000);
	}
}

/*
	METHOD: Fetch and display  the replies when number of replies is clicked.
	Append fetched replies to current page
 */
function onClickFetchReplies(parentCommentId, numofexistingreplies)
{	
	if(nr[parentCommentId] == 0)
	{
		var commentelem = document.getElementById(parentCommentId);
		var parentCommentData = {};
		parentCommentData["commenturl"] = pageUrl;
		parentCommentData["parCommentId"] = parentCommentId;
		if(commentelem!= null)
		{ 
			$.ajax({
				type: "POST",
				url: readRepliesUrl,
				contentType: "application/json",
				data: JSON.stringify(parentCommentData),
				success: function(data){
					if(500 == data.code)
					{
						console.log('Number of replies received = '+data.object.length);
						if(0 < data.object.length)
						{
							userRepliesObjs = data.object;
							$('#nr'+parentCommentId).html("<span class='glyphicon glyphicon-chevron-up'></span>"+numofexistingreplies);
							displayrepliesUI(parentCommentId);				
						}
					}
					else
					{
						console.log('readReplyUrl response code = '+data.code);
						console.log(parentCommentId);
					}
				}
			}); //ajax
			nr[parentCommentId]++;
		}
		else
		{
			console.log('commentelem not present');
		}
	}
	else
	{	
		//replies has already been displayed .On another click hide the replies
		$('#nr'+parentCommentId).html('<span class="glyphicon glyphicon-chevron-down"></span>'+numofexistingreplies);
		while(numofexistingreplies){
			$('#replyDisplay'+parentCommentId).remove();
			numofexistingreplies--;
		}
		nr[parentCommentId]--;
	}
/*	 if(numofreplies > 0)
    	; //Fetch replies from DB

	else//No need to fetch replies as 0 replies to this comment.
    	 alert("Sorry No replies present");
  */  	
}

function  displayrepliesUI(parentCommentId){

	var replystrtindex=0;

	console.log(userRepliesObjs);

	for(var repliescnt=(userRepliesObjs.length)-1 ;repliescnt>=replystrtindex;repliescnt--)
	{
		var repliesdisplayelem=document.createElement("div");
		repliesdisplayelem.setAttribute("id","replyDisplay"+parentCommentId);
		console.log("userRepliesObjs"+userRepliesObjs);
		
		var numofexistingreplies = userRepliesObjs[repliescnt].numofreplies;
		var localDateTime = new Date(userRepliesObjs[repliescnt].dateTime);
		var commentId= userRepliesObjs[repliescnt].commentId;
		if(userRepliesObjs[repliescnt].userAvatar){
			commentuserAvatar = userRepliesObjs[repliescnt].userAvatar;
		}
		else
			commentuserAvatar = "images/guest.png";
		upvoteDisabled = false;
		downvoteDisabled = false;
		replyDeleteDisabled = false;
				
		var onclickdisplayrepliesfunc = "onClickFetchReplies('";
		onclickdisplayrepliesfunc+= userRepliesObjs[repliescnt].commentId;
		onclickdisplayrepliesfunc+="'";
		onclickdisplayrepliesfunc+=',';
		onclickdisplayrepliesfunc+=numofexistingreplies;
		onclickdisplayrepliesfunc+=",'";
		onclickdisplayrepliesfunc+=commentId;
		onclickdisplayrepliesfunc+="');";
	
		var onClickFunc= "addReplyContainer('";
		onClickFunc += userRepliesObjs[repliescnt].commentId;
		onClickFunc += "');";

		var onClickDeleteFunc= "onClickdeleteComment('";
		onClickDeleteFunc += userRepliesObjs[repliescnt].commentId;
		onClickDeleteFunc += "');";
		
			if(userRepliesObjs[repliescnt].upvotesBy != null) {
			for(var i=0; i< (userRepliesObjs[repliescnt].upvotesBy.length); i++) {
				if(userRepliesObjs[repliescnt].upvotesBy[i] == userBaseId)
					upvoteDisabled = true;
				}
			}
			if(userRepliesObjs[repliescnt].downvotesBy != null) {			
			for(i=0;i<userRepliesObjs[repliescnt].downvotesBy.length;i++) {
				if(userRepliesObjs[repliescnt].downvotesBy[i] == userBaseId)
					downvoteDisabled = true;
				}
			}
			if(userRepliesObjs[repliescnt].userBaseId != userBaseId) {
				
				replyDeleteDisabled = true;
			}
			
			repliesdisplayelem.innerHTML+= '\
				<div id="comment_header" style="margin-left: 40px; border-top: 1px solid lightgrey">\
					<p>\
						<img style="width: 37px; height:40px" src="'+commentuserAvatar+'"></img><strong> \
						'+userRepliesObjs[repliescnt].userName+'</strong>\
						<span style="float: right"> '+getCommentTime(localDateTime)+'</span>\
					</p>\
				</div>\
				<div id='+userRepliesObjs[repliescnt].commentId+' style="margin-left: 40px;">\
					<p style="margin-left: 40px">'+userRepliesObjs[repliescnt].userComment+'</p>\
				</div>\
				<div id="repy-error'+commentId+'" style="display: none"><span style="margin-left: 40px; color: red">Please log in to comment</span></div>\
				<div id="reply-error'+commentId+'" style="display: none"><span style="margin-left: 40px; color: red">Please enter a comment</span></div><p>';
			
			if(upvoteDisabled == false && downvoteDisabled == false) {
				repliesdisplayelem.innerHTML+='<span id="like'+commentId+'">\
				<a id="like_'+commentId+'" onclick="likeclicked(event);" style="margin-left: 40px; margin-right: 10px" href="javascript:;" >Like</a>\
				</span>\
				<span id="dislike'+commentId+'">\
				<a id="dislike_'+commentId+'" onclick="dislikeclicked(event);" style="margin-right: 10px" href="javascript:;" >Dislike</a>\
				</span>';
			}
			
			else if(upvoteDisabled == true && downvoteDisabled == false)
				repliesdisplayelem.innerHTML += '<span style="color: blue; margin-left: 40px; margin-right: 10px" id="like'+commentId+'">\
				You like this!\
				</span>\
				';
		
			else if(upvoteDisabled == false && downvoteDisabled == true)
				repliesdisplayelem.innerHTML += '<span style="color: red; margin-left: 40px; margin-right: 10px" id="dislike'+commentId+'">\
				You dislike this!\
				</span>\
				';
		
			else {
				/*This should never occur*/
			}			
			
			repliesdisplayelem.innerHTML+='\
				<a id="replyLink_'+commentId+'" style="margin-right: 10px" href="javascript:;" onclick="'+onClickFunc+'">Reply</a>';
			/*if(numofexistingreplies > 0)*/	
				repliesdisplayelem.innerHTML+='\<a id="nr'+commentId+'" style="margin-right: 10px" href="javascript:;" onclick="'+onclickdisplayrepliesfunc+'"><span class="glyphicon glyphicon-chevron-down"></span><span id="rpCount'+commentId+'">'+numofexistingreplies+'</span></a>';
				
			if(upvoteDisabled == false && downvoteDisabled == false) {
				repliesdisplayelem.innerHTML+='<span id="up'+commentId+'" style="float: right" class="glyphicon glyphicon-thumbs-up"><span id="like_count'+commentId+'">'+userRepliesObjs[repliescnt].numUpvotes+'</span></span>\
				<span id="dup'+commentId+'" style="margin-right: 10px" class="glyphicon glyphicon-thumbs-down pull-right"><span id="dislike_count'+commentId+'">'+userRepliesObjs[repliescnt].numDownvotes+'</span></span>\
				<span id="deleteLink_'+commentId+'" style="margin-right: 10px" class="glyphicon glyphicon-trash" onclick="'+onClickDeleteFunc+'"></span>\
				</p>\
				<div id="parent'+commentId+'">\
				</div>\
				';
			}
			else if(upvoteDisabled == true && downvoteDisabled == false) {
				repliesdisplayelem.innerHTML+='\
				<span id="up'+commentId+'" style="color: blue; float: right" class="glyphicon glyphicon-thumbs-up"><span id="like_count'+commentId+'">'+userRepliesObjs[repliescnt].numUpvotes+'</span></span>\
				<span id="dup'+commentId+'" style="margin-right: 10px" class="glyphicon glyphicon-thumbs-down pull-right"><span id="dislike_count'+commentId+'">'+userRepliesObjs[repliescnt].numDownvotes+'</span></span>\
				<span id="deleteLink_'+commentId+'" style="margin-right: 10px" class="glyphicon glyphicon-trash" onclick="'+onClickDeleteFunc+'"></span>\
				</div>\
				<div id="parent'+commentId+'">\
				</div>\
				';
			}
				
			else if(upvoteDisabled == false && downvoteDisabled == true) {
				repliesdisplayelem.innerHTML+='\
				<span id="up'+commentId+'" style="float: right" class="glyphicon glyphicon-thumbs-up"><span id="like_count'+commentId+'">'+userRepliesObjs[repliescnt].numUpvotes+'</span></span>\
				<span id="dup'+commentId+'" style="color: red; margin-right: 10px" class="glyphicon glyphicon-thumbs-down pull-right"><span id="dislike_count'+commentId+'">'+userRepliesObjs[repliescnt].numDownvotes+'</span></span>\
				<span id="deleteLink_'+commentId+'" style="margin-right: 10px" class="glyphicon glyphicon-trash" onclick="'+onClickDeleteFunc+'"></span>\
				</div>\
				<div id="parent'+commentId+'">\
				</div>\
				';
			}
			
			
			else {
				/*This should never occur*/
			}
			nr[commentId] = 0;
			$(document.getElementById("parent"+parentCommentId)).after(repliesdisplayelem);
			if(replyDeleteDisabled)
				$('#deleteLink_'+commentId).hide();
	}
	
}

function likeclicked(event) {
	var id = event.target.id;
	var countId = id.substring(5);
	onClickUpvote(countId);
	$("#dislike"+countId).empty();
	$("#like"+countId).empty();
	$("#like"+countId).append("<span style='margin-left: 40px; color: blue; margin-right: 10px;'>You like this!</span>");
	var count = parseInt($('#like_count'+countId).text());
	$('#like_count'+countId).text(++count);
	$("#up"+countId).css("color", "blue");
}

function plikeclicked(event) {
	var id = event.target.id;
	var countId = id.substring(5);
	onClickUpvote(countId);
	$("#dislike"+countId).empty();
	$("#like"+countId).empty();
	$("#like"+countId).append("<span style='color: blue;'><span style='margin-right: 10px;'>You like this!</span></span>");
	var count = parseInt($('#like_count'+countId).text());
	$('#like_count'+countId).text(++count);
	$("#tup"+countId).css("color", "blue");
}

function dislikeclicked(event) {
	var id = event.target.id;
	var countId = id.substring(8);
	onClickDownvote(countId);
	$("#like"+countId).empty();
	$("#dislike"+countId).empty();
	$("#dislike"+countId).append("<span style='color: red; margin-right: 10px; margin-left: 40px'>You dislike this!</span>");
	var count = parseInt($('#dislike_count'+countId).text());
	$('#dislike_count'+countId).text(++count);
	$("#dup"+countId).css("color", "red");
}

function pdislikeclicked(event) {
	var id = event.target.id;
	var countId = id.substring(8);
	onClickDownvote(countId);
	$("#like"+countId).empty();
	$("#dislike"+countId).empty();
	$("#dislike"+countId).append("<span style='color: red; margin-right: 10px'>You dislike this!</span>");
	var count = parseInt($('#dislike_count'+countId).text());
	$('#dislike_count'+countId).text(++count);
	$("#dtup"+countId).css("color", "red");
}

/*******************************************************************
 * METHOD: To delete parent comment.When deleting parent comment
 * also delete the child replies
 * ******************************************************************/
function onClickdeleteComment(commentId)
{

	$.ajax({
		type: "POST",
		url: deleteCommentUrl,
		contentType: "application/json",
		data: commentId,
		success: function(data){
			if(500 == data.code)
			{
				console.log('deleteCommentUrl response code = '+data.code);
				displayUIafterdelete();
			}
			else
			{
				console.log('deleteCommentUrl response code = '+data.code);
			}
		}
	});
}

function displayUIafterdelete() {
	$('#display-user-comments').empty();
	displayComments();
}
/*******************************************************************
 * METHOD: To check the user session
 * ******************************************************************/
function checkUserSession() {
	
	if($.cookie("login_data")) {
		var temp_data = JSON.parse($.cookie("login_data"));
		var logincookie = {};
		if(temp_data.username == undefined){
			logincookie = JSON.parse(temp_data);
		}else{
			logincookie = temp_data;	
		}
		userName = logincookie.username;
		anonymoususername = logincookie.username;
		socialMediaAttrName = logincookie.social;
		userBaseId = logincookie.userbase;
		if(logincookie.profilepic){
			userAvatar = logincookie.profilepic;
		}
		userLoggedIn = true;
		return true;
	}
	

	else{
		userAvatar = "images/guest.png";
		anonymoususername = "Guest";
		userName = "Guest";
		userBaseId = null;
		console.log("Comment plugin: no user session available!");
		userLoggedIn = false;
		return false;
	}



}
/**************************************************************************
 * *METHOD: To upvote the comment
 * ***********************************************************************/
function onClickUpvote(commentId) {//alert(commentId);
	if(checkUserSession()){
		$.ajax({
			type: "POST",
			url: upvoteUrl,
			contentType: "application/json",
			data: JSON.stringify({"commentId":commentId,"upvoteBy":userBaseId}),
			success: function(data){
				if(500 == data.code)
				{
					console.log('onClickUpvote response code = '+data.code);
				}
				else
				{
					console.log('onClickUpvote response code = '+data.code);
				}
			}
		});
	}
	else {
		$('#upvote-error_'+commentId).text("Please login to like");
	}
}

/**************************************************************************
 * *METHOD: To upvote the comment
 * ***********************************************************************/
function onClickDownvote(commentId) {//alert(commentId);
	if(checkUserSession()){
		$.ajax({
			type: "POST",
			url: downvoteUrl,
			contentType: "application/json",
			data: JSON.stringify({"commentId":commentId,"downvoteBy":userBaseId}),
			success: function(data){
				if(500 == data.code)
				{
					console.log('onClickDownvote response code = '+data.code);
				}
				else
				{
					console.log('onClickDownvote response code = '+data.code);
				}
			}
		});
	}
	else {
		$('#upvote-error_'+commentId).text("Please login to like");
	}
}

/*******************************************************************
 * METHOD: To calculate how old a posted comment is	 
 *******************************************************************/
function getCommentTime(localDateTime)
{
	var currentDateTime = new Date();
	var commentTime;
	var diff = (currentDateTime - localDateTime)/1000;
	diff = Math.abs(Math.floor(diff));
	var days = Math.floor(diff/(24*60*60));
	var leftSec = diff - days * 24*60*60;
	var hrs = Math.floor(leftSec/(60*60));
	var leftSec = leftSec - hrs * 60*60;
	var min = Math.floor(leftSec/(60));
	var leftSec = leftSec - min * 60;

	if(days == 1)
		commentTime = days+' day ago';
	else if(days > 1)
		commentTime = days+' days ago';
	else if(hrs < 1 && min <= 1)
		commentTime = leftSec+' seconds ago';
	else if(hrs < 1 && min > 1)
		commentTime = min+ ' mins ago';
	else if(hrs == 1)
		commentTime = hrs+' hour ago';
	else
		commentTime = hrs+' hours ago';
	return commentTime;
}

function trashpop() {//alert("some");
	$("trash").popover({trigger: 'hover'});
}
	



