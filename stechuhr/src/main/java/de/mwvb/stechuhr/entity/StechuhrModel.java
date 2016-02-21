package de.mwvb.stechuhr.entity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.stechuhr.dao.StechuhrDAO;

/**
 * Tagesdatum und alle Stechuhr-Einträge
 */
public class StechuhrModel {
	private final LocalDate tag;
	private final List<Stunden> stundenliste = new ArrayList<Stunden>();
	
	public StechuhrModel(LocalDate tag) {
		this.tag = tag;
	}

	public LocalDate getTag() {
		return tag;
	}

	public String getTagString() {
		return tag.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
	}

	public String getWTTagString() {
		DateTimeFormatter f = DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy");
		return tag.format(f);
	}

	public List<Stunden> getStundenliste() {
		return stundenliste;
	}

	public void calculateDauer() {
		stundenliste.sort((a, b) -> a.getUhrzeit().compareTo(b.getUhrzeit()));
		int n = stundenliste.size();
		for (int i = 0, j = 1; i < n; i++, j++) {
			Stunden a = stundenliste.get(i);
			Stunden b = j < n ? stundenliste.get(j) : new Stunden("now");

			Duration d = Duration.between(a.getUhrzeit(), b.getUhrzeit());
			long s = 0;
			long m = d.toMinutes();
			if (m >= 0) {
				while (m >= 60) {
					s++;
					m -= 60;
				}
				a.setDauer(s + ":" + zweistellig(m));
			} else {
				a.setDauer("");
			}
		}
	}
	
	public static String zweistellig(long i) {
		return i < 10 ? "0" + i : "" + i;
	}

	public void stop() {
		List<Exportstunden> export = createExportstunden();
		optimieren(export);
		for (Exportstunden x : export) {
			System.out.println(x);
		}
		new StechuhrDAO().saveExport(export);
	}

	private List<Exportstunden> createExportstunden() {
		List<Exportstunden> export = new ArrayList<>();
		int n = stundenliste.size() - 1;
		for (int i = 0, j = 1; i < n; i++, j++) {
			Stunden a = stundenliste.get(i);
			Stunden b = stundenliste.get(j);

			Duration d = Duration.between(a.getUhrzeit(), b.getUhrzeit());
			long s = 0;
			long m = d.toMinutes();
			while (m >= 60) {
				s++;
				m -= 60;
			}

			Exportstunden x = new Exportstunden();
			x.setTag(tag);
			x.setLeistung(a.getLeistung());
			x.setTicket(a.getTicket());
			x.setStunden((int) s);
			x.setMinuten((int) m);
			export.add(x);
		}
		return export;
	}

	private void optimieren(List<Exportstunden> export) {
		boolean nochmal;
		do {
			nochmal = false;
			for (int i = 0; i < export.size(); i++) {
				Exportstunden a = export.get(i);
				for (int j = 0; j < i; j++) {
					Exportstunden b = export.get(j);
					if (a.getLeistung().equals(b.getLeistung()) && a.getTicket().equals(b.getTicket()) && a.getTag().equals(b.getTag())) {
						// a und b zusammenfassen
						a.inc(b);
						export.remove(j);
						nochmal = true;
						break;
					}
				}
				if (nochmal) {
					break;
				}
			}
		} while (nochmal);
		for (int i = export.size() - 1; i >= 0; i--) {
			if (export.get(i).getTicket().equals(Stunden.STOP)) {
				export.remove(i);
			}
		}
	}
}
