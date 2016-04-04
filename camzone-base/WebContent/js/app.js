var app = angular.module('camZone', ['aricabApp','ngRoute','controllers']);

app.config(function($routeProvider) {
    $routeProvider
    	.when('/home', {
    		templateUrl: 'views/home.html',
            controller: 'HomeCtrl'
    	})
    	.when('/product/:param', {
    		templateUrl: 'views/product.html',
            controller: 'ProductCtrl'
    	})
    	.when('/categories/Camera', {
    		templateUrl: 'views/camera.html',
            controller: 'CameraCtrl'
    	})
    	.when('/webRtc', {
    		templateUrl: 'views/avmainpage.html',
            controller: 'audioVideoSimpleCtrl'
    	})
		.when('/categories/cameraAccesories', {
    		templateUrl: 'views/cameraAccesories.html',
            controller: 'CameraAccesoryCtrl'
    	})
    	.when('/cart', {
    		templateUrl: 'views/cart.html',
            controller: 'CartCtrl'
    	})
    	.otherwise({
            redirectTo: '/home'
        });
});


app.run(function($rootScope, $location) { //Insert in the function definition the dependencies you need.
        $rootScope.$on("$locationChangeStart", function(event, next, current) {
            if (easyrtc.webSocket) {
                easyrtc.disconnect();
            }
        });
});


app.factory('productRepository', function($http) { 
	
	return {
	   getAllProducts: function() {
		   return $http.get(socialPluginConfig.getProductsUrl);
	   },
	   getAllProductByCategory: function(camera) {
		   return $http.post(socialPluginConfig.getProductsByCategoryUrl,camera);
	   }, 
	   viewedProduct : function(product) {
		   return $http.post(socialPluginConfig.getProductViewedUrl, product);
	   }, 
	   fetchItemsInCart : function(userBase) {
		   return $http.post(socialPluginConfig.getProductsInCart, userBase);
	   },
	   addItemToCart : function(userBase) {
		   return $http.post(socialPluginConfig.getAddToCartUrl, userBase);
	   },
	   getProductByName : function(product_id) {
		   return $http.post(socialPluginConfig.getproductByNameUrl, product_id);
	   },
	   getProductById : function(product_id) {
		   return $http.post(socialPluginConfig.getproductByIdUrl, product_id);
	   },
	   deleteFromCart : function(params) {
		   return $http.post(socialPluginConfig.getDeleteFromCartUrl, params);
	   }, 
	   getRecommendedProducts : function(userId){
		   return $http.get(socialPluginConfig.getRecommendedProductsUrl+userId+"/10");
	   },
	   getRecommendedProductsFromBH : function(userBase)  {
		   return $http.post(socialPluginConfig.getRecommendedProductsFromBHUrl, userBase);
	   },
	   getRecentlyViewedproducts : function(userBase) {
		   return $http.post(socialPluginConfig.getproductsViewedRecentlyUrl, userBase);
	   },
	   getRecommendationForCart : function(userBase) {
		   return $http.post(socialPluginConfig.getCartingRecommendationUrl, userBase);
	   }
	}
});



prodList = [
    {name:"Nikon D5300", image:"nikon-1-d5300.png", description:"Featuring enhanced quality and speed, the EXPEED 4 image processor is the perfect companion to the 24.2-megapixel DX-format CMOS"}, 
    {name:"Nikon D810", image:"nikon-1-d810.png", description:"Redefine the possibilities of high-megapixel video and still photography with the full-frame performance of the new and improved Nikon D810"},
    {name:"Nikon D750", image:"nikon-1-d750.png", description:"Packed with an array of powerful features in an incredibly compact frame, the Nikon D750 is the ideal companion on your shootings"},
    {name:"Cannon EOS 1100d", image:"canon-eos-1100d.jpeg", description:"Canon EOS 1100D is an advanced and affordable camera that encourages DSLR beginners with its user friendly interface."},
    {name:"Cannon EOS 1200d", image:"canon-eos-1200d.jpeg", description:"The Canon EOS 1200D is the perfect DSLR camera for novice photographers."},
    {name:"Cannon EOS 60d", image:"canon-eos-60d.jpeg", description:"Canon 60D is an 18 megapixel SLR camera powered by DIGIC 4 Image processor for high quality image."}
];
accesoryList = [
    {name:"Nikon AF-S DX VR Zoom-Nikkor 55 - 200 mm f/4-5.6G IF-ED Lens", image:"lens3.jpeg" ,description:"Compactly designed, this Nikon AF-S DX format 55- 200 mm NIKKOR lens is constructed with 15 elements in 11 groups. Also, it features an extra-low dispersion (ED) glass element which helps to compensate chromatic aberrations. Moreover, it comes with 52 mm filter attachment size. So, go ahead and attach compatible screw-on filters of your choice and create pictures the way you want it."},
    {name:"Photron Stedy 450 Tripod", image:"tripod.jpeg", description:"The Photron Tripod is designed with a self-adjusting camera platform that perfectly seats the camera in the slot. It comes with a 3-way multipurpose pan head that you can tilt and turn to suit your shot. The tripod comes with a quick-flip lever leg lock that can be used to lock the leg firmly."},         
    {name:"SanDisk SDHC 16 GB Class 10", image:"memoryCard.jpeg", description:"SanDisk Ultra SDHC memory card has a Class 10 speed ratings - an ideal rating for Full HD (1080p) videos. Have a pleasurable experience while capturing Full HD videos when with family and friends, without having to worry about the quality and memory used by the camera. Note: The Full HD (1920x1080) video support may vary depending upon the host device, file attributes and other factors."}             
]
         
				