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
	public Conversation saveConversation(Conversation conversation) {
		if (conversation.getId() == null) {
			this.em.persist(conversation);
			this.em.persist(conversation.getLastMessage());
			conversation.getConversationUsers().forEach(conversationUser -> this.em.persist(conversationUser));
		} else {
			System.err.println("merge");
			conversation = this.em.merge(conversation);
		}
		return conversation;
	}

	//@formatter:off
	@Override
	public List<Conversation> findLatestConversationsOfUser(User user) {
		return this.em.createQuery(
				"SELECT DISTINCT(c) FROM Conversation c"
				+ " JOIN FETCH c.conversationUsers cu"
				+ " JOIN FETCH cu.user u"
				+ " JOIN FETCH c.lastMessage m"
				+ " JOIN FETCH m.sender s"
				+ " WHERE cu.conversation IN (SELECT cu2.conversation FROM ConversationUser cu2 where cu2.user = :user)"
//				+ " AND u.enabled = true"
//				+ " AND s.enabled = true"
				+ " ORDER BY m.id DESC"
				, Conversation.class)
				.setParameter("user", user)
				.getResultList();
	}

	@Override
	public Long getNumberOfUnseenConversations(User user) {
		try {
			return (Long) this.em.createQuery(
					"SELECT COUNT(cu.id)"
					+ " FROM ConversationUser cu"
					+ " WHERE cu.seen = false"
					+ " AND cu.user = :user")
			.setParameter("user", user)
			.getSingleResult();
		} catch (NoResultException e) {
			return (long) 0;
		}
	}

	@Override
	public int setConversationSeen(Conversation conversation, User user) {
		return this.em.createQuery(
				"UPDATE ConversationUser cu SET seen = true"
				+ " WHERE cu.conversation = :conversation"
				+ " AND cu.user = :user")
		.setParameter("conversation", conversation)
		.setParameter("user", user)
		.executeUpdate();

	}

	@Override
	public Conversation findConversationOfUsers(User loggedUser, Integer partnerId) {
		try{
		return this.em.createQuery(
				"SELECT c FROM Conversation c"
				+ " JOIN ConversationUser conv_u ON c.id = conv_u.conversation"
				+ " JOIN FETCH c.conversationUsers ccu"
				+ " JOIN FETCH ccu.user"
				+ " JOIN FETCH c.lastMessage m"
				+ " JOIN FETCH m.sender"
				+ " WHERE conv_u.user = :loggedUser"
				+ " AND c IN ("
				+ " SELECT cu2.conversation FROM ConversationUser cu"
				+ " JOIN ConversationUser cu2 ON cu.conversation = cu2.conversation"
				+ " WHERE cu.user.id = :partnerId"
				+ " GROUP BY cu2.conversation"
				+ " HAVING COUNT(*) = 2)", Conversation.class)
		.setParameter("loggedUser", loggedUser)
		.setParameter("partnerId", partnerId)
		.getSingleResult();
		}catch (NoResultException e) {
			return null;
		}
	}

}
