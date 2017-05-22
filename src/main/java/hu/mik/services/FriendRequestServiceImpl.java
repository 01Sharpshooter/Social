package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import hu.mik.beans.FriendRequest;
import hu.mik.dao.FriendRequestDao;

public class FriendRequestServiceImpl implements FriendRequestService{
	
	@Autowired
	FriendRequestDao friendRequestDao;

	@Override
	public List<FriendRequest> findAllByRequestedId(int requestedId) {		
		return friendRequestDao.findAllByRequestedId(requestedId);
	}

	@Override
	public FriendRequest findOne(int requestorId, int requestedId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FriendRequest saveFriendRequest(FriendRequest request) {
		return friendRequestDao.saveFriendRequest(request);
	}

	@Override
	public void deleteFriendRequest(FriendRequest request) {
		friendRequestDao.deleteFriendRequest(request);
		
	}
	
}
