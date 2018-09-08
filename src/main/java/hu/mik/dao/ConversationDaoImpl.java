package hu.mik.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Conversation;

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

}
