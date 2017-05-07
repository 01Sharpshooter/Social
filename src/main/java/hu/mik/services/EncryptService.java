package hu.mik.services;

public interface EncryptService {
	public String encryptPw(String password);
	
	public boolean comparePW(String password, String hashed);
}
