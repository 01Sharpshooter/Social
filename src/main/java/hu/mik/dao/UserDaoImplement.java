package hu.mik.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.User;

@Repository
@Transactional(propagation=Propagation.REQUIRED)
public class UserDaoImplement implements UserDao{
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public void save(User user) {
		em.persist(user);
		
	}

	@Override
	public boolean takenUsername(String username) {
		List<User> list=new ArrayList<>();
		list=em.createQuery("SELECT u FROM User u where u.username= :username", User.class)
				.setParameter("username", username)
				.getResultList();	

		if(!list.isEmpty()){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public User userByName(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
