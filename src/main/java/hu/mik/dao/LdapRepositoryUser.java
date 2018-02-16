package hu.mik.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import hu.mik.beans.UserLdap;

@Repository
public interface LdapRepositoryUser extends org.springframework.data.ldap.repository.LdapRepository<UserLdap>{
	UserLdap findByUsername(String username);
	List<UserLdap> findByFullNameContaining(String fullName);
}
