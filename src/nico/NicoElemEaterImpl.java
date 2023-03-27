package nico;

import java.util.GregorianCalendar;

public class NicoElemEaterImpl implements NicoElemEater {
	private final static int NB_COURBES_MAX=8;
	private NicoCourbe courbes[] = new NicoCourbe[NB_COURBES_MAX];
	private NicoDet dets[] = new NicoDet[NB_COURBES_MAX];
	private NicoVeh vehs[] = new NicoVeh[NB_COURBES_MAX];

	public NicoElemEaterImpl() {
		this(new GregorianCalendar());
	}
	public NicoElemEaterImpl(GregorianCalendar cal) {
		for (int indCour = 0; indCour < 8; indCour++) {
			courbes[indCour] = new NicoCourbe(indCour, cal);
			dets[indCour] = new NicoDet(indCour, cal);
			vehs[indCour] = new NicoVeh(indCour, cal);
		} 

	}
	public NicoCourbe[] getCourbes() {
		return courbes;
	}

	public NicoDet[] getDets() {
		return dets;
	}

	public NicoVeh[] getVehs() {
		return vehs;
	}

	@Override
	public long add(int courbe, NicoCourbeElem elem) {
		getCourbe(courbe).add(elem);
		return getCourbe(courbe).size();
	}

	@Override
	public void add(int courbe, NicoVehElem elem) {
		
		getVeh(courbe).add(elem);

	}

	@Override
	public void add(int courbe, NicoDetElem elem) {
		getDet(courbe).add(elem);

	}

	@Override
	public void setChanged(boolean b) {
		// TODO Auto-generated method stub

	}

	public NicoCourbe getCourbe(int indCourbe) {
		return courbes[indCourbe];
	}
	public NicoVeh getVeh(int indCourbe) {
		return vehs[indCourbe];
	}
	public NicoDet getDet(int indCourbe) {
		return dets[indCourbe];
	}


}
