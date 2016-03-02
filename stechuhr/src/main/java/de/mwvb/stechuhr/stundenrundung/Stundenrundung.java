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
		dauerListe.clear();
		int diff = sollSumme.getMinuten() - istSumme.getMinuten();
		List<Integer> ausschluss = new ArrayList<>();
		int loop = 0;
		while (diff != 0) {
			if (++loop > neueListe.size() * 2) {
				throw new RuntimeException("killerloop");
			}
			int index = -1;
			if (diff > 0) {
				index = suche0Wert(neueListe, ausschluss);
			}
			if (index == -1) {
				index = sucheGroesstenWert(neueListe, ausschluss);
			}
			if (index != -1) {
				ausschluss.add(index);
				if (diff < 0) {
					neueListe = ersetze(neueListe, index, -rundung);
					diff += rundung;
				} else {
					neueListe = ersetze(neueListe, index, rundung);
					diff -= rundung;
				}
			}
		}
		dauerListe.addAll(neueListe);
	}

	private List<GerundeteDauer> rundeAlleStunden(int rundung) {
		List<GerundeteDauer> neueListe = new ArrayList<>();
		for (Dauer i : dauerListe) {
			neueListe.add(GerundeteDauer.runde(i, rundung));
		}
		return neueListe;
	}

	/**
	 * Eine 0:00 Dauer suchen
	 * @return Index, -1 wenn nicht gefunden
	 */
	private int suche0Wert(List<GerundeteDauer> neueListe, List<Integer> ausschluss) {
		for (int i = 0; i < neueListe.size(); i++) {
			if (ausschluss.contains(i)) {
				continue;
			}
			if (neueListe.get(i).getMinuten() == 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Den größten Wert suchen. Falls alle gleich groß sind: den letzten nehmen.
	 * @return Index
	 */
	private int sucheGroesstenWert(List<GerundeteDauer> neueListe, List<Integer> ausschluss) {
		int groesster = -1;
		for (int i = 0; i < neueListe.size(); i++) {
			if (ausschluss.contains(i)) {
				continue;
			}
			if (groesster == -1) {
				groesster = i;
			} else if (neueListe.get(i).getMinuten() >= neueListe.get(groesster).getMinuten()) {
				groesster = i;
			}
		}
		return groesster;
	}
	
	/**
	 * Ändert die Dauer mit Index index um den Minutenwert additiv. Die neue Liste wird zurück gegeben.
	 */
	private List<GerundeteDauer> ersetze(List<GerundeteDauer> list, int index, int additiv) {
		List<GerundeteDauer> ret = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			GerundeteDauer d = list.get(i);
			if (i == index) {
				d = new GerundeteDauer(d.getMinuten() + additiv, d.getUngerundet());
			}
			ret.add(d);
		}
		return ret;
	}
}
