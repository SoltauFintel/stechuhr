package de.mwvb.stechuhr.service.export;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.mwvb.stechuhr.AbstractStundenTest;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.service.export.Exportstunden;
import de.mwvb.stechuhr.service.export.HTMLExport;

/**
 * Hilfsklasse, die HTMLExport aufruft, um immer die gleiche HTML-Datei zu erzeugen
 */
public class HTMLExportTest extends AbstractStundenTest {
	private static final LocalDate datum = LocalDate.of(2030, 3, 1);

	@Test
	public void test() {
		// Prepare
		List<Exportstunden> exportstunden = new ArrayList<>();
		Exportstunden s1 = new Exportstunden();
		s1.setTag(datum);
		s1.setStunden(2);
		s1.setMinuten(15);
		s1.setTicket("4711");
		s1.setLeistung("Heute wurde viel gemacht und noch was anderes...");
		exportstunden.add(s1);

		s1 = new Exportstunden();
		s1.setTag(datum);
		s1.setStunden(0);
		s1.setMinuten(30);
		s1.setTicket(Stunden.PAUSE);
		exportstunden.add(s1);

		s1 = new Exportstunden();
		s1.setTag(datum);
		s1.setStunden(2);
		s1.setMinuten(0);
		s1.setTicket("Service");
		s1.setLeistung("HTML & Co korrigiert: <b>...</b>");
		exportstunden.add(s1);

		// Test
		HTMLExport exporteur = new HTMLExport() {
			protected File getExportFile(LocalDate tag) {
				return new File("HTMLExportTest.html");
			}
		};
		exporteur.export(exportstunden);
		
		new File("HTMLExportTest.html").deleteOnExit();
	}
}
