package hu.mik.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Role;
import hu.mik.beans.User;
import hu.mik.services.LdapService;

@Repository
@Transactional
public class UserDaoImplement implements UserDao {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	LdapService ldapService;

	@Override
	public User save(User user, Role role) {
		if (this.findByUsername(user.getUsername()) == null) {
			this.em.persist(user);
			this.em.persist(role);
		}
		return this.findByUsername(user.getUsername());
	}

	@Override
	public User save(User user) {
		if (this.findByUsername(user.getUsername()) == null) {
			this.em.persist(user);
		} else {
			this.em.merge(user);
		}
		return this.findByUsername(user.getUsername());
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
	public User findByUsername(String username) {
		User user;
		try {
			user = this.em.createQuery("SELECT u FROM User u where u.username= :username", User.class)
					.setParameter("username", username).getSingleResult();
		} catch (Exception e) {
			user = null;
		}
		return user;
	}

	@Override
	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		list = this.em.createQuery("SELECT u FROM User u", User.class).getResultList();
		return list;

	}

	@Override
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
	public List<User> findByFullNameContaining(String fullName) {
		List<User> list = new ArrayList<>();
		list = this.em
				.createQuery("SELECT u FROM User u where UPPER(u.fullName) LIKE CONCAT('%',:username,'%')", User.class)
				.setParameter("username", fullName.toUpperCase()).getResultList();
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

}
