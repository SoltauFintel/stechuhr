package de.mwvb.stechuhr;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.Exportstunden;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.gui.Window;

/**
 * Test der Klasse VortagCheck
 * 
 * @author Marcus Warm
 */
public class VortagCheckTest extends AbstractStundenTest {
	VortagCheckAccess access;
	List<Exportstunden> exportstunden;
	int nachFeierabendGefragt;
	
	@Before
	public void init() {
		exportstunden = null;
		nachFeierabendGefragt = 0;
		Application.plugin = new IStechuhrPlugin() {
			@Override
			public void export(List<Exportstunden> pExportstunden) {
				exportstunden = pExportstunden;
			}
		};
		Window.testmodus = true; // keine Alerts anzeigen
	}
	
	@After
	public void cleanup() {
		exportstunden = null;
		Application.plugin = null;
		Window.testmodus = false;
	}
	
	/**
	 * Hier wird simuliert, dass kein STOP am Vortag durchgeführt wurde.
	 * Diese Situation tritt ein, wenn der Anwender vergessen hatte am Vortag auf STOP zu drücken und nun die Stechuhr startet.
	 * Erwartung: Feierabend-Uhrzeit wird abgefragt und STOP wird durchgeführt.
	 */
	@Test
	public void testStopWirdDurchgefuehrt() {
		// Prepare
		final LocalDate heute = LocalDate.of(2020, 11, 9); // Montag
		prepare();
		
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
				nachFeierabendGefragt++;
				return Optional.of("15:15");
			}
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(1, r);
		Assert.assertEquals("STOP-Satz fehlt!", 2, model.getStundenliste().size());
		Assert.assertEquals("2. Satz muss STOP Ticket sein!", Stunden.STOP, model.getStundenliste().get(1).getTicket());
		Assert.assertTrue("StechuhrModel wurde nicht gespeichert!", access.saved);
		Assert.assertEquals("Feierabend-Uhrzeit hätte abgefragt werden müssen!", 1, nachFeierabendGefragt);
		Assert.assertNotNull("Stunden wurden nicht exportiert!", exportstunden);
		Assert.assertEquals("Anzahl Exportstunden falsch!", 1, exportstunden.size());
		Assert.assertEquals("Exportstunden-Dauer ist falsch!", "7:15", exportstunden.get(0).getSSMM());
	}

	/**
	 * Ein Stop müsste durchgeführt werden, aber der Anwender bricht ab.
	 */
	@Test
	public void testAbbruchDurchAnwender() {
		// Prepare
		Application.plugin = new IStechuhrPlugin() {
			@Override
			public void export(List<Exportstunden> pExportstunden) {
				exportstunden = pExportstunden;
			}
		};
		final LocalDate heute = LocalDate.of(2020, 11, 9);
		prepare();
		
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
				nachFeierabendGefragt++;
				return Optional.empty();
			}
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(-1, r);
		Assert.assertEquals("STOP-Satz darf nicht hinzugefügt werden!", 1, model.getStundenliste().size());
		Assert.assertFalse("StechuhrModel darf nicht gespeichert werden!", access.saved);
		Assert.assertEquals("Feierabend-Uhrzeit hätte abgefragt werden müssen!", 1, nachFeierabendGefragt);
		Assert.assertNull("Stunden dürfen nicht exportiert werden!", exportstunden);
	}

	/**
	 * Der Anwender gibt beim ersten und zweiten Mal eine vom Format her falsche Uhrzeit ein und bricht danach die Aktion ab.
	 */
	@Test
	public void testFalscheUhrzeit() {
		// Prepare
		final LocalDate heute = LocalDate.of(2020, 11, 9);
		prepare();
		
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
				if (++nachFeierabendGefragt <= 2) {
					return Optional.of("24:01");
				}
				return Optional.empty(); // Abbruch
			}
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(-1, r);
		Assert.assertEquals(3, nachFeierabendGefragt);
	}

	/**
	 * Wenn der Benutzer eine Uhrzeit eingibt, die vor der letzten eingegebenen Vortag-Uhrzeit liegt, muss der VortagCheck
	 * diese Eingabe ablehnen.
	 */
	@Test
	public void testUhrzeitVorLetzterUhrzeit() {
		// Prepare
		Application.plugin = new IStechuhrPlugin() {
			@Override
			public void export(List<Exportstunden> pExportstunden) {
				exportstunden = pExportstunden;
			}
		};
		final LocalDate heute = LocalDate.of(2020, 11, 9);
		prepare();
		
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
				if (++nachFeierabendGefragt == 1) {
					return Optional.of("07:00");
				}
				return Optional.of("9"); // verkürzte Eingabe muss akzeptiert werden
			}
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(1, r);
		Assert.assertEquals(2, nachFeierabendGefragt);
		Assert.assertEquals("1:00", exportstunden.get(0).getSSMM()); // 8:00 - 9:00
	}

	private void prepare() {
		access = new VortagCheckAccess();
		model = new StechuhrModel(LocalDate.of(2020, 11, 6)); // Freitag
		access.existingDays.add(model.getTag());
		createStunden(LocalTime.of(8, 0), "A", "");
		access.models.put(model.getTag(), model);
	}

	/**
	 * Wenn bereits am aktuellen Tag Stunden eingegeben worden sind, macht der Vortag-Check nichts.
	 * Diese Situtation tritt ein, wenn der Anwender das 2. Mal die Stechuhr an einem Tag startet.
	 */
	@Test
	public void testSchonStundenEingegeben() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2020, 11, 9));
		createStunden(LocalTime.of(8, 0), "A", "");
		
		// Test
		int r = new VortagCheck().check(model);
		
		// Verify
		Assert.assertEquals(-2, r);
	}

	/**
	 * Der Vortag-Check findet keine Vortagsdaten.
	 * Diese Situation tritt ein, wenn der Anwender das 1. Mal die Stechuhr benutzt - oder die lokalen Stechuhrdaten
	 * gelöscht hat.
	 */
	@Test
	public void testEsGibtKeinenVortag() {
		// Prepare
		final LocalDate heute = LocalDate.of(2020, 11, 9);
		access = new VortagCheckAccess();
		
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
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(-3, r);
	}

	/**
	 * Der Vortag-Check findet keine Vortagsdaten, weil diese zu lange zurück liegen.
	 * Diese Situation tritt ein, wenn der Anwender die Stechuhr lange Zeit nicht mehr benutzt hatte.
	 * 
	 * Achtung: Es kann sein, dass nicht gewünscht ist, dass man Daten vom vorigen Monat noch verändert.
	 * Dieser Fehler sollte dann aber beim Export durch das Fremdsystem auftreten.
	 */
	@Test
	public void testVortagLiegtZuLangeZurueck() {
		// Prepare
		final LocalDate heute = LocalDate.of(2030, 11, 9);
		prepare();

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
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(-3, r);
	}

	/**
	 * Dieser Test testet eigentlich den Normalfall: Der Anwender hat den Vortag korrekt gestoppt.
	 * Abgesehen vom Check wird also nichts gemacht.
	 */
	@Test
	public void testVortagWurdeGestoppt() {
		// Prepare
		final LocalDate heute = LocalDate.of(2020, 11, 6);
		access = new VortagCheckAccess();
		model = new StechuhrModel(LocalDate.of(2020, 11, 5));
		access.existingDays.add(model.getTag());
		createStunden(LocalTime.of(9, 0), "A", "");
		createStunden(LocalTime.of(17, 0), Stunden.STOP, "");
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
				nachFeierabendGefragt++;
				return Optional.empty();
			}
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(0, r);
		Assert.assertEquals(0, nachFeierabendGefragt);
	}

	/**
	 * Wenn ein leerer Vortag gefunden wird, braucht dieser nicht gestoppt zu werden.
	 * Die Prüfung ist dann zu Ende.
	 */
	@Test
	public void testVortagHatKeineStunden() {
		// Prepare
		final LocalDate heute = LocalDate.of(2020, 11, 6);
		access = new VortagCheckAccess();
		model = new StechuhrModel(LocalDate.of(2020, 11, 5));
		access.existingDays.add(model.getTag());
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
		};
		
		// Test
		int r = vc.check(testmodel);
		
		// Verify
		Assert.assertEquals(0, r);
	}
}
