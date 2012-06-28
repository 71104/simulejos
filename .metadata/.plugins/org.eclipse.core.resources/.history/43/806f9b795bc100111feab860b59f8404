package it.uniroma1.di.simulejos.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import it.uniroma1.di.simulejos.Simulation;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

final class NewRobotDialog extends JDialog {
	private static final long serialVersionUID = -6803948702078460070L;

	NewRobotDialog(JFrame owner, Simulation simulation) {
		super(owner, "Simulejos - Add new robot", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		final JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lowerPanel.add(new JButton(new AbstractAction("OK") {
			private static final long serialVersionUID = 6317203848141758068L;

			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO add robot
				dispose();
			}
		}));
		lowerPanel.add(new JButton(new AbstractAction("Cancel") {
			private static final long serialVersionUID = 2038577777364795947L;

			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		}));
		add(lowerPanel, BorderLayout.SOUTH);
		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}
