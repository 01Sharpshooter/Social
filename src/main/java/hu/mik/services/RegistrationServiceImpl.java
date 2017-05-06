package hu.mik.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.mik.beans.User;
import hu.mik.dao.UserDao;
@Component
public class RegistrationServiceImpl implements RegistrationService{
	@Autowired
	UserDao userDao;

	@Override
	public void registerUser(String username, String passwd) {
		User user=new User();
		user.setUsername(username);
		user.setPassword(passwd);
		userDao.save(user);
		
	}

	@Override
	public boolean takenUserName(String username) {		
		return userDao.takenUsername(username);
	}
	
	
	
}
