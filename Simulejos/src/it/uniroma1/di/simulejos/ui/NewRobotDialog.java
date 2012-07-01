package it.uniroma1.di.simulejos.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import it.uniroma1.di.simulejos.Robot;
import it.uniroma1.di.simulejos.Simulation;
import it.uniroma1.di.simulejos.util.FullReader;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

final class NewRobotDialog extends JDialog {
	private static final long serialVersionUID = -6803948702078460070L;

	private static final JFileChooser scriptChooser = new JFileChooser();
	{
		scriptChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"JavaScript File", ".js"));
		scriptChooser.setAcceptAllFileFilterUsed(true);
	}

	private static final JFileChooser classPathChooser = new JFileChooser();
	{
		classPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	private static final JFileChooser modelChooser = new JFileChooser();
	{
		modelChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"Wavefront File", ".obj"));
		modelChooser.setAcceptAllFileFilterUsed(true);
	}

	private static final class ClassListModel extends
			DefaultComboBoxModel<String> {
		private static final long serialVersionUID = -3255613875270018961L;

		private static String getBinaryName(String classFilePath) {
			String string = classFilePath.replace(File.separatorChar, '.');
			return string.substring(0, string.lastIndexOf(".class"));
		}

		private void detectMainClasses(ClassLoader classLoader, File root,
				String path) {
			for (String file : new File(root, path).list()) {
				final String subPath;
				if (path.isEmpty()) {
					subPath = file;
				} else {
					subPath = path + File.separator + file;
				}
				if (new File(root, subPath).isDirectory()) {
					detectMainClasses(classLoader, root, subPath);
				} else if (subPath.endsWith(".class")) {
					final Class<?> c;
					try {
						c = classLoader.loadClass(getBinaryName(subPath));
					} catch (ClassNotFoundException e) {
						continue;
					}
					final Method mainMethod;
					try {
						mainMethod = c
								.getDeclaredMethod("main", String[].class);
					} catch (NoSuchMethodException e) {
						continue;
					}
					if ((mainMethod.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
						addElement(c.getCanonicalName());
					}
				}
			}
		}

		public void detectMainClasses(File path) {
			final URL url;
			try {
				url = path.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			final URL[] urls = { url };
			final URLClassLoader classLoader = new URLClassLoader(urls);
			detectMainClasses(classLoader, path, "");
		}
	}

	NewRobotDialog(final JFrame owner, final Simulation simulation) {
		super(owner, "Simulejos - Add new robot", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(new JLabel("Name: "), constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(new JLabel(Robot.getNextName()), constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(new JLabel("Script file: "), constraints);

		final JTextField scriptFileField = new JTextField(20);
		scriptFileField.setEditable(false);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(scriptFileField, constraints);

		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
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
		mainPanel.add(new JLabel("Class path: "), constraints);

		final JTextField classPathField = new JTextField(20);
		classPathField.setEditable(false);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(classPathField, constraints);

		final ClassListModel classList = new ClassListModel();
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(new JButton(new AbstractAction("Browse...") {
			private static final long serialVersionUID = 3235759166819332223L;

			@Override
			public void actionPerformed(ActionEvent event) {
				if (classPathChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					classList.removeAllElements();
					final File classPath = classPathChooser.getSelectedFile();
					classList.detectMainClasses(classPath);
					classPathField.setText(classPath.getAbsolutePath());
				}
			}
		}), constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(new JLabel("Main class: "), constraints);

		final JComboBox<String> mainClassField = new JComboBox<String>(
				classList);
		mainClassField.setEditable(true);
		mainClassField.setPrototypeDisplayValue(Simulejos.class
				.getCanonicalName());
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(mainClassField, constraints);

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(new JLabel("Wavefront file: "), constraints);

		final JTextField modelFileField = new JTextField(20);
		modelFileField.setEditable(false);
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(modelFileField, constraints);

		constraints.gridx = 2;
		constraints.gridy = 4;
		constraints.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(new JButton(new AbstractAction("Browse...") {
			private static final long serialVersionUID = 2320102428453353410L;

			@Override
			public void actionPerformed(ActionEvent event) {
				if (modelChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					modelFileField.setText(modelChooser.getSelectedFile()
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
				try {
					final String script = new FullReader(new FileReader(
							scriptChooser.getSelectedFile())).readAll();
					simulation.addRobot(classPathChooser.getSelectedFile(),
							classList.getSelectedItem().toString(), script,
							modelChooser.getSelectedFile());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(owner, e.getMessage(),
							"Simulejos", JOptionPane.ERROR_MESSAGE);
				}
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
