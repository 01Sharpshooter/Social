package hu.mik.dao;

import java.util.List;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.User;

public interface MessageDao {

	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation);

	public void save(Message message);

	public List<Message> findLatestMessagesOfUser(int number, User user);

}
