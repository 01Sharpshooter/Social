package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import hu.mik.beans.Friendship;
import hu.mik.dao.FriendShipDao;

public class FriendShipServiceImpl implements FriendshipService{
	
	@Autowired
	FriendShipDao friendshipDao;

	@Override
	public List<Friendship> findAllByUserId(int userId) {
		return friendshipDao.findAllByUserId(userId);
	}

	@Override
	public Friendship findOne(int userId, int friendId) {
		return friendshipDao.findOne(userId, friendId);
	}

	@Override
	public Friendship saveFriendship(Friendship friendship) {
		return friendshipDao.saveFriendship(friendship);
	}

	@Override
	public void deleteFriendship(Friendship friendship) {
		friendshipDao.deleteFriendship(friendship);
		
	}

}
