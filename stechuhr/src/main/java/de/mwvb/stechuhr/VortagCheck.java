package de.mwvb.stechuhr;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;
import javafx.scene.control.TextInputDialog;

/**
 * Überprüfen, ob am vorigen Arbeitstag STOP ausgeführt worden ist.
 * Falls nicht: mit Benutzerinteraktion STOP nachträglich durchführen.
 */
@SuppressWarnings("unused")
public class VortagCheck {

	public void check(StechuhrModel model) {
/*		
		if (!model.getStundenliste().isEmpty()) return;
		
		LocalDate d = sucheVortag(StechuhrModel.today());
		if (d == null) return; // Es wurde keine Vortagsdatei gefunden.
		if (endsWithStop(d)) return; // Vortag wurde korrekt gestoppt.
		
		// STOP wurde am Tag "d" nicht durchgeführt!
		Optional<String> uhrzeit = askForVortagEndeUhrzeit(d);
		if (uhrzeit.isPresent()) {
			// TODO Validierung
			// TODO STOP durchführen
		}
*/
	}
	
	private LocalDate sucheVortag(LocalDate today) {
		StechuhrDAO access = new StechuhrDAO();
		LocalDate d = today.minusDays(1);
		int loop = 0;
		while (loop < 100 && !access.getStechuhrModelFile(d).exists()) {
			d = d.minusDays(1);
			loop++;
		}
		return access.getStechuhrModelFile(d).exists() ? d : null;
	}
	
	private boolean endsWithStop(LocalDate d) {
		StechuhrModel vmodel = new StechuhrDAO().load(d);
		List<Stunden> stundenliste = vmodel.getStundenliste();
		int n = stundenliste.size();
		return n == 0 || Stunden.STOP.equals(stundenliste.get(n - 1).getTicket());
	}
	
	// TODO PRB: zu früh für Fensteranzeige
	private Optional<String> askForVortagEndeUhrzeit(LocalDate d) {
		TextInputDialog dialog = new TextInputDialog("17:15"); // TODO Diese Uhrzeit könnte man aus Altdaten erraten.
		dialog.setTitle("Vortag");
		dialog.setHeaderText("Am " + d + " wurde nicht auf STOP gedrückt. Dies kann jetzt nachgeholt werden.");
		dialog.setContentText("Feierabend Uhrzeit:");
		return dialog.showAndWait();
	}
}
