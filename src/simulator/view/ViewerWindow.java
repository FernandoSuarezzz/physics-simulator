package simulator.view;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class ViewerWindow extends JFrame implements SimulatorObserver {

	private static final long serialVersionUID = 1L;

	private Controller _ctrl;
	private SimulationViewer _viewer;
	private JFrame _parent;

	ViewerWindow(JFrame parent, Controller ctrl) {
		super("Simulation Viewer");
		_ctrl = ctrl;
		_parent = parent;
		intiGUI();
		_ctrl.addObserver(this);
	}

	private void intiGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		// El visor va en el centro, dentro de un JScrollPane
		_viewer = new Viewer();
		mainPanel.add(new JScrollPane(_viewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		// Al cerrar la ventana deja de observar el simulador
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				_ctrl.removeObserver(ViewerWindow.this);
			}
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
		// Colocar la ventana centrada sobre la ventana principal
		setLocationRelativeTo(_parent);
		setVisible(true);
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
		_viewer.update();
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_viewer.reset();
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		// addGroup ya añade al visor los cuerpos del grupo
		for (BodiesGroup g : groups.values()) {
			_viewer.addGroup(g);
		}
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		_viewer.addGroup(g);
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
		_viewer.addBody(b);
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {
	}
}
