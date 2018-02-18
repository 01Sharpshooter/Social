package hu.mik.dao;

import java.util.List;

import hu.mik.beans.Friendship;
import hu.mik.beans.FriendRequest;

public interface FriendShipDao {
	public List<Friendship> findAllByUserId(int userId);
	
	public Friendship findOne(int userId, int friendId);
	
	public Friendship saveFriendship(Friendship friendship);
	
	public void saveFriendship(int userId, int friendId);
	
	public void deleteFriendship(int userId, int friendId);
}
