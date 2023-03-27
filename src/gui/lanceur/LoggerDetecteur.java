package gui.lanceur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.IllegalFormatException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import com.fazecast.jSerialComm.SerialPort;

import communication.Blagueur;
import communication.BlagueurSerie;
import communication.BlagueurSocket;
import communication.CaracteresEvent;
import communication.CaracteresListener;
import communication.Config;
import communication.ConfigSerie;
import communication.ConfigSocket;
import communication.ConnectionEvent;
import communication.ConnectionListener;
import communication.gui.ConfigSelection;
import nico.NicoCourbe;
import nico.NicoCourbeElem;
import nico.NicoDet;
import nico.NicoDetElem;
import nico.NicoElemEater;
import nico.NicoElemEaterImpl;
import nico.NicoExtract;
import nico.NicoProcessor;
import nico.NicoVeh;
import nico.NicoVehElem;
import nico.gui.NicoGrapheConfig;
import nico.gui.NicoPanel;
import traitement.LoggeurProtNico;
import util.FileLoad;

public class LoggerDetecteur extends JFrame implements CaracteresListener, ConnectionListener, NicoElemEater {
	final private static int NB_COURBES_MAX = 8;
	private static int MAX_COURBES_AFF = 3;
	private Blagueur blagueur = null; // @jve:decl-index=0:
	private LoggeurProtNico loggueur = null;
	private File fichiers[];
	private int fichierCourant = -1;

	private JTabbedPane tabbedPane = null;
	private JEditorPane epLog = null;
	private NicoPanel panelCourbe[] = new NicoPanel[MAX_COURBES_AFF];
	private NicoCourbe courbe[] = new NicoCourbe[MAX_COURBES_AFF];
	private NicoDet det[] = new NicoDet[MAX_COURBES_AFF];
	private NicoVeh veh[] = new NicoVeh[MAX_COURBES_AFF];
	private JPanel courbes;
	private GregorianCalendar cal = new GregorianCalendar();
	private JProgressBar progressBar;
	private JLabel labelDate;
	private JPanel panelControleZoom = null;
	private JButton btnNewButton_4 = null;
	private JButton btnNewButton_5 = null;
	static private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.SSS");
	static SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss.SSS");
	private JPanel panelControleInfo;
	private JFormattedTextField tfId;
	private JButton nextPl;
	private JButton nextVeh;
	private JButton precPl;
	private JButton precVeh;
	private JButton progressNext;
	private JButton progressPrec;
	private NicoProcessor processor = new NicoProcessor();

	public Blagueur getBlagueur() {
		return blagueur;
	}

	public void setBlagueur(Blagueur blagueur) {
		if (null != this.blagueur) {
			this.blagueur.removeCaracteresListener(this);
			this.blagueur.removeConnectionListener(this);
		}
		this.blagueur = blagueur;
		blagueur.addCaracteresListener(this);
		blagueur.addConnectionListener(this);
	}

	private void setFiles(File listeFichiers[], int prem, int nb) {
		NicoExtract extract = new NicoExtract();
		if (prem < listeFichiers.length) {

			cal = LoggerDetecteur.getDateFromFilename(listeFichiers[prem].getName());
			this.setTitle("Debut : " + new SimpleDateFormat().format(cal.getTime()));
			NicoElemEaterImpl eater=new NicoElemEaterImpl(cal);
				courbe = eater.getCourbes();
				det=eater.getDets();
				veh=eater.getVehs();
			for (int indice = 0; indice < nb; indice++) {
				if ((prem + indice) < listeFichiers.length) {
					File nomFichier = listeFichiers[prem + indice];
					extract.getCourbes(nomFichier, eater);
				}
			}
		}
	}

	/**
	 * @param args
	 * @wbp.parser.constructor
	 */
	private LoggerDetecteur(File nomFichier[]) {
		if (0 != nomFichier.length) {
			setFichiers(nomFichier);
			setFichierCourant(0);
			this.setFiles(nomFichier, 0, 10);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			initialize();
		}
	}

