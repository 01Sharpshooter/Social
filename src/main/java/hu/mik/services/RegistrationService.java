package hu.mik.services;

import hu.mik.beans.User;

public interface RegistrationService {
	public void registerUser(String username, String passwd);
	
	public boolean takenUserName(String username);
	
}
