package com.home.pdx;

import java.util.ArrayList;

public class Pacient {
	private String menoAPriez;
	private String datum;
	private String rc;
	private String adresa;
	private String tel;
	private String email;
	private String dop;
	private String poslZmena;
	private ArrayList<String> vysetrenia;
	
	public ArrayList<String> getVysetrenia() {
		return vysetrenia;
	}
	public void setVysetrenia(ArrayList<String> vysetrenia) {
		this.vysetrenia = vysetrenia;
	}
	public String getMenoAPriez() {
		return menoAPriez;
	}
	public void setMenoAPriez(String menoAPriez) {
		this.menoAPriez = menoAPriez;
	}
	public String getDatum() {
		return datum;
	}
	public void setDatum(String datum) {
		this.datum = datum;
	}
	public String getRc() {
		return rc;
	}
	public void setRc(String rc) {
		this.rc = rc;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDop() {
		return dop;
	}
	public void setDop(String dop) {
		this.dop = dop;
	}
	public String getPoslZmena() {
		return poslZmena;
	}
	public void setPoslZmena(String poslZmena) {
		this.poslZmena = poslZmena;
	}
	
	// custom
	public boolean allSet() {
		if(adresa.equals("")) return false;
		if(menoAPriez.equals("")) return false;
		if(datum.equals("")) return false;
		if(rc.equals("")) return false;
		if(tel.equals("")) return false;
		if(email.equals("")) return false;
		if(poslZmena.equals("")) return false;
		return true;	
	}
}
