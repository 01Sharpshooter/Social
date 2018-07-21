package hu.mik.services;

import java.util.List;

import hu.mik.beans.User;

public interface UserService {
	public User createDefaultUserWithUsername(String username);

	public boolean takenUserName(String username);

	public User findUserByUsername(String username);

	public User findUserById(int id);

	public List<User> listAll();

	public List<User> findAllLike(String username);

	public User save(User user);

}
