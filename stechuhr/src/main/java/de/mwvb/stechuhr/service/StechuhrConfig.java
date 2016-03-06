package de.mwvb.stechuhr.service;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.gui.StageAdapter;

/**
 * Laden, Speichern und ggf. Vorhalten von:
 * - Fensterpositionen
 * - Quick Button Labels
 * - old ticket list
 */
public class StechuhrConfig {
	private String quickButtonLabels[] = new String[10];
	private final List<String> oldTickets;

	public StechuhrConfig() {
		for (int i = 0; i < 10; i++) {
			quickButtonLabels[i] = "";
		}
		List<String> m = new StechuhrDAO().load("Quick-Buttons");
		for (int i = 0; i < m.size() && i < 10; i++) {
			quickButtonLabels[i] = m.get(i);
		}
		
		oldTickets = new StechuhrDAO().load("old tickets");
	}

	public String[] getQuickButtonLabels() {
		return quickButtonLabels;
	}

	public List<String> getOldTickets() {
		return oldTickets;
	}
	
	public void saveOldTickets() {
		new StechuhrDAO().save("old tickets", oldTickets);
	}

	public void saveQuickButtonLabels() {
		List<String> m = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			m.add(quickButtonLabels[i]);
		}
		new StechuhrDAO().save("Quick-Buttons", m);
	}

	public void saveWindowPosition(String name, StageAdapter stage) {
		if (stage.isIconified()) { // x+y sind versaut
			return;
		}
		String data = stage.getX() + ";" + stage.getY();
		if (stage.isResizable()) {
			data += ";" + stage.getWidth() + ";" + stage.getHeight(); 
		}
		List<String> arr = new ArrayList<>();
		arr.add(data);
		new StechuhrDAO().save("WindowPosition-" + name, arr);
	}

	public void loadWindowPosition(String name, StageAdapter stage) {
		List<String> data = new StechuhrDAO().load("WindowPosition-" + name);
		if (data.size() > 0 && data.get(0).contains(";")) {
			String w[] = data.get(0).split(";");
			stage.setX(Double.parseDouble(w[0]));
			stage.setY(Double.parseDouble(w[1]));
			if (stage.isResizable()) {
				stage.setWidth(Double.parseDouble(w[2]));
				stage.setHeight(Double.parseDouble(w[3]));
			}
		}
	}
}
