package it.uniroma1.di.simulejos.ui;

import it.uniroma1.di.simulejos.Simulation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLJPanel;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public final class Simulejos extends JFrame {
	private static final long serialVersionUID = 1344391485057572344L;

	private final JSplitPane splitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT);
	private volatile GLJPanel canvas = new Canvas();
	private final LogWindow logWindow = new LogWindow();
	private volatile Simulation simulation = new Simulation(this, canvas,
			logWindow.getWriter());

	private boolean discard() {
		if (simulation.isDirty()
				&& (JOptionPane.showConfirmDialog(this,
						"Do you want to discard the current simulation?",
						"Simulejos", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)) {
			return false;
		}
		simulation.stop();
		logWindow.setText("");
		return true;
	}

	private boolean reset() {
		if (discard()) {
			final GLAutoDrawable oldCanvas = canvas;
			canvas = new Canvas();
			splitPane.setLeftComponent(canvas);
			oldCanvas.destroy();
			simulation = new Simulation(this, canvas, logWindow.getWriter());
			canvas.repaint();
			return true;
		} else {
			return false;
		}
	}

	private static abstract class MyAction extends AbstractAction {
		private static final long serialVersionUID = -2624871633451306156L;

		private static Icon loadIcon(String name) {
			try {
				return new ImageIcon(ImageIO.read(Simulejos.class
						.getResourceAsStream(name + ".png")));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public MyAction(String label, String iconName) {
			super(label, loadIcon(iconName));
		}
	}

	public final Action RESET_ACTION = new MyAction("Reset", "reset") {
		private static final long serialVersionUID = -4726361137806256305L;

		@Override
		public void actionPerformed(ActionEvent e) {
			reset();
		}
	};
	public final Action EXIT_ACTION = new AbstractAction("Exit") {
		private static final long serialVersionUID = -1289143153929543605L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (reset()) {
				dispose();
			}
		}
	};
	public final Action SETTINGS_ACTION = new MyAction("Settings...",
			"settings") {
		private static final long serialVersionUID = 7589371832890775093L;

		@Override
		public void actionPerformed(ActionEvent event) {
			new SettingsDialog(Simulejos.this, simulation);
			canvas.repaint();
		}
	};
	public final Action ADD_ROBOT_ACTION = new MyAction("Add robot...", "add") {
		private static final long serialVersionUID = 5318430767695567625L;

		@Override
		public void actionPerformed(ActionEvent event) {
			new NewRobotDialog(Simulejos.this, simulation);
			canvas.repaint();
		}
	};
	public final Action PLAY_ACTION = new MyAction("Play", "play") {
		private static final long serialVersionUID = 5318430767695567625L;

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				simulation.play();
			} catch (ScriptException e) {
				JOptionPane.showMessageDialog(Simulejos.this, e.getMessage(),
						"Simulejos", JOptionPane.ERROR_MESSAGE);
			}
		}
	};
	public final Action SUSPEND_ACTION = new MyAction("Suspend", "suspend") {
		private static final long serialVersionUID = 2050007264701571826L;

		@Override
		public void actionPerformed(ActionEvent event) {
			simulation.suspend();
		}
	};
	public final Action STOP_ACTION = new MyAction("Stop", "stop") {
		private static final long serialVersionUID = 6375156354908574128L;

		@Override
		public void actionPerformed(ActionEvent event) {
			simulation.stop();
		}
	};

	private Simulejos() {
		super("Simulejos");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		final JMenu fileMenu = new JMenu("File");
		fileMenu.add(RESET_ACTION);
		fileMenu.addSeparator();
		fileMenu.add(EXIT_ACTION);
		menuBar.add(fileMenu);
		final JMenu simulationMenu = new JMenu("Simulation");
		simulationMenu.add(SETTINGS_ACTION);
		simulationMenu.add(ADD_ROBOT_ACTION);
		simulationMenu.addSeparator();
		simulationMenu.add(PLAY_ACTION);
		simulationMenu.add(SUSPEND_ACTION);
		simulationMenu.add(STOP_ACTION);
		menuBar.add(simulationMenu);
		splitPane.setLeftComponent(canvas);
		splitPane.setRightComponent(new JScrollPane(logWindow));
		splitPane.setResizeWeight(1);
		add(splitPane, BorderLayout.CENTER);
		final JToolBar toolbar = new JToolBar("Simulejos", JToolBar.HORIZONTAL);
		toolbar.add(RESET_ACTION);
		toolbar.add(SETTINGS_ACTION);
		toolbar.addSeparator();
		toolbar.add(ADD_ROBOT_ACTION);
		toolbar.addSeparator();
		toolbar.add(PLAY_ACTION);
		toolbar.add(SUSPEND_ACTION);
		toolbar.add(STOP_ACTION);
		add(toolbar, BorderLayout.NORTH);
		pack();
		if (Preferences.userNodeForPackage(Simulejos.class).getBoolean(
				"maximizedWindow", false)) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent event) {
				if (event.getNewState() != JFrame.MAXIMIZED_BOTH) {
					Preferences.userNodeForPackage(Simulejos.class).putBoolean(
							"maximizedWindow", false);
				} else {
					Preferences.userNodeForPackage(Simulejos.class).putBoolean(
							"maximizedWindow", true);
				}
			}
		});
		setLocationByPlatform(true);
		setVisible(true);
	}

	public static class CommandLineException extends RuntimeException {
		private static final long serialVersionUID = 2806285902459793394L;

		private CommandLineException() {
			super("Invalid command line");
		}
	}

	public static void main(String[] arguments) {
		if (arguments.length > 1) {
			throw new CommandLineException();
		}
		if (arguments.length > 0) {
			if (arguments[0].equals("--debug")) {
				Simulation.setDebugMode(true);
			} else {
				throw new CommandLineException();
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Simulejos();
			}
		});
	}
}
