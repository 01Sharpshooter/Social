package hu.mik.services;

import java.util.List;

import hu.mik.beans.FriendRequest;

public interface FriendRequestService {
	public List<FriendRequest> findAllByRequestedId(int requestedId);
	
	public FriendRequest findOne(int requestorId, int requestedId);
	
	public FriendRequest saveFriendRequest(FriendRequest request);
	
	public void deleteFriendRequest(int requestorId, int requestedId);
	
	public boolean IsAlreadyRequested(int requestorId, int requestedId);
}
