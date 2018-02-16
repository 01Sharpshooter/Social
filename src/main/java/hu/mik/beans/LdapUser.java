package hu.mik.beans;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import hu.mik.constants.LdapConstants;

@Entry(base=LdapConstants.OU_USERS, objectClasses= {"top", "inetOrgPerson", "person"})
public class LdapUser {
	@Id
	private Name id;
	
	@Attribute(name="uid")
	private String username;
	@Attribute(name="cn")
	private String fullName;
	@Attribute(name="userPassword")
	private String userPassword;
	@Attribute(name="mobile")
	private String mobile;
	@Attribute(name="mail")
	private String mail;
	
	public Name getId() {
		return id;
	}
	public void setId(Name id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	
}