	private LoggerDetecteur(String title, Blagueur blague, LoggeurProtNico log) {
		setLoggueur(log);
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for (int indice = 0; indice < MAX_COURBES_AFF; indice++) {
			courbe[indice] = new NicoCourbe(indice, new GregorianCalendar());
		}
		setBlagueur(blague);
		initialize();
	}

	public File[] getFichiers() {
		return fichiers;
	}

	public void setFichiers(File[] fichiers) {
		this.fichiers = fichiers;
	}

	public int getFichierCourant() {
		return fichierCourant;
	}

	public void setFichierCourant(int fichierCourant) {
		this.fichierCourant = fichierCourant;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(new Dimension(800, 600));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/images/courbes.png")));
		this.setPreferredSize(new Dimension(800, 600));
		this.setContentPane(getTabbedPane());
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (null != getLoggueur()) {
					getLoggueur().terminer();
				}
				if (null != getBlagueur()) {
					if (getBlagueur().isOpen()) {
						for (int indice = 0; indice < 10; indice++) {
							getBlagueur().write("\r".getBytes());
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					getBlagueur().terminer();
				}
			}
		});
	}

	private static int getParity(String parity) {
		int par = SerialPort.NO_PARITY;
		if (parity.equals("None")) {
			par = SerialPort.NO_PARITY;
		}
		if (parity.equals("Even")) {
			par = SerialPort.EVEN_PARITY;
		}
		if (parity.equals("Odd")) {
			par = SerialPort.ODD_PARITY;
		}
		return par;
	}

	/**
	 * This method initializes tabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JPanel getCourbes() {
		if (null == courbes) {
			courbes = new JPanel(new BorderLayout());
			JPanel aux = new JPanel(new GridLayout(MAX_COURBES_AFF, 1));
			for(int indice=0;indice<MAX_COURBES_AFF;indice++)  {
				aux.add(getPanelCourbe(indice));
			}
			courbes.add(aux, BorderLayout.CENTER);
			courbes.add(getPanelControle(), BorderLayout.SOUTH);
		}
		return courbes;
	}

	private JPanel getPanelControle() {
		if (null == panelControleInfo) {
			panelControleInfo = new JPanel(new BorderLayout());
			panelControleInfo.add(getLabelDate(), BorderLayout.WEST);
			JPanel panelProgress = new JPanel(new BorderLayout());
			panelProgress.add(getProgressBar(), BorderLayout.CENTER);
			panelProgress.add(getProgressPrec(), BorderLayout.EAST);
			panelProgress.add(getProgressNext(), BorderLayout.WEST);
			panelControleInfo.add(panelProgress, BorderLayout.CENTER);
			panelControleInfo.add(getControleZoom(), BorderLayout.EAST);
		}
		return panelControleInfo;
	}

	private JButton getProgressNext() {
		if (null == progressNext) {
			progressNext = new JButton();
			progressNext.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/go-first.png")));
			progressNext.setToolTipText("Afficher un peu avant");
			progressNext.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					setScaleFrom(getPremPanel().getNbX(), getPremPanel().getFrom() - getPremPanel().getNbX() / 10.);
				}
			});
		}
		return progressNext;
	}

	private JButton getProgressPrec() {
		if (null == progressPrec) {
			progressPrec = new JButton();
			progressPrec.setToolTipText("Afficher un peu apres");
			progressPrec.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/finish.png")));
			progressPrec.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					setScaleFrom(getPremPanel().getNbX(), getPremPanel().getFrom() + getPremPanel().getNbX() / 10.);
				}
			});
		}
		return progressPrec;
	}

	private JLabel getLabelDate() {
		if (null == labelDate) {
			labelDate = new JLabel(sdf.format(cal.getTime()));
		}
		return labelDate;
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("Courbe", null, getCourbes(), null);
//			tabbedPane.addTab("Log", null, getSpLog(), null);
		}
		return tabbedPane;
	}

//	/**
//	 * This method initializes spLog
//	 * 
//	 * @return javax.swing.JScrollPane
//	 */
//	private JScrollPane getSpLog() {
//		if (spLog == null) {
//			spLog = new JScrollPane();
//			spLog.setViewportView(getEpLog());
//		}
//		return spLog;
//	}

	/**
	 * This method initializes epLog
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getEpLog() {
		if (epLog == null) {
			epLog = new JEditorPane();
			epLog.setPreferredSize(new Dimension(500, 500));
			epLog.setText("");
			epLog.setEditable(false);
		}
		return epLog;
	}

	public static GregorianCalendar getDateFromFilename(String nomFichier) {
		GregorianCalendar car = new GregorianCalendar();
		int indice = 0;
		MessageFormat format = new MessageFormat("{" + indice++ + "}" + "_{" + indice++ + ",number}" + "_{" + indice++
				+ ",number}" + "_{" + indice++ + ",number}" + "_{" + indice++ + ",number}" + "hh{" + indice++
				+ ",number}" + "mn{" + indice++ + ",number}ss.{" + indice++ + "}");
		indice = 0;
		indice = 0;
		MessageFormat bis = new MessageFormat(
				"{" + indice++ + "}_{" + indice++ + "}_{" + indice++ + ",date,yyMMdd_HHmmss}.det");
		indice = 0;
		MessageFormat ter = new MessageFormat(
				"{" + indice++ + "}_{" + indice++ + "}_{" + indice++ + ",date,yyMMdd_HHmmss}_bis.det");

		try {
			Object tabInfos[] = format.parse(nomFichier);
			car.set(Calendar.DAY_OF_MONTH, ((Number) tabInfos[1]).intValue());
			car.set(Calendar.MONTH, ((Number) tabInfos[2]).intValue() - 1);
			car.set(Calendar.YEAR, ((Number) tabInfos[3]).intValue());
			car.set(Calendar.HOUR_OF_DAY, ((Number) tabInfos[4]).intValue());
			car.set(Calendar.MINUTE, ((Number) tabInfos[5]).intValue());
			car.set(Calendar.SECOND, ((Number) tabInfos[6]).intValue());
		} catch (ParseException e) {
			try {
				Object tabInfos[] = bis.parse(nomFichier);
				car.setTime((Date) tabInfos[2]);
			} catch (ParseException e1) {
				try {
					Object tabInfos[] = ter.parse(nomFichier);
					car.setTime((Date) tabInfos[2]);
				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
		return car;
	}

	public static void main(String[] argv) {
		ConfigSerie confSerie = new ConfigSerie();
		Blagueur blagueur = null;
		int indice = 0;
//		String chaineConnexion="ID SIAT AI * 0\r";
		String chaineConnexion = "X";
		String destination = "localhost";
		int port = 3500;
		LoggeurProtNico loggueur = null;
		ConfigSocket confSock = new ConfigSocket();
		Config config = confSock;
		boolean hasConfig = false;
		File repBase = new File(System.getProperty("user.dir"));
		String nomFichier = "traceLcr_";
		boolean fichierOk = false;
		boolean fichierSel = false;
		boolean console = true;
		boolean hasRep = false;
		boolean isJournalier = false;
		boolean isHoraire = false;
		boolean erreur = false;
		boolean repSel = false;
		int timeoutConnect = 120000;

		confSerie.setStopBits(SerialPort.ONE_STOP_BIT);
		confSerie.setParity(getParity("None"));
		confSerie.setNbBits(8);
		confSerie.setBaud(9600);
		confSock.setPort(34000);
		confSock.setHote("172.20.0.10");
		while (indice < argv.length) {
			if (argv[indice].equals("-dest")) {
				if (++indice < argv.length) {
					destination = argv[indice];
					config = confSock;
					confSock.setHote(destination);
					hasConfig = true;
				}
			} else if (argv[indice].equals("-ai")) {
				chaineConnexion = "AI * 0\r";
			} else if (argv[indice].equals("-repSel")) {
				fichierOk = true;
				repSel = true;
			} else if (argv[indice].equals("-nbCourbes")) {
				if (++indice < argv.length) {
					try {
						MAX_COURBES_AFF=Integer.parseInt(argv [indice]);
					} catch(NumberFormatException nfe)  {
						nfe.printStackTrace();
						System.out.println("Nombre de courbes incorrect "+argv[indice]);
						MAX_COURBES_AFF=4;
					}
					if((0>=MAX_COURBES_AFF)||(MAX_COURBES_AFF>8)) {
						System.out.println("Nombre de courbes incorrect "+MAX_COURBES_AFF);
						MAX_COURBES_AFF=4;
					}
				}
			} else if (argv[indice].equals("-ficSel")) {
				fichierOk = true;
				fichierSel = true;

			} else if (argv[indice].equals("-journalier")) {
				isJournalier = true;
				isHoraire = false;
			} else if (argv[indice].equals("-horaire")) {
				isJournalier = false;
				isHoraire = true;
			} else if (argv[indice].equals("-noconsole")) {
				console = false;
			} else if (argv[indice].equals("-rep")) {
				if (++indice < argv.length) {
					repBase = new File(argv[indice]);
					hasRep = true;
				}
			} else if (argv[indice].equals("-fichier")) {
				if (++indice < argv.length) {
					fichierOk = true;
					nomFichier = argv[indice];
				}
			} else if (argv[indice].equals("-port")) {
				if (++indice < argv.length) {
					try {
						port = Integer.parseInt(argv[indice]);
					} catch (IllegalFormatException ife) {
						port = 3300;
					}
					config = confSock;
					confSock.setPort(port);
					hasConfig = true;
				}
			} else if (argv[indice].equals("-timeout")) {
				if (++indice < argv.length) {
					try {
						timeoutConnect = Integer.parseInt(argv[indice]);
					} catch (IllegalFormatException ife) {
						timeoutConnect = 120000;
					}
				}
			} else if (argv[indice].equals("-vitesse")) {
				if (++indice < argv.length) {
					config = confSerie;
					confSerie.setBaud(Integer.parseInt(argv[indice]));
					hasConfig = true;
				}
			} else if (argv[indice].equals("-parite")) {
				if (++indice < argv.length) {
					config = confSerie;
					confSerie.setParity(getParity(argv[indice]));
					hasConfig = true;
				}
			} else if (argv[indice].equals("-nbBits")) {
				if (++indice < argv.length) {
					String databits = argv[indice];
					int val = 8;
					config = confSerie;
					if (databits.equals("7")) {
						val = 7;
					} else if (databits.equals("8")) {
						val = 8;
					}
					confSerie.setNbBits(val);
					hasConfig = true;
				}
			} else if (argv[indice].equals("-stopBits")) {
				if (++indice < argv.length) {
					String stopbits = argv[indice];
					if (stopbits.equals("1")) {
						confSerie.setStopBits(SerialPort.ONE_STOP_BIT);
					}
					if (stopbits.equals("1.5")) {
						confSerie.setStopBits(SerialPort.ONE_POINT_FIVE_STOP_BITS);
					}
					if (stopbits.equals("2")) {
						confSerie.setStopBits(SerialPort.TWO_STOP_BITS);
					}
					config = confSerie;
					hasConfig = true;
				}
			} else if (argv[indice].equals("-portSerie")) {
				if (++indice < argv.length) {
					confSerie.setPort(argv[indice]);
				}
				config = confSerie;
				hasConfig = true;
			} else if ((argv[indice].equals("-help")) || (argv[indice].equals("-h")) || (argv[indice].equals("-?"))) {
				System.out.println(
						"Syntaxe : LoggeurDetecteur [-noconsole][-fichier nom] [-rep repertoireBase][-journalier][-horaire][-ficSel][-repSel][-nbCourbes nb][-help|-h|-?]");
			}
			indice++;
		}
		if (fichierOk && console) {
			File listeFichiers[] = null;
			if (fichierSel) {
				File nomFichierBin;
				ArrayList<String> extensions = new ArrayList<>();
				extensions.add(".bin");
				extensions.add(".det");
				nomFichierBin = FileLoad.getFile(LoggerDetecteur.class.getName(), extensions,
						"Releves detecteur format MDB48S");
				if (null != nomFichierBin) {
					listeFichiers = new File[1];
					listeFichiers[0] = nomFichierBin;
				}
			} else if (repSel) {
				File nomRepBin;
				ArrayList<String> extensions = new ArrayList<>();
				extensions.add(".bin");
				extensions.add(".det");
				nomRepBin = FileLoad.getDir(LoggerDetecteur.class.getName());
				if (null != nomRepBin) {
					listeFichiers = FileLoad.listFilesSorted(nomRepBin, extensions);
				}
			}
			if (null != listeFichiers) {
				LoggerDetecteur simulateur = new LoggerDetecteur(listeFichiers);
				simulateur.pack();
				simulateur.setVisible(true);
			}
		} else {
			{
				if (false == hasRep) {
					JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					File defaultRep = new File(".loggerDetecteur");
					if (defaultRep.exists()) {
						ObjectInputStream ois;
						try {
							ois = new ObjectInputStream(new FileInputStream(defaultRep));
							Object obj = ois.readObject();
							Object confFic = ois.readObject();
							ois.close();
							if (obj instanceof File) {
								fc = new JFileChooser();
								fc.setCurrentDirectory((File) obj);
							}
							if (confFic instanceof Config) {
								config = (Config) confFic;
							}
						} catch (ClassCastException e) {
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setAcceptAllFileFilterUsed(false);
					fc.setFileFilter(new FileFilter() {
						@Override
						public boolean accept(File f) {
							return f.isDirectory();
						}

						@Override
						public String getDescription() {
							// TODO Auto-generated method stub
							return "Directory";
						};
					});
					fc.setDialogTitle("Selection du dossier de sauvegarde");
					fc.showDialog(null, "Selectionner");
					if (null != fc.getSelectedFile()) {
						File fichier = fc.getSelectedFile();
						repBase = fichier;
					} else {
						erreur = true;
					}

				}
			}
			if ((false == erreur) && (false == hasConfig)) {
				ConfigSelection selection = new ConfigSelection(config);
				selection.pack();
				selection.setVisible(true);
				if (null != selection.getConfig()) {
					config = selection.getConfig();
				} else {
					System.out.println("Probleme... ");
					erreur = true;
				}
			}
			if (false == erreur) {
				ObjectOutputStream oos;
				try {
					File defaultRep = new File(".loggerDetecteur");
					oos = new ObjectOutputStream(new FileOutputStream(defaultRep));
					oos.writeObject(repBase);
					oos.writeObject(config);
					oos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (false == erreur) {

				if (0 != timeoutConnect) {
					config.setTimeoutConnect(timeoutConnect);
				}
				if (config instanceof ConfigSocket) {
					BlagueurSocket blagSock = new BlagueurSocket((ConfigSocket) config);
					ConfigSocket configSock = (ConfigSocket) config;
					blagueur = blagSock;
					if (configSock.isServeur()) {
						nomFichier = "traceServeur_" + configSock.getPort();
					} else {
						nomFichier = "traceClient_" + configSock.getHote() + "_" + configSock.getPort() + "_";
					}
				} else {
					blagueur = new BlagueurSerie((ConfigSerie) config);
				}

				loggueur = new LoggeurProtNico(blagueur, chaineConnexion, isJournalier, isHoraire, repBase, nomFichier);
				LoggerDetecteur simulateur = null;
				if (console) {
					simulateur = new LoggerDetecteur(config.toString(), blagueur, loggueur);
					simulateur.pack();
					simulateur.setVisible(true);
				}

				{
					int nbCarLus;
					byte caracteres[] = new byte[500];
					boolean notFin = true;

					try {
						while (((nbCarLus = System.in.read(caracteres, 0, 500)) != -1) && (notFin)) {
							String lecture = new String(caracteres, 0, nbCarLus);
							if (lecture.startsWith("quit")) {
								notFin = false;
							} else if (lecture.startsWith("console")) {
								if (null == simulateur) {
									simulateur = new LoggerDetecteur(config.toString(), blagueur, loggueur);
									simulateur.pack();
									simulateur.setVisible(true);
								} else {
									simulateur.setVisible(!simulateur.isVisible());
								}

							} else if (lecture.startsWith("help") || lecture.startsWith("?")) {
								help();
							} else {
								System.out.println("Commande non comprise : " + lecture);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	static private void help() {
		System.out.println("La liste des commandes est :");
		System.out.println("  help : obtenir la liste des commandes");
		System.out.println("  console : affiche la console");
		System.out.println("  quit : quitter l'application");

	}

	@Override
	public void caracteresChanged(CaracteresEvent e) {
		processor.processInfo(e.getCaracteres(), 0, e.getNbCaracteres(),this);

	}

	private void setAffichageDate() {
		GregorianCalendar cal = getPanelCourbe(0).getDate();
		labelDate.setText(sdf.format(cal.getTime()));

	}

	private void moveProgress(int numero) {
		getProgressBar().setValue(numero);
		for (NicoPanel panel : panelCourbe) {
			if (null != panel) {
				panel.setFrom(numero);
				panel.repaint();
			}
		}
		GregorianCalendar calBis = new GregorianCalendar();
		calBis.setTime(cal.getTime());
		calBis.add(Calendar.MILLISECOND, 10 * Math.min((int) panelCourbe[0].getNbCourbeElem(), numero));
		labelDate.setText(sdf.format(calBis.getTime()));

	}

	public JProgressBar getProgressBar() {
		if (null == progressBar) {
			progressBar = new JProgressBar();
			progressBar.setValue(50);
			progressBar.setMinimum(0);
			progressBar.setMaximum(Math.max(100, (int) courbe[0].size()));
			progressBar.setOrientation(JScrollBar.HORIZONTAL);
			progressBar.setToolTipText(
					"Cliquer pour visualier a un endroit precis ou deplacer en gardant le bouton appuye");
			progressBar.addMouseMotionListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					int numero = (int) (progressBar.getMaximum() * e.getPoint().getX()
							/ (double) progressBar.getWidth());
//					System.out.println("mouseClicked "
//							+ (int) (progressBar.getMaximum() * e.getPoint().getX() / (double) progressBar.getWidth()));
					moveProgress(numero);
					/*
					 * bStop.setEnabled(true); bPause.setEnabled(true);
					 * bStart.setEnabled(!getPanelGrapheDetections().isAvant());
					 * bRetour.setEnabled(getPanelGrapheDetections().isAvant());
					 */
				}

				public void mouseDragged(java.awt.event.MouseEvent e) {
					int numero = (int) (progressBar.getMaximum() * e.getPoint().getX()
							/ (double) progressBar.getWidth());
//					System.out.println("mouseDragged " + numero);
					moveProgress(numero);
				}

				public void mousePressed(java.awt.event.MouseEvent e) {
					int numero = (int) (progressBar.getMaximum() * e.getPoint().getX()
							/ (double) progressBar.getWidth());
					moveProgress(numero);
				}

				public void mouseMoved(java.awt.event.MouseEvent e) {
				}
			});
			progressBar.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					int numero = (int) (progressBar.getMaximum() * e.getPoint().getX()
							/ (double) progressBar.getWidth());
					moveProgress(numero);
				}

				public void mouseDragged(java.awt.event.MouseEvent e) {
				}

				public void mousePressed(java.awt.event.MouseEvent e) {
					int numero = (int) (progressBar.getMaximum() * e.getPoint().getX()
							/ (double) progressBar.getWidth());
					moveProgress(numero);
				}

				public void mouseMoved(java.awt.event.MouseEvent e) {
				}
			});
		}
		return progressBar;
	}

	@Override
	public void connectionChanged(ConnectionEvent e) {
		// TODO Auto-generated method stub
		if (e.getOuvert()) {
			/* Mettre une bordure verte */
			getTabbedPane().setBorder(BorderFactory.createLineBorder(Color.yellow));
		} else {
			/* Mettre une bordure rouge */
			processor.reinit();
			getTabbedPane().setBorder(BorderFactory.createLineBorder(Color.red));
		}
	}

	public void setLoggueur(LoggeurProtNico loggueur) {
		this.loggueur = loggueur;
	}

	public LoggeurProtNico getLoggueur() {
		return loggueur;
	}

	private NicoPanel getPanelCourbe(int indCour) {
		if (panelCourbe[indCour] == null) {
			panelCourbe[indCour] = new NicoPanel(courbe[indCour], det[indCour], veh[indCour]);
		}
		return panelCourbe[indCour];

	}

	private NicoPanel getPremPanel() {
		return getPanelCourbe(0);
	}

	private NicoPanel getSecPanel() {
		return getPanelCourbe(1);
	}

	private NicoPanel getSilPanel() {
		return getPanelCourbe(2);
	}

	private JButton getLarge() {

		if (null == btnNewButton_4) {
			btnNewButton_4 = new JButton("");
			btnNewButton_4.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/large.png")));
			btnNewButton_4.setToolTipText("Zoom plus");
			btnNewButton_4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("Avant nb X " + getPremPanel().getNbX() + " from : " + getPremPanel().getFrom());
					double scaleX = getPremPanel().getNbX() / 2.0;
					double from = getPremPanel().getFrom() + getPremPanel().getNbX() / 4.0;
					for (NicoPanel panel : panelCourbe) {
						if (null != panel) {
							panel.setNbX(scaleX);
							panel.setFrom(from);
							panel.repaint();
						}
					}
					System.out.println("Apres nb X " + getPremPanel().getNbX() + " from : " + getPremPanel().getFrom());
				}
			});
		}
		return btnNewButton_4;
	}

	private JButton getEtroit() {
		if (null == btnNewButton_5) {
			btnNewButton_5 = new JButton("");
			btnNewButton_5.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/etroit.png")));
			btnNewButton_5.setToolTipText("Zoom moins");
			btnNewButton_5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("Avant nb X " + getPremPanel().getNbX() + " from : " + getPremPanel().getFrom());
					double scaleX = getPremPanel().getNbX() * 2.0;
					double from = getPremPanel().getFrom() - getPremPanel().getNbX() / 2;
					for (NicoPanel panel : panelCourbe) {
						if (null != panel) {
							panel.setNbX(scaleX);
							panel.setFrom(from);
							panel.repaint();
						}
					}
					System.out.println("Apres nb X " + getPremPanel().getNbX() + " from : " + getPremPanel().getFrom());
				}
			});
		}
		return btnNewButton_5;
	}

	private JPanel getControleZoom() {
		if (null == panelControleZoom) {
			panelControleZoom = new JPanel(new FlowLayout());
			panelControleZoom.add(getLarge());
			panelControleZoom.add(getEtroit());
			panelControleZoom.add(getPrecPl());
			panelControleZoom.add(getPrecVeh());
			panelControleZoom.add(getNumDetection());
			panelControleZoom.add(getNextVeh());
			panelControleZoom.add(getNextPl());
		}
		return panelControleZoom;
	}

	private JButton getNextPl() {
		if (null == nextPl) {
			nextPl = new JButton();
			nextPl.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/forwardPL.png")));
			nextPl.setToolTipText("PL suivant");
			nextPl.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object obj = getNumDetection().getValue();
					if (obj instanceof Number) {
						int idNum = ((Number) obj).intValue() + 1;
						NicoDetElem elem = det[1].getNextPl(idNum);
						setElem(elem, idNum);
					}
				}
			});

		}
		return nextPl;
	}

	private JButton getPrecPl() {
		if (null == precPl) {
			precPl = new JButton();
			precPl.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/backPL.png")));
			precPl.setToolTipText("PL pr�c�dent");
			precPl.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object obj = getNumDetection().getValue();
					if (obj instanceof Number) {
						int idNum = ((Number) obj).intValue() - 1;
						NicoDetElem elem = det[1].getPrecPl(idNum);
						setElem2(elem, idNum);
					}
				}
			});

		}
		return precPl;
	}

	private JButton getNextVeh() {
		if (null == nextVeh) {
			nextVeh = new JButton();
			nextVeh.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/forward.png")));
			nextVeh.setToolTipText("V�hicule suivant");
			nextVeh.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object obj = getNumDetection().getValue();
					if (obj instanceof Number) {
						int idNum = ((Number) obj).intValue() + 1;
						NicoDetElem elem = det[1].getNext(idNum);
						setElem(elem, idNum);
					}
				}
			});
		}
		return nextVeh;
	}

	private JButton getPrecVeh() {
		if (null == precVeh) {
			precVeh = new JButton();
			precVeh.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/back.png")));
			precVeh.setToolTipText("V�hicule pr�c�dent");
			precVeh.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object obj = getNumDetection().getValue();
					if (obj instanceof Number) {
						int idNum = ((Number) obj).intValue() - 1;
						NicoDetElem elem = det[1].getPrec(idNum);
						setElem2(elem, idNum);
					}
				}
			});
		}
		return precVeh;
	}

	private JFormattedTextField getNumDetection() {
		if (null == tfId) {
			tfId = new JFormattedTextField(new DecimalFormat("00000"));
			tfId.setValue(0);
			;
			tfId.setToolTipText("Visualiser la d�tection avec le num�ro");
			tfId.addPropertyChangeListener("value", new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					tfId.setBackground(Color.white);
				}
			});
			tfId.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object obj = tfId.getValue();
					if (obj instanceof Number) {
						int idNum = ((Number) obj).intValue();
						NicoDetElem elem = det[1].getNext(idNum);
						setElem(elem, idNum);
					}
				}
			});
			tfId.setColumns(5);

		}
		return tfId;
	}

	private void setElem(NicoDetElem elem, int idNum) {
		if (null != elem) {
			if (elem.getNumero() != idNum) {
				tfId.setValue(elem.getNumero());
			}
			setScaleFrom(getPremPanel().getNbX(), elem.getIndex() - getPremPanel().getNbX() * 3. / 4.);
		} else {
			tfId.setValue(0);
		}

	}

	private void setElem2(NicoDetElem elem, int idNum) {
		if (null != elem) {
			if (elem.getNumero() != idNum) {
				tfId.setValue(elem.getNumero());
			}
			setScaleFrom(getPremPanel().getNbX(), elem.getIndex() - getPremPanel().getNbX() * 3. / 4.);
		} else {
			tfId.setValue(Integer.MAX_VALUE);
		}

	}

	private void setScaleFrom(double scale, double from) {
		for (NicoPanel panel : panelCourbe) {
			if (null != panel) {
				panel.setNbX(scale);
				panel.setFrom(from);
				panel.repaint();
			}
		}
		moveProgress((int) from);

	}

	@Override
	public long add(int courbe, NicoCourbeElem elem) {
		boolean changed = false;
		long sizeCourbe = 0;
//		StringBuffer monBuff = new StringBuffer();
//		if (0 == courbe) {
//			monBuff.append(formatDate.format(new Date()) + ";");
//			monBuff.append(("" + elem.getCpt() + ";"));
//		}
//
//		monBuff.append(("" + elem.getEquilibre() + ";"));
//		monBuff.append(("" + elem.getSeuil() + ";"));
//		monBuff.append(("" + elem.getSeuilNeg() + ";"));
//		monBuff.append(("" + elem.getVal() + ";"));
//
//		if ((NB_COURBES_MAX - 1) == courbe) {
//			monBuff.append(("\n"));
//		}
//		Document doc = getEpLog().getDocument();
//		try {
//			doc.insertString(doc.getLength(), monBuff.substring(0), null);
//			getEpLog().setCaretPosition(doc.getLength());
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		if (courbe < MAX_COURBES_AFF) {
			changed = getPanelCourbe(courbe).add(elem);
			sizeCourbe = getPanelCourbe(courbe).nbElems();
			if (changed & ((MAX_COURBES_AFF - 1) == courbe)) {
				getCourbes().repaint();
				setAffichageDate();
			}
			if (0 == courbe) {
				getProgressBar().setMaximum(Math.max(100, (int) sizeCourbe));
			}
		}
		return sizeCourbe;
	}

	@Override
	public void add(int courbe, NicoVehElem elem) {
		if (courbe < MAX_COURBES_AFF) {
			getPanelCourbe(courbe).add(elem);
		}
	}

	@Override
	public void add(int courbe, NicoDetElem elem) {
		if (courbe < MAX_COURBES_AFF) {
			getPanelCourbe(courbe).add(elem);
		}
	}

	@Override
	public void setChanged(boolean b) {
		if (b) {
			getTabbedPane().setBorder(BorderFactory.createLineBorder(Color.green));
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"