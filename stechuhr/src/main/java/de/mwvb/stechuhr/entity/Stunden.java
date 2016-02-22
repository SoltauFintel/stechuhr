package de.mwvb.stechuhr.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Stechuhr-Eintrag
 */
public class Stunden {
	public static final String PAUSE = "PAUSE";
	public static final String STOP = "STOP";
	private LocalDate tag = StechuhrModel.today();
	private LocalTime uhrzeit;
	private String ticket;
	private String leistung = "";
	private String notizPrivat = "";
	private String dauer = "";
	
	public Stunden() {
		uhrzeit = StechuhrModel.now();
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

	/**
	 * @param date darf nicht null sein
	 * @return Wochentag TT.MM.JJJJ
	 */
	public static String formatWTDate(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy"));
	}
}
