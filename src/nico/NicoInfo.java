package nico;

public class NicoInfo {
	public static final int NB_CLASSES=16;
	int compteurs[]=new int[NB_CLASSES];
	int distribution[][]=new int[NB_CLASSES][NB_CLASSES];
	public NicoInfo() {
		for(int indice=0;indice<NB_CLASSES;indice++) {
			compteurs[indice]=0;
		}
	}
	public void add(NicoInfo infoBis) {
		for(int indice=0;indice<NB_CLASSES;indice++) {
			compteurs[indice]+=infoBis.compteurs[indice];
			for(int ind2=0;ind2<NB_CLASSES;ind2++) {
				distribution[indice][ind2]+=infoBis.distribution[indice][ind2];
			}
		}
	}
	public void add(int silhouette) {
		compteurs[silhouette%16]++;
		distribution[silhouette%16][silhouette/16]++;

	}
	public String print() {
		StringBuffer sb=new StringBuffer();
		int somme=0;
		for(int indice=1;indice<(NB_CLASSES-1);indice++) {
			sb.append(";"+compteurs[indice]);
			somme+=compteurs[indice];
		}
		sb.append(";"+somme);
		return sb.toString();
	}
	public String printEntete() {
		StringBuffer sb=new StringBuffer();
		for(int indice=1;indice<(NB_CLASSES-1);indice++) {
			sb.append("; classe "+indice);
		}
		sb.append("; total");
		return sb.toString();

	}
	public Object printClasse(int classe) {
		StringBuffer sb=new StringBuffer();
		int somme=0;
		for(int indice=1;indice<(NB_CLASSES-1);indice++) {
			sb.append(";"+distribution[classe][indice]);
			somme+=distribution[classe][indice];
		}
		sb.append(";"+somme);
//		for(int indice=1;indice<(NB_CLASSES-1);indice++) {
//			double valeur=(100.*(double)distribution[classe][indice])/(double)somme;
//			sb.append(";"+((int)valeur)+"."+((int)(10*(valeur-(int)valeur)))+"%");
//		}
		return sb.toString();
	}
}
