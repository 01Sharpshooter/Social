package hu.mik.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {

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
