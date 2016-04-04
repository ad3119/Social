var app = angular.module('controllers', ['aricabApp','ngRoute' ]);


app.filter('offset', function() {
	  return function(input, start) {
	    start = parseInt(start, 10);
	    if(input != undefined)
	    return input.slice(start);
	   };
});
	
app.controller('HomeCtrl', ['$scope', function ($scope) {
	$scope.myVar = true;
	$scope.products = [];
	$scope.accessories = [];
    $scope.images = [
		   {background:"pic-3.jpg", number:0},
		   {background:"pic-2.jpg", number:1},
		   {background:"pic-1.jpg", number:2},
		   {background:"pic-4.jpg", number:3},
		   {background:"pic-5.jpg", number:4}	
    ];
    
	
	/*
	 * var getProducts = function() {
		$http({
            url: socialPluginConfig.getWidgetsUrl,
            method:  'GET',
            async:   false
        }).
        success(function(data, status, headers, config) {
        	prodList= data.object;
        });
	}*/
    
    var defineScopeProductList = function() {
	    for( var i=0 ; i<3 ; i++) {
	    	 $scope.products.push(prodList[i]);
	    	 $scope.accessories.push(accesoryList[i]);
	   }
    }
  
   defineScopeProductList();
   widget.init("Home");
    
}])

