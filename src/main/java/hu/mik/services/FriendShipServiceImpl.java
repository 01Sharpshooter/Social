package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.mik.beans.Friendship;
import hu.mik.dao.FriendShipDao;

@Component
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
	public void deleteFriendship(int userId, int friendId) {
		friendshipDao.deleteFriendship(userId, friendId);
		
	}

	@Override
	public void saveFriendship(int userId, int friendId) {
		friendshipDao.saveFriendship(userId, friendId);
		
	}

}
