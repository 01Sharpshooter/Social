package hu.mik.dao;

import java.util.List;

import hu.mik.beans.Conversation;
import hu.mik.beans.User;

public interface ConversationDao {
	public Conversation saveConversation(Conversation conversation);

	public List<Conversation> findLatestConversationsOfUser(User user);

	public Long getNumberOfUnseenConversations(User user);

	public int setConversationSeen(Conversation conversation, User user);

	public Conversation findConversationOfUsers(User loggedUser, Integer partnerId);

}
