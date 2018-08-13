package hu.mik.beans;

public class SocialUserWrapper {
	private User dbUser;
	private LdapUser ldapUser;

	public SocialUserWrapper(User dbUser, LdapUser ldapUser) {
		this.dbUser = dbUser;
		this.ldapUser = ldapUser;
	}

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
