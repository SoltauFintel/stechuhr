package de.mwvb.stechuhr;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

	public void check(StechuhrModel model) {
		if (!model.getStundenliste().isEmpty()) return; // Stop wird nur als vergessen angesehen, wenn es noch keine Stunden für den akt. Tag gibt.
		
		LocalDate vortagDate = sucheVortag();
		if (vortagDate == null) return; // Es wurde keine Vortagsdatei mit vertretbarem Aufwand gefunden.
		StechuhrModel vmodel = endsWithStop(vortagDate);
		if (vmodel == null) return; // Vortag wurde korrekt gestoppt.
		
		vortagWurdeNichtGestoppt(vmodel, vortagDate);
	}
	
	/**
	 * @return liefert i.d.R. das Datum vom Vortag bzw. vom letzten Freitag. Aber wenn der Benutzer Urlaub gemacht hat,
	 * ist die Lücke größer. Zur Vermeidung einer Endlosschleife wird max. 100 Tage zurück gesucht.
	 */
	private LocalDate sucheVortag() {
		StechuhrDAO access = new StechuhrDAO();
		LocalDate d = StechuhrModel.today().minusDays(1);
		int loop = 0;
		while (loop < 100 && !access.existsStechuhrModelFile(d)) {
			d = d.minusDays(1);
			loop++;
		}
		return access.getStechuhrModelFile(d).exists() ? d : null;
	}
	
	/**
	 * @param d
	 * @return null wenn Tag "d" mit STOP endet, sonst StechuhrModel von Tag "d"
	 */
	private StechuhrModel endsWithStop(LocalDate d) {
		StechuhrModel vmodel = new StechuhrDAO().load(d);
		List<Stunden> stundenliste = vmodel.getStundenliste();
		int n = stundenliste.size();
		return (n == 0 || Stunden.STOP.equals(stundenliste.get(n - 1).getTicket())) ? null : vmodel;
	}

	private void vortagWurdeNichtGestoppt(final StechuhrModel vmodel, final LocalDate vortagDate) {
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
			new StechuhrDAO().save(vmodel);
			vmodel.stop();
		}
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
	
	private Optional<String> askForVortagEndeUhrzeit(LocalDate d, String vorschlag) {
		TextInputDialog dialog = new TextInputDialog(vorschlag);
		dialog.setTitle("Vortag");
		dialog.setHeaderText("Am " + Stunden.formatWTDate(d) + " wurde nicht auf STOP gedrückt."
				+ "\nDies kann jetzt nachgeholt werden."
				+ "\nWie spät wurde an dem Tag Feierabend gemacht?");
		dialog.setContentText("Feierabend Uhrzeit:");
		return dialog.showAndWait();
	}
}
