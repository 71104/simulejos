package it.uniroma1.di.simulejos.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import it.uniroma1.di.simulejos.Robot;
import it.uniroma1.di.simulejos.Simulation;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class NewRobotDialog extends JDialog {
	private static final long serialVersionUID = -6803948702078460070L;

	private static final JFileChooser modelChooser = new JFileChooser(); // TODO
	private static final JFileChooser scriptChooser = new JFileChooser(); // TODO
	private static final JFileChooser classPathChooser = new JFileChooser(); // TODO

	NewRobotDialog(final JFrame owner, Simulation simulation) {
		super(owner, "Simulejos - Add new robot", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(new JLabel("Name: " + Robot.getNextName()), constraints);
		constraints.anchor = GridBagConstraints.CENTER;

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(new JLabel("Script file:"), constraints);
		constraints.anchor = GridBagConstraints.CENTER;

		final JTextField scriptFileField = new JTextField(20);
		constraints.gridx = 1;
		constraints.gridy = 1;
		mainPanel.add(scriptFileField, constraints);

		constraints.gridx = 2;
		constraints.gridy = 1;
		mainPanel.add(new JButton(new AbstractAction("Browse...") {
			private static final long serialVersionUID = -904623933009783192L;

			@Override
			public void actionPerformed(ActionEvent event) {
				if (scriptChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					scriptFileField.setText(scriptChooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		}), constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(new JLabel("Class path:"), constraints);
		constraints.anchor = GridBagConstraints.CENTER;

		final JTextField classPathField = new JTextField(20);
		constraints.gridx = 1;
		constraints.gridy = 2;
		mainPanel.add(classPathField, constraints);

		constraints.gridx = 2;
		constraints.gridy = 2;
		mainPanel.add(new JButton(new AbstractAction("Browse...") {
			private static final long serialVersionUID = 3235759166819332223L;

			@Override
			public void actionPerformed(ActionEvent event) {
				if (classPathChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					classPathField.setText(classPathChooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		}), constraints);

		add(mainPanel, BorderLayout.CENTER);

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
