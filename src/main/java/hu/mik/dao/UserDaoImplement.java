package hu.mik.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
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
		if(findByUsername(user.getUsername())==null){
			em.persist(user);
		}else{
			em.merge(user);
		}
		
	}
	@Override
	public boolean takenUsername(String username) {
		List<User> list=findByUsername(username);

		if(list==null){
			return false;
		}
		else{
			return true;
		}
	}
	@Override
	public List<User> findByUsername(String username) {
		List<User> list=new ArrayList<>();
		list=em.createQuery("SELECT u FROM User u where u.username= :username", User.class)
				.setParameter("username", username)
				.getResultList();
		if(list.isEmpty()){
			return null;
		}else{
			return list;
		}
	}




	

}
