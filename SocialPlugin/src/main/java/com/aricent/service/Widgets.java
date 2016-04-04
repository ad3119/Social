package com.aricent.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aricent.constant.GlobalConstants;
import com.aricent.control.MailModel;
//import com.aricent.model.PageWidgets;
import com.aricent.model.PortalWidgets;
import com.aricent.transaction.RestResponse;
import com.aricent.transaction.UserTransaction;

@Component
@Path("/widgets")
public class Widgets {

	@Autowired
	UserTransaction oUserTransaction;
	private static final Logger logger = Logger.getLogger(Widgets.class);

	@POST
	@Path("/addToPortal")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse addToPortal(String widgetId) {
		logger.info("Inside addto poratal " + widgetId);
		oUserTransaction.addToPortal(widgetId);
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/addNewWidget")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse addNewWidget(String json) {
		RestResponse resp;
		logger.info("Inside add New Widget");
		Integer success = oUserTransaction.addNewWidget(json);
		if (success == GlobalConstants.OPRERATION_SUCCESS) {
			resp = new RestResponse(null, GlobalConstants.OPRERATION_SUCCESS,
					null);
		} else
			resp = new RestResponse(null, GlobalConstants.OPRERATION_FAILURE,
					null);

		return resp;
	}

	@POST
	@Path("/addToPage")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse addToPage(String json) {
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(json);
			String widgetId = jsonObj.getString("widgetId");
			JSONArray pages = jsonObj.getJSONArray("pages");
			String align = jsonObj.getString("align");
			logger.info(widgetId + "::" + pages);
			String success = oUserTransaction.addToPage(widgetId, pages, align);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}

		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/deleteFromPage")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse deleteFromPage(String widgetId) {

		logger.info("Inside delete page " + widgetId);
		String success = oUserTransaction.deleteFromPage(widgetId);
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@POST
	@Path("/deleteFromPortal")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse deleteFromPortal(String widgetId) {
		RestResponse resp = new RestResponse(null,
				GlobalConstants.OPRERATION_FAILURE, null);
		logger.info("Inside delete portal " + widgetId);

		try {
			String success = oUserTransaction.deleteFromPortal(widgetId);
			if ("Y".equals(success)) {
				resp = new RestResponse(null,
						GlobalConstants.OPRERATION_SUCCESS, null);
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return resp;
	}

	@GET
	@Path("/getPortalWidgets")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getPortalWidgets() {
		logger.info("Inside getPortalWidgets ::");
		List<PortalWidgets> portalWidgetList = oUserTransaction
				.fetchPortalWidgets();
		RestResponse resp = new RestResponse(portalWidgetList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@GET
	@Path("/getPageWidgets")
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	public RestResponse getPageWidgets() {
		logger.info("Inside getPortalWidgets ::");
		List<PortalWidgets> pageWidgetList = oUserTransaction
				.fetchPageWidgets();
		RestResponse resp = new RestResponse(pageWidgetList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}

	@GET
	@Path("/fetchAllWidgets")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse getAllAvailbaleWidgets() {
		logger.info("Inside fetchAllWidgets");
		List<PortalWidgets> availableWidgetList = oUserTransaction
				.fetchWidgets();
		RestResponse resp = new RestResponse(availableWidgetList,
				GlobalConstants.OPRERATION_SUCCESS, null);
		return resp;
	}
}
