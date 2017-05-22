package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Friendship;

@Repository
@Transactional(propagation=Propagation.REQUIRED)
public class FriendShipDaoImpl implements FriendShipDao{
	
	@PersistenceContext
	EntityManager em;

	@Override
	public List<Friendship> findAllByUserId(int userId) {
		List<Friendship> list=new ArrayList<>();
		list=em.createQuery("select f from Friendship f where userid= :userId", 
				Friendship.class)
				.setParameter("userId", userId)
				.getResultList();
				
		return list;
	}

	@Override
	public Friendship saveFriendship(Friendship friendship) {
		em.persist(friendship);
		return friendship;
	}

	@Override
	public void deleteFriendship(Friendship friendship) {
		em.remove(friendship);
		
	}

	@Override
	public Friendship findOne(int userId, int friendId) {	
		Friendship fs;
		try {
			fs=em.createQuery("select f from Friendship f where userid= :userId and friendid= :friendId", 
					Friendship.class)
					.setParameter("userId", userId)
					.setParameter("friendId", friendId)
					.getSingleResult();
			
		} catch (NoResultException e) {
			fs=null;
		}		
		return fs;
	}

}
