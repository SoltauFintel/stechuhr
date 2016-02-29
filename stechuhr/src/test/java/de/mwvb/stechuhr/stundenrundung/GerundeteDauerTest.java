package de.mwvb.stechuhr.stundenrundung;

import org.junit.Assert;
import org.junit.Test;

/**
 * Durchführung von Rundungen testen
 */
public class GerundeteDauerTest {

	@Test
	public void runde0() {
		Dauer dauer = Dauer.ZERO;
		Assert.assertEquals(Dauer.ZERO, GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde1() {
		Dauer dauer = new Dauer(1);
		Assert.assertEquals(Dauer.ZERO, GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde6() {
		Dauer dauer = new Dauer(6);
		Assert.assertEquals(Dauer.ZERO, GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde7() {
		Dauer dauer = new Dauer(7);
		Assert.assertEquals(new Dauer(15), GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde8() {
		Dauer dauer = new Dauer(8);
		Assert.assertEquals(new Dauer(15), GerundeteDauer.runde(dauer, 15));
	}
	
	@Test
	public void runde14() {
		Dauer dauer = new Dauer(14);
		Assert.assertEquals(new Dauer(15), GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde15() {
		Dauer dauer = new Dauer(15);
		Assert.assertEquals(new Dauer(15), GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde16() {
		Dauer dauer = new Dauer(16);
		Assert.assertEquals(new Dauer(15), GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde32() {
		Dauer dauer = new Dauer(32);
		Assert.assertEquals(new Dauer(30), GerundeteDauer.runde(dauer, 15));
	}
	
	@Test
	public void runde59() {
		Dauer dauer = new Dauer(59);
		Assert.assertEquals(new Dauer(60), GerundeteDauer.runde(dauer, 15));
	}
	
	@Test
	public void runde63() {
		Dauer dauer = new Dauer(63);
		Assert.assertEquals(new Dauer(60), GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde232() {
		Dauer dauer = new Dauer(240 - 8);
		Assert.assertEquals(new Dauer(240), GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde42_auf15() {
		Dauer dauer = new Dauer(42);
		Assert.assertEquals(new Dauer(45), GerundeteDauer.runde(dauer, 15));
	}

	@Test
	public void runde42_auf30() {
		Dauer dauer = new Dauer(42);
		Assert.assertEquals(new Dauer(30), GerundeteDauer.runde(dauer, 30));
	}

	@Test
	public void runde42_auf60() {
		Dauer dauer = new Dauer(42);
		Assert.assertEquals(new Dauer(60), GerundeteDauer.runde(dauer, 60));
	}

	@Test
	public void rundeAuf() {
		Dauer dauer = new Dauer(42);
		GerundeteDauer erg = GerundeteDauer.runde(dauer, 15).rundeAuf(15);
		Assert.assertEquals(new Dauer(60), erg);
		Assert.assertEquals("getUngerundet muss alten Wert 42 liefern", 42, erg.getUngerundet().getMinuten());
	}

	@Test
	public void rundeAb() {
		Dauer dauer = new Dauer(42);
		GerundeteDauer erg = GerundeteDauer.runde(dauer, 15).rundeAb(15);
		Assert.assertEquals(new Dauer(30), erg);
		Assert.assertEquals("getUngerundet muss alten Wert 42 liefern", 42, erg.getUngerundet().getMinuten());
	}
}
