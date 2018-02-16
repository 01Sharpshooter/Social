package hu.mik.beans;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import hu.mik.constants.LdapConstants;

@Entry(base=LdapConstants.OU_GROUPS, objectClasses={"top", "groupOfNames"})
public class LdapGroup{
	@Id
	private Name id;
	
	@Attribute(name="cn")
	private String groupName;	
	@Attribute(name="member")
	private List<Name> listOfMembers;
	
	

	public Name getId() {
		return id;
	}

	public void setId(Name id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Name> getListOfMembers() {
		return listOfMembers;
	}

	public void setListOfMembers(List<Name> listOfMembers) {
		this.listOfMembers = listOfMembers;
	}



	
	
}