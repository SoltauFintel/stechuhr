package de.mwvb.stechuhr.dao;

import java.io.File;

import de.mwvb.base.xml.XMLDocument;
import de.mwvb.base.xml.XMLElement;
import de.mwvb.stechuhr.entity.Leistung;

public class LeistungenDAO {
	private final String dateiname;
	
	public LeistungenDAO(String dateiname) {
		this.dateiname = StechuhrDAO.getPfad() + "/" + dateiname;
	}

	/**
	 * @return erste Leistung der Datei, null wenn Datei leer ist
	 */
	public Leistung load() {
		if (!new File(dateiname).exists()) {
			return null;
		}
		XMLDocument dok = XMLDocument.load(dateiname);
		Leistung first = null;
		Leistung vorgaenger = null;
		for (XMLElement e : dok.getChildren()) {
			Leistung neu = new Leistung(e.getValue("leistung"));
			if (first == null) {
				first = neu;
			}
			for (XMLElement e_t : e.getChildren()) {
				neu.getTickets().add(e_t.getText());
			}
			if (vorgaenger != null) {
				vorgaenger.setNext(neu);
			}
			vorgaenger = neu;
		}
		return first;
	}
	
	public void save(final Leistung first) {
		XMLDocument dok = new XMLDocument("<leistungen/>");
		XMLElement root = dok.getElement();
		Leistung p = first;
		while (p != null) {
			XMLElement e = root.add("leistung");
			e.setValue("leistung", p.getLeistung());
			for (String i : p.getTickets()) {
				e.add("ticket", i);
			}
			
			p = p.next();
		}
		dok.saveFile(dateiname);
	}
	
	public void delete() {
		File f = new File(dateiname);
		if (f.exists()) {
			f.delete();
		}
	}
}
