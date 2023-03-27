package nico;

public interface NicoElemEater {
	public long add(int courbe,NicoCourbeElem elem) ;
	public void add(int courbe,NicoVehElem elem);
	public void add(int courbe,NicoDetElem elem);
	public void setChanged(boolean b);
	
}
