package it.uniroma1.di.simulejos.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import it.uniroma1.di.simulejos.Simulation;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

final class SettingsDialog extends JDialog {
	private static final long serialVersionUID = -8041853049696400841L;

	public SettingsDialog(final Frame owner, final Simulation simulation) {
		super(owner, "Simulejos - Settings", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());
		final JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lowerPanel.add(new JButton(new AbstractAction("OK") {
			private static final long serialVersionUID = -7301830459342807788L;

			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		}));
		lowerPanel.add(new JButton(new AbstractAction("Cancel") {
			private static final long serialVersionUID = 8753217697250928446L;

			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		}));
		add(lowerPanel, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}
