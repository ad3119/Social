package com.aricent.spark.models;

import java.io.Serializable;
import java.text.MessageFormat;
/*
 * @author Himalaya
 */

public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	public Integer movieid;
	public String moviename;
	public String moviegenere;

	public Integer getMovieid() {
		return movieid;
	}

	public void setMovieid(Integer movieid) {
		this.movieid = movieid;
	}

	public String getMoviename() {
		return moviename;
	}

	public void setMoviename(String moviename) {
		this.moviename = moviename;
	}

	public String getMoviegenere() {
		return moviegenere;
	}

	public void setMoviegenere(String moviegenere) {
		this.moviegenere = moviegenere;
	}
	
	public static Product newInstance(Integer id, String name, String genere) {
        Product product = new Product();
        product.setMovieid(id);
        product.setMoviename(name);
        product.setMoviegenere(genere);
        return product;
    }
	
	@Override
	public String toString() {
		return MessageFormat.format(
				"Product'{'id={0}, name=''{1}'', genere={2}'}'", 
				movieid, moviename, moviegenere);
	}

}
