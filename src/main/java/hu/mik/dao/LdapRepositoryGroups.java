package hu.mik.dao;

import java.util.List;

import javax.naming.Name;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import hu.mik.beans.LdapGroup;

@Repository
public interface LdapRepositoryGroups extends LdapRepository<LdapGroup> {
	public LdapGroup findByGroupName(String groupName);

	public List<LdapGroup> findByMember(Name memberId);
}
