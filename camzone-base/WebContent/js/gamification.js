
var baseURL = socialPluginConfig.baseURL;
var getUserPointsURL = baseURL+"getUserPoints";
var UserBadgesURL = baseURL+"getUserBadges";
var badgesURL = baseURL+"getBadges";
var getLeaderBoardURL = baseURL+"getLeaderBoard";
var profilepicBaseURL = socialPluginConfig.profilepicBaseURL;
var getUserName = baseURL+"username";
var testUserId ="bf24a1df-2000-489e-81ae-f6a2a53ba230";
var testid2="b49c5c56-e6ba-4641-91b9-8bec241259dc";

var firstBadgeURL = socialPluginConfig.firstBadgeURL;
var secondBadgeURL = socialPluginConfig.secondBadgeURL;
var thirdBadgeURL = socialPluginConfig.thirdBadgeURL;
var userBaseId = null;


function intialiseGamification() {
	var isUserLoggedIn = false;
	
	
	isUserLoggedIn = checkUserSession();
	console.log("gamification.js" +isUserLoggedIn);
	console.log(isUserLoggedIn);
	console.log(userBaseId);
	
	if(isUserLoggedIn){
		
		$("#myBoardWidget").attr("style","display:block");
		postData(getUserPointsURL, userBaseId);
	}
	else{
	
		$("#myBoardWidget").attr("style","display:none");
	}
	
	getLeaderBoard(getLeaderBoardURL);
	getUserBadges(badgesURL, userBaseId);
	
	//return false;
}





