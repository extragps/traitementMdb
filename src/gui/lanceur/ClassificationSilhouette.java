package gui.lanceur;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.Color;

public class ClassificationSilhouette extends JFrame {
	
	public ClassificationSilhouette() {
		setTitle("ClassificationSilhouette");
		setPreferredSize(new Dimension(800,500));
		getContentPane().setLayout(new GridLayout(4,3));
		JButton button = new JButton("");
		button.setIcon(new ImageIcon(ClassificationSilhouette.class.getResource("/images/K2.png")));
		getContentPane().add(button);
		JButton button_1 = new JButton("");
		button_1.setBackground(Color.GREEN);
		button_1.setIcon(new ImageIcon(ClassificationSilhouette.class.getResource("/images/K3.png")));
		getContentPane().add(button_1);
		getContentPane().add(new JButton("K4"));
		getContentPane().add(new JButton("K7"));
		getContentPane().add(new JButton("K10"));
		getContentPane().add(new JButton("K6"));
		getContentPane().add(new JButton("K8"));
		getContentPane().add(new JButton("K5"));
		getContentPane().add(new JButton("K9"));
		JButton button_2 = new JButton("");
		button_2.setIcon(new ImageIcon(ClassificationSilhouette.class.getResource("/images/K11.png")));
		getContentPane().add(button_2);
		getContentPane().add(new JButton("K12"));
		JButton button_3 = new JButton("");
		button_3.setIcon(new ImageIcon(ClassificationSilhouette.class.getResource("/images/K13.png")));
		getContentPane().add(button_3);

	}

	public static void main(String[] args) {
		ClassificationSilhouette silhouette=new ClassificationSilhouette();
		
		silhouette.pack();
		silhouette.setVisible(true);
	}

}
