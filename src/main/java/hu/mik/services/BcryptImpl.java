package hu.mik.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BcryptImpl implements EncryptService {

	@Override
	public String encryptPw(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}

	@Override
	public boolean comparePW(String password, String hashed) {

		return BCrypt.checkpw(password, hashed);
	}

}
