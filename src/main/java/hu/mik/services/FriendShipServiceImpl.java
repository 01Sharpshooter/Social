package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.Friendship;
import hu.mik.beans.User;
import hu.mik.dao.FriendShipDao;

@Service
public class FriendShipServiceImpl implements FriendshipService {

	@Autowired
	FriendShipDao friendshipDao;

	@Override
	public List<Friendship> findAllByUserId(int userId) {
		return this.friendshipDao.findAllByUserId(userId);
	}

	@Override
	public Friendship findOne(int userId, int friendId) {
		return this.friendshipDao.findOne(userId, friendId);
	}

	@Override
	public Friendship saveFriendship(Friendship friendship) {
		return this.friendshipDao.saveFriendship(friendship);
	}

	@Override
	public void deleteFriendship(int userId, int friendId) {
		this.friendshipDao.deleteFriendship(userId, friendId);

	}

	@Override
	public void saveFriendship(User user, User friend) {
		this.friendshipDao.saveFriendship(user, friend);

	}

}
