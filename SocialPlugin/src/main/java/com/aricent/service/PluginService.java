package com.aricent.service;

import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aricent.constant.GlobalConstants;
import com.aricent.plugin.reaction.ReactionProcessor;
import com.aricent.plugin.reaction.model.CumulativeReaction;
import com.aricent.plugin.reaction.model.Reaction;
import com.aricent.plugin.reaction.model.UserReaction;
import com.aricent.transaction.RestResponse;

/**
 * 
 * PluginService - Perform Create, Read operations based on plugins
 */
@Component
@Path("/plugins")
public class PluginService {

	@Autowired
	ReactionProcessor oReactProcessorImpl;

	@GET
	@Path("/{pluginName}/{pageId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getPluginData(@PathParam("pageId") String pageId,
			@PathParam("pluginName") String pluginName) {

		RestResponse reactionsResp = null;
		if (pluginName.equals(GlobalConstants.PLUGIN_REACTION)) {

			List<CumulativeReaction> cumReactions = oReactProcessorImpl
					.getPageReaction(pageId);

			if (cumReactions != null) {
				reactionsResp = new RestResponse(cumReactions,
						GlobalConstants.OPRERATION_SUCCESS, null);
			} else {
				reactionsResp = new RestResponse(cumReactions,
						GlobalConstants.OPRERATION_FAILURE, null);
			}
		}

		return reactionsResp;
	}

	@GET
	@Path("/{pluginName}/{pageId}/{userBaseId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getUserReactionData(
			@PathParam("userBaseId") String userBaseId,
			@PathParam("pluginName") String pluginName,
			@PathParam("pageId") String pageId) {

		RestResponse reactionsResp = null;
		if (pluginName.equals(GlobalConstants.PLUGIN_REACTION)) {

			UserReaction reaction = oReactProcessorImpl.getUserReaction(
					userBaseId, pageId);

			if (reaction != null) {
				reactionsResp = new RestResponse(reaction,
						GlobalConstants.OPRERATION_SUCCESS, null);
			} else {
				reactionsResp = new RestResponse(reaction,
						GlobalConstants.OPRERATION_FAILURE, null);
			}
		}

		return reactionsResp;
	}

	@POST
	@Path("/{pluginName}/{pageId}/{userBaseId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse saveUserReactionData(
			@PathParam("userBaseId") String userBaseId,
			@PathParam("pluginName") String pluginName,
			@PathParam("pageId") String pageId, Object data) {

		RestResponse reactionsResp = null;

		if (pluginName.equals(GlobalConstants.PLUGIN_REACTION)) {

			LinkedHashMap<?, ?> genericParams = (LinkedHashMap<?, ?>) data;

			UserReaction reaction = new UserReaction();
			reaction.setUserBaseId(userBaseId);
			reaction.setPageId(pageId);
			reaction.setReactId(genericParams.get("reactId").toString());

			int result = oReactProcessorImpl.saveUserReaction(reaction);

			if (result != GlobalConstants.USER_REACTION_UPDATE_FAILED) {
				reactionsResp = new RestResponse(null,
						GlobalConstants.OPRERATION_SUCCESS, null);
			} else {
				reactionsResp = new RestResponse(null,
						GlobalConstants.OPRERATION_FAILURE, null);
			}
		}

		return reactionsResp;
	}

	@GET
	@Path("/{pluginName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse getPluginData(@PathParam("pluginName") String pluginName) {

		RestResponse reactionsResp = null;
		if (pluginName.equals(GlobalConstants.PLUGIN_REACTION)) {

			List<Reaction> reactions = oReactProcessorImpl.getReactions();

			if (reactions != null) {
				reactionsResp = new RestResponse(reactions,
						GlobalConstants.OPRERATION_SUCCESS, null);
			} else {
				reactionsResp = new RestResponse(reactions,
						GlobalConstants.OPRERATION_FAILURE, null);
			}
		}

		return reactionsResp;
	}

	@POST
	@Path("/createReaction")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse createReaction(Reaction newReaction) {

		int result = oReactProcessorImpl.createReaction(newReaction);

		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/editReaction")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse editReaction(Reaction newReaction) {

		int result = oReactProcessorImpl.editReaction(newReaction);

		return new RestResponse(null, result, null);
	}

	@POST
	@Path("/deleteReaction")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RestResponse deleteReaction(String reactionId) {

		int result = oReactProcessorImpl.deleteReaction(reactionId);

		return new RestResponse(null, result, null);
	}

}
