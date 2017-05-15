package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.News;
import hu.mik.beans.User;

@Repository
@Transactional(propagation=Propagation.REQUIRED)
public class NewsDaoImpl implements NewsDao{
	
	@PersistenceContext
	EntityManager em;

	@Override
	public List<News> lastGivenNewsAll(int number) {
		List<News> list=new ArrayList<>();
		list=em.createQuery("select n from News n order by n.time asc", News.class)
				.setFirstResult(0)
				.setMaxResults(number-1)
				.getResultList();
		return list;
	}

	@Override
	public List<News> lastGivenNewsUser(int number, User user) {
		List<News> list=new ArrayList<>();
		list=em.createQuery("select n from News n where n.user_id= :user_id order by n.time asc", News.class)
				.setParameter("user_id", user.getId())
				.setFirstResult(0)
				.setMaxResults(number-1)
				.getResultList();
		return list;
	}

	@Override
	public void saveNews(News news) {
		em.persist(news);
		
	}
	
}
