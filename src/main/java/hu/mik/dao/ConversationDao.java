package hu.mik.dao;

import java.util.List;

import hu.mik.beans.Conversation;
import hu.mik.beans.User;

public interface ConversationDao {
	public void saveConversation(Conversation conversation);

	public List<Conversation> findLatestConversationsOfUser(User user);

	public Long getNumberOfUnseenConversations(User user);

	public int setConversationSeen(Conversation conversation);

}
