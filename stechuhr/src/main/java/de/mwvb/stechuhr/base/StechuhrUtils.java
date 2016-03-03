package de.mwvb.stechuhr.base;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StechuhrUtils {

	private StechuhrUtils() {
	}
	
	public static String zweistellig(long i) {
		return i < 10 ? "0" + i : "" + i;
	}

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
}
