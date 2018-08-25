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
@Transactional(propagation = Propagation.REQUIRED)
public class NewsDaoImpl implements NewsDao {

	@PersistenceContext
	EntityManager em;

	//@formatter:off
	@Override
	public List<News> getPagedNews(int offset, int pageSize) {
		List<News> list = new ArrayList<>();
		list = this.em.createQuery("select n from News n join fetch n.user order by n.time desc", News.class)
				.setFirstResult(offset)
				.setMaxResults(pageSize)
				.getResultList();
		return list;
	}
	@Override
	public List<News> getPagedNewsByUsernames(int offset, int pageSize, List<String> usernames){
		List<News> list = new ArrayList<>();
		list = this.em.createQuery(
				"SELECT n FROM News n"
				+ " JOIN FETCH n.user u"
				+ " WHERE u.username IN (:usernames)"
				+ " ORDER BY n.time DESC", News.class)
				.setParameter("usernames", usernames)
				.setFirstResult(offset)
				.setMaxResults(pageSize)
				.getResultList();
		return list;
	}

	@Override
	public List<News> lastGivenNewsUser(int number, User user) {
		List<News> list = new ArrayList<>();
		list = this.em.createQuery("select n from News n where n.newsUser= :user order by n.time desc", News.class)
				.setParameter("user", user)
				.setFirstResult(0)
				.setMaxResults(number - 1)
				.getResultList();
		return list;
	}

	@Override
	public void saveNews(News news) {
		this.em.persist(news);

	}

}
