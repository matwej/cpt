package com.home.pdx;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SCKey {
	
	private SecretKey sk;
	
	private SecretKey generateKey(char[] pass, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(pass, salt, 4096, 128);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		return secret;
	}    
	
	public SCKey(String pass, byte[] salt) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		sk = generateKey(pass.toCharArray(), salt);
	}

	public SecretKey getSk() {
		return sk;
	}

	public void setSk(SecretKey sk) {
		this.sk = sk;
	}
}
