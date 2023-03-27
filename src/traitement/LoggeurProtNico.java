package traitement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import communication.Blagueur;
import communication.CaracteresEvent;
import communication.CaracteresListener;
import communication.ConnectionEvent;
import communication.ConnectionListener;
import nico.NicoMess;

public class LoggeurProtNico implements ConnectionListener, CaracteresListener {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss");
	private File repBase = new File("./");
	String fichierTrace = "traceDet_";

	public void setFichierTrace(String fichierTrace) {
		this.fichierTrace = fichierTrace;
	}

	private Blagueur blagueur = null;
	private FileOutputStream fos = null;
	private String chaineLog = "AI * 65000\r";
	private GregorianCalendar dateLast = new GregorianCalendar();
	private boolean journalier = false;
	private NicoMess infoCour = null;
	private boolean horaire = true;
	static SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.SSS");

	private GregorianCalendar getDateLast() {
		return dateLast;
	}

	private void setDateLast(GregorianCalendar dateLast) {
		this.dateLast = dateLast;
	}

	private boolean isHoraire() {
		return horaire;
	}

	public void setHoraire(boolean ho) {
		this.horaire = ho;
	}

	private boolean isJournalier() {
		return journalier;
	}

	private void setJournalier(boolean journalier) {
		this.journalier = journalier;
	}

	public LoggeurProtNico(Blagueur blag) {
		setBlagueur(blag);
	}

	public LoggeurProtNico(Blagueur blag, String chaine, boolean journalier, boolean horaire, File repBase,
			String prefixe) {
		this.repBase = repBase;
		this.fichierTrace = prefixe;
		setHoraire(horaire);
		setJournalier(journalier);
		setChaineLog(chaine);
		setBlagueur(blag);
	}

	public LoggeurProtNico(Blagueur blag, String chaine, boolean journalier, File repBase, String prefixe) {
		this(blag, chaine, journalier, false, repBase, prefixe);
	}

	public LoggeurProtNico(Blagueur blag, String chaine, boolean journalier, File repBase) {
		this(blag, chaine, journalier, repBase, "traceLcr_");
	}

	public LoggeurProtNico(Blagueur blag, String chaine, boolean journalier) {
		setChaineLog(chaine);
		setBlagueur(blag);
		setJournalier(journalier);
	}

	public LoggeurProtNico(Blagueur blag, String chaine) {
		this(blag, chaine, false);
	}

	@Override
	public void connectionChanged(ConnectionEvent e) {
		if (e.getOuvert()) {
			String chaine = "TRACE\r\r";
			blagueur.write(chaine.getBytes());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			blagueur.write(getChaineLog().getBytes());

		}
	}

	private String getFileName(GregorianCalendar dateCour) {
		return fichierTrace + "_" + sdf.format(dateCour.getTime()) + ".det";
	}

	@Override
	public void caracteresChanged(CaracteresEvent e) {
		GregorianCalendar dateCour = new GregorianCalendar();
		if (null == fos) {
			try {
				String nomFichier = getFileName(dateCour);
//			File fichierComplet=new File(nomFichier);
				File fichierComplet = new File(repBase, nomFichier);
				fos = new FileOutputStream(fichierComplet);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (isJournalier()) {
			/* Si on assiste a un changement de jour.... */
			if (getDateLast().get(GregorianCalendar.DAY_OF_MONTH) != dateCour.get(GregorianCalendar.DAY_OF_MONTH)) {
				/* Fermeture du fichier precedent... */
				if (null != fos) {
					try {
						fos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					fos = null;
				}
				/* Creation du nouveau fichier... */
				try {
					String nomFichier = getFileName(dateCour);
					File fichierComplet = new File(repBase, nomFichier);
					fos = new FileOutputStream(fichierComplet);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		} else if (isHoraire()) {
			/* Si on assiste a un changement de jour.... */
			if (getDateLast().get(GregorianCalendar.HOUR_OF_DAY) != dateCour.get(GregorianCalendar.HOUR_OF_DAY)) {
				/* Fermeture du fichier precedent... */
				if (null != fos) {
					try {
						fos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					fos = null;
				}
				/* Creation du nouveau fichier... */
				try {
					String nomFichier = getFileName(dateCour);
					File fichierComplet = new File(repBase, nomFichier);
					fos = new FileOutputStream(fichierComplet);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

		if (null != fos) {
			processInfo(fos, e.getCaracteres(), 0, e.getNbCaracteres());
		}
		setDateLast(dateCour);
	}

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes, int length) {
		char[] hexChars = new char[length * 2];
		for (int j = 0; j < length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	private void processInfo(FileOutputStream fos2, byte[] caracteres, int i, int nbCaracteres) {
		for (int indice = 0; indice < nbCaracteres; indice++) {
			byte car = caracteres[i + indice];
			if (null == infoCour) {
				infoCour = NicoMess.fabrique(car);
			} else {
				try {

					if (infoCour.ajouter(car)) {
						if (true) {
							switch (infoCour.getCode()) {
							default:
								infoCour.write(fos2);
								break;
							}
							infoCour = null;
						}
					}
				} catch (Exception e) {
					infoCour = null;
				}
			}
		}
	}

	public void setBlagueur(Blagueur blag) {
		if (null != blagueur) {
			blagueur.removeCaracteresListener(this);
			blagueur.removeConnectionListener(this);
		}
		this.blagueur = blag;
		if (null != blagueur) {
			blagueur.addCaracteresListener(this);
			blagueur.addConnectionListener(this);
		}
	}

	public Blagueur getBlagueur() {
		return blagueur;
	}

	public void terminer() {
		setBlagueur(null);
		try {
			if (null != fos) {
				fos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fos = null;
	}

	public String getChaineLog() {
		return chaineLog;
	}

	public void setChaineLog(String chaineLog) {
		this.chaineLog = chaineLog;
	}
}
