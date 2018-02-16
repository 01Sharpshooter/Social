package hu.mik.services;

import java.util.List;

import org.springframework.stereotype.Service;

import hu.mik.beans.UserLdap;

public interface LdapService {
	public UserLdap findUserByUsername(String username);
	
	public List<UserLdap> findByFullNameContaining(String fullName);
}
