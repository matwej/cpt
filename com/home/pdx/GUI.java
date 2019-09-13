package com.home.pdx;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.naming.SizeLimitExceededException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GUI {

	private static int dotSize;
	
	// vseobecne premenne
	protected Shell shell;
	private TabFolder tabContainer;
	private DocumentBuilderFactory docFactory;
	protected boolean doneLoad;
	
	// premenne tykajuce sa pacienta
	private TabItem tabPacient;
	private Composite contPac;
	private Text txtPacMenAPrv;
	private Text txtPacDatum;
	private Text txtPacRC;
	private Text txtPacByd;
	private Text txtPacTel;
	private Text txtPacEmail;
	private StyledText txtPacDop;
	private Label lPacDatpzm;
	private Button btnPacUloz;
	private Button btnPacEdit;
	private Pacient pacient;
	private boolean novy;
	private String xmlPath;
	
	// premenne tykajuce sa vysetrenia
	private TabItem tabVys;
	private Composite contVys;
	private StyledText txtPoznamka;
	private Label casVys;
	private Text txtNazovVys;
	private Text txtVykonal;
	private Text uhol;
	private Combo comboVys;
	private Button btnVysUloz;
	private Button btnVysEdit;
	private boolean noveVys;
	private String otvVys;
	private String vybrateVys;
	
	// premenne na obrazok
	private String urlObr;
	private Canvas canvas;
	private Image image;
	private boolean zlava;
	private ArrayList<Rectangle> recs;
	
	// krypto kluc
	private SecretKey secretkey;
	
	// pomocne funkcie
	private static void copyFileUsingChannel(File source, File dest) throws IOException {
	    FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
	    try {
	        sourceChannel = new FileInputStream(source).getChannel();
	        destChannel = new FileOutputStream(dest).getChannel();
	        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	       }finally{
	           sourceChannel.close();
	           destChannel.close();
	       }
	}
	
	public static boolean deleteDir(File dir) { 
	 if (dir.isDirectory()) { 
	  String[] children = dir.list(); 
	  for (int i=0; i<children.length; i++) { 
	    boolean success = deleteDir(new File(dir, children[i])); 
	    if (!success) return false;
	  }
	 }
	  return dir.delete();
	} 
	
	// vyber a mazanie pacient
	private void vyber(Display d) {
		final Shell okno = new Shell(d);
		okno.setText("Vyber");
		okno.setLayout(new GridLayout());
		okno.setBackground(SWTResourceManager.getColor(255,255,255));
		okno.setLocation(d.getBounds().x+100,d.getBounds().y+100);
		
		final List list = new List(okno,SWT.PUSH);
		Document doc = null;
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			for (File f : new File("data").listFiles()) {
				doc = docBuilder.parse(f.getPath()+"/pac_db.xml");
				
				list.add(new String(decrypt(new PBEStorage(doc.getElementsByTagName("menoapr").item(0).getTextContent()), secretkey)) + " | " + f.getPath());
			}
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			new Messages().msg(okno,MsgTypes.ERROR,6,e1.getMessage());
		} catch(InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | DOMException excp) {
			new Messages().msg(okno,MsgTypes.ERROR,12,excp.getMessage());
			excp.printStackTrace();
		}
		Button vyk = new Button(okno,SWT.PUSH);
		vyk.setText("Vyber");
		vyk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(list.getSelection().length != 0) {
	                String[] items = list.getSelection();
	                xmlPath = items[0].substring(items[0].indexOf("|"));
	                xmlPath = xmlPath.substring(2);
	                okno.dispose();
				}
			}
		});
		Button zrus = new Button(okno,SWT.PUSH);
		zrus.setText("Zruš");
		zrus.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
	                xmlPath = null;
					okno.dispose();
			}
		});
		okno.pack();
		okno.open();
		while (!okno.isDisposed()) {
	     if (!d.readAndDispatch()) {
	      d.sleep();
	     }
	    }
	}
	
	// vyber a mazanie vysetrenie
	private void vyberVys(Display d) {
	if(pacient!=null) {
		final Shell okno = new Shell(d);
		okno.setText("Vyber");
		okno.setLayout(new GridLayout());
		okno.setBackground(SWTResourceManager.getColor(255,255,255));
		okno.setLocation(d.getBounds().x+100,d.getBounds().y+100);
		
		final List list = new List(okno,SWT.PUSH);
		Document doc = null;
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
			final String url = doc.getElementsByTagName("obr").item(0).getTextContent();
			NodeList nodelist = doc.getElementsByTagName("vys");
			for (int i=0;i<nodelist.getLength();i++) {
				Element node = (Element) nodelist.item(i);
				list.add(node.getAttribute("id"));
			}
			Button vyk = new Button(okno,SWT.PUSH);
			vyk.setText("Vyber");
			vyk.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(list.getSelection().length != 0) {
						String[] items = list.getSelection();
						vybrateVys = items[0];
						urlObr = url;
						okno.dispose();
					}
				}
			});
			Button zrus = new Button(okno,SWT.PUSH);
			zrus.setText("Zruš");
			zrus.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
	                vybrateVys = null;
					okno.dispose();
				}
			});
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			new Messages().msg(okno,MsgTypes.ERROR,6,e1.getMessage());
		}
		okno.pack();
		okno.open();
		while (!okno.isDisposed()) {
	     if (!d.readAndDispatch()) {
	      d.sleep();
	     }
	    }
	}
	}
	
	// pom fcia na zmenu hesla
	/*
	private void zmenaHesla(Display d) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		final Shell okno = new Shell(d);
		okno.setText("Zmena");
		okno.setLayout(new GridLayout());
		okno.setBackground(SWTResourceManager.getColor(255,255,255));
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		okno.setLocation(gd.getDisplayMode().getWidth()/2-100,gd.getDisplayMode().getHeight()/2-50);
		
		Label lStare = new Label(okno,SWT.PUSH);
		lStare.setText("Staré heslo");
		final Text stareHeslo = new Text(okno, SWT.PASSWORD | SWT.BORDER);
		stareHeslo.setEditable(true);
		Label lNove = new Label(okno,SWT.PUSH);
		lNove.setText("Nové heslo");
		final Text noveHeslo = new Text(okno, SWT.PASSWORD | SWT.BORDER);
		noveHeslo.setEditable(true);
		Label lNoveOpak = new Label(okno,SWT.PUSH);
		lNoveOpak.setText("Nové heslo znovu");
		final Text noveHesloOpak = new Text(okno, SWT.PASSWORD | SWT.BORDER);
		noveHesloOpak.setEditable(true);
		
		Button zmen = new Button(okno, SWT.PUSH);
		zmen.setText("Zmeò");
		zmen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		
		okno.pack();
		okno.open();
		while (!okno.isDisposed()) {
	     if (!d.readAndDispatch()) {
	      d.sleep();
	     }
	    }
		
	}
	*/
	// fcia pomocna na ratanie uhla
	protected static double getAngle(int x1,int y1,int x2,int y2,int x3,int y3) {
		int l1x = x2 - x1;
		int l1y = y2 - y1;
		int l2x = x3 - x1;
		int l2y = y3 - y1;
		double ang1 = Math.atan2(l1y, l1x);
		double ang2 = Math.atan2(l2y, l2x);
		return Math.abs(Math.toDegrees(ang2-ang1));
    }
	
	// crypto funkcie
	private PBEStorage encrypt(byte[] cleartext, SecretKey sk) throws 
	NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
	InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		return new PBEStorage(cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV(),cipher.doFinal(cleartext));
	}
	private byte[] decrypt(PBEStorage storage, SecretKey sk) throws 
	BadPaddingException, IllegalBlockSizeException,
    InvalidAlgorithmParameterException, InvalidKeyException, 
    NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(storage.getIv()));
		return cipher.doFinal(storage.getCiphertext());
	}
	
	// hlavna open funkcia
	public void open() {
		
		final Display display = Display.getDefault();
		final Shell loginShell = new Shell(display);
		loginShell.setText("Login");
		loginShell.setLayout(new GridLayout());
		loginShell.setBackground(SWTResourceManager.getColor(192, 192, 192));
		// location to center
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		loginShell.setLocation(width/2-100,height/2-50);
		Label loginText = new Label(loginShell, SWT.PUSH);
		loginText.setText("Zadajte kryptovacie heslo: ");
		final Text text = new Text(loginShell, SWT.PASSWORD | SWT.BORDER);
		Button button = new Button(loginShell, SWT.PUSH);
		button.setText("Prihlási");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!(text.getText().equals(new String("qwe456asdB")))) {
					new Messages().msg(loginShell, MsgTypes.ERROR, 2, "");
				} else {
					String pass = text.getText();
					loginShell.dispose();
					openMainShell(display,pass);
				}
			}
		});
		// main event loop
		loginShell.pack();
		loginShell.open();
		while (!loginShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	/**
	 * @wbp.parser.entryPoint
	 */	
	private void openMainShell(Display dis, final String pass) {
		Shell mainSh = new Shell(dis);
		
		final Shell loadScreen = new Shell(dis,SWT.NULL);
		Label loadingImg = new Label(loadScreen, SWT.NONE);
		Image img = new Image(dis,getClass().getResourceAsStream("/img/loading.gif"));
		loadingImg.setImage(img);
		loadingImg.setSize(400, 80);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		loadScreen.setLocation(width/2-200,height/2-40);
		loadScreen.pack();
		loadScreen.open();
		createContents(dis, mainSh, pass);
		loadScreen.dispose();
		
		mainSh.pack();
		mainSh.setMaximized(true);
		mainSh.open();
		while (!mainSh.isDisposed()) {
			if (!dis.readAndDispatch()) {
				dis.sleep();
			}
		}
		dis.dispose();
	}

	protected void createContents(final Display dis, final Shell shell, String cryptoPass) {
		docFactory = DocumentBuilderFactory.newInstance();
		urlObr = null;
		final Properties prop = new Properties();
		InputStream inprop = getClass().getResourceAsStream("/com/home/pdx/gen.properties"); 
		try {
			prop.load(inprop);
			inprop.close();
		} catch (IOException e2) {
			new Messages().msg(shell, MsgTypes.ERROR, 11, "");
			System.exit(0);
		}
		dotSize = Integer.parseInt(prop.getProperty("dot.size"));
		Image favicon = new Image(dis,getClass().getResourceAsStream("/img/favicon.gif"));
		shell.setImage(favicon);
		shell.setBackground(SWTResourceManager.getColor(192, 192, 192));
		shell.setText(prop.getProperty("program.name"));
	    shell.setLayout(new GridLayout(1, false));
		
		// nacitanie kluca na krypto
		try {
		  secretkey = new SCKey(cryptoPass, prop.getProperty("salt").getBytes()).getSk();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException	| IOException e) {
			new Messages().msg(shell, MsgTypes.ERROR, 1,e.getMessage());
			shell.dispose();
			System.exit(0);
		}		
		
		// vytvorenie data adresara pokial neexistuje pre xml a pacientov
		File dataDir = new File("data");
		if(!dataDir.exists()) dataDir.mkdir();
		
		// main menu
		Menu mainMenu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mainMenu);
		
		// menu Pacienti, cascading
		MenuItem miPacienti = new MenuItem(mainMenu, SWT.CASCADE);
		miPacienti.setText("Pacienti");
		
		// Pacienti items
		Menu menuPac = new Menu(miPacienti);
		miPacienti.setMenu(menuPac);
		
		MenuItem miPacPridaj = new MenuItem(menuPac, SWT.NONE);
		miPacPridaj.setImage(SWTResourceManager.getImage(GUI.class, "/javax/swing/plaf/metal/icons/ocean/file.gif"));
		miPacPridaj.setText("Pridaj");
		miPacPridaj.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pacient = new Pacient();
				novy = true;
				txtPacMenAPrv.setText("");txtPacMenAPrv.setEditable(true);
				txtPacDatum.setText("");txtPacDatum.setEditable(true);
				txtPacRC.setText("");txtPacRC.setEditable(true);
				txtPacByd.setText("");txtPacByd.setEditable(true);
				txtPacEmail.setText("");txtPacEmail.setEditable(true);
				txtPacTel.setText("");txtPacTel.setEditable(true);
				txtPacDop.setText("");txtPacDop.setEditable(true);
				btnPacUloz.setEnabled(true);
				lPacDatpzm.setText("");
				tabContainer.setSelection(tabPacient);
			}
		});
		
		MenuItem miPacVyber = new MenuItem(menuPac, SWT.NONE);
		miPacVyber.setImage(SWTResourceManager.getImage(GUI.class, "/javax/swing/plaf/metal/icons/ocean/hardDrive.gif"));
		miPacVyber.setText("V\u00FDber");
		miPacVyber.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				vyber(dis);
				if(xmlPath != null) {
					pacient = new Pacient();
					Document doc = null;
	        		DocumentBuilder docBuilder;
					try {
						docBuilder = docFactory.newDocumentBuilder();
						doc = docBuilder.parse(xmlPath+"/pac_db.xml");
					} catch (ParserConfigurationException | SAXException | IOException e1) {
						new Messages().msg(shell,MsgTypes.ERROR,6,e1.getMessage());
					}
					NodeList list = doc.getDocumentElement().getChildNodes();
					try {
					for(int i=0;i<list.getLength();i++) {
						Node item = list.item(i);
						if("menoapr".equals(item.getNodeName())) txtPacMenAPrv.setText(new String(decrypt(new PBEStorage(item.getTextContent()), secretkey)));
						if("adresa".equals(item.getNodeName())) txtPacByd.setText(new String(decrypt(new PBEStorage(item.getTextContent()), secretkey)));
						if("rc".equals(item.getNodeName())) {
							String rcdec = new String(decrypt(new PBEStorage(item.getTextContent()), secretkey));
							txtPacRC.setText(rcdec);
							pacient.setRc(rcdec);
						}
						if("datumnar".equals(item.getNodeName())) txtPacDatum.setText(new String(decrypt(new PBEStorage(item.getTextContent()), secretkey)));
						if("email".equals(item.getNodeName())) txtPacEmail.setText(new String(decrypt(new PBEStorage(item.getTextContent()), secretkey)));
						if("tel".equals(item.getNodeName())) txtPacTel.setText(new String(decrypt(new PBEStorage(item.getTextContent()), secretkey)));
						if("dop".equals(item.getNodeName())) txtPacDop.setText(item.getTextContent());
						if("zmena".equals(item.getNodeName())) lPacDatpzm.setText(item.getTextContent());
					}
					} catch(InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | DOMException excp) {
						new Messages().msg(shell,MsgTypes.ERROR,12,excp.getMessage());
					}
					tabContainer.setSelection(tabPacient);
					btnPacUloz.setEnabled(false);
					btnPacEdit.setEnabled(true);
					txtPacMenAPrv.setEditable(false);
					txtPacDatum.setEditable(false);
					txtPacRC.setEditable(false);
					txtPacByd.setEditable(false);
					txtPacEmail.setEditable(false);
					txtPacTel.setEditable(false);
					txtPacDop.setEditable(false);
				}
			}
		});
		
		MenuItem miPacVymaz = new MenuItem(menuPac, SWT.NONE);
		miPacVymaz.setImage(SWTResourceManager.getImage(GUI.class, "/javax/swing/plaf/metal/icons/ocean/close.gif"));
		miPacVymaz.setText("Vyma\u017E");
		miPacVymaz.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				vyber(dis);
				if(xmlPath != null) {
					boolean del = deleteDir(new File(xmlPath));
					if(del) new Messages().msg(shell,MsgTypes.INFO,3,"");
					else new Messages().msg(shell,MsgTypes.INFO,4,"");
					String aktual = "data\\" + txtPacRC.getText().replace("/","-");
					if(xmlPath.equals(aktual)) {
						txtPacMenAPrv.setText("");
						txtPacDatum.setText("");
						txtPacRC.setText("");
						txtPacByd.setText("");
						txtPacEmail.setText("");
						txtPacTel.setText("");
						txtPacDop.setText("");
						xmlPath=null;
						pacient=null;
					}
				}
			}
		});
		
		// cascade menu Vysetrenia in main menu
		MenuItem miVysetrenia = new MenuItem(mainMenu, SWT.CASCADE);
		miVysetrenia.setText("Vy\u0161etrenia");
		
		// Vysetrenia items
		Menu menuVys = new Menu(miVysetrenia);
		miVysetrenia.setMenu(menuVys);
		
		// PRIDAJ VYSETRENIE
		MenuItem miVysPridaj = new MenuItem(menuVys, SWT.NONE);
		miVysPridaj.setImage(SWTResourceManager.getImage(GUI.class, "/javax/swing/plaf/metal/icons/ocean/file.gif"));
		miVysPridaj.setText("Pridaj");
		miVysPridaj.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(pacient != null) {
					noveVys = true;
					urlObr = null;
					tabContainer.setSelection(tabVys);
					txtNazovVys.setText("");txtNazovVys.setEditable(true);
					txtVykonal.setText("");txtVykonal.setEditable(true);
					txtPoznamka.setText("");txtPoznamka.setEditable(true);
					comboVys.setEnabled(true);
					casVys.setText("DD-MM-YYYY");
					btnVysEdit.setEnabled(false);
					btnVysUloz.setEnabled(true);
				} else {
					new Messages().msg(shell,MsgTypes.ERROR,7,"");
				}
			}
		});
		
		MenuItem miVysVyber = new MenuItem(menuVys, SWT.NONE);
		miVysVyber.setImage(SWTResourceManager.getImage(GUI.class, "/javax/swing/plaf/metal/icons/ocean/directory.gif"));
		miVysVyber.setText("V\u00FDber");
		miVysVyber.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				vyberVys(dis);
				if(vybrateVys != null) {
					otvVys = vybrateVys;
					recs.removeAll(recs);
					int x1=-1,x2=-1,x3=-1,y1=-1,y2=-1,y3=-1;
	        		Document doc = null;
	        		DocumentBuilder docBuilder;
					try {
						docBuilder = docFactory.newDocumentBuilder();
						doc = docBuilder.parse("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
					} catch (ParserConfigurationException | SAXException | IOException e1) {
						new Messages().msg(shell,MsgTypes.ERROR,6,e1.getMessage());
					}
					NodeList list = doc.getElementsByTagName("vys");
					for(int i=0;i<list.getLength();i++) {
						Element item = (Element) list.item(i);
						if(item.getAttribute("id").equals(vybrateVys)) {
							NodeList nodelist = item.getChildNodes();
							for(int j=0;j<nodelist.getLength();j++) {
								Node node = nodelist.item(j);
								if("nazov".equals(node.getNodeName())) txtNazovVys.setText(node.getTextContent());
								if("poz".equals(node.getNodeName())) txtPoznamka.setText(node.getTextContent());
								if("vyk".equals(node.getNodeName())) txtVykonal.setText(node.getTextContent());
								if("typ".equals(node.getNodeName())) comboVys.select(comboVys.indexOf(node.getTextContent()));
								if("datum".equals(node.getNodeName())) casVys.setText(node.getTextContent());
								if("obr".equals(node.getNodeName())) {
									urlObr = node.getTextContent();
									if("".equals(urlObr) || "del".equals(urlObr)) urlObr=null;
								}
								if("bod1x".equals(node.getNodeName())) if(!("".equals(node.getTextContent())))x1 = Integer.parseInt(node.getTextContent());
								if("bod1y".equals(node.getNodeName())) if(!("".equals(node.getTextContent())))y1 = Integer.parseInt(node.getTextContent());
								if("bod2x".equals(node.getNodeName())) if(!("".equals(node.getTextContent())))x2 = Integer.parseInt(node.getTextContent());
								if("bod2y".equals(node.getNodeName())) if(!("".equals(node.getTextContent())))y2 = Integer.parseInt(node.getTextContent());
								if("bod3x".equals(node.getNodeName())) if(!("".equals(node.getTextContent())))x3 = Integer.parseInt(node.getTextContent());
								if("bod3y".equals(node.getNodeName())) if(!("".equals(node.getTextContent())))y3 = Integer.parseInt(node.getTextContent());
								
							}
						}
					}
					if(x1!=-1&&y1!=-1)recs.add(new Rectangle(x1, y1, dotSize, dotSize));
					if(x2!=-1&&y2!=-1)recs.add(new Rectangle(x2, y2, dotSize, dotSize));
					if(x3!=-1&&y3!=-1)recs.add(new Rectangle(x3, y3, dotSize, dotSize));
					if(urlObr!=null)image = new Image(Display.getCurrent(),urlObr);
					
					tabContainer.setSelection(tabVys);
					btnVysUloz.setEnabled(false);
					btnVysEdit.setEnabled(true);
					txtNazovVys.setEditable(false);
					txtPoznamka.setEditable(false);
					txtVykonal.setEditable(false);
					comboVys.setEnabled(false);
				}
			}
		});
		
		MenuItem miVysVymaz = new MenuItem(menuVys, SWT.NONE);
		miVysVymaz.setImage(SWTResourceManager.getImage(GUI.class, "/javax/swing/plaf/metal/icons/ocean/close.gif"));
		miVysVymaz.setText("Vyma\u017E");
		miVysVymaz.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				vyberVys(dis);
				if(vybrateVys != null) {
					Document doc = null;
	        		DocumentBuilder docBuilder;
					try {
						docBuilder = docFactory.newDocumentBuilder();
						doc = docBuilder.parse("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
						NodeList list = doc.getElementsByTagName("vys");
						for(int i=0;i<list.getLength();i++) {
							Element item = (Element) list.item(i);
							if(item.getAttribute("id").equals(vybrateVys)) doc.getElementsByTagName("vysetrenia").item(0).removeChild(item);
						}
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
						transformer.transform(source, result);
					} catch (ParserConfigurationException | SAXException | IOException | TransformerException e1) {
						new Messages().msg(shell,MsgTypes.ERROR,6,e1.getMessage());
					}
					String aktual = txtNazovVys.getText()+"-"+casVys.getText();
					if(vybrateVys.equals(aktual)) {
						txtNazovVys.setText("");
						txtPoznamka.setText("");
						txtVykonal.setText("");
						casVys.setText("");
						vybrateVys=null;
					}
				}
			}
		});
		
		// info about program in main menu
		MenuItem miOPrograme = new MenuItem(mainMenu, SWT.NONE);
		miOPrograme.setText("O programe");
		miOPrograme.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageBox oProg = new MessageBox(shell,SWT.ICON_INFORMATION | SWT.OK); 
				oProg.setText("O programe "+prop.getProperty("program.name"));
				oProg.setMessage(prop.getProperty("o.programe"));		
				oProg.open();
			}
		});
		
		// zmena hesla
		MenuItem miHesla = new MenuItem(mainMenu,  SWT.CASCADE);
		miHesla.setText("Nastavenie hesla");
		Menu menuHesla = new Menu(miHesla);
		miHesla.setMenu(menuHesla);
		/*
		MenuItem zmenitHeslo = new MenuItem(menuHesla, SWT.NONE);
		zmenitHeslo.setText("Zmeni heslo");
		zmenitHeslo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					zmenaHesla(dis);
				} catch (NoSuchAlgorithmException | IOException e1) {
					new Messages().msg(shell, MsgTypes.ERROR, 17, e1.getMessage());
				}
			}
		});
		*/
		// tlacidlo na ukoncenie
		MenuItem miSkoncit = new MenuItem(mainMenu, SWT.NONE);
		miSkoncit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
				System.exit(0);
			}
		});
		miSkoncit.setText("Skon\u010Di\u0165");
		
		tabContainer = new TabFolder(shell, SWT.NONE);
		tabContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		tabPacient = new TabItem(tabContainer, SWT.NONE);
		tabPacient.setText("Pacient");
		
		contPac = new Composite(tabContainer, SWT.NONE);
		contPac.setBackground(SWTResourceManager.getColor(135, 206, 235));
		tabPacient.setControl(contPac);
		
		lPacDatpzm = new Label(contPac, SWT.NONE);
		lPacDatpzm.setBounds(111, 459, 90, 15);
		lPacDatpzm.setText("DD - MM - YYYY");
		
		Label lPacMenAPrv = new Label(contPac, SWT.NONE);
		lPacMenAPrv.setBackground(SWTResourceManager.getColor(135, 206, 235));
		lPacMenAPrv.setBounds(10, 10, 95, 15);
		lPacMenAPrv.setText("Meno a priezvisko");
		
		Label lPacDatum = new Label(contPac, SWT.NONE);
		lPacDatum.setBackground(SWTResourceManager.getColor(135, 206, 235));
		lPacDatum.setBounds(10, 31, 92, 15);
		lPacDatum.setText("D\u00E1tum narodenia");
		
		Label LPacRC = new Label(contPac, SWT.NONE);
		LPacRC.setBackground(SWTResourceManager.getColor(135, 206, 235));
		LPacRC.setBounds(10, 52, 61, 15);
		LPacRC.setText("Rodn\u00E9 \u010D\u00EDslo");
		
		Label lPacByd = new Label(contPac, SWT.NONE);
		lPacByd.setBackground(SWTResourceManager.getColor(135, 206, 235));
		lPacByd.setBounds(10, 73, 55, 15);
		lPacByd.setText("Bydlisko");
		
		Label lPacTel = new Label(contPac, SWT.NONE);
		lPacTel.setBackground(SWTResourceManager.getColor(135, 206, 235));
		lPacTel.setBounds(10, 94, 55, 15);
		lPacTel.setText("Telef\u00F3n");
		
		Label lPacEmail = new Label(contPac, SWT.NONE);
		lPacEmail.setBackground(SWTResourceManager.getColor(135, 206, 235));
		lPacEmail.setBounds(10, 115, 55, 15);
		lPacEmail.setText("E-mail");
		
		Label lPacDop = new Label(contPac, SWT.NONE);
		lPacDop.setBackground(SWTResourceManager.getColor(135, 206, 235));
		lPacDop.setBounds(10, 136, 68, 15);
		lPacDop.setText("Doporu\u010Denie");
		
		txtPacMenAPrv = new Text(contPac, SWT.BORDER);
		txtPacMenAPrv.setBounds(125, 7, 210, 21);
		txtPacMenAPrv.setEditable(false);
		
		txtPacDatum = new Text(contPac, SWT.BORDER);
		txtPacDatum.setBounds(125, 28, 78, 21);
		txtPacDatum.setEditable(false);
		
		txtPacRC = new Text(contPac, SWT.BORDER);
		txtPacRC.setBounds(125, 49, 78, 21);
		txtPacRC.setTextLimit(11);
		txtPacRC.setEditable(false);
		
		txtPacByd = new Text(contPac, SWT.BORDER);
		txtPacByd.setBounds(125, 70, 420, 21);
		txtPacByd.setEditable(false);
		
		txtPacTel = new Text(contPac, SWT.BORDER);
		txtPacTel.setBounds(125, 91, 95, 21);
		txtPacTel.setEditable(false);
		
		txtPacEmail = new Text(contPac, SWT.BORDER);
		txtPacEmail.setBounds(125, 112, 210, 21);
		txtPacEmail.setEditable(false);
		
		txtPacDop = new StyledText(contPac, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtPacDop.setBounds(10, 157, 950, 296);
		txtPacDop.setEditable(false);
		txtPacDop.setAlwaysShowScrollBars(false);
		
		btnPacEdit = new Button(contPac, SWT.NONE);
		btnPacEdit.setBackground(SWTResourceManager.getColor(128, 0, 0));
		btnPacEdit.setBounds(804, 110, 75, 25);
		btnPacEdit.setText("Zmeni\u0165");
		btnPacEdit.setEnabled(false);
		btnPacEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				novy = false;
				txtPacMenAPrv.setEditable(true);
				txtPacDatum.setEditable(true);
				txtPacRC.setEditable(true);
				txtPacByd.setEditable(true);
				txtPacEmail.setEditable(true);
				txtPacTel.setEditable(true);
				txtPacDop.setEditable(true);
				btnPacEdit.setEnabled(false);
				btnPacUloz.setEnabled(true);
			}
		});
		
		btnPacUloz = new Button(contPac, SWT.NONE);
		btnPacUloz.setBackground(SWTResourceManager.getColor(0, 128, 0));
		btnPacUloz.setBounds(885, 110, 75, 25);
		btnPacUloz.setText("Ulo\u017Ei\u0165");
		btnPacUloz.setEnabled(false);
		btnPacUloz.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean chyba = false;
				pacient.setMenoAPriez(txtPacMenAPrv.getText());
				pacient.setAdresa(txtPacByd.getText());
				if(!novy && !pacient.getRc().equals(txtPacRC.getText())) {
					novy = true;
					deleteDir(new File("data/"+pacient.getRc().replace("/","-")));
				}
				pacient.setRc(txtPacRC.getText());
				pacient.setDatum(txtPacDatum.getText());
				pacient.setEmail(txtPacEmail.getText());
				pacient.setTel(txtPacTel.getText());
				pacient.setDop(txtPacDop.getText());
				String zm = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
				pacient.setPoslZmena(zm);
				File xmlf = new File("data/"+pacient.getRc().replace("/","-"));
				if(pacient.getRc().length()<10 || pacient.getRc().length()>11) { // kontrola rodneho cisla
					new Messages().msg(shell,MsgTypes.ERROR,15,"");
					chyba = true;
				}
				if(!xmlf.exists() && !chyba) { // kontrola ci uz dotycny existuje v DB
					xmlf.mkdir();
				} else if(novy) {
					new Messages().msg(shell,MsgTypes.ERROR,3,"");
					chyba = true;
				}
				if(pacient.allSet()); // kontrola ci su vsetky polia neprazdne a vyplnene, okrem doporucenia, ktore nie je potrebne	
				else {
					new Messages().msg(shell,MsgTypes.ERROR,4,"");
					chyba = true;
				}
				if(!pacient.getEmail().contains("@")) { // kontrola emailu
					new Messages().msg(shell,MsgTypes.ERROR,14,"");
					chyba = true;
				}
				try {
					if(!chyba) {
						Document doc;
		        		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
						if(novy){
							doc = docBuilder.newDocument();
							Element rootEl = doc.createElement("pacient");
							doc.appendChild(rootEl);
							Element menoapr = doc.createElement("menoapr");
							menoapr.appendChild(doc.createTextNode(encrypt(pacient.getMenoAPriez().getBytes(), secretkey).toString()));
							rootEl.appendChild(menoapr);							
							Element adresa = doc.createElement("adresa");
							adresa.appendChild(doc.createTextNode(encrypt(pacient.getAdresa().getBytes(), secretkey).toString()));
							rootEl.appendChild(adresa);							
							Element rc = doc.createElement("rc");
							rc.appendChild(doc.createTextNode(encrypt(pacient.getRc().getBytes(), secretkey).toString()));
							rootEl.appendChild(rc);							
							Element datumnar = doc.createElement("datumnar");
							datumnar.appendChild(doc.createTextNode(encrypt(pacient.getDatum().getBytes(), secretkey).toString()));
							rootEl.appendChild(datumnar);							
							Element email = doc.createElement("email");
							email.appendChild(doc.createTextNode(encrypt(pacient.getEmail().getBytes(), secretkey).toString()));
							rootEl.appendChild(email);							
							Element tel = doc.createElement("tel");
							tel.appendChild(doc.createTextNode(encrypt(pacient.getTel().getBytes(), secretkey).toString()));
							rootEl.appendChild(tel);							
							Element zmena = doc.createElement("zmena");
							zmena.appendChild(doc.createTextNode(zm));
							rootEl.appendChild(zmena);							
							Element dop = doc.createElement("dop");
							dop.appendChild(doc.createTextNode(pacient.getDop()));
							rootEl.appendChild(dop);							
							Element vys = doc.createElement("vysetrenia");
							rootEl.appendChild(vys);
						} else {
							doc = docBuilder.parse(xmlf+"/pac_db.xml");
							NodeList list = doc.getDocumentElement().getChildNodes();
							for(int i=0;i<list.getLength();i++) {
								Node item = list.item(i);
								if("menoapr".equals(item.getNodeName())) item.setTextContent(pacient.getMenoAPriez());
								if("adresa".equals(item.getNodeName())) item.setTextContent(pacient.getAdresa());
								if("rc".equals(item.getNodeName())) item.setTextContent(pacient.getRc());
								if("datumnar".equals(item.getNodeName())) item.setTextContent(pacient.getDatum());
								if("email".equals(item.getNodeName())) item.setTextContent(pacient.getEmail());
								if("tel".equals(item.getNodeName())) item.setTextContent(pacient.getTel());
								if("dop".equals(item.getNodeName())) item.setTextContent(pacient.getDop());
								if("zmena".equals(item.getNodeName())) item.setTextContent(zm);
							}
						}
						// samotna tvorba xml
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(xmlf+"/pac_db.xml");
						transformer.transform(source, result);
					}
				} catch (TransformerException | ParserConfigurationException | SAXException | IOException ex) {
					new Messages().msg(shell,MsgTypes.ERROR,6,ex.getMessage());
					chyba = true;
				} catch (InvalidParameterSpecException | InvalidKeyException|DOMException|NoSuchAlgorithmException|NoSuchPaddingException|IllegalBlockSizeException|BadPaddingException e1) {					
					new Messages().msg(shell,MsgTypes.ERROR, 12, e1.getMessage());
					chyba = true;
					e1.printStackTrace();
				} 
				if(!chyba) {
					btnPacUloz.setEnabled(false);
					btnPacEdit.setEnabled(true);
					txtPacMenAPrv.setEditable(false);
					txtPacDatum.setEditable(false);
					txtPacRC.setEditable(false);
					txtPacByd.setEditable(false);
					txtPacEmail.setEditable(false);
					txtPacTel.setEditable(false);
					txtPacDop.setEditable(false);
					lPacDatpzm.setText(new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()));
					if(novy) new Messages().msg(shell,MsgTypes.INFO,1,"");
					else new Messages().msg(shell,MsgTypes.INFO,2,""); 
				}
			}
		});
		
		Label lPacPoszm = new Label(contPac, SWT.NONE);
		lPacPoszm.setBackground(SWTResourceManager.getColor(135, 206, 235));
		lPacPoszm.setBounds(10, 459, 95, 15);
		lPacPoszm.setText("Posledn\u00E1 zmena - ");
		
		// VYSETRENIE
		tabVys = new TabItem(tabContainer, SWT.NONE);
		tabVys.setText("Vy\u0161etrenie");
		
		// container na elementy mimo canvasu
		contVys = new Composite(tabContainer, SWT.NONE);
		tabVys.setControl(contVys);
		contVys.setBackground(SWTResourceManager.getColor(102, 205, 170));
		
		// nazov vysetrenia
		Label lNazovVys = new Label(contVys, SWT.NONE);
		lNazovVys.setBackground(SWTResourceManager.getColor(102, 205, 170));
		lNazovVys.setBounds(10, 20, 89, 15);
		lNazovVys.setText("N\u00E1zov vy\u0161etrenia");
		txtNazovVys = new Text(contVys, SWT.BORDER);
		txtNazovVys.setEditable(false);
		txtNazovVys.setBounds(10, 41, 188, 21);
		
		// datum vysetrenia
		Label lDatum = new Label(contVys, SWT.NONE);
		lDatum.setBackground(SWTResourceManager.getColor(102, 205, 170));
		lDatum.setBounds(530, 20, 55, 15);
		lDatum.setText("D\u00E1tum");
		casVys = new Label(contVys, SWT.NONE);
		casVys.setBounds(530, 38, 90, 15);
		casVys.setText("DD - MM - YYYY");
		
		// typ vysetrenia
		Label lTyp = new Label(contVys, SWT.NONE);
		lTyp.setBackground(SWTResourceManager.getColor(102, 205, 170));
		lTyp.setBounds(420, 20, 55, 15);
		lTyp.setText("Typ");
		comboVys = new Combo(contVys, SWT.NONE);
		comboVys.setEnabled(false);
		comboVys.setBounds(420, 41, 91, 23);
		comboVys.setItems(new String[] {"Vstupn\u00E9", "Kontrola", "V\u00FDstupn\u00E9"});
		comboVys.setText("Zvo\u013E...");
		
		// kto ho vykonal
		Label lVykonal = new Label(contVys, SWT.NONE);
		lVykonal.setBackground(SWTResourceManager.getColor(102, 205, 170));
		lVykonal.setBounds(210, 20, 55, 15);
		lVykonal.setText("Vykonal");
		txtVykonal = new Text(contVys, SWT.BORDER);
		txtVykonal.setEditable(false);
		txtVykonal.setBounds(210, 41, 188, 21);
		
		//uhol
		Label lUhol = new Label(contVys, SWT.NONE);
		lUhol.setBackground(SWTResourceManager.getColor(102, 205, 170));
		lUhol.setBounds(635, 20, 55, 15);
		lUhol.setText("Uhol");
		uhol = new Text(contVys, SWT.BORDER);
		uhol.setEditable(false);
		uhol.setBounds(635, 41, 60, 21);
		
		// poznamka k vysetreniu
		Label lPoznamka = new Label(contVys, SWT.NONE);
		lPoznamka.setBackground(SWTResourceManager.getColor(102, 205, 170));
		lPoznamka.setBounds(10, 99, 55, 15);
		lPoznamka.setText("Pozn\u00E1mka");
		txtPoznamka = new StyledText(contVys, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtPoznamka.setEditable(false);
		txtPoznamka.setAlwaysShowScrollBars(false);
		txtPoznamka.setBounds(10, 120, 960, 310);
		
		// na ulozenie zmien
		btnVysUloz = new Button(contVys, SWT.NONE);
		btnVysUloz.setForeground(SWTResourceManager.getColor(0, 0, 0));
		btnVysUloz.setBackground(SWTResourceManager.getColor(46, 139, 87));
		btnVysUloz.setBounds(881, 39, 89, 25);
		btnVysUloz.setEnabled(false);
		btnVysUloz.setText("Ulo\u017Ei\u0165");
		btnVysUloz.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			if(pacient != null) {
				boolean chyba = false;
				File f = new File("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
				String zm = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
				String genID = txtNazovVys.getText()+"-"+zm;
				Document doc = null;
				DocumentBuilder docBuilder;
				try {
					docBuilder = docFactory.newDocumentBuilder();
					doc = docBuilder.parse(f);
					if(noveVys) {
						NodeList list = doc.getElementsByTagName("vys");
						for (int i=0;i<list.getLength();i++) {
								Element n = (Element) list.item(i);
								if(n.getAttribute("id").equals(genID)) chyba=true;
						}
						if(!chyba) {
							Node core = doc.getElementsByTagName("vysetrenia").item(0);
							Element main = doc.createElement("vys");
							main.setAttribute("id", genID);
							core.appendChild(main);
							otvVys = genID;
							vybrateVys = otvVys;
							Element nazov = doc.createElement("nazov");
							nazov.appendChild(doc.createTextNode(txtNazovVys.getText()));
							main.appendChild(nazov);
							Element vyk = doc.createElement("vyk");
							vyk.appendChild(doc.createTextNode(txtVykonal.getText()));
							main.appendChild(vyk);
							Element pozn = doc.createElement("poz");
							pozn.appendChild(doc.createTextNode(txtPoznamka.getText()));
							main.appendChild(pozn);
							Element ulozena = doc.createElement("datum");
							ulozena.appendChild(doc.createTextNode(zm));
							main.appendChild(ulozena);
							Element typ = doc.createElement("typ");
							if(comboVys.getSelectionIndex()==-1) throw new IllegalArgumentException();
							typ.appendChild(doc.createTextNode(comboVys.getItem(comboVys.getSelectionIndex())));
							main.appendChild(typ);
							Element obr = doc.createElement("obr");
							main.appendChild(obr);
							Element bod1x = doc.createElement("bod1x");
							main.appendChild(bod1x);
							Element bod2x = doc.createElement("bod2x");
							main.appendChild(bod2x);
							Element bod3x = doc.createElement("bod3x");
							main.appendChild(bod3x);
							Element bod1y = doc.createElement("bod1y");
							main.appendChild(bod1y);
							Element bod2y = doc.createElement("bod2y");
							main.appendChild(bod2y);
							Element bod3y = doc.createElement("bod3y");
							main.appendChild(bod3y);
						}
					} else {
						NodeList list = doc.getElementsByTagName("vys");
						for(int i=0;i<list.getLength();i++) {
							Element n = (Element) list.item(i);
							if(n.getAttribute("id").equals(otvVys)) {
								NodeList nl = n.getChildNodes();
								for (int j=0;j<nl.getLength();j++) {
									Node node = nl.item(j);
									if("nazov".equals(node.getNodeName())) node.setTextContent(txtNazovVys.getText());
									if("poz".equals(node.getNodeName())) node.setTextContent(txtPoznamka.getText());
									if("typ".equals(node.getNodeName())) node.setTextContent(comboVys.getItem(comboVys.getSelectionIndex()));
									if("vyk".equals(node.getNodeName())) node.setTextContent(txtVykonal.getText());
									if("datum".equals(node.getNodeName())) node.setTextContent(zm);
								}
							}
						}
					}
					if(!chyba) {
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(f);
						transformer.transform(source, result);
						txtNazovVys.setEditable(false);
						txtPoznamka.setEditable(false);
						txtVykonal.setEditable(false);
						comboVys.setEnabled(false);
						btnVysUloz.setEnabled(false);
						btnVysEdit.setEnabled(true);
					}
				} catch (ParserConfigurationException | SAXException | IOException | TransformerException e1) {
					new Messages().msg(shell,MsgTypes.ERROR,6,e1.getMessage());
				} catch (IllegalArgumentException ee) {
					new Messages().msg(shell,MsgTypes.ERROR,13,"");
				}
			} else {
				new Messages().msg(shell,MsgTypes.ERROR,7,"");
			}
			}
		});
		
		btnVysEdit = new Button(contVys, SWT.NONE);
		btnVysEdit.setBackground(SWTResourceManager.getColor(139, 0, 0));
		btnVysEdit.setBounds(800, 39, 75, 25);
		btnVysEdit.setEnabled(false);
		btnVysEdit.setText("Zmeni\u0165");
		btnVysEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				noveVys = false;
				txtNazovVys.setEditable(true);
				txtPoznamka.setEditable(true);
				txtVykonal.setEditable(true);
				comboVys.setEnabled(true);
				btnVysEdit.setEnabled(false);
				btnVysUloz.setEnabled(true);
			}
		});
		
		// OBRAZOK
		TabItem tabObr = new TabItem(tabContainer, SWT.NONE);
		tabObr.setText("Obr\u00E1zok");
		zlava = true;
		// canvas na obrazok
		canvas = new Canvas(tabContainer, SWT.NONE);
		tabObr.setControl(canvas);
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		recs = new ArrayList<Rectangle>();
		canvas.addPaintListener(new PaintListener() {
			  public void paintControl(PaintEvent e) {
				if(urlObr!=null && urlObr!="del") {
					image = new Image(Display.getCurrent(),urlObr);
					double ratio = (float)canvas.getBounds().height / image.getBounds().height; 
					e.gc.drawImage(image, 0, 0,image.getBounds().width,image.getBounds().height,0,0,(int)(image.getBounds().width*ratio),canvas.getSize().y);
					for(int i=0;i<recs.size();i++) {
						if(i==0) e.gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
						else e.gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
						e.gc.fillOval(recs.get(i).x, recs.get(i).y, recs.get(i).width, recs.get(i).height);
					}
					if(recs.size()==3) {
						e.gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
						int half = dotSize/2;
						e.gc.drawLine(recs.get(0).x+half,recs.get(0).y+half,recs.get(1).x+half,recs.get(1).y+half);
						e.gc.drawLine(recs.get(0).x+half,recs.get(0).y+half,recs.get(2).x+half,recs.get(2).y+half);
						double angle = getAngle(recs.get(0).x,recs.get(0).y,recs.get(1).x,recs.get(1).y,recs.get(2).x,recs.get(2).y);
						if(zlava) uhol.setText(Double.toString(360.0-angle)); 
						else uhol.setText(Double.toString(angle));
					} else uhol.setText("N/A");
				} else if(urlObr=="del") {
					urlObr = null;
					e.gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					e.gc.fillRectangle(canvas.getBounds());
				}
			  }
		});
		
		Menu popupMenu = new Menu(canvas);
		MenuItem loadPic = new MenuItem(popupMenu, SWT.NONE);
		loadPic.setText("Naèítaj obrázok");
		loadPic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if((vybrateVys !=null && urlObr==null)||noveVys) {
					FileDialog fd = new FileDialog(shell, SWT.OPEN);
					String[] filterExt = {"*.jpg","*.jpeg","*.bmp","*.png",".gif","*.*"};
					fd.setFilterExtensions(filterExt);
					urlObr = fd.open();
					String newUrl = "data/"+pacient.getRc().replace("/","-")+
							"/"+vybrateVys+urlObr.substring(urlObr.lastIndexOf("."));
					try {
						System.out.println(urlObr + " ----- " + newUrl);
						copyFileUsingChannel(new File(urlObr), new File(newUrl));
					} catch (IOException e1) {
						new Messages().msg(shell,MsgTypes.ERROR,8,"");
					}
					urlObr = newUrl;
				}
				canvas.redraw();
			}
		});
		MenuItem delPic = new MenuItem(popupMenu, SWT.NONE);
		delPic.setText("Vymaza");
		delPic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(vybrateVys!=null) {
					Document doc = null;
					DocumentBuilder docBuilder;
					try {
						docBuilder = docFactory.newDocumentBuilder();
						doc = docBuilder.parse("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
						doc.getElementsByTagName("obr").item(0).setTextContent("");
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
						transformer.transform(source, result);
					} catch(IOException | SAXException | TransformerException | ParserConfigurationException exc) {
						new Messages().msg(shell,MsgTypes.ERROR,6,exc.getMessage());
					}
					new File(urlObr).delete();
					urlObr = "del";
					for(int i=0;i<recs.size();i++) {
						recs.remove(i);
					}
					canvas.redraw();
				}
			}
		});
		
		canvas.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {}
			public void mouseDown(MouseEvent e){}
			public void mouseDoubleClick(MouseEvent e) {
				if (vybrateVys!=null && urlObr!=null) {
					boolean there = false;
					Rectangle r = new Rectangle(e.x, e.y, dotSize, dotSize);
					for(Rectangle rs : recs) {
						if(e.x >= rs.x && e.x <= rs.x+dotSize && e.y >= rs.y && e.y <= rs.y+dotSize) {there=true;r=rs;}
					}
					if(there) {
						recs.remove(r);
					}
					else if(recs.size()<=2) {
						recs.add(r);
					}
					canvas.redraw();
				}
			}
		});
		
		MenuItem savePic = new MenuItem(popupMenu, SWT.NONE);
		savePic.setText("Uloži");
		savePic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("vysetrenie - " + vybrateVys);
				System.out.println("obr - " + urlObr);
				if(vybrateVys !=null && urlObr!=null) {
					System.out.println("uklada");
				Document doc = null;
				DocumentBuilder docBuilder;
				try {
					docBuilder = docFactory.newDocumentBuilder();
					doc = docBuilder.parse("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
					if(recs.size()!=3) throw new SizeLimitExceededException();
					NodeList list = doc.getElementsByTagName("vys");
					for(int i=0;i<list.getLength();i++) {
						Element n = (Element) list.item(i);
						if(n.getAttribute("id").equals(otvVys)) {
							NodeList nl = n.getChildNodes();
							for (int j=0;j<nl.getLength();j++) {
								Node node = nl.item(j);
								if("obr".equals(node.getNodeName())) node.setTextContent(urlObr);
								if("bod1x".equals(node.getNodeName())) node.setTextContent(Integer.toString(recs.get(0).x));
								if("bod1y".equals(node.getNodeName())) node.setTextContent(Integer.toString(recs.get(0).y));
								if("bod2x".equals(node.getNodeName())) node.setTextContent(Integer.toString(recs.get(1).x));
								if("bod2y".equals(node.getNodeName())) node.setTextContent(Integer.toString(recs.get(1).y));
								if("bod3x".equals(node.getNodeName())) node.setTextContent(Integer.toString(recs.get(2).x));
								if("bod3y".equals(node.getNodeName())) node.setTextContent(Integer.toString(recs.get(2).y));
							}
						}
					}
					
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult("data/"+pacient.getRc().replace("/","-")+"/pac_db.xml");
					transformer.transform(source, result);
				} catch(IOException | SAXException | TransformerException | ParserConfigurationException exc) {
					new Messages().msg(shell,MsgTypes.ERROR,6,exc.getMessage());
				} catch (SizeLimitExceededException e1) {
					new Messages().msg(shell,MsgTypes.ERROR,10,"");
				}
				}
			}
		});
		MenuItem uholZlava = new MenuItem(popupMenu, SWT.NONE);
		uholZlava.setText("Uhol z¾ava");
		uholZlava.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				zlava = true;
			}
		});
		MenuItem uholSprava = new MenuItem(popupMenu, SWT.NONE);
		uholSprava.setText("Uhol sprava");
		uholSprava.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				zlava = false;
			}
		});
		canvas.setMenu(popupMenu);
	}
}
