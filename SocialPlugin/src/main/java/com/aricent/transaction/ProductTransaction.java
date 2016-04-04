package com.aricent.transaction;

import java.util.List;

import com.aricent.model.Product;
import com.aricent.model.ProductUser;

public interface ProductTransaction {
	public int insertProduct(Product product);

	public Product findProductById(String productId);

	public Product findProductByName(String productName);

	public List<Product> findProductByRating(String productRating);

	public int removeProduct(String productId);

	public List<Product> findAllProducts();

	public List<Product> findProductsByCategory(String productCategory);

	public String viewedProductByUser(ProductUser category);

	public List<ProductUser> fetchProductInCart(String category);

	public String addProductToCart(ProductUser productUser);

	public String deleteProductFromCart(ProductUser productUser);

	public List<ProductUser> fetchProductFromBH(String userBase);

	public List<ProductUser> fetchProductsRecentlyViewed(String userBase);

	public List<Product> fetchRecomendationForCart(String userBase);
}
