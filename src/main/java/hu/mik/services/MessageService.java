package hu.mik.services;

import java.util.List;

import hu.mik.beans.Message;

public interface MessageService {

	public List<Message> findAllByUserIDs(int number, int id1, int id2);
	
	public void saveMessage(Message message);
}
