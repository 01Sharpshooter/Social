package hu.mik.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.spring.annotation.SpringComponent;

import hu.mik.beans.LdapUser;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@SpringComponent
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserUtils {
	private UserService userService;
	private LdapService ldapService;

	private SocialUserWrapper socialUser;

	@Autowired
	public UserUtils(UserService userService, LdapService ldapService) {
		this.userService = userService;
		this.ldapService = ldapService;
	}

	public SocialUserWrapper initSocialUser(String username) {
		LdapUser ldapUser = this.ldapService.findUserWithGroups(username);
		if (ldapUser == null) {
			return null;
		}
		User user = this.userService.findUserByUsername(username);
		if (user == null) {
			user = new User();
			user.setUsername(ldapUser.getUsername());
			user.setFullName(ldapUser.getFullName());
			user = this.userService.registerDefaultUser(user);
		}
		SocialUserWrapper socialUser = new SocialUserWrapper(user, ldapUser);
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
