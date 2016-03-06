package de.mwvb.stechuhr.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.gui.Window;
import de.mwvb.stechuhr.gui.bearbeiten.BearbeitenWindowController;
import javafx.scene.control.TextInputDialog;

/**
 * Überprüfen, ob am vorigen Arbeitstag STOP ausgeführt worden ist.
 * Falls nicht: mit Benutzerinteraktion STOP nachträglich durchführen.
 */
public class VortagCheck {
	
	/**
	 * Prüft, ob der Vortag gestoppt werden muss und führt ggf. Stop mit Benutzerinteraktion durch.
	 * 
	 * @param model StechuhrModel des aktuellen Tages
	 * @return
	 * 0: Vortag wurde korrekt gestoppt.<br>
	 * 1: Vortag wurde nun nachträglich gestoppt.<br>
	 * -1: Vortag muss gestoppt werden; Aktion wurde aber durch Anwender abgebrochen<br>
	 * -2: Es gibt bereits Stunden für den aktuellen Tag,<br>
	 * -3: Es gibt keine Vortag-Daten.<br>
	 */
	public int check(StechuhrModel model) {
		if (!model.getStundenliste().isEmpty()) return -2; // Stop wird nur als vergessen angesehen, wenn es noch keine Stunden für den akt. Tag gibt.
		
		LocalDate vortagDate = sucheVortag();
		if (vortagDate == null) return -3; // Es wurde keine Vortagsdatei mit vertretbarem Aufwand gefunden.
		StechuhrModel vmodel = endsWithStop(vortagDate);
		if (vmodel == null) return 0; // Vortag wurde korrekt gestoppt.
		
		return vortagWurdeNichtGestoppt(vmodel, vortagDate);
	}
	
	/**
	 * @return liefert i.d.R. das Datum vom Vortag bzw. vom letzten Freitag. Aber wenn der Benutzer Urlaub gemacht hat,
	 * ist die Lücke größer. Zur Vermeidung einer Endlosschleife wird max. 100 Tage zurück gesucht.
	 */
	private LocalDate sucheVortag() {
		StechuhrDAO access = getAccess();
		LocalDate d = today().minusDays(1);
		int loop = 0;
		while (loop < 100 && !access.existsStechuhrModelFile(d)) {
			d = d.minusDays(1);
			loop++;
		}
		return access.existsStechuhrModelFile(d) ? d : null;
	}
	
	/**
	 * @param d
	 * @return null wenn Tag "d" mit STOP endet, sonst StechuhrModel von Tag "d"
	 */
	private StechuhrModel endsWithStop(LocalDate d) {
		StechuhrModel vmodel = getAccess().load(d);
		List<Stunden> stundenliste = vmodel.getStundenliste();
		int n = stundenliste.size();
		if (n == 0) {
			return null;
		}
		String letztesTicket = stundenliste.get(n - 1).getTicket();
		return Stunden.STOP.equals(letztesTicket) ? null : vmodel;
	}

	private int vortagWurdeNichtGestoppt(final StechuhrModel vmodel, final LocalDate vortagDate) {
		LocalTime nichtVor = vmodel.getStundenliste().get(vmodel.getStundenliste().size() - 1).getUhrzeit();
		String vorschlag = "17:15"; // TODO Diese Uhrzeit könnte man aus Altdaten erraten.
		if (LocalTime.parse(vorschlag).isBefore(nichtVor)) {
			vorschlag = nichtVor.toString();
		}
		LocalTime feierabendUhrzeit = ermittleFeierabendUhrzeit(vortagDate, vorschlag, nichtVor);
		if (feierabendUhrzeit != null) {
			// STOP durchführen
			Stunden stop = new Stunden(feierabendUhrzeit);
			stop.setTicket(Stunden.STOP);
			vmodel.getStundenliste().add(stop);
			getAccess().save(vmodel);
			vmodel.stop();
			return 1;
		}
		return -1;
	}
	
	private LocalTime ermittleFeierabendUhrzeit(final LocalDate vortagDate, final String vorschlag, final LocalTime nichtVor) {
		Optional<String> eingabe = askForVortagEndeUhrzeit(vortagDate, vorschlag);
		if (!eingabe.isPresent()) {
			return null; // Abbruch
		}
		String uhrzeit = BearbeitenWindowController.validateUhrzeit(eingabe.get());
		if (uhrzeit == null) { // Validierung gescheitert -> Dialog nochmal anzeigen
			return ermittleFeierabendUhrzeit(vortagDate, eingabe.get(), nichtVor); // rekursiv
		}
		if (LocalTime.parse(uhrzeit).isBefore(nichtVor)) {
			Window.alert("Die Uhrzeit darf nicht vor " + nichtVor.toString() + " sein!");
			return ermittleFeierabendUhrzeit(vortagDate, nichtVor.toString(), nichtVor); // rekursiv
		}
		return LocalTime.parse(uhrzeit); // Erfolg
	}
	
	Optional<String> askForVortagEndeUhrzeit(LocalDate d, String vorschlag) {
		TextInputDialog dialog = new TextInputDialog(vorschlag);
		dialog.setTitle("Vortag");
		dialog.setHeaderText("Am " + StechuhrUtils.formatWTDate(d) + " wurde nicht auf STOP gedrückt."
				+ "\nDies kann jetzt nachgeholt werden."
				+ "\nWie spät wurde an dem Tag Feierabend gemacht?");
		dialog.setContentText("Feierabend Uhrzeit:");
		return dialog.showAndWait();
	}
	
	StechuhrDAO getAccess() {
		return new StechuhrDAO();
	}
	
	LocalDate today() {
		return StechuhrModel.today();
	}
}
