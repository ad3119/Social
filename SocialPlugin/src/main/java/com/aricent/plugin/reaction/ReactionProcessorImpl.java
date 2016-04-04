package com.aricent.plugin.reaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlTemplate;

import com.aricent.constant.GamificationConstants;
import com.aricent.constant.GlobalConstants;
import com.aricent.plugin.reaction.model.CumulativeReaction;
import com.aricent.plugin.reaction.model.Reaction;
import com.aricent.plugin.reaction.model.UserReaction;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;

public class ReactionProcessorImpl implements ReactionProcessor {

	@Autowired
	private CqlTemplate cassandraTemplate;

	private static final Logger logger = Logger
			.getLogger(ReactionProcessorImpl.class);

	public static final String USER_KEYSPACE = "user_tables";
	private static final String REACTION_COLLECTION = "reactions";
	private static final String USER_REACTION_COLLECTION = "usereactions";

	public void init() {

		logger.info("Initializing ReactionProcessor");

		try {
			// cassandraTemplate.execute("DROP TABLE user_tables.usereactions");
			cassandraTemplate
					.execute("CREATE TABLE IF NOT EXISTS user_tables.reactions ("
							+ "reactId text PRIMARY KEY,"
							+ "reactName text,"
							+ "reactUrl text," + "dtUpdated timestamp" + ");");

			cassandraTemplate
					.execute("CREATE TABLE IF NOT EXISTS user_tables.usereactions ("
							+ "userBaseId text,"
							+ "pageId text,"
							+ "reactId text,"
							+ "dtReacted timestamp,"
							+ "PRIMARY KEY (userBaseId,pageId)" + ");");

			logger.info("Completed Initializing ReactionProcessor");

		} catch (Exception e) {
			logger.error("Initializing Failed");
			logger.error(e);
		}
	}

	@Override
	public List<CumulativeReaction> getPageReaction(String pageId) {

		List<CumulativeReaction> cumReactList = new ArrayList<CumulativeReaction>();
		List<String> userReactionList = new ArrayList<String>();
		Map<String, Integer> userReactionMap = new HashMap<String, Integer>();
		Select reactionSelect = QueryBuilder.select().from(USER_KEYSPACE,
				USER_REACTION_COLLECTION);
		// reactionSelect.where(QueryBuilder.eq("pageId", pageId));
		int reactCount = 0;
		List<Row> rows = cassandraTemplate.query(reactionSelect).all();
		for (Row r : rows) {
			if (r.getString("pageId").equals(pageId)) {
				if (!userReactionList.contains(r.getString("reactId"))) {
					userReactionList.add(r.getString("reactId"));
					if (userReactionMap.get(r.getString("reactId")) != null)
						reactCount = userReactionMap
								.get(r.getString("reactId"));
					userReactionMap.put(r.getString("reactId"), reactCount + 1);
				}
			}
		}

		List<Reaction> reactions = getReactions();
		Map<String, Object> reactUrlMap = new HashMap<String, Object>();

		for (Reaction reaction : reactions)
			reactUrlMap.put(reaction.getReactId(), reaction);

		// for(int i=0;i<cumReact.size();i++){

		if (!userReactionMap.isEmpty()) {
			Iterator it = userReactionMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				String rurl = ((Reaction) reactUrlMap.get(pair.getKey()))
						.getReactUrl();
				String rname = ((Reaction) reactUrlMap.get(pair.getKey()))
						.getReactName();
				String reactId = ((Reaction) reactUrlMap.get(pair.getKey()))
						.getReactId();
				CumulativeReaction cumReact = new CumulativeReaction();
				cumReact.setReactUrl(rurl);
				cumReact.setReactName(rname);
				cumReact.setReactId(reactId);
				cumReact.setReactCount((Integer) pair.getValue());
				cumReactList.add(cumReact);
			}
		}

		/*
		 * else { for(int i =0;i<reactions.size();i++) { //String rurl =
		 * ((Reaction)reactUrlMap.get(i)).getReactUrl(); //String rname =
		 * ((Reaction)reactUrlMap.get(i)).getReactName(); String rurl =
		 * reactions.get(i).getReactUrl(); String rname =
		 * reactions.get(i).getReactName(); CumulativeReaction cumReact = new
		 * CumulativeReaction(); cumReact.setReactUrl(rurl);
		 * cumReact.setReactName(rname); cumReactList.add(cumReact); }
		 * 
		 * }
		 */

