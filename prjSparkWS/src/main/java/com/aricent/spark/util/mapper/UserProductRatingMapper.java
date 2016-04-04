package com.aricent.spark.util.mapper;

import java.util.regex.Pattern;
import org.apache.spark.api.java.function.Function;
import com.aricent.spark.models.UserProductRating;

/*
 * @author Himalaya
 */

/**
 * Use this class to make the Flat-File data to an RDD. 
 * <br/>A simple mapper function which splits the data based on defined sepearator<br/>
 * <code>Pattern DOUBLE_COLON = Pattern.compile("::");</code>
 */
public class UserProductRatingMapper implements Function<String, UserProductRating> {
	private static final long serialVersionUID = 1L;
	private static final Pattern DOUBLE_COLON = Pattern.compile("::");

	@Override
	public UserProductRating call(String line) {
		String[] tok = DOUBLE_COLON.split(line);
		Integer userid = Integer.valueOf(tok[0]);
		Integer productid = Integer.valueOf(tok[1]);
		Double rating = Double.valueOf(tok[2]);
		return UserProductRating.newInstance(0, userid, productid, rating);
	}
}