app.controller('ProductCtrl', ['$scope','$http','$location', '$routeParams','productRepository', function($scope,$http,$location,$routeParams, prodRepository) {
	
	var product_id = $routeParams.param;
    console.log("Product id :"+product_id);
    $scope.product ;
    
    prodRepository.getProductById(product_id).success(function(data) {
    	
    	$scope.product = data.object;
    	if($scope.product.totalNumberOfRatings == undefined)
    		$scope.product.totalNumberOfRatings =  0;
    });
    
    
    $scope.showRecommendations = true;
    $scope.showproductsBH = true;
    $scope.showrecentlyViewed = true;
    $scope.currentPageRE = 0;
    
    $scope.itemsPerPage = 3;
    $scope.quantity = 0;
	$scope.recommendProduts = [];
	$scope.recentlyViewedProducts = [];
	$scope.recommendProdutsFromBH = [];
	$scope.clickedProduct = function(product) {
		console.log("In getViewedProducts:");
		console.log(product);
		if(product.id == undefined) {
			product.id = product.productId;
		}
		
		if($.cookie("login_data")) {
    		var loginCookie = JSON.parse($.cookie("login_data"));
    		var parameters = {
    				userBase : loginCookie.userbase,
    				product : product.productName, 
    				productId:product.id,
    				price:product.price
    		}
    		prodRepository.viewedProduct(JSON.stringify(parameters)).success(function(prod) {
    			
    			var url = "product/"+product.id;
    			$location.path(url);
    		});
		}
		else {
			var url = "product/"+product.id;
			$location.path(url);
		}
		
	}
    $scope.addToCart =  function(product) {
    	var quant = $scope.quantity;
    	if($.cookie("login_data")){
    		if($scope.quantity != undefined && $scope.quantity > 0) {
	    		var loginCookie = JSON.parse($.cookie("login_data"));
	    		var parameters = {
	    				userBase : loginCookie.userbase,
	    				product : product.productName, 
	    				price : product.price, 
	    				quantity : quant, 
	    				productId : product.id
	    		}
	    		prodRepository.addItemToCart(JSON.stringify(parameters)).success(function(data) {
	    			alert("Successfully Added to Cart");
	    		});
	    	}
	    	else {
	    		alert("Please select atleast 1")
	    	}
    	}
    	else {
    		alert("Please Login to add products to cart")
    	}
    }
    
   recuserId = "";
   var getRecommendProducts = function() {
		if($.cookie("login_data")){
			var login_cookie = JSON.parse($.cookie("login_data"));
			console.log("Login Details = "+JSON.stringify(login_cookie))
			if((login_cookie) && (login_cookie.userbase)){	
				
				if(login_cookie.email == "Vipul@gmail.com") {
					recuserId = "1";
				}
				else if(login_cookie.email == "cynargyapp@gmail.com") {
					recuserId = "2";
				}
				else if(login_cookie.email == "adminCynargyapp@gmail.com") {
					recuserId = "3";
				}
				else if(login_cookie.email == "demo@gmail.com") {
					recuserId = "4";
				}
				else if(login_cookie.email == "adminCynargy@gmail.com") {
					recuserId = "5";
				}
				else if(login_cookie.email == "dummy@xxx.com") {
					recuserId = "6";
				}
				else{
					recuserId = "10";
				}
			}
			
			// Code for Recommendations from Spark
			prodRepository.getRecommendedProducts(recuserId).success(function(recommendation){
				var recommendedItemsArray = recommendation.object;
				recommendedItems =  recommendedItemsArray.split(",");
				$scope.showRecommendations = false;
				angular.forEach(recommendedItems, function(value) {
					prodRepository.getProductById(value).success(function(result){
						
						if(result.code == 500 && result.object != null){
							$scope.recommendProduts.push(result.object);
							
						}
					})
				});
			})
			
			// Code for Recommendations from Browsing History
			prodRepository.getRecommendedProductsFromBH(login_cookie.userbase).success(function(data) {
				if(data.object != null && data.code == "500") {
					$scope.showproductsBH = false;
					$scope.currentPageBH = 0;
					var splicingIndex = -1;
					angular.forEach(data.object, function(value, index) {
						if(value.productId == product_id) {
							splicingIndex = index;
						}
					});
					if(splicingIndex >= 0){
						data.object.splice(splicingIndex, 1);
					}
					for(var i=0;i<10 && i< data.object.length ;i++) {
						$scope.recommendProdutsFromBH.push(data.object[i]);
					}
				}
			});
			
			
			// Code for fetching recently viewed Items
			prodRepository.getRecentlyViewedproducts(login_cookie.userbase).success(function(data) {
				$scope.currentPage = 0;
				if(data.object != null && data.code == "500") {
					$scope.showrecentlyViewed = false;
					data.object.splice(0, 1);
					for(var i=0;i<10 && i< data.object.length  ;i++) {
							$scope.recentlyViewedProducts = data.object;
					}
				}
			})
		}
   	}
 

	 $scope.prevPage = function() {
	    if ($scope.currentPage > 0) {
	      $scope.currentPage--;
	    }
	  }

	  $scope.pageCount = function() {
	    return Math.ceil($scope.recentlyViewedProducts.length/$scope.itemsPerPage)-1;
	  }

	  $scope.nextPage = function() {
	    if ($scope.currentPage < $scope.pageCount()) {
	      $scope.currentPage++;
	    }
	  }

	  $scope.prevPageBH = function() {
		    if ($scope.currentPageBH > 0) {
		      $scope.currentPageBH--;
		    }
	  };

	  $scope.pageCountBH = function() {
		    return Math.ceil($scope.recommendProdutsFromBH.length/$scope.itemsPerPage)-1;
	  }
	  
	  $scope.nextPageBH = function() {
		if ($scope.currentPageBH < $scope.pageCountBH()) {
			$scope.currentPageBH++;
		}
	  }
	  
	  $scope.prevPageRE = function() {
		    if ($scope.currentPageRE > 0) {
		      $scope.currentPageRE--;
		    }
	  };

	  $scope.pageCountRE = function() {
		    return Math.ceil($scope.recommendProduts.length/$scope.itemsPerPage)-1;
	  }
	  
	  $scope.nextPageRE = function() {
		if ($scope.currentPageRE < $scope.pageCountRE()) {
			$scope.currentPageRE++;
		}
	  }
	  
	  
  	getRecommendProducts();
    widget.init("Product");
 
}])

