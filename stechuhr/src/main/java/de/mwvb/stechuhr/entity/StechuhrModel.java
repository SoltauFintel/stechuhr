package de.mwvb.stechuhr.entity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.service.Leistungen;
import de.mwvb.stechuhr.service.export.ExportManager;
import de.mwvb.stechuhr.service.export.Exportstunden;
import de.mwvb.stechuhr.service.stundenrundung.Stundenrundung;

/**
 * Tagesdatum und alle Stechuhr-Eintr�ge
 * 
 * @author Marcus Warm
 */
public class StechuhrModel { // TODO Fachlogik herausl�sen!
	private final LocalDate tag;
	private final List<Stunden> stundenliste = new ArrayList<Stunden>();
	private final Leistungen leistungen;
	
	public StechuhrModel(LocalDate tag) {
		this.tag = tag;
		leistungen = Leistungen.open("Leistungen.xml");
	}

	public LocalDate getTag() {
		return tag;
	}

	public String getTagString() {
		return StechuhrUtils.formatDate(tag);
	}

	public String getWTTagString() {
		return StechuhrUtils.formatWTDate(tag);
	}

	public List<Stunden> getStundenliste() {
		return stundenliste;
	}
	
	/**
	 * @return aktuelle Ticketnummer oder null wenn Stundenliste leer ist
	 */
	public String getCurrentTicket() {
		return stundenliste.isEmpty() ? null : stundenliste.get(stundenliste.size() - 1).getTicket();
	}

	/**
	 * @return vorige Ticketnummer oder null wenn Stundenliste h�chstens einen Eintrag hat
	 */
	public String getPreviousTicket() {
		return stundenliste.size() <= 1 ? null : stundenliste.get(stundenliste.size() - 2).getTicket();
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
			String dauer = "";
			if (m >= 0) {
				while (m >= 60) {
					s++;
					m -= 60;
				}
				dauer = s + ":" + StechuhrUtils.zweistellig(m);
			}
			a.setDauer(dauer);
		}
	}
	
	public void stop(LocalTime stopUhrzeit) {
		Stunden s = new Stunden(stopUhrzeit);
		s.setTicket(Stunden.STOP);
		stundenliste.add(s);
		stop();
	}
	
	// TODO Aus den stop() Methoden eine eigene Fachlogik Klasse machen! ExportstundenTest muss dann entsprechend umbenannt werden.
	// TODO Stechuhr-Datei nach STOP umbenennen? Wenn Anwender erneut Stechuhr starten w�rde, w�rde er mit nackter Datei anfangen.
	public void stop() {
		List<Exportstunden> export = createExportstunden();
		optimieren(export);
		runde(export);
		ExportManager.getInstance().export(export);
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
			x.setTagDate(tag);
			x.setLeistung(a.getLeistung());
			x.setTicket(a.getTicket());
			x.setStunden((int) s);
			x.setMinuten((int) m);
			export.add(x);
		}
		return export;
	}

	// TODO Verschachtelung zu tief
	private void optimieren(List<Exportstunden> export) {
		boolean nochmal;
		do {
			nochmal = false;
			int n = export.size();
			for (int i = 0; i < n; i++) {
				Exportstunden a = export.get(i);
				for (int j = i + 1; j < n; j++) {
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
			// TODO 0:00 Stunden l�schen (+Testcase)
			if (export.get(i).getTicket().equals(Stunden.STOP)) {
				export.remove(i); // TODO F�r diese Codezeile einen Testcase schreiben!
			}
		}
	}

	private void runde(List<Exportstunden> export) {
		Stundenrundung r = new Stundenrundung();
		for (Exportstunden stunden : export) {
			r.add(new Dauer(stunden.getSSMM()));
		}
		
		r.ausgleichung(15);
		
		final List<Dauer> gerundet = r.getDauerListe();
		for (int i = 0; i < export.size(); i++) {
			Exportstunden stunden = export.get(i);
			Dauer dauer = gerundet.get(i);
			stunden.setStunden(dauer.getStundenTeil());
			stunden.setMinuten(dauer.getMinutenTeil());
		}
	}

	/**
	 * Zentrale Stelle, die das Tagesdatum liefert.
	 */
	public static LocalDate today() {
		return LocalDate.now();
	}
	
	/**
	 * Zentrale Stelle, die die aktuelle Uhrzeit liefert.
	 */
	public static LocalTime now() {
		return LocalTime.now().withSecond(0).withNano(0);
	}
	
	public Leistungen getLeistungen() {
		return leistungen;
	}

	public Dauer getDauerOhnePausen(LocalTime now) {
		int minuten = 0;
		int n = stundenliste.size() - 1;
		if (n < 0) {
			return null;
		}
		for (int i = 0; i < n; i++) {
			if (stundenliste.get(i).isNotPause()) {
				LocalTime bis = stundenliste.get(i + 1).getUhrzeit();
				if (now.isBefore(bis)) {
					minuten += Duration.between(stundenliste.get(i).getUhrzeit(), now).toMinutes();
					return new Dauer(minuten);
				}
				minuten += Duration.between(stundenliste.get(i).getUhrzeit(), bis).toMinutes();
			}
		}
		if (stundenliste.get(n).isNotPause()) {
			Duration d = Duration.between(stundenliste.get(n).getUhrzeit(), now);
			minuten += d.toMinutes();
		}
		return new Dauer(minuten);
	}

	public LocalTime getFeierabendUhrzeit() {
		if (stundenliste.isEmpty()) {
			return null;
		}
		int pausenzeit = 0;
		for (int i = 0; i < stundenliste.size() - 1; i++) {
			if (stundenliste.get(i).isPause()) {
				pausenzeit += Duration.between(stundenliste.get(i).getUhrzeit(), stundenliste.get(i + 1).getUhrzeit()).toMinutes();
			}
		}
		return stundenliste.get(0).getUhrzeit().plus(8, ChronoUnit.HOURS).plus(pausenzeit, ChronoUnit.MINUTES); 
	}
}
