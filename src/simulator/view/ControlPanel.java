package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class ControlPanel extends JPanel implements SimulatorObserver {

	private static final long serialVersionUID = 1L;

	private Controller _ctrl;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton; // Botón de cerrar el programa
	private JButton _file; // Botón de seleccionar archivos
	private JButton _forceLaws; // Botón de cambiar la ley de fuerza
	private JButton _simulation; // Botón de abrir el visor de la simulación
	private JButton _advance; // Botón de run
	private JButton _stop; // Botón de parar la simulación
	private JSpinner st; // Número de pasos de simulación
	private JTextField dt; // Delta-time
	private ForceLawsDialog f; // Diálogo para seleccionar una nueva ley de fuerza

	public ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		_toolaBar.setFloatable(false);
		this.add(_toolaBar, BorderLayout.PAGE_START);

		_file = new JButton();
		_forceLaws = new JButton();
		_simulation = new JButton();
		_advance = new JButton();
		_stop = new JButton();

		// Botón de seleccionar ficheros
		_file.setToolTipText("Load bodies file into the editor");
		_file.setIcon(new ImageIcon("resources/icons/open.png"));
		_toolaBar.add(_file);
		_toolaBar.addSeparator();

		// Botón de cambiar las leyes de fuerza
		_forceLaws.setToolTipText("Change force laws");
		_forceLaws.setIcon(new ImageIcon("resources/icons/physics.png"));
		_toolaBar.add(_forceLaws);

		// Botón de ver la simulación
		_simulation.setToolTipText("View the simulation of the groups");
		_simulation.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_toolaBar.add(_simulation);
		_toolaBar.addSeparator();

		// Botón de ejecutar la simulación
		_advance.setToolTipText("Run the simulation");
		_advance.setIcon(new ImageIcon("resources/icons/run.png"));
		_toolaBar.add(_advance);

		// Botón de parar la simulación
		_stop.setToolTipText("Stop the simulation");
		_stop.setIcon(new ImageIcon("resources/icons/stop.png"));
		_toolaBar.add(_stop);

		// Steps (JSpinner) y Delta-time (JTextField)
		st = new JSpinner(new SpinnerNumberModel(10000, 1, 1000000, 100));
		st.setToolTipText("Simulation steps to run: 1-1000000");
		st.setPreferredSize(new Dimension(80, 40));
		st.setMaximumSize(new Dimension(80, 40));
		st.setMinimumSize(new Dimension(80, 40));

		dt = new JTextField();
		dt.setToolTipText("Real time (seconds) corresponding to a step");
		dt.setPreferredSize(new Dimension(80, 40));
		dt.setMaximumSize(new Dimension(80, 40));
		dt.setMinimumSize(new Dimension(80, 40));

		_toolaBar.add(new JLabel(" Steps: "));
		_toolaBar.add(st);
		_toolaBar.add(new JLabel(" Delta-time: "));
		_toolaBar.add(dt);

		// Quit Button
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_toolaBar.add(_quitButton);
		_quitButton.addActionListener((e) -> Utils.quit(this));

		// Selector de ficheros
		_fc = new JFileChooser(new File(System.getProperty("user.dir"), "resources/examples/input"));
		_fc.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));

		// Listeners de los diferentes botones
		_file.addActionListener((e) -> loadFile());

		_forceLaws.addActionListener((e) -> {
			if (f == null) {
				f = new ForceLawsDialog(Utils.getWindow(ControlPanel.this), _ctrl);
			}
			f.open();
		});

		_simulation.addActionListener((e) -> new ViewerWindow((JFrame) Utils.getWindow(ControlPanel.this), _ctrl));

		_advance.addActionListener((e) -> {
			try {
				_ctrl.setDeltaTime(Double.parseDouble(dt.getText().trim()));
			} catch (NumberFormatException ex) {
				Utils.showErrorMsg(this, "Invalid delta-time: '" + dt.getText() + "'");
				return;
			} catch (IllegalArgumentException ex) {
				Utils.showErrorMsg(this, ex.getMessage());
				return;
			}
			_stopped = false;
			setButtonsEnabled(false);
			run_sim((Integer) st.getValue());
		});

		_stop.addActionListener((e) -> _stopped = true);
	}

	private void loadFile() {
		int option = _fc.showOpenDialog(Utils.getWindow(this));
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = _fc.getSelectedFile();
			try (InputStream in = new FileInputStream(file)) {
				_ctrl.reset();
				_ctrl.loadData(in);
			} catch (Exception ex) {
				Utils.showErrorMsg(this, "Could not load '" + file.getName() + "': " + ex.getMessage());
			}
		}
	}

	private void setButtonsEnabled(boolean enabled) {
		_file.setEnabled(enabled);
		_forceLaws.setEnabled(enabled);
		_simulation.setEnabled(enabled);
		_advance.setEnabled(enabled);
		_quitButton.setEnabled(enabled);
	}

	private void run_sim(int n) {
		if (n > 0 && !_stopped) {
			try {
				_ctrl.run(1);
			} catch (Exception e) {
				Utils.showErrorMsg(this, e.getMessage());
				setButtonsEnabled(true);
				_stopped = true;
				return;
			}
			SwingUtilities.invokeLater(() -> run_sim(n - 1));
		} else {
			_stopped = true;
			setButtonsEnabled(true);
		}
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		this.dt.setText(String.valueOf(dt));
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		this.dt.setText(String.valueOf(dt));
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		this.dt.setText(String.valueOf(dt));
	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {
	}
}
