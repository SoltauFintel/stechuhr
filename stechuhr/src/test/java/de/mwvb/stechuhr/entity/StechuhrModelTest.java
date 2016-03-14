package de.mwvb.stechuhr.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Test;

import de.mwvb.stechuhr.AbstractStundenTest;

public class StechuhrModelTest extends AbstractStundenTest {

	@Test
	public void testCalculateDauer() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2016, 2, 20));
		Stunden s2 = createStunden(LocalTime.of(10, 4), "B", "");
		Stunden s1 = createStunden(LocalTime.of(8, 1), "A", "");
		
		// Test
		model.calculateDauer();
		
		// Verify
		Assert.assertEquals("2:03", s1.getDauer());
		Assert.assertNotNull(s2.getDauer());
	}
	
	@Test
	public void testGetWTTagString() {
		model = new StechuhrModel(LocalDate.of(2016, 2, 1));
		Assert.assertEquals("Montag 01.02.2016", model.getWTTagString());
	}
	
	@Test
	public void testStundensumme() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2016, 3, 14));
		createStunden(LocalTime.of(8, 3), "A", "");
		createStunden(LocalTime.of(9, 15), Stunden.PAUSE, "");
		createStunden(LocalTime.of(9, 30), "A", "");
		createStunden(LocalTime.of(12, 0), "B", "");
		
		// Test
		Dauer dauer = model.getDauerOhnePausen(LocalTime.of(15, 0));
		
		// Verify
		Assert.assertEquals(new Dauer("6:42"), dauer);
	}
	
	@Test
	public void testStundensumme_letztesIstPause() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2016, 3, 14));
		createStunden(LocalTime.of(8, 3), "A", "");
		createStunden(LocalTime.of(9, 15), Stunden.PAUSE, "");
		createStunden(LocalTime.of(9, 30), "A", "");
		createStunden(LocalTime.of(12, 0), Stunden.PAUSE, "");
		
		// Test
		Dauer dauer = model.getDauerOhnePausen(LocalTime.of(15, 0));
		
		// Verify
		Assert.assertEquals(new Dauer("3:42"), dauer);
	}
	
	@Test
	public void testStundensumme_nowIstMittendrin() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2016, 3, 14));
		createStunden(LocalTime.of(8, 0), "A", "");
		createStunden(LocalTime.of(10, 0), "B", "");
		
		// Test
		Dauer dauer = model.getDauerOhnePausen(LocalTime.of(9, 0));
		
		// Verify
		Assert.assertEquals(new Dauer("1:00"), dauer);
	}

	@Test
	public void testStundensumme_nowIstMittendrinInPause() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2016, 3, 14));
		createStunden(LocalTime.of(6, 0), "A", "");
		createStunden(LocalTime.of(8, 0), Stunden.PAUSE, "");
		
		// Test
		Dauer dauer = model.getDauerOhnePausen(LocalTime.of(9, 0));
		
		// Verify
		Assert.assertEquals(new Dauer("2:00"), dauer);
	}

	@Test
	public void testFeierabend() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2016, 3, 14));
		createStunden(LocalTime.of(8, 15), "A", "");
		
		// Test
		LocalTime fa = model.getFeierabendUhrzeit();
		
		// Verify
		Assert.assertEquals("16:15", fa.toString());
	}

	@Test
	public void testFeierabend_mitPause() {
		// Prepare
		model = new StechuhrModel(LocalDate.of(2016, 3, 14));
		createStunden(LocalTime.of(8, 0), "A", "");
		createStunden(LocalTime.of(11, 45), Stunden.PAUSE, "");
		createStunden(LocalTime.of(12, 0), "A", "");
		createStunden(LocalTime.of(13, 0), "B", "");
		
		// Test
		LocalTime fa = model.getFeierabendUhrzeit();
		
		// Verify
		Assert.assertEquals("16:15", fa.toString());
	}
}
