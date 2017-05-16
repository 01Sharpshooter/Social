package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Message;

@Repository
@Transactional(propagation=Propagation.REQUIRED)
public class MessageDaoImpl implements MessageDao {
	
	@PersistenceContext
	EntityManager em;

	@Override
	public List<Message> findAllByUserIDs(int number, int id1, int id2) {
		List<Message> list=new ArrayList<>();
		list=em.createQuery("select m from Message m where "
				+ "(m.senderId= :id1 and m.receiverId= :id2)"
				+ "or (m.senderId= :id2 and m.receiverId= :id1)"
				+ "order by m.time asc", 
				Message.class)
				.setParameter("id1", id1)
				.setParameter("id2", id2)
				.setFirstResult(0)
				.setMaxResults(number-1)
				.getResultList();
		return list;
	}

	@Override
	public void save(Message message) {
		em.persist(message);
	}

}
