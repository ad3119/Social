package com.aricent.service;

import javax.ws.rs.Consumes;

import com.aricent.model.MessageModel;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.aricent.constant.NotifConstants;
import com.aricent.control.*;
//TODO...Add other CRUD services
import com.aricent.model.NotifResponse;

@Path("/Notify")
public class NotificationService {

	private static final Logger logger = Logger
			.getLogger(NotificationService.class);

	@POST
	@Path("/welcome")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public NotifResponse SendWelcomeNotification(MessageModel ipMessageModel) {
		logger.info("In welcome page");
		MailController lMailController = new MailController();
		int responsecode = lMailController.sendmail(ipMessageModel);
		NotifResponse resp = new NotifResponse(null, responsecode, null);
		return resp;
	}
	

}
