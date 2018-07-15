package hu.mik.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import hu.mik.beans.LdapUser;

@Repository
public interface LdapRepositoryUser extends org.springframework.data.ldap.repository.LdapRepository<LdapUser> {
	LdapUser findByUsername(String username);

	List<LdapUser> findByFullNameContaining(String fullName);

	@Override
	List<LdapUser> findAll();
//	LdapUser findById(Name name);
}
