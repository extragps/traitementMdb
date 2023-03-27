package nico;

public class NicoVehElem {
	long index;
	int bcl;
	int compteur;
	int val;
	int vitesse;
	int longueur;
	int tp;
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
	public int getBcl() {
		return bcl;
	}
	public void setBcl(int bcl) {
		this.bcl = bcl;
	}
	public int getCompteur() {
		return compteur;
	}
	public void setCompteur(int compteur) {
		this.compteur = compteur;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}
	public int getVitesse() {
		return vitesse;
	}
	public void setVitesse(int vitesse) {
		this.vitesse = vitesse;
	}
	public int getLongueur() {
		return longueur;
	}
	public void setLongueur(int longueur) {
		this.longueur = longueur;
	}
	public int getTp() {
		return tp;
	}
	public void setTp(int tp) {
		this.tp = tp;
	}
	public NicoVehElem(long index,int boucle,int compteur,int vitesse,int longueur,int vam,int pres) {
		setIndex(index);
		setBcl(boucle);
		setCompteur(compteur);
		setVitesse(vitesse);
		setLongueur(longueur);
		setVal(vam);
		setTp(pres);
	}
}
