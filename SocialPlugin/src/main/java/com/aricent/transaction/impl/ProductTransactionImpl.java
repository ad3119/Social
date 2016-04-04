package com.aricent.transaction.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlTemplate;
import com.aricent.model.CartGrouping;
import com.aricent.model.Product;
import com.aricent.model.ProductUser;
import com.aricent.transaction.ProductTransaction;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;

public class ProductTransactionImpl implements ProductTransaction {
	@Autowired
	private CqlTemplate cassandraTemplate;
	public static final String USER_KEYSPACE = "user_tables";
	public static final String PRODUCT_COLLECTION = "productcollection";
	public static final String PRODUCT_USER_COLLECTION = "product_user";

	private static final Logger logger = Logger
			.getLogger(ProductTransactionImpl.class);

	public void init() {
		logger.info("Initializing ProductTransaction");

		try {

			// cassandraTemplate.execute("DROP TABLE user_tables.product_user");
			cassandraTemplate
					.execute("CREATE TABLE IF NOT EXISTS user_tables.product_user ("
							+ "userBase text,"
							+ "product text,"
							+ "viewedCount int,"
							+ "addedToCart boolean,"
							+ "quantity int,"
							+ "price text,"
							+ "productId text,"
							+ "viewedTime timestamp,"
							+ "PRIMARY KEY (userBase,productId)" + ");");

			logger.info("Created product_user.");
		} catch (Exception e) {
			logger.info("Initializing Failed");
			logger.info(e.getMessage());
			// logger.info(e.getStackTrace());
		}

	}

	@Override
	public int insertProduct(Product product) {
		try {
			// mongoTemplate.save(product, PRODUCT_COLLECTION);
			Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			insert.value("id", product.getid());
			insert.value("productName", product.getProductName());
			insert.value("productRating", product.getProductRating());
			insert.value("description", product.getDescription());
			insert.value("category", product.getCategory());
			insert.value("price", product.getPrice());
			insert.value("productCreated", product.getProductCreated());
			insert.value("productLastUpdated", product.getProductLastUpdated());
			insert.value("totalNumberOfRatings",
					product.getTotalNumberOfRatings());
			cassandraTemplate.execute(insert);
			logger.info("Product " + product.getProductName()
					+ " inserted successfully");
			return 1;
		} catch (Exception e) {
			logger.error("Failed to insert product");

			return 0;
		}
	}

