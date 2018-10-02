package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Conversation;
import hu.mik.beans.ConversationUser;
import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.dao.ConversationDao;
import hu.mik.dao.MessageDao;
import hu.mik.dao.UserDao;
import hu.mik.utils.UserUtils;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

	@Autowired
	private MessageDao messageDao;
	@Autowired
	private ConversationDao conversationDao;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private UserDao userDao;

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
	public int setConversationSeen(Conversation conversation, User user) {
		return this.conversationDao.setConversationSeen(conversation, user);

	}

	@Override
	public Long getNumberOfUnseenConversations(User user) {
		return this.conversationDao.getNumberOfUnseenConversations(user);
	}

	@Override
	public List<Conversation> findLatestConversationsOfUser(User user) {
		System.err.println(this.conversationDao.findLatestConversationsOfUser(user));
		return this.conversationDao.findLatestConversationsOfUser(user);
	}

	@Override
	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation) {
		return this.messageDao.findAllPagedByConversation(offset, pageSize, conversation);
	}

	@Override
	public Conversation saveConversation(Conversation conversation) {
		return this.conversationDao.saveConversation(conversation);

	}

	@Override
	public Conversation findOrCreateConversationWithUser(Integer userId) {
		Conversation conversation = this.conversationDao
				.findConversationOfUsers(this.userUtils.getLoggedInUser().getDbUser(), userId);
		if (conversation == null) {
			conversation = new Conversation();
			User partner = this.userDao.findById(userId); // TODO mi van, ha nem talál?
			ConversationUser conversationUser = new ConversationUser(conversation,
					this.userUtils.getLoggedInUser().getDbUser());
			ConversationUser conversationPartner = new ConversationUser(conversation, partner);
			conversation.addConversationUser(conversationUser);
			conversation.addConversationUser(conversationPartner);
		}
		return conversation;
	}
}
