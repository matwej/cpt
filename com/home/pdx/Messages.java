package com.home.pdx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

enum MsgTypes {
	ERROR, INFO
}

public class Messages {
	
	public void msg(Shell parent,MsgTypes type, int msgNo, String trace) {
		MessageBox box = null;
		switch(type) {
		case ERROR:
			box = new MessageBox(parent,SWT.ICON_ERROR | SWT.OK); 
			box.setText("Vyskytla sa chyba!!!");
			switch(msgNo) {
				case 1:
					box.setMessage("Chyba #"+msgNo+": Chyba kryptovania! Trace: "+trace);
				break;
				case 2:
					box.setMessage("Chyba #"+msgNo+": Chyba hesla ku kryptovaniu! Zl� alebo pr�zdne heslo!");
				break;
				case 3:
					box.setMessage("Chyba #"+msgNo+": Pacient so zadan�m rodn�m ��slom u� existuje! Zme�te �daje!");
				break;
				case 4:
					box.setMessage("Chyba #"+msgNo+": Niektor� vstupn� pole je pr�zdne! Skontrolujte zadan� �daje!");
				break;
				case 5:
					box.setMessage("Chyba #"+msgNo+": Nie je mo�n� vytvori� osobn� s�bor, chyba zapisovania!");
				break;
				case 6:
					box.setMessage("Chyba #"+msgNo+": Chyba parsovania XML datab�zy: "+trace);
				break;
				case 7:
					box.setMessage("Chyba #"+msgNo+": Nie je na��tan� �iadny pacient!");
				break;
				case 8:
					box.setMessage("Chyba #"+msgNo+": Nie je mo�n� skop�rova� obr�zok do datab�zy!");
				break;
				case 9:
					box.setMessage("Chyba #"+msgNo+": Nie je mo�n� na��ta� obr�zok, nem�te vybrat� vy�etrenie!");
				break;
				case 10:
					box.setMessage("Chyba #"+msgNo+": Nepodarilo sa ulo�i� obr�zok, m�te m�lo bodov ur�en�ch!");
				break;
				case 11:
					box.setMessage("Chyba #"+msgNo+": Nepodarilo sa na��ta� konfigura�n� s�bor!");
				break;
				case 12:
					box.setMessage("Chyba #"+msgNo+": Nastala chyba pri sifrovani! Trace: " + trace);
				break;
				case 13:
					box.setMessage("Chyba #"+msgNo+": Nezvolili ste typ!");
				break;
				case 14:
					box.setMessage("Chyba #"+msgNo+": Zl� form�t emailovej adresy!");
				break;
				case 15:
					box.setMessage("Chyba #"+msgNo+": Zl� form�t rodn�ho ��sla!");
				break;
				case 16:
					box.setMessage("Chyba #"+msgNo+": Star� heslo nespr�vne, alebo sa nezhodovalo zadan� nov�!");
				break;
				case 17:
					box.setMessage("Chyba #"+msgNo+": Chyba zmeny hesla! Trace: " + trace);
				break;
			}
		break;
		case INFO:
			box = new MessageBox(parent,SWT.ICON_INFORMATION | SWT.OK);
			box.setText("Inform�cia");
			switch(msgNo) {
				case 1:
					box.setMessage("Pacient �spe�ne vytvoren�");
				break;
				case 2:
					box.setMessage("�daje �spe�ne zmenen� a zap�san�");
				break;
				case 3:
					box.setMessage("Pacient �spe�ne vymazan�");
				break;
				case 4:
					box.setMessage("Pacient nebol vymazan�");
				break;
				case 5:
					box.setMessage("Chyba #"+msgNo+": Obrazok neulo�en�!");
				break;
			}
		break;
		}
		box.open();
	}
	
}
