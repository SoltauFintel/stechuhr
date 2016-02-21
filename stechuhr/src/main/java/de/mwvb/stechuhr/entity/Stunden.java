package de.mwvb.stechuhr.entity;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Stechuhr-Eintrag
 */
public class Stunden {
	public static final String PAUSE = "PAUSE";
	public static final String STOP = "STOP";
	private LocalDate tag = LocalDate.now();
	private LocalTime uhrzeit;
	private String ticket;
	private String leistung = "";
	private String notizPrivat = "";
	private String dauer = "";
	
	public Stunden() {
		uhrzeit = LocalTime.now().withSecond(0).withNano(0);
	}

	public Stunden(LocalTime uhrzeit) {
		this.uhrzeit = uhrzeit;
	}

	public Stunden(String ticket) {
		this();
		this.ticket = ticket;
	}

	public LocalDate getTag() {
		return tag;
	}

	public void setTag(LocalDate tag) {
		this.tag = tag;
	}

	public LocalTime getUhrzeit() {
		return uhrzeit;
	}

	public void setUhrzeit(LocalTime uhrzeit) {
		this.uhrzeit = uhrzeit;
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

	public String getNotizPrivat() {
		return notizPrivat;
	}

	public void setNotizPrivat(String notizPrivat) {
		this.notizPrivat = notizPrivat;
	}
	
	public String getUhrzeitString() {
		return uhrzeit.toString();
	}

	public String getDauer() {
		return dauer;
	}

	public void setDauer(String dauer) {
		this.dauer = dauer;
	}
}
