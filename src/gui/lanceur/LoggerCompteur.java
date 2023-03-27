package gui.lanceur;


import java.io.File;

import javax.swing.filechooser.FileFilter;


import nico.NicoCompteur;
import nico.NicoInfo;
import util.FileLoad;

public class LoggerCompteur {

	public static void main(String[] args) {
		FileFilter filtre=new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory();
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "Repertoire contenant les fichiers de detection";
			}
		};
		File repertoire = FileLoad.getDir(new LoggerCompteur().getClass().getName(),filtre );
		if(null!=repertoire) {
			File[] listeDenm = repertoire.listFiles(new java.io.FileFilter() {

				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					return pathname.isFile() && (pathname.getName().endsWith(".det"));
				}
			});
			NicoInfo recap=new NicoInfo();
			System.out.println(recap.printEntete());
			for (File fichier : listeDenm) {
				try {
					NicoCompteur compteur=new NicoCompteur();
					NicoInfo info=new NicoInfo();
					compteur.getCourbes(fichier, info);
					System.out.println(fichier.getName()+info.print());
					recap.add(info);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Total"+recap.print());

			System.out.println(System.getProperty("line.separator")+recap.printEntete());
			
			for(int indice=1;indice<(NicoInfo.NB_CLASSES-1);indice++) {
				System.out.println("Distribution classe "+indice+recap.printClasse(indice));
			}
	
		}
	}

}
