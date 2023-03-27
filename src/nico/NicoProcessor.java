package nico;

public class NicoProcessor {
	final private static int NB_COURBES_MAX = 8;
	private NicoMess infoCour = null;
	private short seuil[] = new short[NB_COURBES_MAX];
	private short equilibre[] = new short[NB_COURBES_MAX];
	private short seuilNeg[] = new short[NB_COURBES_MAX];
	private long sizeCourbe[] = new long[NB_COURBES_MAX];
	private short compteur;
	private boolean equilInitX = false;
	private boolean equilInitY = false;
	public NicoProcessor() {
		reinit();
		infoCour=null;

	}
	public boolean isEquilInitX() {
		return equilInitX;
	}

	public boolean setEquilInitX(boolean equilInit) {
		boolean changed=false;
		if (equilInit != equilInitX) {
			this.equilInitX = equilInit;
			changed=true;
		}
		return changed;
	}

	public boolean isEquilInitY() {
		return equilInitY;
	}

	public boolean setEquilInitY(boolean equilInit) {
		boolean changed=false;
		if (equilInit != equilInitY) {
			this.equilInitY = equilInit;
			changed=true;
		}
		return changed;
	}

	public void processInfo(byte[] caracteres, int i, int nbCaracteres,NicoElemEater eater) {
		for (int indice = 0; indice < nbCaracteres; indice++) {
			byte car = caracteres[i + indice];
			if (null == infoCour) {
				infoCour = NicoMess.fabrique(car);
			} else {
				try {
					if (infoCour.ajouter(car)) {
						switch (infoCour.getCode()) {
						case 'U':
							if (isEquilInitX() && isEquilInitY()) {
								for (int indCour = 0; indCour < 8; indCour++) {
									NicoCourbeElem elem = new NicoCourbeElem(compteur, infoCour.getInfo(indCour),
											seuil[indCour], equilibre[indCour], seuilNeg[indCour]);
									sizeCourbe[indCour]=eater.add(indCour, elem);
								}
							}

							/* Positionnement de la date... */
							break;
						case 'K': {
							/* Il s'agit d'une detection a traiter... */
							int boucle = infoCour.getChar(0);
							int silhouette = infoCour.getChar(1);
							int tp = infoCour.getShort(2);
							int vam = infoCour.getShort(4);
							int vax = infoCour.getShort(6);
							long cpt = infoCour.getLong(8);
							NicoDetElem elem = null;
							if (15 != (silhouette % 16)) {
								elem = new NicoDetElem(sizeCourbe[boucle] - 1, (int) compteur, vam, vax,
										tp, (int) cpt, silhouette);
							} else {
								elem = new NicoDetElem(sizeCourbe[boucle] - 1, (int) tp, 255, 255, 50,
										(int) cpt, silhouette);
							}
							eater.add(boucle, elem);
//							System.out.println("Detection " + boucle + " tp: " + tp + " vam: " + vam + " vax: " + vax
//									+ " cpt: " + cpt + " compteur: " + compteur);
						}
							break;
						case 'L': {
							int boucle = infoCour.getChar(0);
							int vitesse = infoCour.getShort(1);
							int longueur = infoCour.getShort(3);
							int vam = infoCour.getShort(5);
							int pres = infoCour.getShort(7);
							NicoVehElem veh = new NicoVehElem(sizeCourbe[boucle] - 1, (int) boucle,
									compteur, vitesse, longueur, vam, pres);
							eater.add(boucle,veh);
						}
							break;

						case 'X':
							if(setEquilInitX(true)) {
								if(isEquilInitX()&&isEquilInitY()) {
									eater.setChanged(isEquilInitX()&&isEquilInitY()) ;
								}
							}
							for (int ind = 0; ind < 4; ind++) {
								seuil[ind] = infoCour.getInfo(3 * ind + 1);
								equilibre[ind] = infoCour.getInfo(3 * ind);
								seuilNeg[ind] = infoCour.getInfo(3 * ind + 2);
							}
							break;
						case 'Y':
							if(setEquilInitY(true)) {
								if(isEquilInitX()&&isEquilInitY()) {
									eater.setChanged(isEquilInitX()&&isEquilInitY()) ;
								}
							}
							for (int ind = 0; ind < 4; ind++) {
								seuil[4 + ind] = infoCour.getInfo(3 * ind + 1);
								equilibre[4 + ind] = infoCour.getInfo(3 * ind);
								seuilNeg[4 + ind] = infoCour.getInfo(3 * ind + 2);
							}
							break;
						case 'W':
							compteur = infoCour.getInfo(0);
							break;
						}
						infoCour = null;
					}
				} catch (Exception e) {
					infoCour = null;
					e.printStackTrace();
				}
			}
		}
	}
	public void reinit() {
		for (int indice = 0; indice < NB_COURBES_MAX; indice++) {
			seuil[indice] = 0;
			equilibre[indice] = 0;
			seuilNeg[indice] = 0;
			sizeCourbe[indice] = 0;
		}
		equilInitX = false;
		equilInitY = false;
	}

}
