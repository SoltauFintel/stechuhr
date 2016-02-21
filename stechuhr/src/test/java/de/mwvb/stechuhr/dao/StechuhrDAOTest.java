package de.mwvb.stechuhr.dao;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;

public class StechuhrDAOTest {

	@BeforeClass
	public static void setup() {
		StechuhrDAO.init();
	}
	
	@Test
	public void saveAndLoadStechuhrModel() {
		// Prepare
		LocalDate tag = LocalDate.of(2000, 1, 2);
		StechuhrModel model = new StechuhrModel(tag);
		Stunden s = new Stunden();
		s.setTag(tag);
		s.setUhrzeit(LocalTime.of(11, 14));
		s.setTicket("4711");
		s.setLeistung("was getan");
		s.setNotizPrivat("privat\n1");
		model.getStundenliste().add(s);
		
		// Test
		new StechuhrDAO().delete(model);
		new StechuhrDAO().save(model);
		StechuhrModel m = new StechuhrDAO().load(tag);
		
		// Verify
		Assert.assertEquals("Anzahl falsch", 1, m.getStundenliste().size());
		Assert.assertEquals("4711", m.getStundenliste().get(0).getTicket());
		Assert.assertEquals("was getan", m.getStundenliste().get(0).getLeistung());
		Assert.assertEquals("privat\n1", m.getStundenliste().get(0).getNotizPrivat());
		Assert.assertNotNull("Uhrzeit nicht belegt", m.getStundenliste().get(0).getUhrzeit());
		Assert.assertEquals("Uhrzeit (Stunde) falsch", 11, m.getStundenliste().get(0).getUhrzeit().getHour());
		Assert.assertEquals(14, m.getStundenliste().get(0).getUhrzeit().getMinute());
		Assert.assertEquals(0, m.getStundenliste().get(0).getUhrzeit().getSecond());
		Assert.assertEquals(tag, m.getStundenliste().get(0).getTag());
		
		// cleanup
		new StechuhrDAO().delete(model);
	}
}