	@Override
	public int removeProduct(String productId) {
		try {
			Delete productDelete = QueryBuilder.delete().from(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			productDelete.where(QueryBuilder.eq("id", productId));
			cassandraTemplate.execute(productDelete);
			logger.info("Product removed successfully");
			return 1;
		} catch (Exception e) {
			logger.error("Failed to remove product");

			return 0;
		}
	}

	@Override
	public Product findProductById(String productId) {
		try {

			// logger.info(productId);
			Product product = null;
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			productSelect.where(QueryBuilder.eq("id", productId));
			Row row = cassandraTemplate.query(productSelect).one();
			if (row != null) {
				product = new Product();
				product.setid(row.getString("id"));
				product.setProductName(row.getString("productName"));
				product.setProductRating(row.getFloat("productRating"));
				product.setDescription(row.getString("description"));
				product.setCategory(row.getString("category"));
				product.setPrice(row.getString("price"));
				product.setProductCreated(row.getDate("productCreated"));
				product.setProductLastUpdated(row.getDate("productLastUpdated"));
				product.setTotalNumberOfRatings(row
						.getInt("totalNumberOfRatings"));

			}

			if (product != null)
				logger.info("Product found");
			else
				logger.info("Product not found");
			return product;
		} catch (Exception e) {
			logger.error("Error in finding product");

			return null;
		}
	}

	@Override
	public Product findProductByName(String productName) {
		try {

			Product product = null;
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			Row row = cassandraTemplate.query(productSelect).one();
			if (row != null) {
				if (row.getString("productName").equals(productName)) {
					product = new Product();
					product.setid(row.getString("id"));
					product.setProductName(row.getString("productName"));
					product.setProductRating(row.getFloat("productRating"));
					product.setDescription(row.getString("description"));
					product.setCategory(row.getString("category"));
					product.setPrice(row.getString("price"));
					product.setProductCreated(row.getDate("productCreated"));
					product.setProductLastUpdated(row
							.getDate("productLastUpdated"));
					product.setTotalNumberOfRatings(row
							.getInt("totalNumberOfRatings"));
				}

			}

			if (product != null)
				logger.info("Product found");
			else
				logger.info("Product not found");
			return product;
		} catch (Exception e) {
			logger.error("Error in finding product");

			return null;
		}
	}

	@Override
	public List<Product> findProductByRating(String productRating) {
		try {

			List<Product> productList = new ArrayList<Product>();
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			Row row = cassandraTemplate.query(productSelect).one();
			if (row != null) {
				if (row.getString("productRating").equals(productRating)) {
					Product product = new Product();
					product.setid(row.getString("id"));
					product.setProductName(row.getString("productName"));
					product.setProductRating(row.getFloat("productRating"));
					product.setDescription(row.getString("description"));
					product.setCategory(row.getString("category"));
					product.setPrice(row.getString("price"));
					product.setProductCreated(row.getDate("productCreated"));
					product.setProductLastUpdated(row
							.getDate("productLastUpdated"));
					product.setTotalNumberOfRatings(row
							.getInt("totalNumberOfRatings"));
					productList.add(product);
				}

			}

			if (!productList.isEmpty())
				logger.info(productList.size() + " products found");
			else
				logger.info("No products found");
			return productList;
		} catch (Exception e) {
			logger.error("Error in finding products");

			return null;
		}
	}

	@Override
	public List<Product> findAllProducts() {
		try {

			List<Product> productList = new ArrayList<Product>();
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			Row row = cassandraTemplate.query(productSelect).one();
			if (row != null) {

				Product product = new Product();
				product.setid(row.getString("id"));
				product.setProductName(row.getString("productName"));
				product.setProductRating(row.getFloat("productRating"));
				product.setDescription(row.getString("description"));
				product.setCategory(row.getString("category"));
				product.setPrice(row.getString("price"));
				product.setProductCreated(row.getDate("productCreated"));
				product.setProductLastUpdated(row.getDate("productLastUpdated"));
				product.setTotalNumberOfRatings(row
						.getInt("totalNumberOfRatings"));
				productList.add(product);

			}

			if (productList != null)
				logger.info(productList.size() + " product(s) found");
			else
				logger.info("No products found");
			return productList;
		} catch (Exception e) {
			logger.error("Error in finding products");

			return null;
		}
	}

	@Override
	public List<Product> findProductsByCategory(String productCategory) {
		try {

			List<Product> productList = new ArrayList<Product>();
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_COLLECTION);
			List<Row> rows = cassandraTemplate.query(productSelect).all();
			for (Row row : rows) {
				if (row.getString("category") != null) {
					if (row.getString("category").equals(productCategory)) {
						Product product = new Product();
						if (row.getString("id") != null)
							product.setid(row.getString("id"));
						else
							product.setid("999");
						if (row.getString("productName") != null)
							product.setProductName(row.getString("productName"));
						product.setProductRating(row.getFloat("productRating"));
						if (row.getString("description") != null)
							product.setDescription(row.getString("description"));
						if (row.getString("category") != null)
							product.setCategory(row.getString("category"));
						if (row.getString("price") != null)
							product.setPrice(row.getString("price"));
						if (row.getDate("productCreated") != null)
							product.setProductCreated(row
									.getDate("productCreated"));
						if (row.getDate("productLastUpdated") != null)
							product.setProductLastUpdated(row
									.getDate("productLastUpdated"));
						product.setTotalNumberOfRatings(row
								.getInt("totalNumberOfRatings"));
						productList.add(product);
					}
				}

			}

			if (!productList.isEmpty())
				logger.info(productList.size() + " product(s) found");
			else
				logger.info("No products found");
			return productList;
		} catch (Exception e) {

			logger.info(e.getMessage());
			logger.error("Error in finding products");

			return null;
		}
	}

