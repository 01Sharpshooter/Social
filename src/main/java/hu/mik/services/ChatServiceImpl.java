package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.dao.ConversationDao;
import hu.mik.dao.MessageDao;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	private MessageDao messageDao;
	@Autowired
	private ConversationDao conversationDao;

	@Override
	public void saveMessage(Message message) {
		this.messageDao.save(message);
		this.conversationDao.saveConversation(message.getConversation());
	}

	@Override
	public List<Message> findLastestMessagesOfUser(int number, User user) {
		return this.messageDao.findLatestMessagesOfUser(number, user);
	}

	@Override
	public int setConversationSeen(Conversation conversation) {
		return this.conversationDao.setConversationSeen(conversation);

	}

	@Override
	public Long getNumberOfUnseenConversations(User user) {
		return this.conversationDao.getNumberOfUnseenConversations(user);
	}

	@Override
	public List<Conversation> findLatestConversationsOfUser(User user) {
		return this.conversationDao.findLatestConversationsOfUser(user);
	}

	@Override
	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation) {
		return this.messageDao.findAllPagedByConversation(offset, pageSize, conversation);
	}

}
