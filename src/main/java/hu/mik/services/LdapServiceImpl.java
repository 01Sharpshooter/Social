package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.UserLdap;
import hu.mik.dao.LdapRepositoryUser;

@Service
public class LdapServiceImpl implements LdapService{
	@Autowired
	private LdapRepositoryUser ldapRepository;


	@Override
	public UserLdap findUserByUsername(String username) {
		return ldapRepository.findByUsername(username);
	}


	@Override
	public List<UserLdap> findByFullNameContaining(String fullName) {
		return ldapRepository.findByFullNameContaining(fullName);
	}

}
