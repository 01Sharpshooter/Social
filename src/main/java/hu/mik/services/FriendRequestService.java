package hu.mik.services;

import java.util.List;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.User;

public interface FriendRequestService {
	public List<FriendRequest> findAllByRequestedId(int requestedId);

	public FriendRequest findOne(User requestor, User requested);

	public FriendRequest saveFriendRequest(FriendRequest request);

	public void deleteFriendRequest(User requestor, User requested);

	public boolean IsAlreadyRequested(User requestor, User requested);
}
