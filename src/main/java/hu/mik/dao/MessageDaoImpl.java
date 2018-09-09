package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.User;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class MessageDaoImpl implements MessageDao {

	@PersistenceContext
	private EntityManager em;

	//@formatter:off

	@Override
	public List<Message> findAllByUsers(int number, User user1, User user2) {
		List<Message> list=new ArrayList<>();
		list=this.em.createQuery("select m from Message m"
				+ " join fetch m.sender"
				+ " join fetch m.receiver where"
				+ " (m.sender= :user1 and m.receiver= :user2)"
				+ " or (m.sender= :user2 and m.receiver= :user1)"
				+ " order by m.time desc",
				Message.class)
				.setParameter("user1", user1)
				.setParameter("user2", user2)
				.setFirstResult(0)
				.setMaxResults(number-1)
				.getResultList();
		return list;
	}

	@Override
	public void save(Message message) {
		//message.getConversation().setLastMessage(message.getId());

		this.em.persist(message);
	}

	@Override
	public List<Message> findLatestMessagesOfUser(int number, User user) {
		List<Message> list=new ArrayList<>();
		list=this.em.createQuery("select m from Message m where"
				+ " (m.sender= :user or m.receiver= :user)"
				+ " order by m.time desc",
				Message.class)
				.setParameter("user", user)
				.setFirstResult(0)
				.setMaxResults(number-1)
				.getResultList();
		return list;

	}

	@Override
	public Message findLastMessageOfUsers(User user1, User user2) {
		Message message;
		try{
			message=this.em.createQuery("select m from Message m where"
					+ " (m.sender= :user1 and m.receiver= :user2)"
					+ " or (m.sender= :user2 and m.receiver= :user1)"
					+ " order by m.time desc",Message.class)
					.setParameter("user1", user1)
					.setParameter("user2", user2)
					.setFirstResult(0)
					.setMaxResults(1)
					.getSingleResult();
		} catch (Exception e) {
			message = null;
		}
		return message;
	}

	@Override
	public int setConversationSeen(Conversation conversation) {
		return this.em.createQuery(
				"UPDATE Conversation c SET seen = true"
				+ " WHERE c = :conversation")
		.setParameter("conversation", conversation)
		.executeUpdate();

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
//
	}

	@Override
	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation) {
		List<Message> list=new ArrayList<>();
		list=this.em.createQuery("select m from Message m"
				+ " join fetch m.sender"
				+ " join fetch m.conversation c"
				+ " join fetch c.user1"
				+ " join fetch c.user2"
				+ " where"
				+ " m.conversation = :conversation"
				+ " and m.id < :offset"
				+ " order by m.id desc",
				Message.class)
				.setParameter("conversation", conversation)
				.setParameter("offset", offset)
				.setMaxResults(pageSize)
				.getResultList();
		return list;
	}

}
