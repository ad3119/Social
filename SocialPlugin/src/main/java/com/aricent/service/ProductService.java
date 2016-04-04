package com.aricent.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aricent.constant.GlobalConstants;
import com.aricent.model.Product;
import com.aricent.model.ProductUser;
import com.aricent.transaction.ProductTransaction;
import com.aricent.transaction.RestResponse;

@Service
@Path("/products")
public class ProductService {
	@Autowired
	ProductTransaction oProductTransaction;

	private static final Logger logger = Logger.getLogger(ProductService.class);

	@POST
	@Path("/insertProduct")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse createProduct(Product product) {
		product.setProductCreated(new Date());
		product.setProductLastUpdated(new Date());
		int result = oProductTransaction.insertProduct(product);
		RestResponse resp;
		if (result == 1) {
			resp = new RestResponse(null, GlobalConstants.OPRERATION_SUCCESS,
					null);
		} else {
			resp = new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		}
		return resp;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse deleteProduct(String productId) {
		int result = oProductTransaction.removeProduct(productId);
		RestResponse resp;
		if (result == 1) {
			resp = new RestResponse(null, GlobalConstants.OPRERATION_SUCCESS,
					null);
		} else {
			resp = new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);
		}
		return resp;
	}

	@POST
	@Path("/getProductById")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductById(String productId) {
		Product product = oProductTransaction.findProductById(productId);
		RestResponse resp = new RestResponse(product,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/getProductByName")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductByName(String productName) {

		Product product = oProductTransaction.findProductByName(productName);
		RestResponse resp = new RestResponse(product,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/getProductsByRating")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductByRating(String productRating) {
		List<Product> productList = new ArrayList<Product>();
		productList = oProductTransaction.findProductByRating(productRating);
		RestResponse resp = new RestResponse(productList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/getProductsByCategory")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductsByCategory(String category) {
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			List<Product> productList = oProductTransaction
					.findProductsByCategory(category);
			resp = new RestResponse(productList,
					GlobalConstants.OPRERATION_SUCCESS, null);
			return resp;
		} catch (Exception e) {
			return resp;
		}
	}

	@POST
	@Path("/productViewed")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse productViewed(ProductUser product) {
		logger.info("Inside product View : " + product.getUserBase() + " ::"
				+ product.getProduct());
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			String product1 = oProductTransaction.viewedProductByUser(product);
			if ("success".equals(product1)) {
				resp = new RestResponse(product,
						GlobalConstants.OPRERATION_SUCCESS, null);
			}
			return resp;
		} catch (Exception e) {
			return resp;
		}
	}

	@POST
	@Path("/itemsFromBH")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductsFromBH(String userBase) {

		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);

		try {
			List<ProductUser> productList = oProductTransaction
					.fetchProductFromBH(userBase);
			logger.info("Inside Items in cart" + userBase + " ::"
					+ productList.size());
			resp = new RestResponse(productList,
					GlobalConstants.OPRERATION_SUCCESS, null);
		} catch (Exception e) {
			logger.info("Exception Occured in getProductsFromBH "
					+ e.getMessage());
		}
		return resp;
	}

	@POST
	@Path("/productsViewedRecently")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductsViewedRecently(String userBase) {
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			List<ProductUser> productList = oProductTransaction
					.fetchProductsRecentlyViewed(userBase);
			logger.info("Inside Items in cart" + userBase + " ::"
					+ productList.size());
			resp = new RestResponse(productList,
					GlobalConstants.OPRERATION_SUCCESS, null);

		} catch (Exception e) {
			logger.info("Exception Occured in getProductsViewedRecently "
					+ e.getMessage());
		}
		return resp;
	}

	@POST
	@Path("/itemsInCart")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getProductsInCart(String userBase) {

		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			List<ProductUser> productList = oProductTransaction
					.fetchProductInCart(userBase);
			logger.info("Inside Items in cart" + userBase + " ::"
					+ productList.size());
			resp = new RestResponse(productList,
					GlobalConstants.OPRERATION_SUCCESS, null);
			return resp;
		} catch (Exception e) {
			return resp;
		}
	}

	@POST
	@Path("/addToCart")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse addItemsToCart(ProductUser productUser) {
		logger.info("Inside addTo CArt View ::" + productUser.getPrice() + ": "
				+ productUser.getUserBase() + " ::" + productUser.getProduct());
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			String product = oProductTransaction.addProductToCart(productUser);
			if ("Success".equals(product)) {
				resp = new RestResponse(product,
						GlobalConstants.OPRERATION_SUCCESS, null);
			}
			return resp;
		} catch (Exception e) {
			return resp;
		}
	}

	@POST
	@Path("/deleteFromCart")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse deleteItemsFromCart(ProductUser productUser) {
		logger.info("Inside addTo CArt View ::" + productUser.getPrice() + ": "
				+ productUser.getUserBase() + " ::" + productUser.getProduct());
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			String product = oProductTransaction
					.deleteProductFromCart(productUser);
			if ("success".equals(product)) {
				resp = new RestResponse(product,
						GlobalConstants.OPRERATION_SUCCESS, null);
			}
			return resp;
		} catch (Exception e) {
			return resp;
		}
	}

	@POST
	@Path("/productsInOthersCart")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse productsInOthersCart(String userBase) {
		logger.info("Inside productsInOthersCart ::" + userBase);
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			List<Product> prodList = oProductTransaction
					.fetchRecomendationForCart(userBase);
			resp = new RestResponse(prodList,
					GlobalConstants.OPRERATION_SUCCESS, null);
			return resp;
		} catch (Exception e) {
			return resp;
		}
	}

	@GET
	@Path("/getAllProducts")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse getAllProducts() {
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		try {
			List<Product> productList = new ArrayList<Product>();
			productList = oProductTransaction.findAllProducts();
			resp = new RestResponse(productList,
					GlobalConstants.OPRERATION_SUCCESS, null);
		} catch (Exception e) {
			return resp;
		}
		return resp;
	}
}