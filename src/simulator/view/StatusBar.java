package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class StatusBar extends JPanel implements SimulatorObserver {

	private static final long serialVersionUID = 1L;

	private JLabel groupsValue;
	private JLabel timeValue;

	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this); // se registra como observador
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));

		// Etiqueta del tiempo actual
		timeValue = new JLabel("Time: 0.0");
		timeValue.setPreferredSize(new Dimension(180, 20));
		this.add(timeValue);

		JSeparator s1 = new JSeparator(JSeparator.VERTICAL);
		s1.setPreferredSize(new Dimension(10, 20));
		this.add(s1);

		// Etiqueta del número de grupos
		groupsValue = new JLabel("Groups: 0");
		groupsValue.setPreferredSize(new Dimension(80, 20));
		this.add(groupsValue);

		JSeparator s2 = new JSeparator(JSeparator.VERTICAL);
		s2.setPreferredSize(new Dimension(10, 20));
		this.add(s2);
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
		this.timeValue.setText("Time: " + time);
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		this.groupsValue.setText("Groups: " + groups.size());
		this.timeValue.setText("Time: " + time);
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		this.groupsValue.setText("Groups: " + groups.size());
		this.timeValue.setText("Time: " + time);
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		this.groupsValue.setText("Groups: " + groups.size());
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// el cambio de delta-time no modifica el tiempo actual
	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {
	}
}
