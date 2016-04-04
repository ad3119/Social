package com.aricent.spark.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class SparkAppContextListener implements ServletContextListener  {

	Logger appLogger = Logger.getLogger(SparkAppContextListener.class);
	
	@Autowired
	private BootstrapSparkContext bootstrapSpark;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		//TODO: handle this event properly. Destroy the context before reloading the app
		appLogger.info("Stopping JavaSparkContext");
		bootstrapSpark.getJsc().stop();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// ignore exception
			
		}
		appLogger.info("Stopped JavaSparkContext");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
