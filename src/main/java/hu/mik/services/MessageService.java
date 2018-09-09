package hu.mik.services;

import java.util.List;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.User;

public interface MessageService {

	public List<Message> findAllByUsers(int number, User user1, User user2);

	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation);

	public void saveMessage(Message message);

	public List<Message> findLastestMessagesOfUser(int number, User user);

	public Message findLastMessageOfUsers(User user1, User user2);

	public int setConversationSeen(Conversation conversation);

	public Long getNumberOfUnseenConversations(User user);

	public List<Conversation> findLatestConversationsOfUser(User user);
}
