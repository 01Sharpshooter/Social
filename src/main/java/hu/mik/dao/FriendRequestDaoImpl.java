package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.FriendRequest;
import hu.mik.services.UserService;

@Repository
@Transactional(propagation=Propagation.REQUIRED)
public class FriendRequestDaoImpl implements FriendRequestDao{
	
	@PersistenceContext
	EntityManager em;

	@Override
	public List<FriendRequest> findAllByRequestedId(int requestedId) {
		List<FriendRequest> friendRequests=new ArrayList<>();
		friendRequests=em.createQuery("select fr from FriendRequest fr where fr.requestedId= :requestedId",
				FriendRequest.class)
				.setParameter("requestedId", requestedId)
				.getResultList();
		return friendRequests;
	}

	@Override
	public hu.mik.beans.FriendRequest findOne(int requestorId, int requestedId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FriendRequest saveFriendRequest(FriendRequest request) {
		em.persist(request);
		return request;
	}

	@Override
	public void deleteFriendRequest(FriendRequest request) {
		em.remove(request);
		
	}

}
