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
import hu.mik.beans.User;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class FriendShipDaoImpl implements FriendShipDao {

	@PersistenceContext
	EntityManager em;

	@Override
	public List<Friendship> findAllByUserId(int userId) {
		//@formatter:off
		List<Friendship> list = new ArrayList<>();
		list = this.em
				.createQuery("select f from Friendship f"
						+ " JOIN FETCH f.user user"
						+ " JOIN FETCH f.friend friend"
						+ " where user.id= :userId OR friend.id = :userId", Friendship.class)
				.setParameter("userId", userId).getResultList();

		return list;
		//@formatter:on
	}

	@Override
	public Friendship saveFriendship(Friendship friendship) {
		this.em.persist(friendship);
		return friendship;
	}

	@Override
	public void deleteFriendship(int userId, int friendId) {
		this.em.remove(this.findOne(userId, friendId));

	}

	@Override
	public Friendship findOne(int userId, int friendId) {
		//@formatter:off
		Friendship fs;
		try {
			fs = this.em
					.createQuery("SELECT f FROM Friendship f"
							+ " JOIN FETCH f.user user"
							+ " JOIN FETCH f.friend friend"
							+ " WHERE (user.id= :userId AND friend.id= :friendId)"
							+ " OR (user.id= :friendId AND friend.id= :userId)",
							Friendship.class)
					.setParameter("userId", userId).setParameter("friendId", friendId).getSingleResult();

		} catch (NoResultException e) {
			fs = null;
		}
		return fs;
		//@formatter:on
	}

	@Override
	public void saveFriendship(User user, User friend) {
		Friendship fs = new Friendship(user, friend);
		this.em.persist(fs);
	}

}