		return cumReactList;
	}

	@Override
	public UserReaction getUserReaction(String userBaseId, String pageId) {
		UserReaction reaction = null;

		// TODO: Check for pageId and UserId
		try {

			Select userReactSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_REACTION_COLLECTION);
			userReactSelect.where(QueryBuilder.eq("userBaseId", userBaseId));
			List<Row> rows = cassandraTemplate.query(userReactSelect).all();
			for (Row r : rows) {
				if (r.getString("pageId").equals(pageId)) {
					reaction = new UserReaction();
					reaction.setUserBaseId(r.getString("userBaseId"));
					reaction.setPageId(r.getString("pageId"));
					reaction.setReactId(r.getString("reactId"));
					reaction.setDtReacted(r.getDate("dtReacted"));
				}
			}

		} catch (Exception ex) {
			logger.error(ex);
		}
		return reaction;
	}

	@Override
	public int saveUserReaction(UserReaction userReaction) {

		try {

			// Query userReactQry = new
			// Query(Criteria.where("userBaseId").is(userReaction.getUserBaseId()).and("pageId").is(userReaction.getPageId()));
			UserReaction prevReaction = null;

			Select userReactSelect = QueryBuilder.select().from(USER_KEYSPACE,
					USER_REACTION_COLLECTION);
			userReactSelect.where(QueryBuilder.eq("userBaseId",
					userReaction.getUserBaseId()));
			List<Row> rows = cassandraTemplate.query(userReactSelect).all();
			for (Row r : rows) {
				if (r.getString("pageId").equals(userReaction.getPageId())) {
					prevReaction = new UserReaction();
					prevReaction.setUserBaseId(r.getString("userBaseId"));
					prevReaction.setPageId(r.getString("pageId"));
					prevReaction.setReactId(r.getString("reactId"));
					prevReaction.setDtReacted(r.getDate("dtReacted"));
				}

			}
			if (prevReaction != null) {

				Update reactUpdate = QueryBuilder.update(USER_KEYSPACE,
						USER_REACTION_COLLECTION);
				reactUpdate.where(
						QueryBuilder.eq("userBaseId",
								userReaction.getUserBaseId())).and(
						QueryBuilder.eq("pageId", userReaction.getPageId()));
				reactUpdate.with(QueryBuilder.set("reactId",
						userReaction.getReactId()));
				reactUpdate.with(QueryBuilder.set("dtReacted", new Date()));
				cassandraTemplate.execute(reactUpdate);
				// mongoTemplate.updateFirst(userReactQry, reactUpdate,
				// UserReaction.class, USER_REACTION_COLLECTION);

				return GlobalConstants.USER_REACTION_UPDATED;

			} else {

				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						USER_REACTION_COLLECTION);
				insert.value("userBaseId", userReaction.getUserBaseId());
				insert.value("pageId", userReaction.getPageId());
				insert.value("reactId", userReaction.getReactId());
				insert.value("dtReacted", userReaction.getDtReacted());
				cassandraTemplate.execute(insert);
				// mongoTemplate.save(userReaction, USER_REACTION_COLLECTION);
				return GlobalConstants.USER_REACTION_UPDATED;
			}

		} catch (Exception ex) {
			logger.error(ex);
			return GlobalConstants.USER_REACTION_UPDATE_FAILED;
		}
	}

	@Override
	public List<Reaction> getReactions() {

		List<Reaction> reactions = new ArrayList<Reaction>();

		try {

			Select reactSelect = QueryBuilder.select().from(USER_KEYSPACE,
					REACTION_COLLECTION);
			List<Row> rows = cassandraTemplate.query(reactSelect).all();
			for (Row r : rows) {
				Reaction reaction = new Reaction();
				reaction.setReactId(r.getString("reactId"));
				reaction.setReactName(r.getString("reactName"));
				reaction.setReactUrl(r.getString("reactUrl"));
				reaction.setDtUpdated(r.getDate("dtUpdated"));
				reactions.add(reaction);
			}

		} catch (Exception ex) {
			logger.error(ex);
		}
		return reactions;
	}

	@Override
	public int createReaction(Reaction newReaction) {
		Update reactionUpdate;

		try {

			if (newReaction != null) {

				if (newReaction.getReactId() != null
						&& !newReaction.getReactId().equals("")) {

					reactionUpdate = QueryBuilder.update(USER_KEYSPACE,
							REACTION_COLLECTION);

					reactionUpdate.where(QueryBuilder.eq("reactId",
							newReaction.getReactId()));
					reactionUpdate.with(QueryBuilder.set("reactName",
							newReaction.getReactName()));
					reactionUpdate.with(QueryBuilder.set("reactUrl",
							newReaction.getReactUrl()));
					reactionUpdate.with(QueryBuilder.set("dtUpdated",
							new Date()));
					reactionUpdate.with(QueryBuilder.set("reactId", UUID
							.randomUUID().toString()));

					cassandraTemplate.execute(reactionUpdate);
					// mongoTemplate.updateFirst(reactionListQuery,
					// reactionUpdate, REACTION_COLLECTION);

					return GlobalConstants.ACTION_REACTION_UPDATED;

				} else {

					newReaction.setReactId(UUID.randomUUID().toString());

					Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
							REACTION_COLLECTION);
					insert.value("reactId", newReaction.getReactId());
					insert.value("reactName", newReaction.getReactName());
					insert.value("reactUrl", newReaction.getReactUrl());
					insert.value("dtUpdated", newReaction.getDtUpdated());
					cassandraTemplate.execute(insert);
					// mongoTemplate.save(newReaction, REACTION_COLLECTION);

					return GlobalConstants.ACTION_REACTION_CREATED;
				}
			} else {
				return GlobalConstants.ACTION_EMPTY_REACTION;
			}

		} catch (Exception e) {
			logger.error("Rule creation/updation Failed");
			logger.error(e);
			return GlobalConstants.ACTION_REACTION_CREATE_UPDATE_FAILED;
		}
	}

	@Override
	public int editReaction(Reaction newReaction) {
		Update reactionUpdate;

		try {
			if (newReaction != null) {

				if (newReaction.getReactId() != null
						&& !newReaction.getReactId().equals("")) {

					reactionUpdate = QueryBuilder.update(USER_KEYSPACE,
							REACTION_COLLECTION);
					reactionUpdate.where(QueryBuilder.eq("reactId",
							newReaction.getReactId()));

					if (newReaction.getReactName() != null)
						reactionUpdate.with(QueryBuilder.set("reactName",
								newReaction.getReactName()));
					if (newReaction.getReactUrl() != null)
						reactionUpdate.with(QueryBuilder.set("reactUrl",
								newReaction.getReactUrl()));
					cassandraTemplate.execute(reactionUpdate);
					// mongoTemplate.updateFirst(reactionListQuery,
					// reactionUpdate, REACTION_COLLECTION);

					return GlobalConstants.ACTION_REACTION_UPDATED;

				} else {
					logger.error("editReaction : Reaction Id is null");
					return GlobalConstants.ACTION_REACTION_CREATE_UPDATE_FAILED;
				}

			} else {
				logger.error("editReaction : Reaction is null");
				return GlobalConstants.ACTION_REACTION_CREATE_UPDATE_FAILED;
			}

		} catch (Exception e) {
			logger.error("Reaction creation/updation Failed");
			logger.error(e);
			return GlobalConstants.ACTION_REACTION_CREATE_UPDATE_FAILED;
		}

	}

	@Override
	public int deleteReaction(String reactionId) {
		// Query reactionListQuery;
		Delete reactionListDelete;
		try {
			if (reactionId != null) {
				reactionListDelete = QueryBuilder.delete().from(USER_KEYSPACE,
						REACTION_COLLECTION);
				reactionListDelete
						.where(QueryBuilder.eq("reactId", reactionId));
				cassandraTemplate.execute(reactionListDelete);
				// mongoTemplate.remove(reactionListQuery, REACTION_COLLECTION);
			}
			return GlobalConstants.ACTION_REACTION_DELETED;

		} catch (Exception e) {
			logger.error("Reaction creation/updation Failed");
			logger.error(e);
			return GlobalConstants.ACTION_REACTION_DELETE_FAILED;
		}
	}

	@Override
	public int saveReaction(Reaction reaction) {

		Update reactUpdate;

		try {

			if ((reaction.getReactId() != null)
					&& (!reaction.getReactId().equals(""))) {

				reactUpdate = QueryBuilder.update(USER_KEYSPACE,
						REACTION_COLLECTION);
				reactUpdate.with(QueryBuilder.set("reactName",
						reaction.getReactName()));
				reactUpdate.with(QueryBuilder.set("reactUrl",
						reaction.getReactUrl()));
				reactUpdate.with(QueryBuilder.set("dtUpdated", new Date()));
				cassandraTemplate.execute(reactUpdate);
				// mongoTemplate.updateFirst(reactQuery, reactUpdate,
				// Reaction.class, REACTION_COLLECTION);
				return GlobalConstants.REACTION_UPDATED;

			} else {
				Insert insert = QueryBuilder.insertInto(USER_KEYSPACE,
						REACTION_COLLECTION);
				insert.value("reactId", reaction.getReactId());
				insert.value("reactName", reaction.getReactName());
				insert.value("reactUrl", reaction.getReactUrl());
				insert.value("dtUpdated", reaction.getDtUpdated());
				cassandraTemplate.execute(insert);
				// mongoTemplate.save(reaction, REACTION_COLLECTION);
				return GlobalConstants.REACTION_UPDATED;
			}

		} catch (Exception ex) {
			return GlobalConstants.REACTION_UPDATE_FAILED;
		}
	}
}
