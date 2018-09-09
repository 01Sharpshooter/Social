package hu.mik.dao;

import java.util.List;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.User;

public interface MessageDao {
	public List<Message> findAllByUsers(int number, User user1, User user2);

	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation);

	public Message findLastMessageOfUsers(User user1, User user2);

	public void save(Message message);

	public List<Message> findLatestMessagesOfUser(int number, User user);

	public int setConversationSeen(Conversation conversation);

	public Long getNumberOfUnseenConversations(User user);

	public List<Conversation> findLatestConversationsOfUser(User user);

}
