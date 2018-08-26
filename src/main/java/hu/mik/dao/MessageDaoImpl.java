package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Message;
import hu.mik.beans.User;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class MessageDaoImpl implements MessageDao {

	@PersistenceContext
	EntityManager em;

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
	public int setAllPreviousSeen(User receiver, User sender) {
		return this.em.createQuery(
				"UPDATE Message m SET seen = 1"
				+ " WHERE m.receiver = :receiver"
				+ " AND m.sender = :sender"
				+ " AND seen = 0")
		.setParameter("receiver", receiver)
		.setParameter("sender", sender)
		.executeUpdate();

	}

	@Override
	public Long getNumberOfUnseenConversations(User user) {
		return (Long) this.em.createQuery(
				"SELECT COUNT(DISTINCT m.sender)"
				+ " FROM Message m"
				+ " WHERE m.receiver = :user"
				+ " AND m.seen = 0"
				+ " ORDER BY m.id DESC")
		.setParameter("user", user)
		.getResultList().get(0);
	}

	@Override
	public List<Message> findLatestConversationsOfUser(User user) {
		return this.em.createQuery(
				"SELECT mess FROM Message mess "
				+ "JOIN FETCH mess.sender "
				+ "JOIN FETCH mess.receiver WHERE (mess.receiver = :user OR mess.sender = :user) AND mess.id IN("
				+ "SELECT MAX(m.id) FROM Message m GROUP BY (m.receiver+m.sender))"
				+ "ORDER BY mess.id DESC"
				, Message.class)
				.setParameter("user", user)
				.getResultList();
//
	}

	@Override
	public List<Message> findAllPagedByUsers(int offset, int pageSize, User user1, User user2) {
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
				.setFirstResult(offset)
				.setMaxResults(pageSize)
				.getResultList();
		return list;
	}

}
