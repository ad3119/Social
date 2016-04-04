var app = angular.module('aricabApp',[]);
app.service("aricabAppService", function() {
    return {
        sharedObject: {
            data: null
        }
    }
});

app.factory("aricabDataService", function($http, $q) {
    return ({
        getDriverData: getData
    });
    function getData() {
        var request = $http({
            method: "get",
            url: "http://10.150.10.172:3000/data/drivers.json",
            params: {
                action: "get"
            }
        });
        return (request.then(handleSuccess, handleError));
    }
    function handleError(response) {
        console.log("error occured");
    }
    function handleSuccess(response) {
        return (response.data);
    }
});



app.controller('audioVideoSimpleCtrl', ['$scope', 'aricabAppService', '$location', 'aricabDataService', '$http', function($scope, aricabAppService, $location, aricabDataService, $http) {
    
	widget.init("webrtc");
	var _init = function() {
        console.log('calling init...');
        $scope.user = {};
        $scope.users = [];
        $scope.chat = {
            allMsgs: '',
            msg: ''
        };
		var login_cookie = JSON.parse($.cookie("login_data")); 
        easyrtc.setRoomApiField("default", "username", login_cookie.username);
        easyrtc.setPeerListener(_peerListener);
        easyrtc.setRoomOccupantListener(_convertListToButtons);
        easyrtc.easyApp("easyrtc.audioVideo", "selfVideo", ["callerVideo"], _loginSuccess, _loginFailure);
    };

  $scope.logout = function() {
        $location.path("/");
    }

    var drawMap = function() {
		
		var driverdata=[];
		var chartheader = ["Lat", "Long", "Name"];
		driverdata.push(chartheader);
		aricabDataService.getDriverData().then(
            function(data) {
				//alert(JSON.stringify(data));
				var tmp = data;
				$scope.driverData = data;
				for(i=0;i < tmp.drivers.length; i++){
					driverdata.push(tmp.drivers[i].location_details);
				}
				var cdata = google.visualization.arrayToDataTable(driverdata);
				var options = {
					showTip: true
				};
				var map = new google.visualization.Map(document.getElementById('chart_div'));
				map.draw(cdata, options);
            }
        );
    };

	$scope.sendMsg = function(drivername) {
		console.log(drivername);
		console.log(document.getElementById('txtMsg').value);
		var msgTxt = document.getElementById('txtMsg').value;
		var driverArr = $scope.driverData;
		for(k=0; k<driverArr.drivers.length; k++){
			if(drivername === driverArr.drivers[k].drivername){
				console.log("got driver");
				var smsGatewayUrl = "http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=himalaya18@gmail.com:himalaya89&senderID=TEST SMS&receipientno=9620014144&dcs=0&msgtxt="+ msgTxt +"&state=4";
				var request = $http({
					method: "get",
					url: smsGatewayUrl,
					params: {
						action: "get"
					}
				});
				break;
			}
		}
	};
	
    $scope.call = function(to) {
        _hangupAllClients();
        easyrtc.call(to, _callSuccess, _callFailed, _callAccepted);
        easyrtc.sendDataWS(to, "chat:videoconnected", null);
    };

    $scope.hangup = function(to) {
        _hangupAllClients();
        easyrtc.sendDataWS(to, "chat:videodisconnected", null);
    };

    var _peerListener = function(who, msgType, content) {
        if (msgType === 'chat:videoconnected') {
            _findUserByEasyRtcId(who).isConnected = true;
        } else if (msgType === 'chat:videodisconnected') {
            _findUserByEasyRtcId(who).isConnected = false;
        }

        $scope.$apply();
    };

    var _callSuccess = function(to) {
        _findUserByEasyRtcId(to).isConnected = true;
        $scope.$apply();
    };

    var _callFailed = function(errCode, errMsg) {};

    var _callAccepted = function(accepted, from) {};

    var _hangupAllClients = function() {
        easyrtc.hangupAll();

        if ($scope.users) {
            for (var i = 0; i < $scope.users.length; i++) {
                $scope.users[i].isConnected = false;
            }
        }
    };

    var _loginSuccess = function(easyrtcid) {
        $scope.user.easyRtcId = easyrtcid;
        $scope.user.name = aricabAppService.sharedObject;
        $scope.$apply();
    };

    var _loginFailure = function(errorCode, message) {
        easyrtc.showError(errorCode, message);
        $scope.$apply();
    };

    /*var _convertListToButtons = function(roomName, occupants, isPrimary) {
        var oldUsers = angular.copy($scope.users);
        $scope.users = [];

        for (easyRtc in occupants) {
            var username = easyrtc.getRoomApiField("default", easyRtc, "username");
			var drivernumber;
			var CabNumber;
			var location;
			var driverArr = $scope.driverData;
			for(k=0; k<driverArr.drivers.length; k++){
				if(username === driverArr.drivers[k].drivername){
					console.log("got driver");
					drivernumber = driverArr.drivers[k].drivernumber;
					CabNumber = driverArr.drivers[k].CabNumber;
					location = driverArr.drivers[k].location_details[2];
					break;
				}
			}
            console.log(username);
            $scope.users.push({
                easyRtcId: easyRtc,
                name: username,
				drivnmbr:drivernumber,
				cabnmbr: CabNumber,
				loc: location,
                isConnected: _findOldUser(oldUsers, easyRtc)
            });
        }

        $scope.$apply();
    };*/
	
	var _convertListToButtons = function (roomName, occupants, isPrimary) {
        var oldUsers = angular.copy($scope.users);
        $scope.users = [];
        //var login_cookie = JSON.parse($.cookie("login_data")); 
		
        for (easyRtc in occupants) {
			
			var username = easyrtc.getRoomApiField("default", easyRtc, "username");
			
            $scope.users.push({
                easyRtcId: easyRtc,
                name: username,
                isConnected: _findOldUser(oldUsers, easyRtc)
            });
        }
        
        $scope.$apply();
    };

    var _findOldUser = function(oldUsers, easyRtc) {
        if (oldUsers) {
            for (var i = 0; i < oldUsers.length; i++) {
                if (oldUsers[i].easyRtcId === easyRtc) {
                    return oldUsers.isConnected;
                }
            }
        }

        return false;
    };

    var _findUserByEasyRtcId = function(id) {
        if (id) {
            for (var i = 0; i < $scope.users.length; i++) {
                if ($scope.users[i].easyRtcId === id) {
                    return $scope.users[i];
                }
            }
        }

        return null;
    };

    _init();
   // drawMap();
}]);