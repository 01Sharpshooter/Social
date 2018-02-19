package hu.mik.dao;

import java.util.List;

import javax.naming.Name;

import org.springframework.stereotype.Repository;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;

@Repository
public interface LdapRepositoryUser extends org.springframework.data.ldap.repository.LdapRepository<LdapUser>{
	LdapUser findByUsername(String username);
	List<LdapUser> findByFullNameContaining(String fullName);
//	LdapUser findById(Name name);
}
