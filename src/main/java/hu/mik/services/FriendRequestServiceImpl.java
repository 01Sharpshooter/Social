package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.mik.beans.FriendRequest;
import hu.mik.dao.FriendRequestDao;

@Component
public class FriendRequestServiceImpl implements FriendRequestService{
	
	@Autowired
	FriendRequestDao friendRequestDao;

	@Override
	public List<FriendRequest> findAllByRequestedId(int requestedId) {		
		return friendRequestDao.findAllByRequestedId(requestedId);
	}

	@Override
	public FriendRequest findOne(int requestorId, int requestedId) {
		return friendRequestDao.findOne(requestorId, requestedId);
	}

	@Override
	public FriendRequest saveFriendRequest(FriendRequest request) {
		return friendRequestDao.saveFriendRequest(request);
	}

	@Override
	public void deleteFriendRequest(int requestorId, int requestedId) {
		friendRequestDao.deleteFriendRequest(requestorId, requestedId);
		
	}

	@Override
	public boolean IsAlreadyRequested(int requestorId, int requestedId) {
		return friendRequestDao.IsAlreadyRequested(requestorId, requestedId);
	}
	
}
