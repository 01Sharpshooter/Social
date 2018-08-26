package hu.mik.services;

import java.util.List;

import hu.mik.beans.Message;
import hu.mik.beans.User;

public interface MessageService {

	public List<Message> findAllByUsers(int number, User user1, User user2);

	public List<Message> findAllPagedByUsers(int offset, int pageSize, User user1, User user2);

	public void saveMessage(Message message);

	public List<Message> findLastestMessagesOfUser(int number, User user);

	public Message findLastMessageOfUsers(User user1, User user2);

	public int setAllPreviousSeen(User receiver, User sender);

	public Long getNumberOfUnseenConversations(User user);

	public List<Message> findLatestConversationsOfUser(User user);
}
