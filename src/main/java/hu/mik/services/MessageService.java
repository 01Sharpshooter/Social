package hu.mik.services;

import java.util.List;

import hu.mik.beans.Message;

public interface MessageService {

	public List<Message> findAllByUserIDs(int number, int id1, int id2);

	public void saveMessage(Message message);

	public List<Message> findLastestMessagesOfUser(int number, int userId);

	public Message findLastByUserIDs(int id1, int id2);

	public int setAllPreviousSeen(Integer receiverId, Integer senderId);

	public Long getNumberOfUnseenMessages(Integer userId);
}
