package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.Message;
import hu.mik.dao.MessageDao;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	MessageDao messageDao;

	@Override
	public List<Message> findAllByUserIDs(int number, int id1, int id2) {
		return this.messageDao.findAllByUserIDs(number, id1, id2);
	}

	@Override
	public void saveMessage(Message message) {
		this.messageDao.save(message);

	}

	@Override
	public List<Message> findLastestMessagesOfUser(int number, int userId) {
		return this.messageDao.findLatestMessagesOfUser(number, userId);
	}

	@Override
	public Message findLastByUserIDs(int id1, int id2) {
		return this.messageDao.findLastByUserIDs(id1, id2);
	}

}
