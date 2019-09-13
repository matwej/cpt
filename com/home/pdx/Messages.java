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
					box.setMessage("Chyba #"+msgNo+": Chyba hesla ku kryptovaniu! Zlé alebo prázdne heslo!");
				break;
				case 3:
					box.setMessage("Chyba #"+msgNo+": Pacient so zadanım rodnım èíslom u existuje! Zmeòte údaje!");
				break;
				case 4:
					box.setMessage("Chyba #"+msgNo+": Niektoré vstupné pole je prázdne! Skontrolujte zadané údaje!");
				break;
				case 5:
					box.setMessage("Chyba #"+msgNo+": Nie je moné vytvori osobnı súbor, chyba zapisovania!");
				break;
				case 6:
					box.setMessage("Chyba #"+msgNo+": Chyba parsovania XML databázy: "+trace);
				break;
				case 7:
					box.setMessage("Chyba #"+msgNo+": Nie je naèítanı iadny pacient!");
				break;
				case 8:
					box.setMessage("Chyba #"+msgNo+": Nie je moné skopírova obrázok do databázy!");
				break;
				case 9:
					box.setMessage("Chyba #"+msgNo+": Nie je moné naèíta obrázok, nemáte vybraté vyšetrenie!");
				break;
				case 10:
					box.setMessage("Chyba #"+msgNo+": Nepodarilo sa uloi obrázok, máte málo bodov urèenıch!");
				break;
				case 11:
					box.setMessage("Chyba #"+msgNo+": Nepodarilo sa naèíta konfiguraènı súbor!");
				break;
				case 12:
					box.setMessage("Chyba #"+msgNo+": Nastala chyba pri sifrovani! Trace: " + trace);
				break;
				case 13:
					box.setMessage("Chyba #"+msgNo+": Nezvolili ste typ!");
				break;
				case 14:
					box.setMessage("Chyba #"+msgNo+": Zlı formát emailovej adresy!");
				break;
				case 15:
					box.setMessage("Chyba #"+msgNo+": Zlı formát rodného èísla!");
				break;
				case 16:
					box.setMessage("Chyba #"+msgNo+": Staré heslo nesprávne, alebo sa nezhodovalo zadané nové!");
				break;
				case 17:
					box.setMessage("Chyba #"+msgNo+": Chyba zmeny hesla! Trace: " + trace);
				break;
			}
		break;
		case INFO:
			box = new MessageBox(parent,SWT.ICON_INFORMATION | SWT.OK);
			box.setText("Informácia");
			switch(msgNo) {
				case 1:
					box.setMessage("Pacient úspešne vytvorenı");
				break;
				case 2:
					box.setMessage("Údaje úspešne zmenené a zapísané");
				break;
				case 3:
					box.setMessage("Pacient úspešne vymazanı");
				break;
				case 4:
					box.setMessage("Pacient nebol vymazanı");
				break;
				case 5:
					box.setMessage("Chyba #"+msgNo+": Obrazok neuloenı!");
				break;
			}
		break;
		}
		box.open();
	}
	
}
