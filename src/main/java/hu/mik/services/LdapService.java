package hu.mik.services;

import java.util.List;

import org.springframework.stereotype.Service;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;

public interface LdapService {
	public LdapUser findUserByUsername(String username);
	
	public List<LdapUser> findByFullNameContaining(String fullName);
	
	public LdapGroup findGroupByGroupName(String groupName);
}
