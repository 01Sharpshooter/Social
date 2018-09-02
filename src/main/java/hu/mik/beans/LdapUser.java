package hu.mik.beans;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import hu.mik.constants.LdapConstants;

@Entry(base = LdapConstants.OU_USERS, objectClasses = { "top", "inetOrgPerson", "person" })
public class LdapUser {
	@Id
	private Name id;

	@Attribute(name = "uid")
	private String username;
	@Attribute(name = "cn")
	private String fullName;
	@Attribute(name = "userPassword")
	private String userPassword;
	@Attribute(name = "mobile")
	private String mobile;
	@Attribute(name = "mail")
	private String mail;
	@Transient
	private List<LdapGroup> ldapGroups;

	public Name getId() {
		return this.id;
	}

	public void setId(Name id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<LdapGroup> getLdapGroups() {
		return this.ldapGroups;
	}

	public void setLdapGroups(List<LdapGroup> ldapGroups) {
		this.ldapGroups = ldapGroups;
	}

}
