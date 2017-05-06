package hu.mik.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class BcryptImpl implements EncryptService{

	@Override
	public String encryptPw(String password) {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		return encoder.encode(password);
	}

}
