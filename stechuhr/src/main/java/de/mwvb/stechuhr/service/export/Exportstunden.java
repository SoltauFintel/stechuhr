package de.mwvb.stechuhr.service.export;

import java.time.LocalDate;

import de.mwvb.stechuhr.base.StechuhrUtils;

/**
 * Fertiger Stundendatensatz für Export
 */
public class Exportstunden extends ExportstundenBase {

	public LocalDate getTagDate() {
		return StechuhrUtils.toDate(getTag());
	}
	
	public void setTagDate(LocalDate date) {
		setTag(StechuhrUtils.formatDate(date));
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
		return StechuhrUtils.getDezimalstunden(stunden, minuten);
	}
	
	public String getSSMM() {
		return stunden + ":" + StechuhrUtils.zweistellig(minuten);
	}
	
	@Override
	public String toString() {
		return getStunden() + ":" + StechuhrUtils.zweistellig(getMinuten()) + " = " + getDezimaldauer() + "\t" + getTicket() + "\t" + getLeistung();
	}
}
