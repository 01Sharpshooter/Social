package hu.mik.services;

import java.util.List;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.User;

public interface ChatService {

	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation);

	public void saveMessage(Message message);

	public List<Message> findLastestMessagesOfUser(int number, User user);

	public int setConversationSeen(Conversation conversation, User user);

	public Long getNumberOfUnseenConversations(User user);

	public List<Conversation> findLatestConversationsOfUser(User user);

	public Conversation saveConversation(Conversation conversation);

	public Conversation findOrCreateConversationWithUser(Integer userId, User loggedUser);
}
