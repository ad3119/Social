package com.aricent.service;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aricent.spark.context.BootstrapSparkContext;
import com.aricent.spark.process.DataLoader;
import com.aricent.spark.process.Recommender;
import com.aricent.spark.test.SparkEnvTest;
import com.aricent.transaction.RestResponse;

/**
 * @author BGH34973 - Himalaya Gupta
 *
 */
@Path("/person")
@Component
public class SparkService {

	
	@Autowired
	private BootstrapSparkContext bootstrapSpark;
	@Autowired
	private DataLoader dataLoader;
	@Autowired
	private Recommender recommender;
	
	@GET
	@Path("/{operation}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getPluginData(@PathParam("operation") String operation) {

		int RESP_SUCCESS = 0;
		
		recommender.trainALSModel();
		String s = recommender.getTrainedModel().toString();
		
		RestResponse reactionsResp = null;
		String message = "Hello, operation is :" + s ;
		reactionsResp = new RestResponse(message, RESP_SUCCESS, null);
		return reactionsResp;
	}
	
	@GET
	@Path("/{personId}/{limit}/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getRecommend(@PathParam("personId") String personId, @PathParam("limit") String limit) {

		int RESP_SUCCESS = 0;
		
		StringBuffer sbuf = new StringBuffer();
		
		Rating[] rr = recommender.recommendProducts(Integer.parseInt(personId), Integer.parseInt(limit));
		
		List<Rating> lst = Arrays.asList(rr);
		for (Rating r : lst)
			sbuf.append(r.product() + ",");
		
		RestResponse reactionsResp = null;
		String message = sbuf.toString();
		reactionsResp = new RestResponse(message, RESP_SUCCESS, null);
		return reactionsResp;
	}
}
