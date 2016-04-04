package com.aricent.plugin.reaction;

import java.util.List;

import com.aricent.plugin.reaction.model.CumulativeReaction;
import com.aricent.plugin.reaction.model.Reaction;
import com.aricent.plugin.reaction.model.UserReaction;

/**
 * @author BGH34973 - Himalaya Gupta
 * 
 */
public interface ReactionProcessor {

	public int saveUserReaction(UserReaction userReaction);

	public UserReaction getUserReaction(String userBaseId, String pageId);

	public List<CumulativeReaction> getPageReaction(String pageId);

	public int saveReaction(Reaction reaction);

	public List<Reaction> getReactions();

	public int createReaction(Reaction newReaction);

	public int editReaction(Reaction newReaction);

	public int deleteReaction(String reactionId);
}
