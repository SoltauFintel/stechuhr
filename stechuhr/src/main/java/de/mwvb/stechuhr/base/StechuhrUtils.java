package de.mwvb.stechuhr.base;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StechuhrUtils {

	private StechuhrUtils() {
	}
	
	/**
	 * @param i >= 0
	 * @return Zahl i als String mit mindestens 2 Stellen, aufgenullt
	 */
	public static String zweistellig(long i) {
		if (i < 0) {
			throw new IllegalArgumentException("Zahl muss >= 0 sein!");
		}
		return i < 10 ? "0" + i : "" + i;
	}

	/**
	 * @return datum als String im Format TT.MM.JJJJ
	 */
	public static String formatDate(LocalDate datum) {
		return datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN));
	}

	/**
	 * @param date darf nicht null sein
	 * @return Wochentag TT.MM.JJJJ
	 */
	public static String formatWTDate(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy", Locale.GERMAN));
	}
	
	/**
	 * Es wird solange die Nummer erhöht, bis ein Dateiname kreiert wird, der noch nicht im Dateisystem existiert.
	 * @param vorne inkl. Pfad
	 * @param hinten
	 * @return Dateiname bestehend aus vorne + Nummer + hinten. Die Datei gibt es noch nicht.
	 */
	public static File getNextFilename(String vorne, String hinten) {
		int zaehler = 0;
		File f;
		do {
			f = new File(vorne + ++zaehler + hinten);
		} while (f.exists());
		f.getParentFile().mkdirs();
		return f;
	}
}
