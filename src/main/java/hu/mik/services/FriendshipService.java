package hu.mik.services;

import java.util.List;

import hu.mik.beans.Friendship;

public interface FriendshipService {
	public List<Friendship> findAllByUserId(int userId);
	
	public Friendship findOne(int userId, int friendId);
	
	public Friendship saveFriendship(Friendship friendship);
	
	public void deleteFriendship(Friendship friendship);
}
