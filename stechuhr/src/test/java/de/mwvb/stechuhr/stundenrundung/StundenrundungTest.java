package de.mwvb.stechuhr.stundenrundung;

import org.junit.Assert;
import org.junit.Test;

public class StundenrundungTest {

	@Test
	public void keineDauer() {
		Stundenrundung r = new Stundenrundung();
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 0, r.getDauerListe().size());
	}

	@Test
	public void nurEineDauer_aufrunden() {
		Stundenrundung r = new Stundenrundung("0:29");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 1, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
	}

	@Test
	public void nurEineDauer_abrunden() {
		Stundenrundung r = new Stundenrundung("0:36");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 1, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
	}

	@Test
	public void basic() {
		Stundenrundung r = new Stundenrundung("0:14", "0:46");
		Assert.assertEquals(new Dauer("1:00"), r.getSumme());
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(1));
		Assert.assertEquals("Summe muss auf jeden Fall bleiben!", new Dauer("1:00"), r.getSumme());
	}

	@Test
	public void einfach_15() {
		Stundenrundung r = new Stundenrundung("0:15", "0:30");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(1));
	}

	@Test
	public void einfach_30() {
		Stundenrundung r = new Stundenrundung("0:30", "1:00");
		
		r.ausgleichung(30);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("1:00"), r.getDauerListe().get(1));
	}

	@Test
	public void einzelnWuerdeAbgerundetWerden() {
		Stundenrundung r = new Stundenrundung("0:36", "0:36");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(1));
	}

	@Test
	public void einzelnWuerdeAbgerundetWerden_2() {
		Stundenrundung r = new Stundenrundung("0:36", "1:36");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("1:45"), r.getDauerListe().get(1));
		Assert.assertEquals("Summe falsch", new Dauer("2:15"), r.getSumme());
	}

	@Test
	public void testNahe0_a() {
		Stundenrundung r = new Stundenrundung("0:01", "0:35");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:00"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(1));
		Assert.assertEquals("Summe falsch", new Dauer("0:30"), r.getSumme());
	}

	@Test
	public void testNahe0_b() {
		Stundenrundung r = new Stundenrundung("0:01", "0:36");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(1));
		Assert.assertEquals("Summe falsch", new Dauer("0:45"), r.getSumme());
	}

	@Test
	public void testNahe0_c() {
		Stundenrundung r = new Stundenrundung("0:01", "0:01", "0:35");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 3, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:00"), r.getDauerListe().get(1));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(2));
		Assert.assertEquals("Summe falsch", new Dauer("0:45"), r.getSumme());
	}

	@Test
	public void einzelnWuerdeAufgerundetWerden() {
		Stundenrundung r = new Stundenrundung("0:53", "0:53");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("1:00"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(1));
		Assert.assertEquals("Summe falsch", new Dauer("1:45"), r.getSumme());
	}

	@Test
	public void einzelnWuerdeAufgerundetWerden_2() {
		Stundenrundung r = new Stundenrundung("0:53", "5:53");   // 6h 45
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("1:00"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("5:45"), r.getDauerListe().get(1));
		Assert.assertEquals("Summe falsch", new Dauer("6:45"), r.getSumme());
	}

	@Test
	public void realWorld() {
		Stundenrundung r = new Stundenrundung("0:17", "0:57", "0:14", "4:35");
		Assert.assertEquals(363, r.getSumme().getMinuten());
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 4, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("1:00"), r.getDauerListe().get(1));
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(2));
		Assert.assertEquals(new Dauer("4:30"), r.getDauerListe().get(3));
		Assert.assertEquals(360, r.getSumme().getMinuten());
		Assert.assertEquals(new Dauer("0:17"), ((GerundeteDauer) r.getDauerListe().get(0)).getUngerundet());
		Assert.assertEquals(new Dauer("0:57"), ((GerundeteDauer) r.getDauerListe().get(1)).getUngerundet());
		Assert.assertEquals(new Dauer("0:14"), ((GerundeteDauer) r.getDauerListe().get(2)).getUngerundet());
		Assert.assertEquals(new Dauer("4:35"), ((GerundeteDauer) r.getDauerListe().get(3)).getUngerundet());
	}

	@Test
	public void restGroesser15_1() {
		Stundenrundung r = new Stundenrundung("0:38", "1:53", "0:38", "0:53", "0:38", "0:38", "0:38");
		int summe = r.getSumme().getMinuten();
		
		r.ausgleichung(15);

		Assert.assertTrue("Summe weicht zu sehr ab: " + summe + " ./. " + r.getSumme().getMinuten()
				+ "\nDifferenz: " + (r.getSumme().getMinuten() - summe)
				+ "\n" + r.getDauerListe(),
				Math.abs(r.getSumme().getMinuten() - summe) < 15);
		Assert.assertEquals("Anzahl falsch!", 7, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("1:45"), r.getDauerListe().get(1));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(2));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(3));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(4));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(5));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(6));
	}

	@Test
	public void restGroesser15_0() {
		Stundenrundung r = new Stundenrundung("0:01", "3:06", "2:06", "0:01", "1:06",
				"0:36", "0:36", "0:36", "0:36", "0:36", "0:36");
		for (Dauer d : r.getDauerListe())System.out.println(d.getMinuten());
		int summe = r.getSumme().getMinuten();
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 11, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("3:15"), r.getDauerListe().get(1));
		Assert.assertEquals(new Dauer("1:00"), r.getDauerListe().get(4));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(5));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(6));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(7));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(8));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(9));
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(10));
		Assert.assertTrue("Summe weicht zu sehr ab: " + summe + " ./. " + r.getSumme().getMinuten()
				+ "\nDifferenz: " + (summe - r.getSumme().getMinuten()),
				Math.abs(r.getSumme().getMinuten() - summe) < 15);
		Assert.assertEquals(new Dauer("2:15"), r.getDauerListe().get(2));
		Assert.assertEquals(new Dauer("0:15"), r.getDauerListe().get(3));
	}
}
