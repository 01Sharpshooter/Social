package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.dao.UserDao;

@Component
public class UserServiceImpl implements UserService{
	@Autowired
	UserDao userDao;
	

	@Override
	public void registerUser(String username, String passwd) {
		User user=new User();
		user.setUsername(username);
		user.setPassword(passwd);
		user.setRole(UserConstants.DEFAULT_ROLE);
		user.setImageName(UserConstants.DEFAULT_PROFILE_PICTURE_NAME);
		userDao.save(user);
		
	}
	@Override
	public boolean takenUserName(String username) {		
		return userDao.takenUsername(username);
	}
	
	@Override
	public User findUserByUsername(String username) {
		if(userDao.findByUsername(username)!=null){
			return userDao.findByUsername(username).get(0);
		}
		else{
			return null;
		}
		
	}

	@Override
	public void saveChanges(User user) {
		userDao.save(user);
		
	}
	@Override
	public List<User> listAll() {
		return userDao.findAll();
		
	}
	
	
	
}
