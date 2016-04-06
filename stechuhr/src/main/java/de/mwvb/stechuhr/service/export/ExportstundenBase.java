package de.mwvb.stechuhr.service.export;

import java.io.Serializable;

/**
 * Gemeinsame Stundenklasse mit Server. Diese Klasse soll von der Stechuhr nicht direkt verwendet werden, sondern Exportstunden.
 */
public class ExportstundenBase implements Serializable {
	private String tag;
	protected int stunden;
	protected int minuten;
	private String ticket;
	private String leistung;

	public ExportstundenBase() {
	}

	/** Kopierkonstruktor */
	public ExportstundenBase(ExportstundenBase s) {
		setLeistung(s.getLeistung());
		setMinuten(s.getMinuten());
		setStunden(s.getStunden());
		setTag(s.getTag());
		setTicket(s.getTicket());
	}

	public String getTag() {
		return tag;
	}

	/**
	 * @param tag Format "tt.mm.jjjj"
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getStunden() {
		return stunden;
	}

	public void setStunden(int stunden) {
		this.stunden = stunden;
	}

	public int getMinuten() {
		return minuten;
	}

	public void setMinuten(int minuten) {
		this.minuten = minuten;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getLeistung() {
		return leistung;
	}

	public void setLeistung(String leistung) {
		this.leistung = leistung;
	}
}
