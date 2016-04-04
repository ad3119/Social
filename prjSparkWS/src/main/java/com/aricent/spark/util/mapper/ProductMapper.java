package com.aricent.spark.util.mapper;

import java.util.regex.Pattern;
import org.apache.spark.api.java.function.Function;
import com.aricent.spark.models.Product;

/*
 * @author Himalaya
 */

/**
 * Use this class to make the Flat-File data to an RDD. <br/>
 * A simple mapper function which splits the data based on defined sepearator<br/>
 * <code>Pattern DOUBLE_COLON = Pattern.compile("::");</code>
 */
public class ProductMapper implements Function<String, Product> {
	private static final long serialVersionUID = 1L;
	private static final Pattern DOUBLE_COLON = Pattern.compile("::");

	@Override
	public Product call(String line) {
		String[] tok = DOUBLE_COLON.split(line);
		int id = Integer.valueOf(tok[0]);
		String name = tok[1];
		String genere = tok[2];
		return Product.newInstance(id, name, genere);
	}
}
