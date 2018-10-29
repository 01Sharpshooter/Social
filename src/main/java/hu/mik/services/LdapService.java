package hu.mik.services;

import java.util.List;

import javax.naming.Name;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;

public interface LdapService {
	public LdapUser findUserByUsername(String username);

	public LdapUser findUserWithGroups(String username);

	public LdapUser findUserByDN(Name name);

	public List<LdapUser> findByFullNameContaining(String fullName);

	public LdapGroup findGroupByGroupName(String groupName);

	public List<LdapGroup> findGroupsByUserId(Name memberId);

	public List<LdapUser> findAllUsers();

	public List<String> findMemberUsernamesOfGroup(LdapGroup ldapGroup);

//	public LdapUser findUserById(Name name);
}
