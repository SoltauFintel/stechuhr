package de.mwvb.stechuhr.dao;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Wenn ein Anwender zu einem Ticket eine Leistung erfasst, dann soll die Leistung automatisch eingetragen werden,
 * wenn der Anwender erneut dieses Ticket aktiviert.
 * 
 * @author Marcus Warm
 */
@Ignore
public class LeistungenTest {
	
	// eine Leistung wird eingegeben -> muss gemerkt werden (1. in Liste, alte Vorkommnisse zuvor raus) -> Speichern (max. MAX_ANZAHL)
	@Test
	public void testLeistungEingeben() {
//		String leistung = "was getan";
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add("T", leistung); // add() muss Leistung oben einfügen
//		Assert.assertEquals(leistung, leistungen.get(0));
//		Assert.assertEquals(1, leistungen.size());
	}

//	@Test
//	public void testLeistungSpeichern() {
//		String leistung = "was getan";
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add("T", leistung); // add() muss automatisch speichern
//		
//		Leistungen geladen = new Leistungen("TEST"); // Liest Datei ein...
//		Assert.assertEquals(leistung, geladen.get(0));
//	}
//	
//	// Programmstart -> Leistungen laden und in Combobox anzeigen. Es werden max. MAX_ANZAHL Leistungen geladen.
//	@Test
//	public void testMaximaleAnzahlLeistungen() {
//		Leistungen leistungen = new Leistungen("TEST");
//		for (int i = 1 ; i <= 1000; i++) {
//			leistungen.add("T", "d" + i);
//		}
//		Assert.assertEquals(Leistungen.MAX_ANZAHL, leistungen.size());
//
//		Leistungen geladen = new Leistungen("TEST");
//		Assert.assertEquals(Leistungen.MAX_ANZAHL, geladen.size());
//	}
//
//	@Test
//	public void testKeineDoppeltenEintraege() {
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add("T", "A");
//		leistungen.add("T", "B");
//		leistungen.add("T", "B");
//		leistungen.add("T", "A");
//		Assert.assertEquals(2, leistungen.size());
//
//		Leistungen geladen = new Leistungen("TEST");
//		Assert.assertEquals(2, geladen.size());
//	}
//
//	@Test
//	public void testKeineLeerEintraegeAufnehmenOderSpeichern() {
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add("T", "");
//		leistungen.add("T", null);
//		leistungen.add("T", "A");
//		Assert.assertEquals(1, leistungen.size());
//		
//		Leistungen geladen = new Leistungen("TEST");
//		Assert.assertEquals(1, geladen.size());
//	}
//
//	// Ticket (außer PAUSE/STOP) wird gewählt. Vom jüngsten Stundeneintrag muss anhand des Tickets nun die Leistung genommen werden.
//	@Test
//	public void testTicketEingabe() {
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add("StandUp", "Besprechung aktueller Themen");
//		leistungen.add("Orga", "Mails lesen");
//		leistungen.add(Stunden.PAUSE, "Pause");
//		leistungen.add(Stunden.STOP, "Feierabend");
//		leistungen.add("Orga", "Emails lesen"); // überschreibt vorigen Eintrag
//		leistungen.add("CI", "Jenkins-Build gefixt");
//		String leistung = leistungen.getLeistungForTicket("Orga");
//		Assert.assertEquals("Emails lesen", leistung);
//		Assert.assertNull("Leistung zu PAUSE soll nicht gespeichert werden!", leistungen.getLeistungForTicket(Stunden.PAUSE));
//		Assert.assertNull("Leistung zu STOP soll nicht gespeichert werden!", leistungen.getLeistungForTicket(Stunden.STOP));
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testAddOhneTicket_null() {
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add(null, null);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testAddOhneTicket_empty() {
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add("", "");
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testAddOhneTicket_blank() {
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add(" ", " ");
//	}
//
//	@Test
//	public void testGleicheLeistungZuZweiTickets() {
//		String leistung = "was getan";
//		Leistungen leistungen = new Leistungen("TEST");
//		leistungen.add("T1", leistung);
//		leistungen.add("T2", leistung);
//		Assert.assertEquals(leistung, leistungen.get(0));
//		Assert.assertEquals(1, leistungen.size());
//		Assert.assertEquals(leistung, leistungen.getLeistungForTicket("T1"));
//		Assert.assertEquals(leistung, leistungen.getLeistungForTicket("T2"));
//	}
//
//	@Before
//	@After
//	public void loescheTestDatei() {
//		Leistungen.delete("TEST");
//	}
}
