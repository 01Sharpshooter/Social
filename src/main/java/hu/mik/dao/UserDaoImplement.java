package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Role;
import hu.mik.beans.User;

@Repository
@Transactional
public class UserDaoImplement implements UserDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public User save(User user, Role role) {
		if (this.findByUsername(user.getUsername()) == null) {
			this.em.persist(user);
			this.em.persist(role);
		}
		return this.findByUsername(user.getUsername());
	}

	@Override
	@Caching(evict = { @CacheEvict(cacheNames = "user", key = "#user.username"),
			@CacheEvict(cacheNames = "user", key = "#user.id"),
			@CacheEvict(cacheNames = "userList", allEntries = true) })
	public User save(User user) {
		if (user.getId() == null) {
			this.em.persist(user);
			return this.findByUsername(user.getUsername());
		} else {
			return this.em.merge(user);
		}
	}

	@Override
	public boolean takenUsername(String username) {
		User user = this.findByUsername(username);

		if (user == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	@Cacheable("user")
	public User findByUsername(String username) {
		User user;
		try {
			user = this.em.createQuery("SELECT u FROM User u where LOWER(u.username) = :username", User.class)
					.setParameter("username", username.toLowerCase()).getSingleResult();
		} catch (Exception e) {
			user = null;
		}
		return user;
	}

	@Override
	public List<User> findAll() {
		return this.em.createQuery("SELECT u FROM User u", User.class).getResultList();
	}

	@Override
	@Cacheable("userList")
	public List<User> findAllEnabled() {
		return this.em.createQuery("SELECT u FROM User u WHERE u.enabled = true", User.class).getResultList();
	}

	@Override
	@Cacheable("user")
	public User findById(int id) {
		User user;
		try {
			user = this.em.createQuery("SELECT u FROM User u where u.id= :id", User.class).setParameter("id", id)
					.getSingleResult();
		} catch (Exception e) {
			user = null;
		}
		return user;
	}

	@Override
	@Cacheable("userList")
	public List<User> findByFullNameContaining(String fullName) {
		List<User> list = new ArrayList<>();
		list = this.em.createQuery(
				"SELECT u FROM User u where UPPER(u.fullName) LIKE CONCAT('%',:username,'%') AND u.enabled = true",
				User.class).setParameter("username", fullName.toUpperCase()).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list;
		}
	}

	@Override
	public void disable(User user) {
		this.em.createQuery("UPDATE User u SET enabled=false WHERE u = :user").setParameter("user", user)
				.executeUpdate();

	}

	@Override
	public List<User> findAllByUsernames(List<String> usernames) {
		if (usernames.isEmpty()) {
			return null;
		}
		//@formatter:off
		return this.em.createQuery(
				"SELECT u FROM User u"
				+ " WHERE LOWER(u.username) IN (:usernames)"
				+ " AND u.enabled = true", User.class)
		.setParameter("usernames", usernames)
		.getResultList();
	}

}
