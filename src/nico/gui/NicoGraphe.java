package nico.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import nico.NicoCourbe;
import nico.NicoCourbeElem;
import nico.NicoDet;
import nico.NicoDetElem;
import nico.NicoVeh;
import nico.NicoVehElem;

public class NicoGraphe extends JPanel {
	NicoCourbe courbe = null;
	NicoDet det = null;
	NicoVeh veh = null;
	private GeneralPath laLigne = new GeneralPath();
	private GeneralPath leSeuil = new GeneralPath();
	private GeneralPath lEquilibre = new GeneralPath();
	private double nbY = 1.;
	private double nbX = 1500.;
	private double offsetY = 0;
	private int localFrom = -100;
	private int localMax = Integer.MIN_VALUE;

	public double getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
		repaint();
	}

	public double getNbX() {
		return nbX;
	}

	public void setNbX(double nbX) {
		this.nbX = nbX;
		repaint();
	}

	public NicoCourbe getCourbe() {
		return courbe;
	}

	public NicoGraphe() {
		this(null, null, null);
	}

	public void setCourbe(NicoCourbe courbe) {
		setBorder(new LineBorder(Color.ORANGE, 3));
		this.courbe = courbe;
		repaint();
	}

//	public NicoGraphe(NicoCourbe courbe) {
//		this(courbe, null);
//	}

	public NicoGraphe(NicoCourbe courbe, NicoDet det, NicoVeh veh) {
		setCourbe(courbe);
		setDet(det);
		setVeh(veh);
	}

	public void setVeh(NicoVeh veh) {
		this.veh = veh;
	}

	@Override
	public void paint(Graphics g3) {
		super.paint(g3);
		if (null != courbe) {
			synchronized (this) {
				int height = this.getHeight();
				int width = this.getWidth();
				if ((0 != width) && (0 != height)) {
					Graphics2D g2 = (Graphics2D) g3;
					int delta = courbe.getMax() - courbe.getMin();
					if (delta <= 0) {
						delta = 10;
					}
					double from;
					if (courbe.size() < nbX) {
						from = 0;
					} else {
						if ((getFrom() >= courbe.size()) || (-1 == getFrom())) {
							from = courbe.size() - nbX;
						} else {
							from = Math.min(courbe.size() - nbX, Math.max(0, getFrom()));
						}
					}
					setFrom((int) from, Math.max(1., ((double) nbX / (double) width)));

					AffineTransform at = new AffineTransform();
					at.setToTranslation((double) -from * (double) width / (double) nbX,
							(double) height + getOffsetY() - (getMax()) / getNbY());
					at.scale((double) width / (double) nbX, 1. / getNbY());

					double deltaY = 50 * getNbY();
					double valLog = Math.log10(deltaY);
					int info = (int) Math.pow(10., (int) valLog);
					if ((valLog - Math.floor(valLog)) > 0.7) {
						info *= 5;
					} else if ((valLog - Math.floor(valLog)) > 0.3) {
						info *= 2;
					}

					Point2D pt = new Point2D.Double(0, (double) height);
					Point2D ptDst = new Point2D.Double();
					Point2D ptFinal = new Point2D.Double();
					AffineTransform inverse;
					try {
						inverse = at.createInverse();
						inverse.transform(pt, ptDst);
						ptDst.setLocation(ptDst.getX(), (double) info * Math.floor((int) ptDst.getY() / info));
						at.transform(ptDst, ptFinal);
						{
							TextLayout lay = new TextLayout("" + (int) ptDst.getY(), g2.getFont(),
									g2.getFontRenderContext());
							Point2D point = new Point2D.Double(20., ptFinal.getY() - 2);

							{
								Stroke oldStroke = g2.getStroke();
								if (0 == (ptDst.getY() % 100)) {
									g2.setColor(Color.gray);
									g2.setStroke(new BasicStroke(2));
								} else {
									g2.setColor(Color.lightGray);
									g2.setStroke(new BasicStroke(1));
								}
								lay.draw(g2, (int) point.getX(), (int) point.getY());
								g2.drawLine(0, (int) ptFinal.getY(), width, (int) ptFinal.getY());
								g2.setStroke(oldStroke);
							}
						}

						while (ptFinal.getY() > 0.d) {
							ptDst.setLocation(ptDst.getX(), ptDst.getY() - info);
							at.transform(ptDst, ptFinal);
							TextLayout lay = new TextLayout("" + (65536+(long) ptDst.getY())%65536, g2.getFont(),
									g2.getFontRenderContext());
							Point2D point = new Point2D.Double(20., ptFinal.getY() - 2);
							{
								Stroke oldStroke = g2.getStroke();
								if (0 == (ptDst.getY() % 100)) {
									g2.setColor(Color.gray);
									g2.setStroke(new BasicStroke(2));
								} else {
									g2.setColor(Color.lightGray);
									g2.setStroke(new BasicStroke(1));
								}
								lay.draw(g2, (int) point.getX(), (int) point.getY());
								g2.drawLine(0, (int) ptFinal.getY(), width, (int) ptFinal.getY());
								g2.setStroke(oldStroke);
							}
						}
					} catch (NoninvertibleTransformException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					{
						Stroke oldStroke = g2.getStroke();
						g2.setStroke(new BasicStroke(2));
						FontRenderContext frc = g2.getFontRenderContext();
						Font font = g2.getFont();
						for (int indCour = 0; indCour < getDet().size(); indCour++) {
							NicoDetElem elem = getDet().get(indCour);
							long index = elem.getIndex();
							if ((index >= Math.max(from-getNbX()/2,0.)) && (index <= (from + 1.5*getNbX()))) {
								int difference = 0;
								int silhouette = elem.getSilhouette();
								double tp = elem.getTp();
								double val = elem.getVal();
								/* On est dans la plage... */
								if (15 == (elem.getSilhouette() % 16)) {
									difference = -elem.getCpt() + courbe.get(index).getCpt();
								}
								if (((elem.getSilhouette() / 16) > 1) && ((elem.getSilhouette() / 16) < 15)
										&& (15 == (elem.getSilhouette() % 16))) {
									switch(((elem.getSilhouette() / 16))) {
										case 7:
											g2.setColor(Color.blue.brighter());
											break;
										case 5:
											g2.setColor(Color.red.brighter());
											break;
										case 3:
											g2.setColor(Color.pink.brighter());
											break;
											default : 
											g2.setColor(Color.cyan.brighter());
											break;
												
									}
								} else {
									if(((elem.getSilhouette()/16)==1)&&(15==(elem.getSilhouette()%16))) {
										g2.setColor(Color.green.brighter());
									} else {
										g2.setColor(Color.yellow.brighter());
									}
								}
								Rectangle2D rect = new Rectangle2D.Double(
										(double) index - difference * 3. - ((tp + 5.) / 10.),
										courbe.get(index).getEquilibre() - val, ((tp + 5.) / 10.), val);
								g2.fill(at.createTransformedShape(rect));
								g2.setColor(Color.orange);
								g2.draw(at.createTransformedShape(rect));
								g2.setColor(Color.black);
								TextLayout lay = new TextLayout("" + elem.getNumero() + "(" + (silhouette % 16) + ")."
										+ (0 != (silhouette / 16) ? (silhouette / 16) : ""), font, frc);
								Point2D point = new Point2D.Double(
										(double) index - difference * 3 - (double) ((elem.getTp() + 5.) / 10 + 10),
										courbe.get(index).getEquilibre());
								Point2D ptsuite = new Point2D.Double();
								at.transform(point, ptsuite);
								lay.draw(g2, (int) ptsuite.getX() + 10, (int) ptsuite.getY() - 10);

							}
						}
						g2.setStroke(oldStroke);
					}
					{
						Stroke oldStroke = g2.getStroke();
						g2.setStroke(new BasicStroke(2));
						FontRenderContext frc = g2.getFontRenderContext();
						Font font = g2.getFont().deriveFont(Font.BOLD);
						Color greenLight=Color.green.brighter().brighter();
						for (int indCour = 0; indCour < getVeh().size(); indCour++) {
							NicoVehElem elem = getVeh().get(indCour);
							int index = (int)elem.getIndex();
							if ((index >= from) && (index <= (from + nbX))) {
								double tp = elem.getTp();
								double val = elem.getVal();
								/* On est dans la plage... */
								Rectangle2D rect = new Rectangle2D.Double(
										(double) index - ((tp + 5.) / 10.),
										courbe.get(index).getEquilibre() - val, ((tp + 5.) / 10.), val);
								g2.setColor(greenLight);
								g2.draw(at.createTransformedShape(rect));
								g2.setColor(Color.black);
								TextLayout lay = new TextLayout("" + elem.getVitesse() + "km/h " + elem.getLongueur() +" cm " + elem.getVal(), font, frc);
								Point2D point = new Point2D.Double((double) index  - ((tp + 5.) / 10.), courbe.get(index).getEquilibre()+2*font.getSize());

								Point2D ptsuite = new Point2D.Double();
								at.transform(point, ptsuite);
								lay.draw(g2, (int) ptsuite.getX() + 10, (int) ptsuite.getY() - 10);

							}
						}
						g2.setStroke(oldStroke);
					}

					Shape seuilS = null;
					Shape equilS = null;
					Shape ligneS = null;
					seuilS = at.createTransformedShape(leSeuil);
					equilS = at.createTransformedShape(lEquilibre);
					ligneS = at.createTransformedShape(laLigne);
					{
						Stroke oldStroke = g2.getStroke();
						g2.setColor(Color.gray);
						g2.setStroke(new BasicStroke(3));
						if (null != ligneS) {
							g2.setColor(Color.black);
							g2.draw(ligneS);
						}
						g2.setStroke(new BasicStroke(1));
						if (null != seuilS) {
							g2.setColor(Color.cyan);
							g2.draw(seuilS);
						}
						if (null != equilS) {
							g2.setColor(Color.red);
							g2.draw(equilS);
						}
						g2.setStroke(oldStroke);
					}
				}
			}
		}
	}

	public NicoDet getDet() {
		if (null == det) {
			det = new NicoDet();
		}
		return det;
	}

	public void setDet(NicoDet det) {
		this.det = det;
	}

	private double getMax() {
		return (double) localMax;
	}

	boolean setFrom(int from, double pitch) {
		boolean changed = false;
		if ((0 == from) || (from != localFrom)) {
			changed = true;
			synchronized (this) {
				localFrom = from;
				int first = (int) Math.max(localFrom, 0);
				int last = (int) Math.min(localFrom + getNbX(), courbe.size());
				int max = Integer.MIN_VALUE;
				double indCour = first;
				boolean prem = true;
				localFrom = from;
				laLigne = new GeneralPath();
				leSeuil = new GeneralPath();
				lEquilibre = new GeneralPath();

				try {

					for (indCour = first; indCour < last; indCour += pitch) {
						NicoCourbeElem elem = courbe.get((int) indCour);
						max = Math.max(max, elem.getSeuilNeg());
						if (prem) {
							prem = false;
							lEquilibre.moveTo(indCour, elem.getEquilibre());
							laLigne.moveTo(indCour, elem.getVal());
							leSeuil.moveTo(indCour, elem.getEquilibre() - elem.getSeuil());
						} else {
							lEquilibre.lineTo(indCour, elem.getEquilibre());
							laLigne.lineTo(indCour, elem.getVal());
							leSeuil.lineTo(indCour, elem.getEquilibre() - elem.getSeuil());
						}
					}
				} catch (Exception e) {
					System.out.println("Les infos : " + first + " indCour : " + indCour + " prem : " + prem);
					e.printStackTrace();
				}
				setMax(max);
			}
		}
		return changed;
	}

	private void setMax(int max) {
		localMax = max;
	}

	public double getNbY() {
		return nbY;
	}

	public void setNbY(double nbY) {
		this.nbY = nbY;
		repaint();
	}

	public boolean add(NicoCourbeElem elem) {
		boolean changed = false;
		if (null != getCourbe()) {
			if (courbe.addTrim(elem)) {
				if (det != null) {
					det.trim(courbe.trimSize());
				}
				changed = true;
			}

//			int from=(int)getFrom();
//			int size=courbe.size();
			if ((getFrom() >= (courbe.size() - getNbX() - 1)) || (courbe.size() < getNbX())) {
				if (0 != getWidth()) {
					changed = setFrom((int) Math.max((double) 0, (double) courbe.size() - getNbX()),
							getNbX() / (double) getWidth());
				}
			} else {

			}
		}
		return changed;
	}

	public void add(NicoDetElem elem) {
		if (null != getDet()) {
			addDet(elem);
		}
	}

	public void add(NicoVehElem elem) {
		if (null != getVeh()) {
			addVeh(elem);
		}
	}

	private void addVeh(NicoVehElem elem) {
		getVeh().add(elem);
	}

	private NicoVeh getVeh() {
		if (null == veh) {
			veh = new NicoVeh();
		}
		return veh;
	}

	private void addDet(NicoDetElem elem) {
		getDet().add(elem);
	}

	public double getFrom() {
		// TODO Auto-generated method stub
		return localFrom;
	}

	public long getNbCourbeElem() {
		return getCourbe().size();
	}
}
