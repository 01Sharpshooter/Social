package hu.mik.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.Role;
import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.dao.UserDao;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	@Override
	public User registerDefaultUser(User user) {
		Role role = new Role();
		role.setRole(UserConstants.DEFAULT_ROLE);
		return this.userDao.save(user);

	}

	@Override
	public boolean takenUserName(String username) {
		return this.userDao.takenUsername(username);
	}

	@Override
	public User findUserByUsername(String username) {
		return this.userDao.findByUsername(username);
//		if (user != null) {
//			return user;
//		} else {
//			return this.registerDefaultUser(username);
//		}

	}

	@Override
	public List<User> listAll() {
		return this.userDao.findAll();

	}

	@Override
	public List<User> listAllEnabled() {
		return this.userDao.findAllEnabled();

	}

	@Override
	public User findUserById(int id) {
		return this.userDao.findById(id);
	}

	@Override
	public List<User> findByFullNameContaining(String fullName) {
		return this.userDao.findByFullNameContaining(fullName);
	}

	@Override
	public User save(User user) {
		return this.userDao.save(user);
	}

	@Override
	public void disable(User user) {
		this.userDao.disable(user);

	}

	@Override
	public List<User> findAllByUsernames(List<String> usernames) {
		List<String> usernamesLower = usernames.stream().map(String::toLowerCase).collect(Collectors.toList());
		return this.userDao.findAllByUsernames(usernamesLower);
	}

}
