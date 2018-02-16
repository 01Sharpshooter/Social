package hu.mik.dao;

import java.util.List;

import javax.naming.Name;

import org.springframework.stereotype.Repository;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;

@Repository
public interface LdapRepositoryGroups extends org.springframework.data.ldap.repository.LdapRepository<LdapGroup>{
	LdapGroup findByGroupName(String groupName);
}
