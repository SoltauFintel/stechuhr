package de.mwvb.stechuhr;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import de.mwvb.stechuhr.entity.Dauer;

@Ignore // TODO Implementierung fehlt noch
public class StundenrundungTest {

	@Test
	public void nurEineDauer_aufrunden() {
		Stundenrundung r = new Stundenrundung("0:29");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 1, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
	}

	@Test
	public void nurEineDauer_abrunden() {
		Stundenrundung r = new Stundenrundung("0:37");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 1, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
	}

	@Test
	public void einzelnWuerdeAbgerundetWerden() {
		Stundenrundung r = new Stundenrundung("0:37", "0:37");
		
		r.ausgleichung(15);

		Assert.assertEquals("Anzahl falsch!", 2, r.getDauerListe().size());
		Assert.assertEquals(new Dauer("0:30"), r.getDauerListe().get(0));
		Assert.assertEquals(new Dauer("0:45"), r.getDauerListe().get(1));
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
}
