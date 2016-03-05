package de.mwvb.stechuhr.service;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.mwvb.stechuhr.dao.LeistungenDAO;
import de.mwvb.stechuhr.entity.Leistung;
import de.mwvb.stechuhr.entity.Stunden;

/**
 * Leistungen verwalten
 * 
 * @author Marcus Warm
 */
public class Leistungen implements Iterable<String> {
	/** Liste begrenzt - auch damit nicht mehr verwendete Einträge verschwinden */
	public static final int MAX_ANZAHL = 99;
	private final LeistungenDAO dao;
	private Leistung first;

	private Leistungen(LeistungenDAO dao) {
		this.dao = dao;
		this.first = this.dao.load();
	}
	
	/**
	 * Datei laden und Leistungen-Objekt liefern.
	 */
	public static Leistungen open(String dateiname) {
		return new Leistungen(new LeistungenDAO(dateiname));
	}

	/**
	 * Datei löschen
	 * @param dateiname
	 */
	public static void delete(String dateiname) {
		new LeistungenDAO(dateiname).delete();
	}

	/**
	 * Vorher isEmpty() aufrufen!
	 * @return erste Leistung
	 */
	public String getFirst() {
		if (first == null) {
			throw new NoSuchElementException();
		}
		return first.getLeistung();
	}

	public boolean isEmpty() {
		return first == null;
	}
	
	public int size() {
		int size = 0;
		Leistung p = first;
		while (p != null) {
			size++;
			
			p = p.next();
		}
		return size;
	}

	@Override
	public Iterator<String> iterator() {
		return new LeistungenIterator(first);
	}

	public void add(String ticket, String leistung) {
		if (ticket == null || ticket.trim().isEmpty()) {
			throw new IllegalArgumentException("Argument ticket darf nicht null oder leer sein!");
		}
		if (Stunden.PAUSE.equals(ticket) || Stunden.STOP.equals(ticket)) {
			// Leistung spielt für diese beiden Sondertickets keine Rolle und wird daher nicht gespeichert.
			return;
		}
		if (leistung == null || leistung.trim().isEmpty()) {
			return;
		}

		findeGleichenEintrag(ticket, leistung);
		
		begrenzeAnzahl();
		dao.save(first);
	}

	private void findeGleichenEintrag(String ticket, String leistung) {
		Leistung gef = null;
		Leistung vorgaenger = null;
		Leistung p = first;
		while (p != null) {
			if (p.getLeistung().equalsIgnoreCase(leistung)) {
				gef = p;
				break;
			}
			vorgaenger = p;
			
			p = p.next();
		}
		if (gef == null) { // Es gibt diese Leistung noch nicht.
			first = new Leistung(ticket, leistung, first);
		} else {
			gef.getTickets().add(ticket);
			if (vorgaenger != null) {
				vorgaenger.setNext(p.next());
			}
			if (first != gef) {
				// alten Eintrag nach vorne stellen
				p.setNext(first);
				first = p;
			}
		}
	}

	private void begrenzeAnzahl() {
		int zaehler = 0;
		Leistung p = first;
		while (p != null) {
			if (++zaehler == MAX_ANZAHL) {
				p.setNext(null);
			}
			
			p = p.next();
		}
	}

	public String getLeistungForTicket(String ticket) {
		Leistung p = first;
		while (p != null) {
			if (p.contains(ticket)) {
				return p.getLeistung();
			}
			
			p = p.next();
		}
		return "";
	}
}
