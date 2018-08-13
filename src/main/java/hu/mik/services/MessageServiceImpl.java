package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.dao.MessageDao;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	MessageDao messageDao;

	@Override
	public List<Message> findAllByUsers(int number, User user1, User user2) {
		return this.messageDao.findAllByUsers(number, user1, user2);
	}

	@Override
	public void saveMessage(Message message) {
		this.messageDao.save(message);

	}

	@Override
	public List<Message> findLastestMessagesOfUser(int number, User user) {
		return this.messageDao.findLatestMessagesOfUser(number, user);
	}

	@Override
	public Message findLastMessageOfUsers(User user1, User user2) {
		return this.messageDao.findLastMessageOfUsers(user1, user2);
	}

	@Override
	public int setAllPreviousSeen(User receiver, User sender) {
		return this.messageDao.setAllPreviousSeen(receiver, sender);

	}

	@Override
	public Long getNumberOfUnseenConversations(User user) {
		return this.messageDao.getNumberOfUnseenConversations(user);
	}

	@Override
	public List<Message> findLatestConversationsOfUser(User user) {
		return this.messageDao.findLatestConversationsOfUser(user);
	}

}
