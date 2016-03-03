package de.mwvb.stechuhr.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.mwvb.stechuhr.AbstractStundenTest;
import de.mwvb.stechuhr.Application;
import de.mwvb.stechuhr.IStechuhrPlugin;

public class ExportstundenTest extends AbstractStundenTest {
	private List<Exportstunden> exportstunden;
	
	@Before
	public void init() {
		Application.plugin = getPlugin();
		model = new StechuhrModel(LocalDate.of(2016, 2, 20));
	}
	
	@Test
	public void basic() {
		// Prepare
		createStunden(LocalTime.of(8, 0), "A", "A-1");
		createStunden(LocalTime.of(9, 0), "B", "B-1");
		
		// Test
		model.stop(LocalTime.of(11, 30));
		
		Assert.assertEquals(2, exportstunden.size());
		Assert.assertEquals("1) Stunde falsch!", 1, exportstunden.get(0).getStunden());
		Assert.assertEquals("1) Stunde falsch!", 0, exportstunden.get(0).getMinuten());
		Assert.assertEquals("2) Stunde falsch!", 2, exportstunden.get(1).getStunden());
		Assert.assertEquals("2) Stunde falsch!", 30, exportstunden.get(1).getMinuten());
		Assert.assertEquals("2) Dezimalstunden falsch!", "2,50", exportstunden.get(1).getDezimaldauer());
		Assert.assertEquals("2) toFileString() falsch!", "20.02.2016 ; 2:30 ; 2,50 ; B ; B-1", exportstunden.get(1).toFileString());
	}

	@Test
	public void zusammenfassen() {
		// Prepare
		createStunden(LocalTime.of(8, 0), "A", "A-1");
		createStunden(LocalTime.of(8, 30), "B", "B-1");
		createStunden(LocalTime.of(11, 0), "A", "A-1");
		
		// Test
		model.stop(LocalTime.of(11, 30));
		
		Assert.assertEquals(2, exportstunden.size());
		Assert.assertEquals("1) Ticket falsch", "A", exportstunden.get(0).getTicket());
		Assert.assertEquals("2) Ticket falsch", "B", exportstunden.get(1).getTicket());
		Assert.assertEquals("1) Stunde falsch!", 1, exportstunden.get(0).getStunden());
		Assert.assertEquals("1) Stunde falsch!", 0, exportstunden.get(0).getMinuten());
		Assert.assertEquals("2) Stunde falsch!", 2, exportstunden.get(1).getStunden());
		Assert.assertEquals("2) Stunde falsch!", 30, exportstunden.get(1).getMinuten());
		Assert.assertEquals("2) Dezimalstunden falsch!", "2,50", exportstunden.get(1).getDezimaldauer());
		Assert.assertEquals("2) toFileString() falsch!", "20.02.2016 ; 2:30 ; 2,50 ; B ; B-1", exportstunden.get(1).toFileString());
	}

	private IStechuhrPlugin getPlugin() {
		return new IStechuhrPlugin() {
			@Override
			public void export(List<Exportstunden> pExportstunden) {
				exportstunden = pExportstunden;
			}
		};
	}
}
