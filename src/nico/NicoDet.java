package nico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class NicoDet {
	List<NicoDetElem> liste = new ArrayList<>();
	int posCour = 0;
	int min = Short.MAX_VALUE;
	int max = Short.MIN_VALUE;
	int numero = -1;
	int indCour = 0;
	int indCourPl = 0;
	private GregorianCalendar cal = new GregorianCalendar();

	public NicoDet() {
		this(-1, new GregorianCalendar());
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public NicoDet(int num, GregorianCalendar cal) {
		this.numero = num;
		this.cal = cal;
	}

	public void add(NicoDetElem elem) {
		liste.add(elem);
	}

	public int size() {
		// TODO Auto-generated method stub
		return liste.size();
	}

	public NicoDetElem get(int i) {
		// TODO Auto-generated method stub
		return liste.get(i);
	}

	public GregorianCalendar getDate(double from) {
		GregorianCalendar gcCalendar = (GregorianCalendar) cal.clone();
		gcCalendar.add(Calendar.MILLISECOND, 10 * (int) from);
		return gcCalendar;
	}

	public void trim(int trimSize) {
		int indCour = 0;
		for (NicoDetElem elem : liste) {
			if (elem.getIndex() > (trimSize)) {
				break;
			}
			indCour++;
		}
		if (indCour < liste.size()) {
			liste = liste.subList(indCour, liste.size());
		}
		for (NicoDetElem elem : liste) {
			elem.setIndex(elem.getIndex() - trimSize);
		}
	}

	public NicoDetElem getNext(int idNum) {
		NicoDetElem elem = null;
		int valPrec = indCour;
		indCour = (indCour+liste.size())%liste.size();
		while (indCour < liste.size()) {
			if (liste.get(indCour).getNumero() < idNum) {
				indCour++;
			} else {
				elem = liste.get(indCour);
				break;
			}
		}
		if (null == elem) {
			indCour = 0;
			while (indCour < valPrec) {
				if ((liste.get(indCour).getCpt() < idNum)) {
					indCour++;
				} else {
					elem = liste.get(indCour);
					break;
				}
			}
		}
		return elem;
	}
	public NicoDetElem getPrec(int idNum) {
		NicoDetElem elem = null;
		int valPrec = indCour;
		indCour = (indCour+liste.size())%liste.size();
		while (indCour >= 0) {
			if (liste.get(indCour).getNumero() > idNum) {
				indCour--;
			} else {
				elem = liste.get(indCour);
				break;
			}
		}
		if (null == elem) {
			indCour = liste.size()-1;
			while (indCour > valPrec) {
				if ((liste.get(indCour).getCpt() < idNum)) {
					indCour--;
				} else {
					elem = liste.get(indCour);
					break;
				}
			}
		}
		return elem;
	}

	public NicoDetElem getNextPl(int idNum) {
		NicoDetElem elem = null;
		int valPrec = indCourPl;
		indCourPl = (indCourPl+liste.size())%liste.size();
		while (indCourPl < liste.size()) {
			if ((liste.get(indCourPl).getNumero() < idNum) || (1 == (liste.get(indCourPl).getSilhouette() % 16))|| (13 == (liste.get(indCourPl).getSilhouette() % 16))) {
				indCourPl++;
			} else {
				elem = liste.get(indCourPl);
				break;
			}
		}
		if (null == elem) {
			indCourPl = 0;
			while (indCourPl < valPrec) {
				if ((liste.get(indCourPl).getCpt() < idNum) || (1 == (liste.get(indCourPl).getSilhouette() % 16))|| (13 == (liste.get(indCourPl).getSilhouette() % 16))) {
					indCourPl++;
				} else {
					elem = liste.get(indCourPl);
					break;
				}
			}
		}
		return elem;
	}
	public NicoDetElem getPrecPl(int idNum) {
		NicoDetElem elem = null;
		int valPrec = indCourPl;
		indCourPl = (indCourPl+liste.size())%liste.size();
		while (indCourPl >= 0) {
			if ((liste.get(indCourPl).getNumero() > idNum) || (1 == (liste.get(indCourPl).getSilhouette() % 16))|| (13 == (liste.get(indCourPl).getSilhouette() % 16))) {
				indCourPl--;
			} else {
				elem = liste.get(indCourPl);
				break;
			}
		}
		if (null == elem) {
			indCourPl = liste.size()-1;
			while (indCourPl > valPrec) {
				if ((liste.get(indCourPl).getCpt() < idNum) || (1 == (liste.get(indCourPl).getSilhouette() % 16))|| (13 == (liste.get(indCourPl).getSilhouette() % 16))) {
					indCourPl--;
				} else {
					elem = liste.get(indCourPl);
					break;
				}
			}
		}
		return elem;
	}
}
