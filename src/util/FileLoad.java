package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileLoad {
	public static File[] listFilesSorted(File repertoire,final ArrayList<String> extensions) {
		java.io.FileFilter filtre=new java.io.FileFilter() {
			@Override
			public boolean accept(File f) {
				boolean retour = f.isDirectory();
				if (!retour) {
					for (String extension : extensions) {
						retour = f.getName().endsWith(extension);
						if (retour) {
							break;
						}
					}
				}
				return retour;
			}};
		return repertoire.listFiles(filtre);
	}
	public static File getFile(String appliName, final ArrayList<String> extensions, final String description) {
		File fichier = null;
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		File defaultRep = new File("." + appliName);
		if (defaultRep.exists()) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(defaultRep));
				Object obj = ois.readObject();
				ois.close();
				if (obj instanceof File) {
					fc = new JFileChooser((File) obj);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public boolean accept(File f) {
				boolean retour = f.isDirectory();
				if (!retour) {
					for (String extension : extensions) {
						retour = f.getName().endsWith(extension);
						if (retour) {
							break;
						}
					}
				}
				return retour;
			}
		});
		fc.showOpenDialog(null);
		if (null != fc.getSelectedFile()) {
			fichier = fc.getSelectedFile();
			if (null != fichier) {
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(new FileOutputStream(defaultRep));
					oos.writeObject(fichier.getParentFile());
					oos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return fichier;
	}

	public static File getFile(String appliName, javax.swing.filechooser.FileFilter fileFilter) {
		File fichier = null;
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		File defaultRep = new File("." + appliName);
		if (defaultRep.exists()) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(defaultRep));
				Object obj = ois.readObject();
				ois.close();
				if (obj instanceof File) {
					fc = new JFileChooser((File) obj);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fc.setFileFilter(fileFilter);
		fc.showOpenDialog(null);
		if (null != fc.getSelectedFile()) {
			fichier = fc.getSelectedFile();
			if (null != fichier) {
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(new FileOutputStream(defaultRep));
					oos.writeObject(fichier.getParentFile());
					oos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return fichier;
	}

	public static File getDir(String appliName) {
		return getDir(appliName,"Repertoire contenant les CAM");
	}
	public static File getDir(String appliName, final String description) {
		javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory();
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return description;
			}
		};
		return getDir(appliName, fileFilter);
	}

	public static File getDir(String appliName, FileFilter fileFilter) {
		File fichier = null;
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File defaultRep = new File("." + appliName);
		if (defaultRep.exists()) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(defaultRep));
				Object obj = ois.readObject();
				ois.close();
				if (obj instanceof File) {
					fc = new JFileChooser((File) obj);
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fc.setFileFilter(fileFilter);
		fc.showOpenDialog(null);
		if (null != fc.getSelectedFile()) {
			fichier = fc.getSelectedFile();
			if (null != fichier) {
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(new FileOutputStream(defaultRep));
					oos.writeObject(fichier.getParentFile());
					oos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return fichier;
	}

}
