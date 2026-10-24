package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class ForceLawsDialog extends JDialog implements SimulatorObserver {

	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> _lawsModel;
	private DefaultComboBoxModel<String> _groupsModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _forceLawsInfo;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _selectedLawsIndex;

	ForceLawsDialog(Frame parent, Controller ctrl) {
		super(parent, true);
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Force Laws Selection");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		// Help
		JLabel help = new JLabel("<html><p>Select a force law and "
				+ "provide values for the parameters in the <b>Value column</b> (default values "
				+ "are used for parameters with no value).</p></html>");
		help.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(help);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		_forceLawsInfo = _ctrl.getForceLawsInfo();

		// Tabla de parámetros (solo la columna Value es editable)
		_dataTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		_dataTableModel.setColumnIdentifiers(_headers);
		JTable table = new JTable(_dataTableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(400);
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
		mainPanel.add(tablePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Combo boxes de leyes y grupos
		_lawsModel = new DefaultComboBoxModel<>();
		_groupsModel = new DefaultComboBoxModel<>();
		for (JSONObject info : _forceLawsInfo) {
			_lawsModel.addElement(info.getString("desc"));
		}
		JComboBox<String> lawsCombo = new JComboBox<>(_lawsModel);
		JComboBox<String> groupsCombo = new JComboBox<>(_groupsModel);

		JPanel combos = new JPanel();
		combos.add(new JLabel("Force Law: "));
		combos.add(lawsCombo);
		combos.add(new JLabel("  Group: "));
		combos.add(groupsCombo);
		combos.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(combos);

		lawsCombo.addActionListener((e) -> {
			_selectedLawsIndex = lawsCombo.getSelectedIndex();
			updateTableModel(_selectedLawsIndex);
		});
		_selectedLawsIndex = lawsCombo.getSelectedIndex();
		updateTableModel(_selectedLawsIndex);

		// Botones
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener((e) -> setVisible(false));
		JButton ok = new JButton("OK");
		ok.addActionListener((e) -> {
			if (changeForceLaws(groupsCombo)) {
				setVisible(false);
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(cancel);
		buttons.add(ok);
		buttons.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(buttons);

		setPreferredSize(new Dimension(700, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public void open() {
		if (_groupsModel.getSize() == 0) {
			Utils.showErrorMsg(getParent(), "There are no groups loaded. Load a bodies file first.");
			return;
		}
		// Abrir el diálogo centrado sobre la ventana principal
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	private void updateTableModel(int index) {
		_dataTableModel.setRowCount(0);
		if (index < 0)
			return;
		JSONObject info = _forceLawsInfo.get(index);
		JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject();
		for (String key : data.keySet()) {
			_dataTableModel.addRow(new Object[] { key, "", data.get(key).toString() });
		}
	}

	// Construye el JSON con los valores introducidos en la columna Value.
	// Cada valor se interpreta como JSON (número, array, ...) y, si no lo es,
	// como cadena de texto.
	private JSONObject getData() {
		JSONObject data = new JSONObject();
		for (int i = 0; i < _dataTableModel.getRowCount(); i++) {
			Object key = _dataTableModel.getValueAt(i, 0);
			Object value = _dataTableModel.getValueAt(i, 1);
			if (key == null || value == null)
				continue;
			String v = value.toString().trim();
			if (v.isEmpty())
				continue;
			try {
				data.put(key.toString(), new JSONTokener(v).nextValue());
			} catch (JSONException e) {
				data.put(key.toString(), v);
			}
		}
		return data;
	}

	private boolean changeForceLaws(JComboBox<String> groupsCombo) {
		Object group = groupsCombo.getSelectedItem();
		if (group == null) {
			Utils.showErrorMsg(this, "Select a group first.");
			return false;
		}
		JSONObject fl = new JSONObject();
		fl.put("type", _forceLawsInfo.get(_selectedLawsIndex).getString("type"));
		fl.put("data", getData());
		try {
			_ctrl.setForceLaws(group.toString(), fl);
		} catch (Exception e) {
			Utils.showErrorMsg(this, "Invalid force law parameters: " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_groupsModel.removeAllElements();
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		_groupsModel.removeAllElements();
		for (String id : groups.keySet()) {
			_groupsModel.addElement(id);
		}
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		_groupsModel.addElement(g.getId());
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {
	}
}
