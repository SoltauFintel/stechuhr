package de.mwvb.stechuhr.dao;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;

@Ignore
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
	
	@Test
	public void testStechuhrDateiNichtGefunden() {
		// Test
		StechuhrModel model = new StechuhrDAO().load(LocalDate.of(1975, 2, 20));
		
		// Verify
		Assert.assertNotNull(model);
		Assert.assertEquals(0, model.getStundenliste().size());
	}
	
	@Test
	public void testGetStechuhrModelFile() {
		// Test
		File file = new StechuhrDAO().getStechuhrModelFile(LocalDate.of(1970, 12, 30));
		
		// Verify
		String dn = file.getAbsolutePath().replace("\\", "/");
		Assert.assertTrue("Dateiname falsch: " + dn, dn.endsWith("1970/12/1970-12-30.xml"));
		Assert.assertFalse("Es wird eigentlich erwartet, dass die Datei nicht vorhanden ist: " + dn,
				new StechuhrDAO().existsStechuhrModelFile(LocalDate.of(1970, 12, 30)));
		
	}
	
	@Test
	public void testSaveAndLoad() {
		// Prepare
		StechuhrDAO access = new StechuhrDAO();
		List<String> list = new ArrayList<>();
		list.add("1=oans");
		list.add(" 2 = zwoa\t ");
		
		// Test
		access.save("TEST", list);
		List<String> loaded = access.load("TEST");
		
		// Verify
		Assert.assertEquals("1=oans", loaded.get(0));
		Assert.assertEquals(" 2 = zwoa\t ", loaded.get(1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSaveOhneDateiname() {
		new StechuhrDAO().save("", new ArrayList<String>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveMitDateinameNull() {
		new StechuhrDAO().save(null, new ArrayList<String>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveMitDataNull() {
		new StechuhrDAO().save("TEST", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoadOhneDateiname() {
		new StechuhrDAO().load("");
	}
}
