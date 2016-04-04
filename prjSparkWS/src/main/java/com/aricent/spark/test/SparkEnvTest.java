package com.aricent.spark.test;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;

public class SparkEnvTest {

	Logger appLogger = Logger.getLogger(SparkEnvTest.class);
	
	public void loadSparkEnv() {
		SparkConf conf = new SparkConf();
		conf.setAppName("Dataloader");
		conf.setMaster("local");
		conf.setMaster("spark://slu23:7077");
		//conf.set("spark.executor.memory", "512m");
		conf.set("spark.cassandra.connection.host", "slu23");
		JavaSparkContext sc = new JavaSparkContext(conf);
		appLogger.info("Context Started...");
		CassandraConnector connector = CassandraConnector.apply(sc.getConf());
		try (Session session = connector.openSession()) {
			appLogger.info("Session opened");
		}
		sc.stop();
	}
}
