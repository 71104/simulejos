package it.uniroma1.di.simulejos.ui;

import it.uniroma1.di.simulejos.Simulation;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.media.opengl.awt.GLJPanel;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public final class Simulejos extends JFrame {
	private static final long serialVersionUID = 1344391485057572344L;

	private final LogWindow logWindow = new LogWindow();
	private volatile Simulation simulation = new Simulation(this,
			logWindow.getWriter());
	private volatile File file = null;

	private final JFileChooser fileChooser = new JFileChooser();

	private boolean reset() {
		if (simulation.isDirty()) {
			// TODO
			return false;
		}
		simulation = new Simulation(this, logWindow.getWriter());
		return true;
	}

	private void save() {
		try {
			new ObjectOutputStream(new FileOutputStream(file))
					.writeObject(simulation);
		} catch (IOException e) {
			// TODO show error message
			return;
		}
		simulation.clearDirty();
	}

	private void saveAs() {
		// TODO
	}

	public final Action NEW_ACTION = new AbstractAction("New") {
		private static final long serialVersionUID = -4726361137806256305L;

		@Override
		public void actionPerformed(ActionEvent e) {
			reset();
		}
	};
	public final Action LOAD_ACTION = new AbstractAction("Load...") {
		private static final long serialVersionUID = 5108135153655697921L;

		@Override
		public void actionPerformed(ActionEvent e) {
			reset();
		}
	};
	public final Action SAVE_ACTION = new AbstractAction("Save") {
		private static final long serialVersionUID = -1829243020102401543L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (file != null) {
				saveAs();
			} else {
				save();
			}
		}
	};
	public final Action SAVE_AS_ACTION = new AbstractAction("Save as...") {
		private static final long serialVersionUID = -2297922078695549898L;

		@Override
		public void actionPerformed(ActionEvent e) {
			saveAs();
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
	public final Action ADD_ROBOT_ACTION = new AbstractAction("Add robot...") {
		private static final long serialVersionUID = 5318430767695567625L;

		@Override
		public void actionPerformed(ActionEvent event) {
			new NewRobotDialog(Simulejos.this, simulation);
		}
	};
	public final Action PLAY_ACTION = new AbstractAction("Play") {
		private static final long serialVersionUID = 5318430767695567625L;

		@Override
		public void actionPerformed(ActionEvent event) {
			simulation.play();
		}
	};
	public final Action SUSPEND_ACTION = new AbstractAction("Suspend") {
		private static final long serialVersionUID = 2050007264701571826L;

		@Override
		public void actionPerformed(ActionEvent event) {
			simulation.suspend();
		}
	};
	public final Action STOP_ACTION = new AbstractAction("Stop") {
		private static final long serialVersionUID = 6375156354908574128L;

		@Override
		public void actionPerformed(ActionEvent event) {
			simulation.stop();
		}
	};

	private Simulejos() {
		super("Simulejos");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		final JMenu fileMenu = new JMenu("File");
		fileMenu.add(NEW_ACTION);
		fileMenu.add(LOAD_ACTION);
		fileMenu.add(SAVE_ACTION);
		fileMenu.add(SAVE_AS_ACTION);
		fileMenu.addSeparator();
		fileMenu.add(EXIT_ACTION);
		menuBar.add(fileMenu);
		final JMenu simulationMenu = new JMenu("Simulation");
		simulationMenu.add(ADD_ROBOT_ACTION);
		simulationMenu.addSeparator();
		simulationMenu.add(PLAY_ACTION);
		simulationMenu.add(SUSPEND_ACTION);
		simulationMenu.add(STOP_ACTION);
		menuBar.add(simulationMenu);
		add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new GLJPanel(), logWindow));
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	public static void main(String[] arguments) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Simulejos();
			}
		});
	}
}
