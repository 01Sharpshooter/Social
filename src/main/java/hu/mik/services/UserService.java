package hu.mik.services;

import java.util.List;

import hu.mik.beans.User;

public interface UserService {
	public User registerDefaultUser(User user);

	public boolean takenUserName(String username);

	public User findUserByUsername(String username);

	public User findUserById(int id);

	public List<User> listAll();

	public List<User> listAllEnabled();

	public List<User> findByFullNameContaining(String username);

	public User save(User user);

	public void disable(User user);

	public List<User> findAllByUsernames(List<String> usernames);

}
