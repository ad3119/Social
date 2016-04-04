package com.aricent.control;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class DummyMailList {
	public static List<String> MailIds = new Vector<String>();
	private static final Logger logger = Logger.getLogger(DummyMailList.class);

	public static void CreateDummyList() {

		MailIds.clear();
		logger.info("CreateDummyMailList Entered");

		MailIds.add("prink_07@rediffmail.com");
		MailIds.add("priyanka.kumari@aricent.com");
		MailIds.add("priyanka0302@yahoo.com");
		MailIds.add("invalidid@yahoo.com");

		logger.info(MailIds);
		// return MailIds;
	}

}
