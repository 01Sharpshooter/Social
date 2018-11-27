package hu.mik.beans;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import lombok.Data;

@Data
@Entry(objectClasses = { "groupOfNames" })
public class LdapGroup {
	@Id
	private Name id;

	@Attribute(name = "cn")
	private String groupName;
	@Attribute(name = "member")
	private List<Name> listOfMembers;
	@Attribute(name = "member")
	private Name member;
	@Transient
	private List<LdapUser> memberUsers;

}
