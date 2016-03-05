package de.mwvb.stechuhr.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity für Leistungen/LeistungenDAO.
 */
public class Leistung {
	private final String leistung;
	private final Set<String> tickets = new HashSet<>();
	private Leistung next;

	/**
	 * @param ticket Ticketnummer, darf nicht leer oder null sein
	 * @param leistung Leistungstext
	 * @param next Nachfolge-Element, darf null sein
	 */
	public Leistung(String ticket, String leistung, Leistung next) {
		this.leistung = leistung;
		this.next = next;
		tickets.add(ticket);
	}

	/**
	 * Konstruktor für Ladeoperation
	 */
	public Leistung(String leistung) {
		this.leistung = leistung;
	}

	/**
	 * @return Leistungstext
	 */
	public String getLeistung() {
		return leistung;
	}

	/**
	 * @return alle Ticketnummern, die diesen Leistungstext verwenden
	 */
	public Set<String> getTickets() {
		return tickets;
	}
	
	/**
	 * @param x Ticketnummer <p>Groß/Kleinschreibung ist egal.
	 * @return true wenn x in getTickets() enthalten ist.
	 */
	public boolean contains(String x) {
		// TODO Geht das einfacher zu implementieren?
		for (String i : tickets) {
			if (i.equalsIgnoreCase(x)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return nächstes Element in der vorwärts verketteten Liste
	 */
	public Leistung next() {
		return next;
	}
	
	/**
	 * @param next Nachfolger-Element
	 */
	public void setNext(Leistung next) {
		this.next = next;
	}
}
