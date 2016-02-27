package de.mwvb.stechuhr;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.Exportstunden;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;

/**
 * Test der Klasse VortagCheck
 * 
 * @author Marcus Warm
 */
public class VortagCheckTest extends AbstractStundenTest {
	VortagCheckAccess access;
	List<Exportstunden> exportstunden;
	boolean nachFeierabendGefragt = false;
	
	/**
	 * Hier wird simuliert, dass kein STOP am Vortag durchgeführt wurde.
	 * Erwartung: Feierabend-Uhrzeit wird abgefragt und STOP wird durchgeführt.
	 */
	@Test
	public void stopWirdDurchgefuehrt() {
		// Prepare
		Application.plugin = new IStechuhrPlugin() {
			@Override
			public void export(List<Exportstunden> pExportstunden) {
				exportstunden = pExportstunden;
			}
		};
		final LocalDate heute = LocalDate.of(2020, 11, 9); // Montag
		access = new VortagCheckAccess();
		access.existingDays.add(LocalDate.of(2020, 11, 6)); // Freitag
		model = new StechuhrModel(LocalDate.of(2020, 11, 6));
		createStunden(LocalTime.of(8, 0), "A", "");
		access.models.put(model.getTag(), model);
		
		StechuhrModel testmodel = new StechuhrModel(heute);
		VortagCheck vc = new VortagCheck() {
			@Override
			StechuhrDAO getAccess() {
				return access;
			}
			
			@Override
			LocalDate today() {
				return heute;
			}
			
			@Override
			Optional<String> askForVortagEndeUhrzeit(LocalDate d, String vorschlag) {
				Assert.assertEquals("Feierabend-Uhrzeit-Abfrage: Datum falsch!", LocalDate.of(2020, 11, 6), d);
				nachFeierabendGefragt = true;
				return Optional.of("15:15");
			}
		};
		
		// Test
		vc.check(testmodel);
		
		// Verify
		Assert.assertEquals("STOP-Satz fehlt!", 2, model.getStundenliste().size());
		Assert.assertEquals("2. Satz muss STOP Ticket sein!", Stunden.STOP, model.getStundenliste().get(1).getTicket());
		Assert.assertTrue("StechuhrModel wurde nicht gespeichert!", access.saved);
		Assert.assertTrue("Feierabend-Uhrzeit hätte abgefragt werden müssen!", nachFeierabendGefragt);
		Assert.assertNotNull("Stunden wurden nicht exportiert!", exportstunden);
		Assert.assertEquals("Anzahl Exportstunden falsch!", 1, exportstunden.size());
		Assert.assertEquals("Exportstunden-Dauer ist falsch!", "7:15", exportstunden.get(0).getSSMM());
	}
}
