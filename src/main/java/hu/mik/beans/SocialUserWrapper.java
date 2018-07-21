package hu.mik.beans;

import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
public class SocialUserWrapper {
	private User dbUser;
	private LdapUser ldapUser;

	public User getDbUser() {
		return this.dbUser;
	}

	public void setDbUser(User dbUser) {
		this.dbUser = dbUser;
	}

	public LdapUser getLdapUser() {
		return this.ldapUser;
	}

	public void setLdapUser(LdapUser ldapUser) {
		this.ldapUser = ldapUser;
	}

	public String getUsername() {
		return this.ldapUser.getUsername();
	}

}
