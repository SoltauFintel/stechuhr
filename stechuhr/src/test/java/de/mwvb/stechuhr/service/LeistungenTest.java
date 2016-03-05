package de.mwvb.stechuhr.service;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.Stunden;

/**
 * Wenn ein Anwender zu einem Ticket eine Leistung erfasst, dann soll die Leistung automatisch eingetragen werden,
 * wenn der Anwender erneut dieses Ticket aktiviert.
 * 
 * @author Marcus Warm
 */
public class LeistungenTest {
	
	// eine Leistung wird eingegeben -> muss gemerkt werden (1. in Liste, alte Vorkommnisse zuvor raus) -> Speichern (max. MAX_ANZAHL)
	@Test
	public void testLeistungEingeben() {
		String leistung = "was getan";
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T", leistung); // add() muss Leistung oben einfügen
		Assert.assertEquals(leistung, leistungen.getFirst());
		Assert.assertEquals(1, leistungen.size());
	}

	@Test
	public void testLeistungSpeichern() {
		String leistung = "was getan";
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T", leistung); // add() muss automatisch speichern
		
		Leistungen geladen = Leistungen.open("TEST"); // Liest Datei ein...
		Assert.assertEquals(leistung, geladen.getFirst());
	}
	
	// Programmstart -> Leistungen laden und in Combobox anzeigen. Es werden max. MAX_ANZAHL Leistungen geladen.
	@Test
	public void testMaximaleAnzahlLeistungen() {
		Leistungen leistungen = Leistungen.open("TEST");
		for (int i = 1 ; i <= Leistungen.MAX_ANZAHL + 20; i++) {
			leistungen.add("T", "d" + i);
		}
		Assert.assertEquals(Leistungen.MAX_ANZAHL, leistungen.size());

		Leistungen geladen = Leistungen.open("TEST");
		Assert.assertEquals(Leistungen.MAX_ANZAHL, geladen.size());
	}

	@Test
	public void testKeineDoppeltenEintraege() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T", "A");
		leistungen.add("T", "B");
		leistungen.add("T", "B");
		leistungen.add("T", "A");
		Assert.assertEquals(2, leistungen.size());

		Leistungen geladen = Leistungen.open("TEST");
		Assert.assertEquals(2, geladen.size());
	}

	@Test
	public void testKeineLeerEintraegeAufnehmenOderSpeichern() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T", "");
		leistungen.add("T", null);
		leistungen.add("T", "A");
		Assert.assertEquals(1, leistungen.size());
		
		Leistungen geladen = Leistungen.open("TEST");
		Assert.assertEquals(1, geladen.size());
	}

	// Ticket (außer PAUSE/STOP) wird gewählt. Vom jüngsten Stundeneintrag muss anhand des Tickets nun die Leistung genommen werden.
	@Test
	public void testTicketEingabe() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("StandUp", "Besprechung aktueller Themen");
		leistungen.add("Orga", "Mails lesen");
		leistungen.add(Stunden.PAUSE, "Pause");
		leistungen.add(Stunden.STOP, "Feierabend");
		leistungen.add("Orga", "Emails lesen"); // überschreibt vorigen Eintrag
		leistungen.add("CI", "Jenkins-Build gefixt");
		String leistung = leistungen.getLeistungForTicket("Orga");
		Assert.assertEquals("Emails lesen", leistung);
		Assert.assertEquals("", leistungen.getLeistungForTicket("NICHT-VORHANDEN"));
		Assert.assertEquals("Leistung zu PAUSE soll nicht gespeichert werden!", "", leistungen.getLeistungForTicket(Stunden.PAUSE));
		Assert.assertEquals("Leistung zu STOP soll nicht gespeichert werden!", "", leistungen.getLeistungForTicket(Stunden.STOP));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOhneTicket_null() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOhneTicket_empty() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOhneTicket_blank() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add(" ", " ");
	}

	@Test
	public void testGleicheLeistungZuZweiTickets() {
		String leistung = "was getan";
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T1", leistung);
		leistungen.add("T2", leistung);
		Assert.assertEquals(leistung, leistungen.getFirst());
		Assert.assertEquals(1, leistungen.size());
		Assert.assertEquals(leistung, leistungen.getLeistungForTicket("T1"));
		Assert.assertEquals(leistung, leistungen.getLeistungForTicket("T2"));
	}
	
	@Test
	public void testIterator() {
		String leistung = "was getan";
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T1", leistung);
		leistungen.add("T2", leistung);
		leistungen.add("T3", "A");
		StringBuilder sb = new StringBuilder();
		for (String x : leistungen) {
			sb.append(x);
			sb.append("|");
		}
		Assert.assertEquals("A|" + leistung + "|", sb.toString());
	}

	@Test(expected = NoSuchElementException.class)
	public void testEmpty_getFirst() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.getFirst();
	}

	@Test
	public void testEmpty_size() {
		Leistungen leistungen = Leistungen.open("TEST");
		Assert.assertEquals(0, leistungen.size());
	}

	@Test
	public void testEmpty_isEmpty() {
		Leistungen leistungen = Leistungen.open("TEST");
		Assert.assertTrue(leistungen.isEmpty());
	}

	@Test
	public void testEmpty_isNotEmpty() {
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T", "A");
		Assert.assertFalse(leistungen.isEmpty());
	}

	@Test
	public void testGetLeistungForTicket_andereLeistungIstFirst() {
		// Prepare
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T1", "A");
		leistungen.add("T2", "B");

		// Test
		leistungen.add("T3", "A");

		// Verify
		Assert.assertEquals("A", leistungen.getLeistungForTicket("T3"));
		Assert.assertEquals("A", leistungen.getLeistungForTicket("T1"));
		Assert.assertEquals("B", leistungen.getLeistungForTicket("T2"));
	}

	@Test
	public void testGetLeistungForTicket_gleicheLeistungIstFirst() {
		// Prepare
		Leistungen leistungen = Leistungen.open("TEST");
		leistungen.add("T2", "B");
		leistungen.add("T1", "A");
		
		// Test
		leistungen.add("T3", "A");
		
		// Verify
		Assert.assertEquals("A", leistungen.getLeistungForTicket("T3"));
		Assert.assertEquals("A", leistungen.getLeistungForTicket("T1"));
		Assert.assertEquals("B", leistungen.getLeistungForTicket("T2"));
	}
	
	@BeforeClass
	public static void init() {
		StechuhrDAO.init();
	}
	
	@Before
	@After
	public void loescheTestDatei() {
		Leistungen.delete("TEST");
	}
}
