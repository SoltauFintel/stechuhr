package de.mwvb.stechuhr.entity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.service.Leistungen;
import de.mwvb.stechuhr.service.export.ExportManager;
import de.mwvb.stechuhr.service.export.Exportstunden;
import de.mwvb.stechuhr.service.stundenrundung.Stundenrundung;

/**
 * Tagesdatum und alle Stechuhr-Einträge
 * 
 * @author Marcus Warm
 */
public class StechuhrModel { // TODO Fachlogik herauslösen!
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
	// TODO Stechuhr-Datei nach STOP umbenennen? Wenn Anwender erneut Stechuhr starten würde, würde er mit nackter Datei anfangen.
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
			x.setTag(tag);
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
			// TODO 0:00 Stunden löschen (+Testcase)
			if (export.get(i).getTicket().equals(Stunden.STOP)) {
				export.remove(i); // TODO Für diese Codezeile einen Testcase schreiben!
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
}
