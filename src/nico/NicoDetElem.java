package nico;

public class NicoDetElem {
	int cpt;
	int val;
	int vax;
	int tp;
	long index;
	int numero;
	int silhouette;
	public int getSilhouette() {
		return silhouette;
	}
	public void setSilhouette(int silhouette) {
		this.silhouette = silhouette;
	}
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
	public NicoDetElem(long index,int cpt,int val,int vax,int tp,int numero,int silh) {
		setCpt(cpt);
		setVal(val);
		setIndex(index);
		setVax(vax);
		setTp(tp);
		setNumero(numero);
		setSilhouette(silh);
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public int getVax() {
		return vax;
	}
	public void setVax(int vax) {
		this.vax = vax;
	}
	public int getTp() {
		return tp;
	}
	public void setTp(int tp) {
		this.tp = tp;
	}
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
}