app.controller('CameraCtrl', ['$scope','$location','productRepository', function($scope, $location ,prodRepository) {
	
	$scope.itemsPerPage = 9;
	$scope.currentPage = 0;
	 	
	prodRepository.getAllProductByCategory("Camera")
    .success(function(prod) {
    	$scope.products = prod.object;
    	
    });
	
	/*$scope.range = function() {
	    var rangeSize = 10;
	    var ret = [];
	    var start;

	    start = $scope.currentPage;
	    if ( start > $scope.pageCount()-rangeSize ) {
	      start = $scope.pageCount()-rangeSize+1;
	    }

	    for (var i=start; i<start+rangeSize; i++) {
	      ret.push(i);
	    }
	    return ret;
	};*/

	  $scope.prevPage = function() {
	    if ($scope.currentPage > 0) {
	      $scope.currentPage--;
	    }
	  };
	  
	  
	  $scope.prevPageDisabled = function() {
	    return $scope.currentPage === 0 ? "disabled" : "";
	  };

	  $scope.pageCount = function() {
	    return Math.ceil($scope.products.length/$scope.itemsPerPage)-1;
	  };
	  
	  $scope.nextPage = function() {
	    if ($scope.currentPage < $scope.pageCount()) {
	      $scope.currentPage++;
	    }
	  };
	  $scope.nextPageDisabled = function() {
	    return $scope.currentPage === $scope.pageCount() ? "disabled" : "";
	  };
/*
	  $scope.setPage = function(n) {
	    $scope.currentPage = n;
	  };*/
	
	
	$scope.clickedProduct = function(product) {
		if($.cookie("login_data")) {
    		var loginCookie = JSON.parse($.cookie("login_data"));
    		var parameters = {
    				userBase : loginCookie.userbase,
    				product : product.productName, 
    				productId:product.id,
    				price:product.price
    		}
    		prodRepository.viewedProduct(JSON.stringify(parameters)).success(function(prod) {
    			var url = "product/"+product.id;
    			$location.path(url);
    		});
		}
		else {
			var url = "product/"+product.id;
			$location.path(url);
		}
		
	}
	widget.init("Camera");
}])

app.controller('CameraAccesoryCtrl', ['$scope', function($scope){
  $scope.products = accesoryList;
  initiliseReviewAndRating();
}])

app.controller('CartCtrl', ['$scope','$location','productRepository', function($scope,$location,prodRepository){
	$scope.cartListLength = "0";
	$scope.itemsPerPage = 3;
	$scope.currentPage = 0;
	$scope.notloggedIn = true;
	$scope.recommendedFromCart = [];
	if($.cookie("login_data")) {
		$scope.notloggedIn = false; 
		loginCookie = JSON.parse($.cookie("login_data"));
		prodRepository.fetchItemsInCart(loginCookie.userbase).success(function(data) {
			$scope.cartListLength = data.object.length;
			$scope.cartList =  data.object;
			if($scope.cartListLength > 0){
				$scope.cartItemsPresent= true;
			}
		})
		
		prodRepository.getRecommendationForCart(loginCookie.userbase).success(function(data){
			if(data.code != "501" && data.object.length > 0 ) {
				for(var i=0;i<10 && i<data.object.length ;i++) {
					var temp = false;
					for(var j=1;j< $scope.recommendedFromCart.length;j++) {
						if(data.object[i].productId =  $scope.recommendedFromCart[j].productId )
							temp =true;
					}
					if(!temp)
					$scope.recommendedFromCart.push(data.object[i]);
				}
			}
		})
	}
	else {
		$scope.cartList = [];
		$scope.recommendedFromCart = [];
		$scope.cartListLength = "0";
	}
	$scope.removeFromCart = function(cartItem) {
		var parameters = {
				userBase : loginCookie.userbase,
				product : cartItem.product, 
		}
		prodRepository.deleteFromCart(JSON.stringify(parameters)).success(function(data){
			if(data.code == "501") {
				alert("Can't delete: Some Error Occured");
			}
			else if(data.code == "500") {
				 var index = $scope.cartList.indexOf(cartItem);
				 $scope.cartList.splice(index, 1);  
				 --$scope.cartListLength;
			}
		})
	}
	$scope.clickedProduct = function(cartItem) {
		var loginCookie = JSON.parse($.cookie("login_data"));
		var getproductId = function() {
			if(cartItem.id == undefined) {
				return cartItem.productId;
			}
			else{
				return cartItem.id;
			}
		}
		var parameters = {
    		userBase : loginCookie.userbase,
    		product : cartItem.product,
    		productId : getproductId(),
    		price:cartItem.price
    	}
		prodRepository.viewedProduct(JSON.stringify(parameters)).success(function(prod) {
			if(cartItem.id == undefined) {
				var url = "product/"+cartItem.productId;
			}
			else {
				var url = "product/"+cartItem.id;
			}
    		$location.path(url);
    	});
	}
	
	$scope.prevPage = function() {
	    if ($scope.currentPage > 0) {
	      $scope.currentPage--;
	    }
	  }

	  $scope.pageCount = function() {
	    return Math.ceil($scope.recommendedFromCart.length/$scope.itemsPerPage)-1;
	  }

	  $scope.nextPage = function() {
	    if ($scope.currentPage < $scope.pageCount()) {
	      $scope.currentPage++;
	    }
	  }

	widget.init("Cart");
}])