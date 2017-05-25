package hu.mik.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.mik.beans.Role;
import hu.mik.beans.User;

@Repository
@Transactional(propagation=Propagation.REQUIRED)
public class UserDaoImplement implements UserDao{
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public void save(User user, Role role) {
		if(findByUsername(user.getUsername())==null){
			em.persist(user);
			em.persist(role);
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
	@Override
	public List<User> findAll() {
		List<User> list=new ArrayList<>();
		list=em.createQuery("SELECT u FROM User u", User.class).getResultList();
		return list;	
		
	}
	@Override
	public List<User> findById(int id) {
		List<User> list=new ArrayList<>();
		list=em.createQuery("SELECT u FROM User u where u.id= :id", User.class)
				.setParameter("id", id)
				.getResultList();
		if(list.isEmpty()){
			return null;
		}else{
			return list;
		}
	}
	@Override
	public List<User> findAllLike(String username) {
		List<User> list=new ArrayList<>();
		list=em.createQuery("SELECT u FROM User u where u.username LIKE CONCAT('%',:username,'%')", User.class)
				.setParameter("username", username)
				.getResultList();
		if(list.isEmpty()){
			return null;
		}else{
			return list;
		}
	}
	@Override
	public void saveChanges(User user) {
		em.merge(user);
		
	}




	

}
