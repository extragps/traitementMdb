package nico;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NicoExtract  {
	final public static int NB_COURBES_MAX = 8;
	private NicoProcessor processor=new NicoProcessor();

	public static GregorianCalendar getDateFromFilename(String nomFichier) {
		GregorianCalendar car = new GregorianCalendar();
		int indice = 0;
		MessageFormat format = new MessageFormat("{" + indice++ + "}" + "_{" + indice++ + ",number}" + "_{" + indice++
				+ ",number}" + "_{" + indice++ + ",number}" + "_{" + indice++ + ",number}" + "hh{" + indice++
				+ ",number}" + "mn{" + indice++ + ",number}ss.bin");
		try {
			Object tabInfos[] = format.parse(nomFichier);
			car.set(Calendar.DAY_OF_MONTH, ((Number) tabInfos[1]).intValue());
			car.set(Calendar.MONTH, ((Number) tabInfos[2]).intValue() - 1);
			car.set(Calendar.YEAR, ((Number) tabInfos[3]).intValue());
			car.set(Calendar.HOUR_OF_DAY, ((Number) tabInfos[4]).intValue());
			car.set(Calendar.MINUTE, ((Number) tabInfos[5]).intValue());
			car.set(Calendar.SECOND, ((Number) tabInfos[6]).intValue());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			car = null;
		}
		return car;
	}

	public static void main(String[] argv) {
		int indice = 0;
		File nomFichier = new File("TerrasseEntree_22_06_2021_10hh00mn45ss.bin");
		while (indice < argv.length) {
			if (argv[indice].equals("-fichier")) {
				if (++indice < argv.length) {
					nomFichier = new File(argv[indice]);
				}

			} else if ((argv[indice].equals("-help")) || (argv[indice].equals("-h")) || (argv[indice].equals("-?"))) {
				System.out.println("Syntaxe : ExtractionFichier [-fichier nom] [-help|-h|-?]");
			}
			indice++;
		}
		GregorianCalendar cal = NicoExtract.getDateFromFilename(nomFichier.getName());
		NicoElemEaterImpl eater=new NicoElemEaterImpl(cal);
		NicoExtract extract = new NicoExtract();
		extract.getCourbes(nomFichier,eater);
	}

	public void getCourbes(File nomFichier,NicoElemEater eater) {
		try {
			String nomFichierString = nomFichier.getName();
			if (nomFichierString.endsWith(".det")) {
				FileInputStream fis=new FileInputStream(nomFichier);
				byte buffer[]=new byte[500];
				int nbCarLus;
				while(0<(nbCarLus=fis.read(buffer))) {
					processor.processInfo(buffer, 0, nbCarLus, eater);
				}
				fis.close();

			} else  if (nomFichierString.endsWith(".bin")) {
				FileInputStream fis = new FileInputStream(nomFichier);
				while (0 != fis.available()) {
					byte infoLong[] = new byte[4];
					byte infoNbMesure[] = new byte[4];
					fis.read(infoLong);
					fis.read(infoNbMesure);
					ByteBuffer bb = ByteBuffer.allocate(4);
					bb.order(ByteOrder.LITTLE_ENDIAN);
					bb.put(infoLong);
					ByteBuffer bbNbInfos = ByteBuffer.allocate(4);
					bbNbInfos.order(ByteOrder.LITTLE_ENDIAN);
					bbNbInfos.put(infoNbMesure);
					Integer timestamp = bb.getInt(0);
					Integer nbMesures = bbNbInfos.getInt(0);
					System.out.println("NbInfos : " + nbMesures);
					ByteBuffer bbinfoSeuil = ByteBuffer.allocate(NB_COURBES_MAX * 3 * 2);
					bbinfoSeuil.order(ByteOrder.LITTLE_ENDIAN);
					byte infoSeuil[] = new byte[NB_COURBES_MAX * 3 * 2];
					fis.read(infoSeuil);
					bbinfoSeuil.put(infoSeuil);
					for (int courant = 0; courant < nbMesures; courant++) {
						byte infoBoucle[] = new byte[NB_COURBES_MAX * 2];
						ByteBuffer bbinfoBoucle = ByteBuffer.allocate(NB_COURBES_MAX * 2);
						bbinfoBoucle.order(ByteOrder.LITTLE_ENDIAN);
						fis.read(infoBoucle);
						bbinfoBoucle.put(infoBoucle);
						for (int indice = 0; indice < NB_COURBES_MAX; indice++) {
							System.out.print("" + bbinfoBoucle.getShort(2 * indice) + ";");
						}
						System.out.println();
						for (int indCour = 0; indCour < NB_COURBES_MAX; indCour++) {
							NicoCourbeElem newElem = new NicoCourbeElem(timestamp, bbinfoBoucle.getShort(2 * indCour),
									bbinfoSeuil.getShort(2 * (8 + indCour)), bbinfoSeuil.getShort(2 * indCour),
									bbinfoSeuil.getShort(2 * (16 + indCour)));
							eater.add(indCour, newElem);
						}
					}
				}
				fis.close();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
