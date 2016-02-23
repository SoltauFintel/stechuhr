package de.mwvb.stechuhr;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.stechuhr.entity.Dauer;

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
		Dauer summe = Dauer.ZERO;
		for (Dauer i : dauerListe) {
			summe = summe.add(i);
		}
		return summe;
	}

	public void ausgleichung(int minuten) {
		// TODO #15
	}
}
