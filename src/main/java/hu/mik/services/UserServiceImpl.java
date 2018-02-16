package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import hu.mik.beans.Role;
import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.dao.UserDao;

@Component
public class UserServiceImpl implements UserService{
	@Autowired
	UserDao userDao;
	

	@Override
	public User registerUser(String username) {
		User user=new User();
		user.setUsername(username);
		user.setImageName(UserConstants.DEFAULT_PROFILE_PICTURE_NAME);
		Role role=new Role();
		role.setUsername(username);
		role.setRole(UserConstants.DEFAULT_ROLE);
		return userDao.save(user);
		
	}
	@Override
	public boolean takenUserName(String username) {		
		return userDao.takenUsername(username);
	}
	
	@Override
	public User findUserByUsername(String username) {
		if(userDao.findByUsername(username)!=null){
			return userDao.findByUsername(username);
		}
		else{
			return null;
		}
		
	}

	@Override
	public void saveChanges(User user) {
		userDao.saveChanges(user);
		
	}
	@Override
	public List<User> listAll() {
		return userDao.findAll();
		
	}
	@Override
	public User findUserById(int id) {
		return userDao.findById(id);
	}
	@Override
	public List<User> findAllLike(String username) {
		return userDao.findAllLike(username);
	}
	
	
	
}
