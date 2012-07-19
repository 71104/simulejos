package it.uniroma1.di.simulejos.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import it.uniroma1.di.simulejos.Simulation;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

final class SettingsDialog extends JDialog {
	private static final long serialVersionUID = -8041853049696400841L;

	private static final JFileChooser floorTextureChooser = new JFileChooser();
	{
		// TODO
		loadLastImageDirectory();
	}

	private static void loadLastImageDirectory() {
		final String path = Preferences
				.userNodeForPackage(SettingsDialog.class).get(
						"lastImageDirectory", null);
		if (path != null) {
			floorTextureChooser.setCurrentDirectory(new File(path));
		}
	}

	private static void storeLastImageDirectory() {
		Preferences.userNodeForPackage(NewRobotDialog.class).put(
				"lastImageDirectory",
				floorTextureChooser.getCurrentDirectory().getAbsolutePath());
	}

	private volatile BufferedImage floorTexture;

	public Image getFloorTexture() {
		return floorTexture;
	}

	public SettingsDialog(final Frame owner, final Simulation simulation) {
		super(owner, "Simulejos - Settings", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());

		final JPanel mainPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.NORTHEAST;
		mainPanel.add(new JLabel("Floor texture:"), constraints);
		constraints.anchor = GridBagConstraints.CENTER;

		final JPanel overviewPanel = new JPanel() {
			private static final long serialVersionUID = -950529863381304233L;

			{
				setPreferredSize(new Dimension(200, 200));
			}

			@Override
			public void paint(Graphics g) {
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(0, 0, 200, 200);
				if (floorTexture != null) {
					g.drawImage(floorTexture, 0, 0, null);
				}
			}
		};
		final JPanel overviewContainer = new JPanel(new BorderLayout());
		overviewContainer.setBorder(new BevelBorder(BevelBorder.LOWERED));
		overviewContainer.add(overviewPanel, BorderLayout.CENTER);
		constraints.gridx = 1;
		constraints.gridy = 0;
		mainPanel.add(overviewContainer, constraints);

		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(new JButton(new AbstractAction("Browse...") {
			private static final long serialVersionUID = 8753217697250928446L;

			@Override
			public void actionPerformed(ActionEvent event) {
				if (floorTextureChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					storeLastImageDirectory();
					try {
						floorTexture = ImageIO.read(floorTextureChooser
								.getSelectedFile());
						overviewPanel.repaint();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(owner, e.getMessage(),
								"Simulejos", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}), constraints);
		constraints.anchor = GridBagConstraints.CENTER;

		final JCheckBox repeatXField = new JCheckBox("Repeat X");
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.WEST;
		mainPanel.add(repeatXField, constraints);
		constraints.anchor = GridBagConstraints.CENTER;

		final JCheckBox repeatYField = new JCheckBox("Repeat Y");
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.WEST;
		mainPanel.add(repeatYField, constraints);
		constraints.anchor = GridBagConstraints.CENTER;

		add(mainPanel, BorderLayout.CENTER);

		final JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		lowerPanel.add(new JButton(new AbstractAction("OK") {
			private static final long serialVersionUID = -7301830459342807788L;

			@Override
			public void actionPerformed(ActionEvent event) {
				simulation.floor.configure(floorTexture,
						repeatXField.isSelected(), repeatYField.isSelected());
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
