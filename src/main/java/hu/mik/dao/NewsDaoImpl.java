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
		list = this.em.createQuery("select n from News n join fetch n.user u where u.enabled = true order by n.id desc", News.class)
				.setFirstResult(offset)
				.setMaxResults(pageSize)
				.getResultList();
		return list;
	}
	@Override
	public List<News> getPagedNewsByUsernames(int offset, int pageSize, List<String> usernames){
		return this.em.createQuery(
				"SELECT n FROM News n"
				+ " JOIN FETCH n.user u"
				+ " WHERE u.username IN (:usernames)"
				+ " AND u.enabled = true"
				+ " ORDER BY n.id DESC", News.class)
				.setParameter("usernames", usernames)
				.setFirstResult(offset)
				.setMaxResults(pageSize)
				.getResultList();
	}

	@Override
	public List<News> getPagedNewsOfUser(int offset, int pageSize, User user){
		return this.em.createQuery(
				"SELECT n FROM News n"
				+ " JOIN FETCH n.user u"
				+ " WHERE n.user = :user"
				+ " ORDER BY n.id DESC", News.class)
				.setParameter("user", user)
				.setFirstResult(offset)
				.setMaxResults(pageSize)
				.getResultList();
	}

	@Override
	public void saveNews(News news) {
		this.em.persist(news);

	}
	@Override
	public void deleteNews(News news) {
		this.em.remove(this.em.getReference(News.class, news.getId()));

	}

}
