package nico.gui;

import java.awt.BorderLayout;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import nico.NicoCourbe;
import nico.NicoCourbeElem;
import nico.NicoDet;
import nico.NicoDetElem;
import nico.NicoVeh;
import nico.NicoVehElem;

public class NicoPanel extends JPanel {	
	NicoGraphe panel = null;
	JProgressBar progressBar;
	NicoGrapheConfig nicoPiloteConfig = null;
	JPanel panelControle = null;
	NicoCourbe courbe = null;
	NicoDet det=null;
	NicoVeh veh=null;
	int nbX = 500;


	public NicoPanel() {
		this(null,null,null);
	}

	public NicoGraphe getPanel() {
		if (null == panel) {
			panel = new NicoGraphe(courbe,det,veh);
		}
		return panel;
	}

	public NicoPanel(NicoCourbe courbe,NicoDet det,NicoVeh veh) {
		setLayout(new BorderLayout(0, 0));
		this.courbe = courbe;
		this.det=det;
		this.veh=veh;
		add(getPanel(), BorderLayout.CENTER);
		add(getPilote(), BorderLayout.EAST);
	}
	public boolean add(NicoCourbeElem elem) {
		boolean changed=false;
		if(null!=getPanel() ) {
			changed=getPanel().add(elem);
		}
		return changed;
	}

	private NicoGrapheConfig getPilote() {
		// TODO Auto-generated method stub
		if (null == nicoPiloteConfig) {
			nicoPiloteConfig = new NicoGrapheConfig(getPanel());
		}
		return nicoPiloteConfig;
	}
	public long nbElems() {
		long retour=-1;
		if(null!=courbe) {
			retour=courbe.size();
		}
		return retour;
	}

	public double getNbX() {
		return getPanel().getNbX();
	}

	public void setNbX(double scaleX) {
		getPanel().setNbX(scaleX);
	}

	public void add(NicoDetElem elem) {
		if(null!=getPanel() ) {
			getPanel().add(elem);
		}
	}
	public GregorianCalendar getDate() {
		GregorianCalendar date=new GregorianCalendar();
		if(null!=courbe) {
			date=courbe.getDate(getPanel().getFrom());
		}
		return date;
	}

	public double getFrom() {
		return getPanel().getFrom();
	}
	public void setFrom(double from) {
		if(null!=getPanel()) {
			if(getPanel().getWidth()!=0) {
				getPanel().setFrom((int)from, Math.max(1.,(getPanel().getNbX()/getPanel().getWidth())));
			}
		}
	}

	public void add(NicoVehElem veh) {
		if(null!=getPanel() ) {
			getPanel().add(veh);
		}
	}

	public long getNbCourbeElem() {
		return getPanel().getNbCourbeElem();
	}
}
