package hu.mik.dao;

import java.util.List;

import hu.mik.beans.FriendRequest;

public interface FriendRequestDao {
	public List<FriendRequest> findAllByRequestedId(int requestedId);
	
	public FriendRequest findOne(int requestorId, int requestedId);
	
	public FriendRequest saveFriendRequest(FriendRequest request);
	
	public void deleteFriendRequest(int requestorId, int requestedId);
}
