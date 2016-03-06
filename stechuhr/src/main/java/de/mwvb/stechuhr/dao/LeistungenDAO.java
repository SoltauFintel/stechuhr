package de.mwvb.stechuhr.dao;

import java.io.File;

import de.mwvb.base.xml.XMLDocument;
import de.mwvb.base.xml.XMLElement;
import de.mwvb.stechuhr.base.XMLDocumentUTF8;
import de.mwvb.stechuhr.entity.Leistung;

public class LeistungenDAO {
	private static final String VERSION = "1";
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
		XMLDocument dok = XMLDocumentUTF8.load(dateiname);
		if (!VERSION.equals(dok.getElement().getValue("version"))) {
			return null;
		}
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
		XMLDocument dok = new XMLDocumentUTF8("<Leistungen version=\"" + VERSION + "\"/>");
		XMLElement root = dok.getElement();
		Leistung p = first;
		while (p != null) {
			XMLElement e = root.add("Leistung");
			e.setValue("leistung", p.getLeistung());
			for (String i : p.getTickets()) {
				e.add("Ticket", i);
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
