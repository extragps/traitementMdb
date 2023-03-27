package nico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class NicoCourbe {
	List<NicoCourbeElem> liste=new ArrayList<NicoCourbeElem>();
	int posCour=0;
	int min=Short.MAX_VALUE; 
	int max=Short.MIN_VALUE;
	int numero=-1;
	private GregorianCalendar cal=new GregorianCalendar();
	private int trimSize=60000;
	public NicoCourbe() {
		this(-1,new GregorianCalendar());
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
	public NicoCourbe(int num,GregorianCalendar cal) {
		this.numero=num;
		this.cal=cal;
	}
	public boolean addTrim(NicoCourbeElem elem) {
		boolean trimmed=false;
		if(liste.size()>(trimSize*2)) {
			liste=liste.subList(trimSize,liste.size());
			cal.add(Calendar.MILLISECOND, trimSize*10);
			trimmed=true;
		}
		liste.add(elem);
		min=Math.min(min, elem.getVal());
		max=Math.max(max, elem.getSeuilNeg());
		return trimmed;
	}
	public void add(NicoCourbeElem elem) {
		liste.add(elem);
		min=Math.min(min, elem.getVal());
		max=Math.max(max, elem.getSeuilNeg());
	}
	public long size() {
		return liste.size();
	}
	public NicoCourbeElem get(long i) {
		return liste.get((int)i);
	}
	public GregorianCalendar getDate(double from) {
		GregorianCalendar gcCalendar=(GregorianCalendar)cal.clone();
		gcCalendar.add(Calendar.MILLISECOND, 10*(int)from);
		return gcCalendar;
	}
	public int trimSize() {
		return trimSize;
	}
}
