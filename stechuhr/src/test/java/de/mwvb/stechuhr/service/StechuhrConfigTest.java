package de.mwvb.stechuhr.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.gui.StageAdapter;
import de.mwvb.stechuhr.gui.StageAdapterForTest;

public class StechuhrConfigTest {

	@BeforeClass
	public static void init() {
		StechuhrDAO.init();
	}

	@Before
	@After
	public void cleanup() {
		new StechuhrDAO().delete("WindowPosition-TEST1");
	}

	@Test
	public void testWindowPos() {
		// Prepare
		StageAdapter stage = new StageAdapterForTest();
		stage.setResizable(true);
		stage.setX(10);
		stage.setY(20);
		stage.setHeight(100);
		stage.setWidth(200);
		StageAdapter loaded = new StageAdapterForTest();
		
		// Test
		new StechuhrConfig().saveWindowPosition("TEST1", stage);
		new StechuhrConfig().loadWindowPosition("TEST1", loaded, null);
		
		// Verify
		Assert.assertEquals("X", 10d, loaded.getX(), 0.01d);
		Assert.assertEquals("Y", 20d, loaded.getY(), 0.01d);
		Assert.assertEquals("w", 200d, loaded.getWidth(), 0.01d);
		Assert.assertEquals("h", 100d, loaded.getHeight(), 0.01d);
	}
	
	@Test
	public void testWindowPos_minimized() {
		// Prepare
		StageAdapter stage = new StageAdapterForTest();
		stage.setX(10);
		stage.setY(20);
		stage.setHeight(100);
		stage.setWidth(200);
		new StechuhrConfig().saveWindowPosition("TEST1", stage);
		
		// Test
		stage.setX(30);
		stage.setIconified(true);
		new StechuhrConfig().saveWindowPosition("TEST1", stage);

		// Verify
		StageAdapter loaded = new StageAdapterForTest();
		new StechuhrConfig().loadWindowPosition("TEST1", loaded, null);
		Assert.assertEquals("Muss alter X Wert sein, da WindowsPos von minimierten Fenstern nicht gespeichert werden kann!", 10d, loaded.getX(), 0.01d);
	}

	@Test
	public void testWindowPos_notResizable() {
		// Prepare
		StageAdapter stage = new StageAdapterForTest();
		stage.setX(11);
		stage.setY(20);
		stage.setHeight(100);
		stage.setWidth(200);
		stage.setResizable(false);
		StageAdapter loaded = new StageAdapterForTest();
		loaded.setResizable(false);
		
		// Test
		new StechuhrConfig().saveWindowPosition("TEST1", stage);
		new StechuhrConfig().loadWindowPosition("TEST1", loaded, null);
		
		// Verify
		Assert.assertEquals("X", 11d, loaded.getX(), 0.01d);
		Assert.assertEquals("Y", 20d, loaded.getY(), 0.01d);
	}
}
