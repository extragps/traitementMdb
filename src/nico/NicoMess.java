package nico;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NicoMess {
	int mess;
	int nbMax;
	int nbCour;
	int []info=null;
	boolean erreur=false;
	ByteBuffer bb=null;
	public static NicoMess fabrique(byte car) {
		NicoMess infoCour=null;
		switch (car) {
		case 'U':
			infoCour = new NicoMess(car, 16);
			break;
		case 'L':
			infoCour = new NicoMess(car, 12);
			break;
		case 'K':
			infoCour = new NicoMess(car, 12);
			break;
		case 'X':
			infoCour = new NicoMess(car, 24);
			break;
		case 'Y':
			infoCour = new NicoMess(car, 24);
			break;
		case 'W':
			infoCour = new NicoMess(car, 2);
			break;
		case 'Z':
			infoCour = new NicoMess(car, 2);
			break;
		case 'F':
			break;
		default:
			System.out.println("Il y a un probleme: " + car);
			break;
		}
		return infoCour;
	}
	public NicoMess(byte car, int nb) {
		mess=car;
		nbCour=0;
		nbMax=nb;
		info=new int[nb];
		bb=ByteBuffer.allocate(nb+1);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(car);
	}
	public boolean ajouter(byte car) {
		if(nbCour<nbMax)  {
		info[nbCour++]=(256+car)%256;
		bb.put(car);
		}
		return nbCour>=nbMax;
	}
	public int getCode() {
		// TODO Auto-generated method stub
		return (int)mess;
	}
	public int getChar(int i) {
		return (((int)info[i])+256)%256;
	}
	public short getInfo(int i) {
		return (short)(info[i*2]+256*info[i*2+1]);
	}
	public short getShort(int i) {
		return (short)(info[i]*256+info[i+1]);
	}
	public long getLong(int i) {
		return (long)(info[i+3] +256*(info[i+2]+ +256*(info[i+1]+ +256*info[i])));
	}
	public void write(FileOutputStream fos2) throws IOException {
		fos2.write(bb.array());
	}
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	public String getHex() {
		// TODO Auto-generated method stub
		return ""+info[1]+" h "+bytesToHex(bb.array());
	}
}
