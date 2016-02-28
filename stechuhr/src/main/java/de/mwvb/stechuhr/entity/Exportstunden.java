package de.mwvb.stechuhr.entity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Fertiger Stundendatensatz für Export
 */
public class Exportstunden {
	private LocalDate tag;
	private int stunden;
	private int minuten;
	private String ticket;
	private String leistung;

	public LocalDate getTag() {
		return tag;
	}

	public String getTagString() {
		return formatDate(tag);
	}
	
	public static String formatDate(LocalDate datum) {
		return datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
	}

	public void setTag(LocalDate tag) {
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
	
	@Override
	public String toString() {
		return getStunden() + ":" + StechuhrModel.zweistellig(getMinuten()) + " = " + getDezimaldauer() + "\t" + getTicket() + "\t" + getLeistung();
	}

	public void inc(Exportstunden b) {
		stunden += b.stunden;
		minuten += b.minuten;
		while (minuten >= 60) {
			stunden++;
			minuten -= 60;
		}
	}

	public String getDezimaldauer() {
		double m = minuten / 60d + stunden;
		DecimalFormatSymbols sy = new DecimalFormatSymbols(Locale.GERMAN);
		return new DecimalFormat("0.00", sy).format(m);
	}
	
	public String getSSMM() {
		return stunden + ":" + StechuhrModel.zweistellig(minuten);
	}
	
	public String toFileString() {
		return getTagString() + ";" + getSSMM() + "; " + getDezimaldauer() + " ;" + getTicket() + ";" + getLeistung();
	}
}
