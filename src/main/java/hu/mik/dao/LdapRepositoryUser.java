package hu.mik.dao;

import java.util.List;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import hu.mik.beans.LdapUser;

@Repository
public interface LdapRepositoryUser extends LdapRepository<LdapUser> {
	public LdapUser findByUsername(String username);

	public List<LdapUser> findByFullNameContaining(String fullName);

	@Override
	public List<LdapUser> findAll();
}
