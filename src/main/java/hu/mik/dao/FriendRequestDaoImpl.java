package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.User;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class FriendRequestDaoImpl implements FriendRequestDao {

	@PersistenceContext
	EntityManager em;

	@Override
	public List<FriendRequest> findAllByRequestedId(int requestedId) {
		// @formatter:off
		List<FriendRequest> friendRequests = new ArrayList<>();
		friendRequests = this.em
				.createQuery("SELECT fr FROM FriendRequest fr"
						+ " JOIN FETCH fr.requestor"
						+ " WHERE fr.requested.id= :requestedId", FriendRequest.class)
				.setParameter("requestedId", requestedId).getResultList();
		return friendRequests;
		// @formatter:on
	}

	@Override
	public FriendRequest findOne(User requestor, User requested) {
		try {
			return this.em
					.createQuery("SELECT fr FROM FriendRequest fr"
							+ " WHERE requestor= :requestor and requested= :requested", FriendRequest.class)
					.setParameter("requestor", requestor).setParameter("requested", requested).getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public FriendRequest saveFriendRequest(FriendRequest request) {
		if (this.findOne(request.getRequestor(), request.getRequested()) == null) {
			this.em.persist(request);
		}
		return request;
	}

	@Override
	public void deleteFriendRequest(User requestor, User requested) {
		FriendRequest fr = this.findOne(requestor, requested);
		this.em.remove(fr);

	}

	@Override
	public boolean IsAlreadyRequested(User requestor, User requested) {
		if (this.findOne(requestor, requested) == null) {
			return false;
		}
		return true;
	}

}
