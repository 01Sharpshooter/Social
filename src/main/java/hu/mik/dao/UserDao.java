package hu.mik.dao;


import java.util.List;

import hu.mik.beans.User;

public interface UserDao{
	public void save(User user);
	
	public boolean takenUsername(String username);
	
	public List<User> findByUsername(String username);
	
}
