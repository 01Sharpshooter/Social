package hu.mik.dao;


import java.util.List;

import hu.mik.beans.Role;
import hu.mik.beans.User;


public interface UserDao{
	public User save(User user, Role role);
	
	public boolean takenUsername(String username);
	
	public User findByUsername(String username);
	
	public User findById(int id);
	
	public List<User> findAll();
	
	public List<User> findAllLike(String username);

	public void saveChanges(User user);
	
}
