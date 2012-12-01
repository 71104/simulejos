package it.uniroma1.di.simulejos.ui;

import it.uniroma1.di.simulejos.Robot;
import it.uniroma1.di.simulejos.Simulation;
import it.uniroma1.di.simulejos.math.Vector3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public final class Simulejos extends JFrame {
	private static final long serialVersionUID = 1344391485057572344L;

	private static abstract class CursorState {
		public void mousePressed(int x, int y) {
		}

		public void mouseMoved(int x, int y) {
		}

		public void mouseReleased(int x, int y) {
		}

		public void mouseWheel(double count) {
		}
	}

	public final CursorState navigateCursorState = new CursorState() {
		private volatile boolean pressed;
		private volatile int x0;
		private volatile int y0;

		@Override
		public void mousePressed(int x, int y) {
			pressed = true;
			x0 = x;
			y0 = y;
		}

		@Override
		public void mouseMoved(int x, int y) {
			if (pressed) {
				simulation.camera.rotate(Math.toRadians(x - x0) / 4,
						Math.toRadians(y - y0) / 4);
				x0 = x;
				y0 = y;
			}
		}

		@Override
		public void mouseReleased(int x, int y) {
			pressed = false;
		}

		@Override
		public void mouseWheel(double count) {
			simulation.camera.move(0, -count);
		}
	};

	public final CursorState moveRobotCursorState = new CursorState() {
		@Override
		public void mousePressed(int x, int y) {
			// TODO
		}

		@Override
		public void mouseMoved(int x, int y) {
			// TODO
		}

		@Override
		public void mouseReleased(int x, int y) {
			// TODO
		}
	};

	public final CursorState deleteRobotCursorState = new CursorState() {
		private volatile Robot selectedRobot;
		private volatile int selectedIndex;

		@Override
		public void mouseMoved(int x, int y) {
			simulation.picker.new PickRequest(x, y) {
				@Override
				public void handle(int index, Vector3 position) {
					selectedIndex = index;
					for (Robot robot : simulation.robots) {
						robot.hilited = (robot.index == selectedIndex);
						selectedRobot = robot;
						break;
					}
					canvas.repaint();
				}
			};
		}

		@Override
		public void mouseReleased(int x, int y) {
			if ((selectedRobot != null)
					&& (JOptionPane.showConfirmDialog(Simulejos.this,
							"Do you actually want to delete NXT"
									+ selectedIndex + "?", "Simulejos",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION)) {
				simulation.removeRobot(selectedRobot);
				selectedRobot = null;
			}
		}
	};

	private volatile CursorState cursorState = navigateCursorState;

	private final MouseAdapter mouseHandler = new MouseAdapter() {
		@Override
		public void mouseMoved(MouseEvent event) {
			cursorState.mouseMoved(event.getX(), event.getY());
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			cursorState.mouseMoved(event.getX(), event.getY());
		}

		@Override
		public void mousePressed(MouseEvent event) {
			cursorState.mousePressed(event.getX(), event.getY());
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			cursorState.mouseReleased(event.getX(), event.getY());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			cursorState.mouseWheel(event.getPreciseWheelRotation());
		}
	};

	private final JSplitPane splitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT);
	private volatile GLJPanel canvas = new Canvas(mouseHandler);
	private final LogWindow logWindow = new LogWindow();
	private volatile Simulation simulation = new Simulation(this, canvas,
			logWindow.getWriter());

	private boolean canDiscard() {
		return !simulation.isDirty()
				|| (JOptionPane.showConfirmDialog(this,
						"Do you want to discard the current simulation?",
						"Simulejos", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION);
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
			if (canDiscard()) {
				simulation.stop();
				final GLAutoDrawable oldCanvas = canvas;
				canvas = new Canvas(mouseHandler);
				simulation = new Simulation(Simulejos.this, canvas,
						logWindow.getWriter());
				splitPane.setLeftComponent(canvas);
				oldCanvas.destroy();
				logWindow.setText("");
				canvas.repaint();
			}
		}
	};
	public final Action EXIT_ACTION = new AbstractAction("Exit") {
		private static final long serialVersionUID = -1289143153929543605L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (canDiscard()) {
				simulation.stop();
				canvas.destroy();
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
		}
	};
	public final Action NAVIGATE_ACTION = new MyAction("Navigate world",
			"navigate") {
		private static final long serialVersionUID = 2357493963267102486L;

		{
			putValue(SELECTED_KEY, true);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			cursorState = navigateCursorState;
		}
	};
	public final Action MOVE_ACTION = new MyAction("Move robot", "move") {
		private static final long serialVersionUID = 2357493963267102486L;

		@Override
		public void actionPerformed(ActionEvent event) {
			cursorState = moveRobotCursorState;
		}
	};
	public final Action DELETE_ACTION = new MyAction("Delete robot...",
			"delete") {
		private static final long serialVersionUID = 2357493963267102486L;

		@Override
		public void actionPerformed(ActionEvent event) {
			cursorState = deleteRobotCursorState;
		}
	};
	public final Action PLAY_ACTION = new MyAction("Play", "play") {
		private static final long serialVersionUID = 5318430767695567625L;

		@Override
		public void actionPerformed(ActionEvent event) {
			NAVIGATE_ACTION.setEnabled(false);
			MOVE_ACTION.setEnabled(false);
			DELETE_ACTION.setEnabled(false);
			NAVIGATE_ACTION.putValue(SELECTED_KEY, true);
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
			NAVIGATE_ACTION.setEnabled(true);
			MOVE_ACTION.setEnabled(true);
			DELETE_ACTION.setEnabled(true);
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
		final ButtonGroup cursorModeButtons = new ButtonGroup();
		final JToggleButton navigateButton = new JToggleButton(NAVIGATE_ACTION);
		navigateButton.setHideActionText(true);
		cursorModeButtons.add(navigateButton);
		final JToggleButton moveButton = new JToggleButton(MOVE_ACTION);
		moveButton.setHideActionText(true);
		cursorModeButtons.add(moveButton);
		final JToggleButton deleteButton = new JToggleButton(DELETE_ACTION);
		deleteButton.setHideActionText(true);
		cursorModeButtons.add(deleteButton);
		toolbar.add(navigateButton);
		toolbar.add(moveButton);
		toolbar.add(deleteButton);
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
