package com.aricent.spark.process;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aricent.spark.context.BootstrapSparkContext;

@Component
public class Recommender implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Logger appLogger = Logger.getLogger(Recommender.class);
	
	@Autowired
	private BootstrapSparkContext bootstrapSpark;

	@Autowired
	private DataLoader dataLoader;
	
	private transient MatrixFactorizationModel alsModel;
	
	public int trainALSModel(){
	
		appLogger.info("Fetching rating data");
		JavaRDD<Rating> trainingDataRDD = dataLoader.getRatings();
		appLogger.info("Fetched rating data");
		appLogger.info("Started Training");
		this.alsModel =  ALS.train(JavaRDD.toRDD(trainingDataRDD),10,15,0.01);
		appLogger.info("Completed Training");
		return 0;
	}

	public Rating[] recommendProducts(int userId, int limit){
		return getTrainedModel().recommendProducts(userId, limit);
	}
	
	public MatrixFactorizationModel getTrainedModel(){
		return alsModel;
	}
}
