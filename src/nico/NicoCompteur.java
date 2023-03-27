package nico;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NicoCompteur implements NicoElemEater {
	final public static int NB_COURBES_MAX = 8;
	private NicoProcessor processor=new NicoProcessor();
	private NicoInfo info = null;

	public NicoInfo getInfo() {
		if(null==info) {
			info=new NicoInfo();
		}
		return info;
	}

	public void setInfo(NicoInfo info) {
		this.info = info;
	}

	public void getCourbes(File nomFichier,NicoInfo info) {
		try {
			String nomFichierString = nomFichier.getName();
			if (nomFichierString.endsWith(".det")) {
				setInfo(info);
				FileInputStream fis=new FileInputStream(nomFichier);
				byte buffer[]=new byte[500];
				int nbCarLus;
				while(0<(nbCarLus=fis.read(buffer))) {
					processor.processInfo(buffer, 0, nbCarLus, this);
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

	@Override
	public long add(int courbe, NicoCourbeElem elem) {
		return 0;
	}

	@Override
	public void add(int courbe, NicoVehElem elem) {
	}

	@Override
	public void add(int courbe, NicoDetElem elem) {
		if(courbe==1) {
			getInfo().add(elem.getSilhouette());
		}
	}

	@Override
	public void setChanged(boolean b) {
		
	}

}
