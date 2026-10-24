package simulator.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class GroupsTableModel extends AbstractTableModel implements SimulatorObserver {
	String[] _header = { "Id", "Force Laws", "Bodies" };
	List<BodiesGroup> _groups;
	GroupsTableModel(Controller ctrl) {
		_groups = new ArrayList<>();
		ctrl.addObserver(this);
	}
	
	@Override
	public String getColumnName(int col) {
		return _header[col];
	}	
	
	@Override
	public int getRowCount() {
		return this._groups.size();
	}
	@Override
	public int getColumnCount() {
		return this._header.length;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;
		switch (columnIndex) {
		case 0:
			s = this._groups.get(rowIndex).getId();
			break;
		case 1:
			s = this._groups.get(rowIndex).getForceLawsInfo();
			break;
		case 2:
			s="";
			Iterator<Body> it= this._groups.get(rowIndex).iterator();
			while(it.hasNext()) {
				s+=it.next().getId()+" ";
			}
			break;
		}
		return s;
	}
	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
	}
	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_groups.clear();
		fireTableDataChanged();
	}
	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		_groups.addAll(groups.values());
		fireTableDataChanged();
	
	}
	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		_groups.add(g);
		fireTableDataChanged();
	
	}
	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
		// cambia la columna "Bodies" del grupo correspondiente
		fireTableDataChanged();
	}
	@Override
	public void onDeltaTimeChanged(double dt) {
	
	}
	@Override
	public void onForceLawsChanged(BodiesGroup g) {
		fireTableDataChanged();
		
	}
}