package de.mwvb.stechuhr.service.stundenrundung;

import org.junit.Assert;
import org.junit.Test;

import de.mwvb.stechuhr.service.stundenrundung.Dauer;

public class DauerTest {

	@Test
	public void add() {
		// Prepare
		Dauer a = new Dauer("0:51");
		Dauer b = new Dauer("3:27");
		
		// Test
		Dauer summe = a.add(b);
		
		// Verify
		Assert.assertEquals("4:18", summe.toString());
		Assert.assertEquals("andersrum muss es auch 4:18 ergeben", new Dauer("4:18"), b.add(a)); // auch andersrum
		Assert.assertEquals(258, summe.getMinuten());
		Assert.assertEquals(18, summe.getMinutenTeil());
		Assert.assertEquals(3, b.getStundenTeil());
	}

	@Test
	public void ohneMinuten() {
		// Prepare
		Dauer a = new Dauer("1");
		Dauer b = new Dauer(2, 0);
		
		// Test + Verify
		Assert.assertEquals(new Dauer("3:00"), a.add(b));
	}
}
