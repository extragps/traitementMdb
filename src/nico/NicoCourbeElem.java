package nico;

public class NicoCourbeElem {
	int cpt;
	int val;
	int seuil;
	public int getCpt() {
		return cpt;
	}
	public void setCpt(int cpt) {
		this.cpt = cpt;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}
	public int getSeuil() {
		return seuil;
	}
	public void setSeuil(int seuil) {
		this.seuil = seuil;
	}
	public int getEquilibre() {
		return equilibre;
	}
	public void setEquilibre(int equilibre) {
		this.equilibre = equilibre;
	}
	public int getSeuilNeg() {
		return seuilNeg;
	}
	public void setSeuilNeg(int seuilNeg) {
		this.seuilNeg = seuilNeg;
	}
	int equilibre;
	int seuilNeg;
	public NicoCourbeElem(int cpt,int val,int seuil,int equilibre,int seuilNeg) {
		setCpt(cpt);
		setVal(val);
		setSeuil(seuil);
		setEquilibre(equilibre);
		setSeuilNeg(seuilNeg);
	}
}
