package hu.mik.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.spring.annotation.SpringComponent;

import hu.mik.beans.LdapUser;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@SpringComponent
@Scope("session")
public class UserUtils {
	@Autowired
	private UserService userService;
	@Autowired
	private LdapService ldapService;

	private SocialUserWrapper socialUser;

	public SocialUserWrapper initSocialUser(String username) {
		SocialUserWrapper socialUser = new SocialUserWrapper();
		User user = this.userService.findUserByUsername(username);
		System.err.println("FINDDB: " + user);
		if (user == null) {
			user = this.userService.registerUser(username);
		}
		LdapUser ldapUser = this.ldapService.findUserByUsername(username);
		System.err.println("FINDLDAP: " + ldapUser);
		socialUser.setDbUser(user);
		socialUser.setLdapUser(ldapUser);
		return socialUser;
	}

	public SocialUserWrapper getLoggedInUser() {
		if (this.socialUser == null) {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			this.socialUser = this.initSocialUser(username);
		}
		return this.socialUser;

	}
}
