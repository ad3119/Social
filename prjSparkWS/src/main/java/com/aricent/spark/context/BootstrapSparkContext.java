package com.aricent.spark.context;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * @author Himalaya
 * Create a JavaSparkContext
 */
public class BootstrapSparkContext implements Serializable {

	public static Logger appLogger = Logger.getLogger(BootstrapSparkContext.class);

	private transient JavaSparkContext jsc;
	private boolean isInitialized = false;

	public BootstrapSparkContext() {
		if (!isInitialized) {
			initContext();
		}
	}

	private void initContext() {

		Properties sparkAppProp = new Properties();

		try {
			sparkAppProp.load(this.getClass().getClassLoader().getResourceAsStream("app.config"));
			appLogger.info("Loaded propeties file");
		} catch (IOException ioex) {
			appLogger.info("Properties file could not be loaded");
			appLogger.error(ioex);
		}

		SparkConf conf = new SparkConf();
		String[] jars = new String[1];
		jars[0] = sparkAppProp.getProperty(getClass().getSimpleName() + ".FatJar");
		conf.setAppName(sparkAppProp.getProperty(getClass().getSimpleName() + ".AppName"));
		conf.setMaster(sparkAppProp.getProperty(getClass().getSimpleName() + ".SparkMasterURL"));
		conf.set("spark.executor.memory", sparkAppProp.getProperty(getClass().getSimpleName() + ".ExecutorMemory"));
		conf.set("spark.cassandra.connection.host", sparkAppProp.getProperty(getClass().getSimpleName() + ".CassandraHost"));
		conf.setJars(jars);
		
		jsc = new JavaSparkContext(conf);
		appLogger.info("Context Started...");
		isInitialized = true;
	}

	public JavaSparkContext getJsc() {
		return jsc;
	}

}
