package de.mwvb.stechuhr.service;

import java.time.LocalTime;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.gui.Window;

// TODO Testcase (Window.alert ggf. anders lösen)
/**
 * Alle Validierungen
 */
public class StechuhrValidator {
	
	private StechuhrValidator() {
	}
	
	/**
	 * Eingegebene Uhrzeit validieren.
	 * @param uhrzeit eingegebene Uhrzeit
	 * @return formatierte Uhrzeit, oder null wenn die Uhrzeit nicht ok ist. Es wurde in dem Fall eine entsprechende Meldung ausgegeben.
	 */
	public static String validateUhrzeit(String uhrzeit) {
		uhrzeit = uhrzeit.replace(",", ":").trim(); // Eingabevereinfachung
		if (StechuhrUtils.nurZiffern(uhrzeit)) { // Eingabe "SMM" und "SSMM" ist auch erlaubt.
			if (uhrzeit.length() == "SMM".length()) {
				uhrzeit = uhrzeit.substring(0, 1) + ":" + uhrzeit.substring(1);
			} else if (uhrzeit.length() == "SSMM".length()) {
				uhrzeit = uhrzeit.substring(0, 2) + ":" + uhrzeit.substring(2);
			}
		}
		if ((uhrzeit.length() == "S:MM".length() && uhrzeit.charAt(1) == ':') || (uhrzeit.length() == "S".length() && uhrzeit.charAt(0) != ':')) {
			uhrzeit = "0" + uhrzeit;
		}
		if (!uhrzeit.contains(":")) { // Aus Kurzfurm "9" wird "09:00".
			try {
				int h = Integer.parseInt(uhrzeit);
				if ("0".equals(uhrzeit) || h > 0) {
					uhrzeit += ":00";
				}
			} catch (NumberFormatException ignore) { //
			}
		}
		try {
			LocalTime ret = LocalTime.parse(uhrzeit).withSecond(0).withNano(0);
			return ret.toString();
		} catch (Exception e) {
			Window.alert("Bitte gebe eine Uhrzeit im Format SS:MM ein!");
			return null;
		}
	}
	
	/**
	 * @param i Index des aktuell gewählten Stechuhreintrags
	 * @return null wenn Validierung nicht ok
	 */
	public static String validateUhrzeit(String eingegebeneUhrzeit, Stunden davorItem) {
		// Uhrzeit valide?
		eingegebeneUhrzeit = StechuhrValidator.validateUhrzeit(eingegebeneUhrzeit.trim()); // Format der Uhrzeit validieren.
		if (eingegebeneUhrzeit == null) {
			return null; // Validierung nicht ok.
		}
		
		// weitere Validierung: Nicht vor Vorgängeruhrzeit?
		LocalTime davor = LocalTime.MIDNIGHT;
		if (davorItem != null) {
			davor = davorItem.getUhrzeit();
		}
		if (LocalTime.parse(eingegebeneUhrzeit).isBefore(davor)) {
			Window.alert("Bitte gebe eine Uhrzeit nach " + davor.toString() + " ein!"
					+ "\nAlternativ kann auch der Vorgängerdatensatz geändert werden.");
			return null;
		}
		return eingegebeneUhrzeit;
	}

	/**
	 * Eingegebene Ticketnummer validieren
	 * @param ticket eingegebene Ticketnummer
	 * @return Ticket wenn ok, sonst null. Es wurde in dem Fall eine entsprechende Meldung ausgegeben.
	 */
	public static String validateTicket(String ticket) {
		ticket = ticket.trim();
		if (ticket.isEmpty()) {
			Window.alert("Bitte Ticketnummer eingeben!");
			return null;
		} else if (ticket.contains(";")) {
			Window.alert("Das Zeichen \";\" ist in der Ticketnummer nicht erlaubt!");
			return null;
		}
		return ticket;
	}
}
