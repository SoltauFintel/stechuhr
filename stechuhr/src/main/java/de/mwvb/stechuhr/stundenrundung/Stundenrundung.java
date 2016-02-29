package de.mwvb.stechuhr.stundenrundung;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Warm
 */
public class Stundenrundung {
	private final List<Dauer> dauerListe = new ArrayList<>();
	
	public Stundenrundung(String ... dauer) {
		for (String i : dauer) {
			dauerListe.add(new Dauer(i));
		}
	}

	public List<Dauer> getDauerListe() {
		return dauerListe;
	}
	
	public Dauer getSumme() {
		return getSumme(dauerListe);
	}
	
	private Dauer getSumme(List<? extends Dauer> list) {
		Dauer summe = Dauer.ZERO;
		for (Dauer i : list) {
			summe = summe.add(i);
		}
		return summe;
	}

	public void ausgleichung(int rundung) {
		GerundeteDauer sollSumme = GerundeteDauer.runde(getSumme(dauerListe), rundung);
		List<GerundeteDauer> neueListe = rundeAlleStunden(rundung);
		Dauer istSumme = getSumme(neueListe);
		int groesster = -1; // = keine Dauer manipulieren
		dauerListe.clear();
		int diff = sollSumme.getMinuten() - istSumme.getMinuten();
		if (diff != 0) {
			// Summe passt nicht mehr. Jetzt muss eine Dauer manipuliert werden.
			if (diff > 0) {
				// Gibt es eine Dauer mit 0:00? Wenn ja: das erste Vorkommnis auf 0:15 ändern.
				List<GerundeteDauer> temp = gibtEs0Stunden(neueListe, diff, rundung);
				diff = sollSumme.getMinuten() - getSumme(temp).getMinuten();
				if (diff == 0) {
					dauerListe.addAll(temp);
					return;
				}
				neueListe = temp;
			}
			groesster = sucheGroesstenWert(neueListe);
		}
		bildeErgebnis(neueListe, diff < 0, groesster, rundung);
	}

	private List<GerundeteDauer> rundeAlleStunden(int rundung) {
		List<GerundeteDauer> neueListe = new ArrayList<>();
		for (Dauer i : dauerListe) {
			neueListe.add(GerundeteDauer.runde(i, rundung));
		}
		return neueListe;
	}

	/**
	 * Den größten Wert suchen. Falls alle gleich groß sind: den letzten nehmen.
	 * @return Index
	 */
	private int sucheGroesstenWert(List<GerundeteDauer> neueListe) {
		int groesster = 0;
		for (int i = 1/*!*/; i < neueListe.size(); i++) {
			if (neueListe.get(i).getMinuten() >= neueListe.get(groesster).getMinuten()) {
				groesster = i;
			}
		}
		return groesster;
	}

	private void bildeErgebnis(List<GerundeteDauer> neueListe, boolean abrunden, int groesster, int rundung) {
		for (int i = 0; i < neueListe.size(); i++) {
			GerundeteDauer dauer = neueListe.get(i);
			if (i == groesster) {
				if (abrunden) {
					dauer = dauer.rundeAb(rundung);
				} else {
					dauer = dauer.rundeAuf(rundung);
				}
			}
			dauerListe.add(dauer);
		}
	}
	
	private List<GerundeteDauer> gibtEs0Stunden(List<GerundeteDauer> neueListe, int diff, int rundung) {
		List<GerundeteDauer> temp = new ArrayList<>();
		for (GerundeteDauer i : neueListe) {
			if (diff > 0 && i.equals(Dauer.ZERO)) {
				temp.add(i.rundeAuf(rundung));
				diff -= rundung;
			} else {
				temp.add(i);
			}
		}
		return temp;
	}
}
