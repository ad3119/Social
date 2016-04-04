var widget = (function() {

	var widgetsArray = [];

	var intialisedPage = "";

	/*
	 * First function that needs to intilased from the user html pages
	 *
	 * */
	var initialise = function(page) {
		if (intialisedPage != page) {
			console.log("Iniside Init" + page + ":" + intialisedPage);
			emptyWidgetContainer();
			widgetsArray = [];
			fetchPageWidget(page);
			intialisedPage = page;
		} else {
			reloadWidgets();
		}

	}

	/*
	 * Fetch the Widgets that are added.
	 *
	 * */
	var fetchPageWidget = function(page) {

		$.getJSON(socialPluginConfig.getPageWidgetsUrl, function(json,
				textStatus) {
			$.each(json.object, function(index, val) {
				console.log('Inside Looping of array');
				var found = checkForPage(page, val);
				if (found) {
					console.log('Widget found');
					addWidgetToPage(val);
				}
				reloadWidgets();
			});
		});
	}
	/*
	 * Hide the widget body on click of glyphicon menu down
	 *
	 * */

	var hideWidget = function(event) {
		var id = event.target.closest('div');
		$(id).next().toggle('hide');
	}

	/*
	 * Remove all widgets
	 *
	 * */

	var emptyWidgetContainer = function() {
		$('#widgetContainer').empty();
		//alert($('#beforeBody').length);
		if ($('#beforeBody').length > 0) {
			$('#beforeBody').empty();
		} else {
			$("<div id='beforeBody'></div>").insertBefore("footer");
		}
	}

	/*
	 * Commom Widget Header template
	 *
	 * */
	var getWidgetHeader = function(val, widgetHeader, refreshFunction) {
		var temp = '<div class="panel panel-primary" id="'
				+ val
				+ '"><div class="panel-heading"><span class="glyphicon glyphicon-menu-down colorGrey" onclick="widget.hideWidget(event)"></span><panel-label>'
				+ widgetHeader
				+ '</panel-label><span class="glyphicon glyphicon-refresh pull-right colorGrey" title="refresh" onclick="'
				+ refreshFunction + '()"></span></div>';
		return temp;
	}

	/*
	 * Check for widget if it is added to page
	 *
	 * */
	var checkForPage = function(page, val) {
		console.log("Inside check For Page");
		var found = $.inArray(page, val.pagesList);
		if (found > -1) {
			widgetsArray.push(val.widgetHeader);
			return true;
		} else {
			return false;
		}
	}
	/*
	 * Add the Widget templates if they are added to the page
	 *
	 * */

	var addWidgetToPage = function(val) {
		console.log('Inside addWidgetToPage');
		switch (val.widgetHeader) {
		case "Comment Widget":
			addCommentWidget(val);
			break;
		case "Review & Rating  Widget":
			addRNRWidget(val);
			break;
		case "Badges Widget":
			addBadgesWidget(val);
			break;
		case "Leader Board Widget":
			addLeaderBoardWidget(val);
			break;
		case "Activity Feed Widget":
			addActivityFeedWidget(val);
			break;
		case "Reaction Widget":
			addReactionWidget(val);
			break;
		case "MyBoard Widget":
			addMyBoardWidget(val);
			break;
		}

	}
	/*
	 * Comment Widget template
	 *
	 * */
	var addCommentWidget = function(val) {
		var temp = getWidgetHeader("commentsWidget", "Comments",
				"initialiseCommentPlugin()")
				+ '<div class="panel panel-body style="height: 600px; overflow:auto;"><div class="showCommentBox"id="show-comment-box"></div><div id="error_holder"style="display: none"></div><div class="sortMenu"id="sortMenu"><div class="dropdown pull-right"><button class="btn btn-warning dropdown-toggle"type="button"id="menu1"data-toggle="dropdown">Sort By<span class="caret"></span></button><ul class="dropdown-menu"role="menu"aria-labelledby="menu1"><li role="presentation"><a role="menuitem"tabindex="-1"onclick="sortCommentsByDate()">Most recent</a></li><li role="presentation"><a role="menuitem"tabindex="-1"onclick="sortCommentsByLikes()">Most liked</a></li><li role="presentation"><a role="menuitem"tabindex="-1"onclick="sortCommentsByDislikes()">Most disliked</a></li><li role="presentation"><a role="menuitem"tabindex="-1"onclick="sortCommentsByReplies()">Most discussed</a></li></ul></div></div><div id="holder"style="display: none"><span style="color: green"><strong>Comment posted successfully!</strong></span></div><div class="displayUserComments"id="display-user-comments"></div></div>';

		if (val.align == "Vertical") {

			$('#widgetContainer').append(temp);
		} else {
			var outerDiv = "<div style='display:inline' class='col-md-6'>";
			outerDiv += temp + "</div>";
			$('#beforeBody').append(outerDiv);
		}
	}

	/*
	 * Review and Rating Widget template
	 *
	 * */
	var addRNRWidget = function(val) {
		var temp = getWidgetHeader("RatingNReviewWidget", "Rating & Review",
				"initiliseReviewAndRating()")
				+ '<div class="panel panel-body "><div class="showOverallRating" id="show-overall-rating"></div><div class="showUserRating" id="show-user-rating"></div><div class="showRateComntsBox" id="show-rate-comnts-box" data-limitratingtoone=YES></div></div></div>';

		console.log("Activity Feed:" + val.align);
		if (val.align == "Vertical") {
			var outerDiv = "<div style ='height:400px; overflow:auto;' id='reviewNRating'>";
			outerDiv += temp;
			$('#widgetContainer').append(outerDiv);
		} else {

			var outerDiv = "<div style='display:inline' class='col-md-6'>";
			outerDiv += temp + "</div>";
			$('#beforeBody').append(outerDiv);
		}
	}

	/*
	 * Reaction Widget template
	 *
	 * */
	var addReactionWidget = function(val) {

		var temp = getWidgetHeader("reactionWidget", "Reaction",
				"getReactionsList")
				+ '<div class="panel-body "><div id="reactions" style="padding-top: 10px;"></div></div>'

		console.log("Adding Widget to page");

		console.log("reaction :" + val.align);
		if (val.align == "Vertical") {

			$('#widgetContainer').append(temp);
		} else {

			var outerDiv = "";
			outerDiv += temp;
			$('#beforeBody').append(outerDiv);
		}
	}
	/*
	 * MyBoard Widget template
	 *
	 * */
	var addMyBoardWidget = function(val) {

		var temp = getWidgetHeader("myBoardWidget", "My Board",
				"intialiseGamification()")
				+ '<div class="panel-body "><div id="userpoints" style="padding-top: 10px;"></div></div>'

		console.log("Adding Widget to page");

		console.log("reaction :" + val.align);
		if (val.align == "Vertical") {

			$('#widgetContainer').append(temp);
		} else {
			var outerDiv = "<div id='myBoardWidget'>";
			outerDiv += temp;
			$('#beforeBody').append(outerDiv);
		}
	}
	/*
	 * Badges Widget template
	 *
	 * */
	var addBadgesWidget = function(val) {

		var temp = getWidgetHeader("badgesWidget", "Badges",
				"intialiseGamification()")
				+ '<div class="panel-body "><div id="userbadges"style="padding-top: 10px;"></div></div>'

		console.log("Adding Widget to page");

		console.log("Badegs :" + val.align);
		if (val.align == "Vertical") {

			$('#widgetContainer').append(temp);
		} else {
			var outerDiv = "<div style ='height:400px; overflow:auto;' id='badgesWidget'>";
			outerDiv += temp;
			$('#beforeBody').append(outerDiv);
		}
	}
	/*
	 * Leader Board Widget template
	 *
	 * */
	var addLeaderBoardWidget = function(val) {
		var temp = getWidgetHeader("leaderBoardWidget", "Leader Board",
				"intialiseGamification()")
				+ '<div class="panel-body " style="height:400px; overflow:auto;"><div id="leaderboard" style="margin-top: 20px"></div>';
		console.log("leader board:" + val.align);
		if (val.align == "Vertical") {
			var outerDiv = "<div id='leaderBoardWidget''>";
			outerDiv += temp;
			$('#widgetContainer').append(outerDiv);
		} else {
			var outerDiv = "<div id='leaderBoardWidget'>";
			outerDiv += temp;
			$('#beforeBody').append(outerDiv);
		}
	}

	/*
	 * Activity Feed Widget template
	 *
	 * */

	var addActivityFeedWidget = function(val) {

		var temp = getWidgetHeader("activityFeedWidget", "Activity Feed",
				"alluserActivity()")
				+ '<div class="panel-body "><ul class="nav nav-tabs"role="tablist"style="background-color: #ccc;"><li role="presentation"class="active"><a style="color: #000;" data-target="#everyone" aria-controls="everyone"role="tab" data-toggle="tab">Everyone</a></li><li role="presentation"><a style="color: #000;"data-target="#friends"aria-controls="friends"role="tab"data-toggle="tab">Friends</a></li><li role="presentation"><a style="color: #000;"data-target="#likes"aria-controls="messages"role="tab"data-toggle="tab">Me</a></li></ul><!--Tab panes--><div class="tab-content" style="max-height:400px;overflow:auto;"><div role="tabpanel"class="tab-pane active"id="everyone"><img src="images/spinner.gif"style="margin-left: 150px; margin-top: 70px;"id="everyonelistloading"/><div id="everyonelist"><div class="row"><div class="col-md-12"id="everyoneactivity"></div></div></div></div><div role="tabpanel"class="tab-pane"id="friends"><img src="images/spinner.gif"style="margin-left: 150px; margin-top: 70px;"id="friendListloading"/><div id="friendList"><div class="row"><div class="col-md-12"id="friendslikes"></div></div></div></div><div role="tabpanel"class="tab-pane"id="likes"><img src="images/spinner.gif"style="margin-left: 150px; margin-top: 70px;"id="myactivitylistloading"/><div id="myactivitylist"><div class="row"><div class="col-md-12"id="myactivity"></div></div></div></div></div></div>'
		console.log("Activity Feed:" + val.align);
		if (val.align == "Vertical") {
			var outerDiv = "<div  id='activityFeedWidget'>";
			outerDiv += temp;
			$('#widgetContainer').append(outerDiv);
		} else {
			var outerDiv = "<div id='activityFeedWidget'>";
			outerDiv += temp;
			$('#beforeBody').append(outerDiv);
		}
	}

	/*
	 * Check for widget is being added as part of page
	 *
	 * */
	var addedWidget = function(widget) {
		if (($.inArray(widget, widgetsArray) > -1)) {
			return true;
		}
	}

	/*
	 * Reload Widgets , Check if they are added to current page , call the particaular function properly
	 *
	 * */
	var reloadWidgets = function() {
		if (addedWidget("Badges Widget") || addedWidget("Leader Board Widget"))
			intialiseGamification();
		if (addedWidget("Review & Rating  Widget"))
			initiliseReviewAndRating();
		if (addedWidget("Activity Feed Widget"))
			alluserActivity();
		if (addedWidget("Reaction Widget"))
			getReactionsList();
		if (addedWidget("MyBoard Widget"))
			intialiseGamification();
		if (addedWidget("Comment Widget"))
			initialiseCommentPlugin();

	}

	/*
	 * Below are the functions which can be called from outside.
	 * 
	 * */
	return {
		init : initialise,
		reloadWidgets : reloadWidgets,
		hideWidget : hideWidget
	}

})();

function callwebRtc() {
	if ($.cookie("login_data")) {
		var login_cookie = JSON.parse($.cookie("login_data"));
		var userBaseId = login_cookie.userbase;
		var userName = login_cookie.username;
		document.location.href = '/#webRtc';
	} else {
		alert("Please Login")
	}

}