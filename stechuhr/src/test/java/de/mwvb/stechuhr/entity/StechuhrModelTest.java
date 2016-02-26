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
}
