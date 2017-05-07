package hu.mik.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hu.mik.dao.UserDao;

@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		hu.mik.beans.User user=userDao.findByUsername(username).get(0);
//		List<GrantedAuthority> authorities=new ArrayList<>();
//		authorities.add(new SimpleGrantedAuthority(user.getRole()));
//		
//		return new User(user.getUsername(), user.getPassword(), authorities);
		return null;
	}


}
