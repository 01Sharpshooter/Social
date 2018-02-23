package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.mik.beans.Message;
import hu.mik.dao.MessageDao;

@Component
public class MessageServiceImpl implements MessageService{
	
	@Autowired
	MessageDao messageDao;
	

	@Override
	public List<Message> findAllByUserIDs(int number, int id1, int id2) {
		return messageDao.findAllByUserIDs(number, id1, id2);
	}

	@Override
	public void saveMessage(Message message) {
		messageDao.save(message);
		
	}

	@Override
	public List<Message> findLastestMessagesOfUser(int number, int userId) {
		return messageDao.findLatestMessagesOfUser(number, userId);
	}

	@Override
	public Message findLastByUserIDs(int id1, int id2) {
		return messageDao.findLastByUserIDs(id1, id2);
	}

}
