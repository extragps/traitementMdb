package nico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class NicoVeh {
	List<NicoVehElem> liste = new ArrayList<>();
	int posCour = 0;
	int min = Short.MAX_VALUE;
	int max = Short.MIN_VALUE;
	int numero = -1;
	
	private GregorianCalendar cal = new GregorianCalendar();

	public NicoVeh() {
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

	public NicoVeh(int num, GregorianCalendar cal) {
		this.numero = num;
		this.cal = cal;
	}

	public void add(NicoVehElem elem) {
		liste.add(elem);
	}

	public int size() {
		// TODO Auto-generated method stub
		return liste.size();
	}

	public NicoVehElem get(int i) {
		// TODO Auto-generated method stub
		return liste.get(i);
	}

	public GregorianCalendar getDate(double from) {
		GregorianCalendar gcCalendar = (GregorianCalendar) cal.clone();
		gcCalendar.add(Calendar.MILLISECOND, 10 * (int) from);
		return gcCalendar;
	}


}
