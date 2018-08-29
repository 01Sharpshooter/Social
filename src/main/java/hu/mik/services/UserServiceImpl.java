package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.Role;
import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.dao.UserDao;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserDao userDao;

	@Override
	public User createDefaultUserWithUsername(String username) {
		User user = new User();
		user.setUsername(username);
		user.setImageName(UserConstants.DEFAULT_PROFILE_PICTURE_NAME);
		Role role = new Role();
		role.setUsername(username);
		role.setRole(UserConstants.DEFAULT_ROLE);
		return this.userDao.save(user);

	}

	@Override
	public boolean takenUserName(String username) {
		return this.userDao.takenUsername(username);
	}

	@Override
	public User findUserByUsername(String username) {
		User user = this.userDao.findByUsername(username);
		if (user != null) {
			return user;
		} else {
			return this.createDefaultUserWithUsername(username);
		}

	}

	@Override
	public List<User> listAll() {
		return this.userDao.findAll();

	}

	@Override
	public User findUserById(int id) {
		return this.userDao.findById(id);
	}

	@Override
	public List<User> findAllLike(String username) {
		return this.userDao.findAllLike(username);
	}

	@Override
	public User save(User user) {
		return this.userDao.save(user);
	}

}
