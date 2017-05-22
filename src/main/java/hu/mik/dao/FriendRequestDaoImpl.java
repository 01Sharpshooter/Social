package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.Friendship;
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
	public FriendRequest findOne(int requestorId, int requestedId) {
		FriendRequest fr;
		try {
			fr=em.createQuery("select fr from FriendRequest fr where requestorid= :requestorId and requestedid= :requestedId", 
					FriendRequest.class)
					.setParameter("requestorId", requestorId)
					.setParameter("requestedId", requestedId)
					.getSingleResult();
			
		} catch (NoResultException e) {
			fr=null;
		}		
		return fr;
	}

	@Override
	public FriendRequest saveFriendRequest(FriendRequest request) {
		if(findOne(request.getRequestorId(), request.getRequestedId())==null){
			em.persist(request);
		}
		return request;
	}

	@Override
	public void deleteFriendRequest(int requestorId, int requestedId) {
		FriendRequest fr=findOne(requestorId, requestedId);
		em.remove(fr);
		
	}

}
