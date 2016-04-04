package com.aricent.spark.process;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aricent.spark.constants.AppConstants;
import com.aricent.spark.context.BootstrapSparkContext;
import com.aricent.spark.models.Product;
import com.aricent.spark.models.UserProductRating;
import com.aricent.spark.util.mapper.UserProductRatingMapper;
import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.japi.CassandraRow;

@Component
public class DataLoader implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static Logger appLogger = Logger.getLogger(DataLoader.class);
	
	@Autowired
	private BootstrapSparkContext bootstrapSpark;
	
	private String KEYSPACE;
	private String PRODUCT_TABLE;
	private String RATING_TABLE;
	private String PROD_FILE;
	private String RATING_FILE;
	private int NUM_SLICES;
	
	public DataLoader(){
		Properties dataloaderProp = new Properties();

		try {
			dataloaderProp.load(this.getClass().getClassLoader().getResourceAsStream("app.config"));
			appLogger.info("Loaded propeties file");
		} catch (IOException ioex) {
			appLogger.info("Properties file could not be loaded");
			appLogger.error(ioex);
		}
		
		this.KEYSPACE = dataloaderProp.getProperty(getClass().getSimpleName() + ".KeySpaceName");
		this.PRODUCT_TABLE = dataloaderProp.getProperty(getClass().getSimpleName() + ".ProductTableName");
		this.RATING_TABLE = dataloaderProp.getProperty(getClass().getSimpleName() + ".RatingTableName");
		this.PROD_FILE = dataloaderProp.getProperty(getClass().getSimpleName() + ".product_file");
		this.RATING_FILE = dataloaderProp.getProperty(getClass().getSimpleName() + ".rating_file");
		this.NUM_SLICES = Integer.parseInt(dataloaderProp.getProperty(getClass().getSimpleName() + ".num_slices"));
	}
	
	public int createSchema(){
		
		CassandraConnector connector = CassandraConnector.apply(bootstrapSpark.getJsc().getConf());
		boolean isCreated = false;
		try (Session session = connector.openSession()) {
			
			appLogger.info("Session opened");
            session.execute("DROP KEYSPACE IF EXISTS "+ this.KEYSPACE);
            session.execute("CREATE KEYSPACE " + this.KEYSPACE + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
            session.execute("CREATE TABLE "+ this.KEYSPACE + "." + this.PRODUCT_TABLE + " (movieid INT PRIMARY KEY, moviename TEXT, moviegenere TEXT)");
            session.execute("CREATE TABLE "+ this.KEYSPACE + "." + this.RATING_TABLE + " (ratingid INT PRIMARY KEY, userid INT, productid INT, rating DECIMAL)");
            appLogger.info("Schema Created");
            isCreated = true;
		} catch (Exception ex) {
			appLogger.error("Schema Creation Failed");
			appLogger.error(ex);
		}
		
		if(isCreated)
			return AppConstants.SCHEMA_CREATION_SUCCESS;
		else
			return AppConstants.SCHEMA_CREATION_FAILURE;
		
	}
	
	public int loadratings() {

		boolean isSaved = false;

		try {

			JavaSparkContext jsc = bootstrapSpark.getJsc();

			appLogger.info("Loading ratings from file " + this.RATING_FILE);
			JavaRDD<UserProductRating> ratingsFileRDD = jsc.textFile(this.RATING_FILE).map(new UserProductRatingMapper());
			appLogger.info("Loaded ratings from file");

			List<UserProductRating> ratings = new ArrayList<UserProductRating>();

			appLogger.info("Saving ratings to table");
			List<UserProductRating> ratingList = ratingsFileRDD.collect();

			int i = 1;

			for (UserProductRating upr : ratingList) {
				upr.setRatingid(i);
				ratings.add(upr);
				i++;
			}
			appLogger.info("Record count: " + i);

			JavaRDD<UserProductRating> ratingsRDD = jsc.parallelize(ratings, this.NUM_SLICES);

			javaFunctions(ratingsRDD).writerBuilder(this.KEYSPACE, this.RATING_TABLE, mapToRow(UserProductRating.class))
					.saveToCassandra();
			
			appLogger.info("Saved ratings to table");
		
			isSaved = true;
		
		} catch (Exception ex) {
			appLogger.error("Ratings could not be saved to DB");
			appLogger.error(ex);
		}

		if (isSaved)
			return AppConstants.RATINGS_LOAD_SUCCESS;
		else
			return AppConstants.RATINGS_LOAD_FAILURE;
	}
	
	public int loadProducts() {
		boolean isSaved = false;
		JavaSparkContext jsc = bootstrapSpark.getJsc();

		try {
			appLogger.info("Loading products from file " + this.PROD_FILE);
			JavaRDD<Product> productsFileRDD = jsc.textFile(this.PROD_FILE).map(new com.aricent.spark.util.mapper.ProductMapper());
			appLogger.info("Loaded products from file");

			appLogger.info("Saving products to table");
			
			List<Product> products = new ArrayList<Product>();
			List<Product> prodList = productsFileRDD.collect();
			
			int i = 1;
			// TODO: Need to understand why this step is required!! -->himalaya
			for (Product p : prodList) {
				products.add(p);
				i++;
			}
			
			appLogger.info("Products read from file: " + i);

			JavaRDD<Product> productsRDD = jsc.parallelize(products);
			javaFunctions(productsRDD).writerBuilder(this.KEYSPACE, this.PRODUCT_TABLE, mapToRow(Product.class)).saveToCassandra();

			appLogger.info("Saved products to table");

		} catch (Exception ex) {
			appLogger.error("Products could not be saved to DB");
			appLogger.error(ex);
		}
		
		if (isSaved)
			return AppConstants.PRODUCTS_LOAD_SUCCESS;
		else
			return AppConstants.PRODUCTS_LOAD_FAILURE;
	}
	
	public int getProducts(){
		JavaSparkContext jsc = bootstrapSpark.getJsc();
		
		JavaRDD<Product> prd =  javaFunctions(jsc).cassandraTable(this.KEYSPACE, this.PRODUCT_TABLE)
		        .select("movieid","moviename","moviegenere").map(new Function<CassandraRow, Product>() {
		        	@Override
					public Product call(CassandraRow cassandraRow) throws Exception {
						int movieid = cassandraRow.getInt("movieid");
						String moviename = cassandraRow.getString("moviename");
						String moviegenere = cassandraRow.getString("moviegenere");
						return Product.newInstance(movieid, moviename, moviegenere);
					}
		        });
		
		appLogger.info("Product List Size : "+prd.count());
		
		return 0;
	}
	
	public JavaRDD<Rating> getRatings(){
		
		JavaSparkContext jsc = bootstrapSpark.getJsc();
		
		JavaRDD<Rating> ratings = javaFunctions(jsc).cassandraTable(this.KEYSPACE, this.RATING_TABLE)
	            .select("productid","userid","rating").map(new Function<CassandraRow, Rating>() {
	                @Override
	                public Rating call(CassandraRow cassandraRow) throws Exception {
	                	int productId = cassandraRow.getInt("productid");
	                	int userId =cassandraRow.getInt("userid");
	                	double rating = cassandraRow.getDouble("rating");
	                	return new Rating(userId,productId,rating);
	                }
	            });
				
				List<Rating> lst = ratings.toArray();
				appLogger.info("Fetched Records from DB: "+lst.size());
				ratings.cache();
		return ratings;
	}
}