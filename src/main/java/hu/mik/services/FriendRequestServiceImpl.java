package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.FriendRequest;
import hu.mik.dao.FriendRequestDao;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

	@Autowired
	FriendRequestDao friendRequestDao;

	@Override
	public List<FriendRequest> findAllByRequestedId(int requestedId) {
		return this.friendRequestDao.findAllByRequestedId(requestedId);
	}

	@Override
	public FriendRequest findOne(int requestorId, int requestedId) {
		return this.friendRequestDao.findOne(requestorId, requestedId);
	}

	@Override
	public FriendRequest saveFriendRequest(FriendRequest request) {
		return this.friendRequestDao.saveFriendRequest(request);
	}

	@Override
	public void deleteFriendRequest(int requestorId, int requestedId) {
		this.friendRequestDao.deleteFriendRequest(requestorId, requestedId);

	}

	@Override
	public boolean IsAlreadyRequested(int requestorId, int requestedId) {
		return this.friendRequestDao.IsAlreadyRequested(requestorId, requestedId);
	}

}
