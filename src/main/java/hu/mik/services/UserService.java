package hu.mik.services;

import java.util.List;

import hu.mik.beans.User;

public interface UserService {
	public void registerUser(String username, String passwd);
	
	public boolean takenUserName(String username);
	
	public User findUserByUsername(String username);	
	
	public void saveChanges(User user);
	
	public List<User> listAll();
	
}
