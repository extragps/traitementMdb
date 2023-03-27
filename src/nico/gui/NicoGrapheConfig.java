package nico.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class NicoGrapheConfig extends JPanel {

	private NicoGraphe graphe=null;
	public NicoGrapheConfig() {
		this(null);
	}
	public NicoGrapheConfig(NicoGraphe panel) {
		setGraphe(panel);
	}
	public NicoGraphe getGraphe() {
		return graphe;
	}
	public void setGraphe(NicoGraphe graphe) {
		this.graphe = graphe;
		setLayout(new GridLayout(4, 1, 0, 0));
		
		JButton btnNewButton_1 = new JButton();
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getGraphe().setOffsetY(getGraphe().getOffsetY()+50);
			}
		});
		btnNewButton_1.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/up.png")));
		btnNewButton_1.setToolTipText("Voir un peu plus haut");
		add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton();
		btnNewButton_2.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/grand.png")));
		btnNewButton_2.setToolTipText("Zoom plus");
		add(btnNewButton_2);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getGraphe().setNbY(getGraphe().getNbY()/2.0);
			}
		});
		
		
		JButton btnNewButton_3 = new JButton("");
		btnNewButton_3.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/petit.png")));
		btnNewButton_3.setToolTipText("Zoom moins");
		add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getGraphe().setNbY(getGraphe().getNbY()*2.0);
			}
		});
		
		JButton btnNewButton = new JButton();
		btnNewButton.setIcon(new ImageIcon(NicoGrapheConfig.class.getResource("/gui/images/down.png")));
		btnNewButton_1.setToolTipText("Voir un peu plus bas");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getGraphe().setOffsetY(getGraphe().getOffsetY()-50);
			}
		});
		add(btnNewButton);
		
	}

}
