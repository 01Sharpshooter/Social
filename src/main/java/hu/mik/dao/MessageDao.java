package hu.mik.dao;

import java.util.List;

import hu.mik.beans.Message;

public interface MessageDao {
	public List<Message> findAllByUserIDs(int number, int id1, int id2);
	
	public void save(Message message);

}
