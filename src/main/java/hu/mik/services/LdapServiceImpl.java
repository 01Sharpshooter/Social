package hu.mik.services;

import java.util.List;

import javax.naming.Name;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;
import hu.mik.dao.LdapRepositoryGroups;
import hu.mik.dao.LdapRepositoryUser;

@Service
public class LdapServiceImpl implements LdapService {
	@Autowired
	private LdapRepositoryUser ldapRepositoryUsers;
	@Autowired
	private LdapRepositoryGroups ldapRepositoryGroups;

	@Override
	public LdapUser findUserByUsername(String username) {
		return this.ldapRepositoryUsers.findByUsername(username);
	}

	@Override
	public List<LdapUser> findByFullNameContaining(String fullName) {
		return this.ldapRepositoryUsers.findByFullNameContaining(fullName);
	}

	@Override
	public LdapGroup findGroupByGroupName(String groupName) {
		return this.ldapRepositoryGroups.findByGroupName(groupName);
	}

	@Override
	public List<LdapGroup> findGroupsByUserId(Name memberId) {
		return this.ldapRepositoryGroups.findByMember(memberId);
	}

	@Override
	public List<LdapUser> findAllUsers() {
		return this.ldapRepositoryUsers.findByFullNameContaining(" "); // findAll is bugged, it doesn't check entry base
	}

	@Override
	public LdapUser findUserWithGroups(String username) {
		LdapUser user = this.findUserByUsername(username);
		user.setLdapGroups(this.findGroupsByUserId(user.getId()));
		return user;
	}

//	@Override
//	public LdapUser findUserById(Name name) {
//		return ldapRepositoryUsers.findById(name);
//	}

}