	@Override
	public String viewedProductByUser(ProductUser productUser) {
		try {

			ProductUser product = null;
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_USER_COLLECTION);
			if (productUser.getProductId() != null)
				productSelect.where(QueryBuilder.eq("productId",
						productUser.getProductId()));
			productSelect.where(QueryBuilder.eq("userBase",
					productUser.getUserBase()));
			List<Row> rows = cassandraTemplate.query(productSelect).all();
			for (Row r : rows) {

				product = new ProductUser();
				product.setUserBase(r.getString("userBase"));
				product.setProduct(r.getString("product"));
				product.setViewedCount(r.getInt("viewedCount"));
				product.setAddedToCart(r.getBool("addedToCart"));
				product.setQuantity(r.getInt("quantity"));
				product.setPrice(r.getString("price"));
				product.setProductId(r.getString("productId"));
				product.setViewedTime(r.getDate("viewedTime"));

			}

			Date date = new Date();
			if (product != null) {
				Integer count = product.getViewedCount();
				Update update = QueryBuilder.update(USER_KEYSPACE,
						PRODUCT_USER_COLLECTION);
				update.with(QueryBuilder.set("viewedCount", ++count));
				update.with(QueryBuilder.set("viewedTime", date));
				update.where(
						QueryBuilder.eq("productId", productUser.getProductId()))
						.and(QueryBuilder.eq("userBase",
								productUser.getUserBase()));
				cassandraTemplate.execute(update);

			} else {
				productUser.setViewedCount(1);
				productUser.setQuantity(0);
				productUser.setAddedToCart(false);
				productUser.setViewedTime(date);
				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						PRODUCT_USER_COLLECTION);
				insert.value("userBase", productUser.getUserBase());
				insert.value("product", productUser.getProduct());
				insert.value("viewedCount", productUser.getViewedCount());
				insert.value("addedToCart", productUser.getAddedToCart());
				insert.value("quantity", productUser.getQuantity());
				insert.value("price", productUser.getPrice());
				insert.value("productId", productUser.getProductId());
				insert.value("viewedTime", productUser.getViewedTime());
				cassandraTemplate.execute(insert);
			}
			return "success";

		} catch (Exception e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	@Override
	public List<ProductUser> fetchProductInCart(String userbase) {
		try {
			List<ProductUser> productUserList = new ArrayList<ProductUser>();
			Select productUserSelect = QueryBuilder.select().from(
					USER_KEYSPACE, PRODUCT_USER_COLLECTION);
			productUserSelect.where(QueryBuilder.eq("userBase", userbase));
			List<Row> rows = cassandraTemplate.query(productUserSelect).all();
			for (Row r : rows) {
				if (r.getBool("addedToCart") == true) {
					ProductUser productUser = new ProductUser();
					productUser.setUserBase(r.getString("userBase"));
					productUser.setProduct(r.getString("product"));
					productUser.setViewedCount(r.getInt("viewedCount"));
					productUser.setAddedToCart(r.getBool("addedToCart"));
					productUser.setQuantity(r.getInt("quantity"));
					productUser.setPrice(r.getString("price"));
					productUser.setProductId(r.getString("productId"));
					productUser.setViewedTime(r.getDate("viewedTime"));
					productUserList.add(productUser);
				}
			}

			if (!productUserList.isEmpty())
				return productUserList;
			else
				return null;
		} catch (Exception e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	@Override
	public List<ProductUser> fetchProductsRecentlyViewed(String userbase) {
		try {

			List<ProductUser> productUserList = new ArrayList<ProductUser>();
			Select productUserSelect = QueryBuilder.select().from(
					USER_KEYSPACE, PRODUCT_USER_COLLECTION);
			productUserSelect.where(QueryBuilder.eq("userBase", userbase));
			// Ordering ordering = QueryBuilder.desc("viewedTime");
			// productUserSelect.orderBy(ordering);
			List<Row> rows = cassandraTemplate.query(productUserSelect).all();
			for (Row r : rows) {
				ProductUser productUser = new ProductUser();
				productUser.setUserBase(r.getString("userBase"));
				productUser.setProduct(r.getString("product"));
				productUser.setViewedCount(r.getInt("viewedCount"));
				productUser.setAddedToCart(r.getBool("addedToCart"));
				productUser.setQuantity(r.getInt("quantity"));
				productUser.setPrice(r.getString("price"));
				productUser.setProductId(r.getString("productId"));
				productUser.setViewedTime(r.getDate("viewedTime"));
				productUserList.add(productUser);
			}

			Collections.sort(productUserList,
					new ProductUser.OrderByViewedtime());
			if (!productUserList.isEmpty())
				return productUserList;
			else
				return null;

		} catch (Exception e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	@Override
	public String addProductToCart(ProductUser productUser) {
		try {

			ProductUser product = null;
			Select productSelect = QueryBuilder.select().from(USER_KEYSPACE,
					PRODUCT_USER_COLLECTION);
			productSelect
					.where(QueryBuilder.eq("productId",
							productUser.getProductId()))
					.and(QueryBuilder.eq("userBase", productUser.getUserBase()));
			List<Row> rows = cassandraTemplate.query(productSelect).all();
			for (Row r : rows) {

				product = new ProductUser();
				product.setUserBase(r.getString("userBase"));
				product.setProduct(r.getString("product"));
				product.setViewedCount(r.getInt("viewedCount"));
				product.setAddedToCart(r.getBool("addedToCart"));
				product.setQuantity(r.getInt("quantity"));
				product.setPrice(r.getString("price"));
				product.setProductId(r.getString("productId"));
				product.setViewedTime(r.getDate("viewedTime"));

			}

			if (product != null) {
				if (product.getAddedToCart()) {
					Integer count = product.getQuantity();
					count += productUser.getQuantity();
					Update update = QueryBuilder.update(USER_KEYSPACE,
							PRODUCT_USER_COLLECTION);
					update.with(QueryBuilder.set("quantity", count));
					update.where(
							QueryBuilder.eq("productId",
									productUser.getProductId())).and(
							QueryBuilder.eq("userBase",
									productUser.getUserBase()));
					cassandraTemplate.execute(update);

				} else {
					Update update = QueryBuilder.update(USER_KEYSPACE,
							PRODUCT_USER_COLLECTION);
					update.with(QueryBuilder.set("addedToCart", true));
					update.with(QueryBuilder.set("quantity",
							productUser.getQuantity()));
					update.where(
							QueryBuilder.eq("productId",
									productUser.getProductId())).and(
							QueryBuilder.eq("userBase",
									productUser.getUserBase()));
					cassandraTemplate.execute(update);
				}
			} else {
				productUser.setViewedCount(1);
				productUser.setAddedToCart(true);
				logger.info("Price = " + productUser.getPrice());

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						PRODUCT_USER_COLLECTION);
				insert.value("userBase", productUser.getUserBase());
				insert.value("product", productUser.getProduct());
				insert.value("viewedCount", productUser.getViewedCount());
				insert.value("addedToCart", productUser.getAddedToCart());
				insert.value("quantity", productUser.getQuantity());
				insert.value("price", productUser.getPrice());
				insert.value("productId", productUser.getProductId());
				insert.value("viewedTime", productUser.getViewedTime());
				cassandraTemplate.execute(insert);

			}
			return "Success";
		} catch (Exception e) {
			logger.info(e.getMessage());
			return null;
		}

	}

	@Override
	public String deleteProductFromCart(ProductUser productUser) {
		try {
			logger.info("Product:" + productUser.getProduct()
					+ "    UserBase = " + productUser.getUserBase());

			String productId = null;
			Select productuserSelect = QueryBuilder.select().from(
					USER_KEYSPACE, PRODUCT_USER_COLLECTION);
			productuserSelect.where(QueryBuilder.eq("userBase",
					productUser.getUserBase()));
			List<Row> rows = cassandraTemplate.query(productuserSelect).all();
			for (Row r : rows) {
				if (r.getBool("addedToCart") == true
						&& r.getString("product").equals(
								productUser.getProduct())) {
					productId = r.getString("productId");
				}
			}

			Update update = QueryBuilder.update(USER_KEYSPACE,
					PRODUCT_USER_COLLECTION);
			update.where(QueryBuilder.eq("userBase", productUser.getUserBase()))
					.and(QueryBuilder.eq("productId", productId));
			update.with(QueryBuilder.set("addedToCart", false));
			update.with(QueryBuilder.set("quantity", 0));
			cassandraTemplate.execute(update);
			return "Success";
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<ProductUser> fetchProductFromBH(String userBase) {
		try {

			List<ProductUser> prodUserList = new ArrayList<ProductUser>();
			Select productUserSelect = QueryBuilder.select().from(
					USER_KEYSPACE, PRODUCT_USER_COLLECTION);
			productUserSelect.where(QueryBuilder.eq("userBase", userBase));
			// Ordering ordering = QueryBuilder.desc("viewedCount");
			// productUserSelect.orderBy(ordering);
			List<Row> rows = cassandraTemplate.query(productUserSelect).all();
			for (Row r : rows) {
				if (r.getInt("viewedCount") > 0) {
					ProductUser product = new ProductUser();
					product.setUserBase(r.getString("userBase"));
					product.setProduct(r.getString("product"));
					product.setViewedCount(r.getInt("viewedCount"));
					product.setAddedToCart(r.getBool("addedToCart"));
					product.setQuantity(r.getInt("quantity"));
					product.setPrice(r.getString("price"));
					product.setProductId(r.getString("productId"));
					product.setViewedTime(r.getDate("viewedTime"));
					prodUserList.add(product);
				}
			}
			Collections.sort(prodUserList);
			return prodUserList;
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public List<Product> fetchRecomendationForCart(String userBase) {
		try {

			List<String> productUserList = new ArrayList<String>();
			Map<String, Integer> productUserMap = new HashMap<String, Integer>();

			int count;
			Select productUserSelect = QueryBuilder.select().from(
					USER_KEYSPACE, PRODUCT_USER_COLLECTION);
			List<Row> rows = cassandraTemplate.query(productUserSelect).all();
			for (Row r : rows) {

				if (r.getBool("addedToCart") == true
						&& !productUserList.contains(r.getString("productId"))) {
					count = 0;
					productUserList.add(r.getString("productId"));
					if (productUserMap.get(r.getString("productId")) != null)
						count = productUserMap.get(r.getString("productId"));
					productUserMap.put(r.getString("productId"), count + 1);
				}

			}
			Map<String, Integer> sortMap = sortByComparator(productUserMap);

			List<Product> result = new ArrayList<Product>();
			List<CartGrouping> groupedList = new ArrayList<CartGrouping>();
			Iterator it = sortMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				String productId = (String) pair.getKey();
				count = (Integer) pair.getValue();
				CartGrouping cartGroup = new CartGrouping();
				cartGroup.setProductId(productId);
				cartGroup.setGroupedCount(count);
				groupedList.add(cartGroup);
			}

			logger.info("groupedList" + groupedList.size());
			int i = 0;
			for (CartGrouping cart : groupedList) {
				if (i < 10) {
					Product prod = findProductById(cart.getProductId());
					result.add(prod);
				}
				i++;
			}

			logger.info("result List : " + result.size());

			/* productUserQuery.with(new Sort(Sort.Direction.ASC,"price")); */

			/*
			 * List<ProductUser> prodUserList = (List<ProductUser>)
			 * mongoTemplate.find(productUserQuery, ProductUser.class,
			 * PRODUCT_USER_COLLECTION);
			 * logger.info("Cart recommendations:"+prodUserList.size());
			 */
			return result;
		} catch (Exception e) {
			logger.info("Error in fetchRecomendationForCart = "
					+ e.getMessage());
			return null;
		}
	}

	private static Map<String, Integer> sortByComparator(
			Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
