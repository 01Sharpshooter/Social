package hu.mik.dao;


import hu.mik.beans.User;

public interface UserDao{
	public void save(User user);
	
	public boolean takenUsername(String username);
	
	public User userByName(String username);
	
}
