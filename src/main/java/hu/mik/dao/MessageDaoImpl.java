package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
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
	public List<Message> findAllPagedByConversation(int offset, int pageSize, Conversation conversation) {
		List<Message> list=new ArrayList<>();
		list=this.em.createQuery("select m from Message m"
				+ " join fetch m.sender"
				+ " join fetch m.conversation c"
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
