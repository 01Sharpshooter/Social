package hu.mik.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Conversation;
import hu.mik.beans.User;

@Repository
@Transactional
public class ConversationDaoImpl implements ConversationDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void saveConversation(Conversation conversation) {
		if (conversation.getId() == null) {
			this.em.persist(conversation);
		} else {
			this.em.merge(conversation);
		}
	}

	//@formatter:off
	@Override
	public List<Conversation> findLatestConversationsOfUser(User user) {
		return this.em.createQuery(
				"SELECT c FROM Conversation c "
				+ "JOIN FETCH c.lastMessage m "
				+ "JOIN FETCH m.sender "
				+ "JOIN FETCH c.user1 "
				+ "JOIN FETCH c.user2 "
				+ "WHERE (c.user1 = :user or c.user2 = :user) "
				+ "ORDER BY m.id DESC"
				, Conversation.class)
				.setParameter("user", user)
				.getResultList();
	}

	@Override
	public Long getNumberOfUnseenConversations(User user) {
		try {
			return (Long) this.em.createQuery(
					"SELECT COUNT(DISTINCT c.id)"
					+ " FROM Conversation c"
					+ " JOIN c.lastMessage m"
					+ " WHERE (c.user1 = :user OR c.user2 = :user)"
					+ " AND c.seen = false"
					+ " AND m.sender != :user")
			.setParameter("user", user)
			.getSingleResult();
		} catch (NoResultException e) {
			return (long) 0;
		}
	}

	@Override
	public int setConversationSeen(Conversation conversation) {
		return this.em.createQuery(
				"UPDATE Conversation c SET seen = true"
				+ " WHERE c = :conversation")
		.setParameter("conversation", conversation)
		.executeUpdate();

	}

}
