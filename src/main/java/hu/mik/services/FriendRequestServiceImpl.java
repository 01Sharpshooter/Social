package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.User;
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
	public FriendRequest saveFriendRequest(FriendRequest request) {
		return this.friendRequestDao.saveFriendRequest(request);
	}

	@Override
	public void deleteFriendRequest(User requestor, User requested) {
		this.friendRequestDao.deleteFriendRequest(requestor, requested);

	}

	@Override
	public boolean IsAlreadyRequested(User requestor, User requested) {
		return this.friendRequestDao.IsAlreadyRequested(requestor, requested);
	}

}
