package com.home.pdx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.SecureRandom;

public class Saltmakeronce {

	private void writeIt(File f, String d) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(f);
		out.println(d);
		out.close();
	}
	
	public void doIt() {
		SecureRandom rnd = new SecureRandom();
		byte[] salt = new byte[8];
		rnd.nextBytes(salt);
		File f = new File("salt.cfg");
			try {
				writeIt(f, new String(salt));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
	}
}