function postData(url, jsonObj){
	$.ajax({
		type: 'POST',
		contentType: 'text/plain',
		url: url,
		data:jsonObj,
		success: function(data){
			
			
			console.log("Inside getUserPoints");
			if(data.code == 500) {
				
				
				$("#userpoints").empty();
				console.log("postData : Data");
				
				var userpoints = data.object;
				var $tbody = $("<tbody>");
				var $tr = $("<tr />");
				
				var img_url;
				
				if(data.object.profile_pic_url.indexOf("https://")> -1){
					img_url = data.object.profile_pic_url;
				}
				else{
					img_url = profilepicBaseURL + data.object.profile_pic_url;	
				}
				
				
				console.log("Image Url :" +img_url);
				var profile_pic = $("<img style='width: 70px; height: 70px;'/>")
                .attr('src',img_url);
				var $td = $("<td style='width:75px;'>");
				$td.append(profile_pic);
				$tr.append($td);
				//$tr.append("<td><img src='images/user.png' alt='' style='width: 70px; height: 70px;' /></td>");
				$tr.append("<td><p style ='display:block; font-family: Arial , sans-serif; font-size : 16px; font-weight : normal;'>"+data.object.username+"</p><p>"+data.object.totalPoints+" points</p></td>");
				$tbody.append($tr);
				$tbody.append("</tbody>");
				$tbody.appendTo("#userpoints");

			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			//alert('LoginUser error: ' + textStatus);
		}
	});
}


function getLeaderBoard(url){
	$.ajax({
		type: 'GET',
		contentType: 'text/plain',
		url: url,
		success: function(data){
			console.log("getLeaderBoardURL:"+url);
			console.log("Inside getLeaderBoard");
			console.log(data);

			if(data.code == 500) {

				
				var userpoints = data.object;
				if(userpoints!=null){
					var leaderboard = $("div#leaderboard");
					leaderboard.empty();
					
					for(var i=0; i < userpoints.length; i++){
						
						var memberCont = $("<div/>").attr("class","col-md-4 memberdispl");
						var member = $("<div/>").attr("class","member");
						var totpoints = $("<p/>");
						var medalImg = $("<img/>");
						var userProfPic = $("<img/>");
						var usrName = $("<p/>");
						var tooltipCont = $("<a/>");
						
						var img_url;
						
						if(userpoints[i].profile_pic_url.indexOf("https://")> -1){
							img_url = userpoints[i].profile_pic_url;
						}
						else{
							img_url = profilepicBaseURL + userpoints[i].profile_pic_url;	
						}
						
						
						
						totpoints.text(userpoints[i].totalPoints);
						userProfPic.attr("src",img_url);
						userProfPic.attr("width","50");
						userProfPic.attr("height","50");
						usrName.text(userpoints[i].username);
						
						if(i==0){
							medalImg.attr("src",firstBadgeURL);
							medalImg.attr("width",16);
							medalImg.attr("z-index",1000);
							medalImg.attr("style","position:absolute;top:0px;");
							
							tooltipCont.attr("href","javascript:;");
							tooltipCont.attr("data-toggle","tooltip");
							tooltipCont.attr("title","Points "+userpoints[i].totalPoints);
							
							tooltipCont.append(userProfPic);
							member.append(medalImg);
							member.append(tooltipCont);
							member.append(usrName);
							memberCont.append(member);
							leaderboard.append(memberCont);	
						}
						else if(i==1){
							medalImg.attr("src",secondBadgeURL);
							medalImg.attr("width",16);
							medalImg.attr("z-index",1000);
							medalImg.attr("style","position:absolute;top:0px;");
							
							tooltipCont.attr("href","javascript:;");
							tooltipCont.attr("data-toggle","tooltip");
							tooltipCont.attr("title","Points "+userpoints[i].totalPoints);
							
							tooltipCont.append(userProfPic);
							member.append(medalImg);
							member.append(tooltipCont);
							member.append(usrName);
							memberCont.append(member);
							leaderboard.append(memberCont);
						}
						else if(i==2){
							medalImg.attr("src",thirdBadgeURL);
							medalImg.attr("width",16);
							medalImg.attr("z-index",1000);
							medalImg.attr("style","position:absolute;top:0px;");
							
							tooltipCont.attr("href","javascript:;");
							tooltipCont.attr("data-toggle","tooltip");
							tooltipCont.attr("title","Points "+userpoints[i].totalPoints);
							
							tooltipCont.append(userProfPic);
							member.append(medalImg);
							member.append(tooltipCont);
							member.append(usrName);
							memberCont.append(member);
							leaderboard.append(memberCont);
						}
						else{
							
							tooltipCont.attr("href","javascript:;");
							tooltipCont.attr("data-toggle","tooltip");
							tooltipCont.attr("title","Points "+userpoints[i].totalPoints);
							
							tooltipCont.append(userProfPic);
							member.append(tooltipCont);
							member.append(usrName);
							memberCont.append(member);
							leaderboard.append(memberCont);
						}
					}
				}//END if(point not null)
				
			}//END IF(500)
			
			$('a[href="javascript:;"]').tooltip();
			
		},
		error: function(jqXHR, textStatus, errorThrown){
			//alert('LoginUser error: ' + textStatus);
		}
	});
}


function getUserBadges(url, jsonObj){
	
	
	$.ajax({
		type: 'POST',
		contentType: 'text/plain',
		url: url,
		success: function(data){
			console.log("Inside getBadges");
			console.log(data);
			var badges = data.object;
			if(data.code == 500) {

				$.ajax({
					type: 'POST',
					contentType: 'text/plain',
					url: UserBadgesURL,
					data:jsonObj,
					success: function(data){
						console.log("Inside getUserBadges");
						$('#userbadges').empty();
						if(badges.length){
						for(var i=0;i<badges.length;i++) {
							
							var $div = $("<div class='col-md-6 memberdispl'>");
							var flag = "false";
							
							if(data.object != null) {
							
								for(var j=0;j<data.object.length;j++) 
								{
									if(data.object[j]==null)
										continue;
									
									if(data.object[j].badgeId == badges[i].badgeId)
									{
										flag = "true";
										break;
									}
								}
							}

							var $badgeimg;
							
							var tooltipCont = $("<a/>");
							
							if(flag == "true"){
								
								tooltipCont.attr("href","javascript:;");
								$badgeimg = $("<img width='120'/>").attr('src',badges[i].activeBadgeUrl);
							}
							else{
								
								tooltipCont.attr("href","javascript:;");
								tooltipCont.attr("data-toggle","tooltip");
								tooltipCont.attr("title","Earn "+badges[i].activationPoints +" points to unlock");
								$badgeimg = $("<img width='120'/>").attr('src',badges[i].disabledBadgeUrl);
							}
								
							
							var $div1 = $("<div class='member'>");

							tooltipCont.append($badgeimg);
							$div1.append(tooltipCont);
							$div1.append("</div>");
							$div.append($div1);
							$div.append("</div>");
							$div.appendTo("#userbadges");
							
						}
					}
						
						$('a[href="javascript:;"]').tooltip();
					},
					error: function(jqXHR, textStatus, errorThrown){
						alert('LoginUser error: ' + textStatus);
					}
				});




			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('LoginUser error: ' + textStatus);
		}
	});
}
