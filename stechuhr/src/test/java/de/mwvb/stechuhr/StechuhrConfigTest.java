package de.mwvb.stechuhr;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import javafx.stage.Stage;

public class StechuhrConfigTest {
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule(); // wegen Stage Klasse erforderlich
	
	@Test
	public void testWindowPos() {
		// Prepare
		StechuhrDAO.init();
		Stage stage = new Stage();
		stage.setResizable(true);
		stage.setX(10);
		stage.setY(20);
		stage.setHeight(100);
		stage.setWidth(200);
		Stage loaded = new Stage();
		
		// Test
		new StechuhrConfig().saveWindowPosition("TEST1", stage);
		new StechuhrConfig().loadWindowPosition("TEST1", loaded);
		
		// Verify
		Assert.assertEquals("X", 10d, loaded.getX(), 0.01d);
		Assert.assertEquals("Y", 20d, loaded.getY(), 0.01d);
		Assert.assertEquals("w", 200d, loaded.getWidth(), 0.01d);
		Assert.assertEquals("h", 100d, loaded.getHeight(), 0.01d);
	}
	
	@Test
	public void testWindowPos_minimized() {
		// Prepare
		StechuhrDAO.init();
		Stage stage = new Stage();
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
		Stage loaded = new Stage();
		new StechuhrConfig().loadWindowPosition("TEST1", loaded);
		Assert.assertEquals("Muss alter X Wert sein, da WindowsPos von minimierten Fenstern nicht gespeichert werden kann!", 10d, loaded.getX(), 0.01d);
	}

	@Test
	public void testWindowPos_notResizable() {
		// Prepare
		StechuhrDAO.init();
		Stage stage = new Stage();
		stage.setX(11);
		stage.setY(20);
		stage.setHeight(100);
		stage.setWidth(200);
		stage.setResizable(false);
		Stage loaded = new Stage();
		loaded.setResizable(false);
		
		// Test
		new StechuhrConfig().saveWindowPosition("TEST1", stage);
		new StechuhrConfig().loadWindowPosition("TEST1", loaded);
		
		// Verify
		Assert.assertEquals("X", 11d, loaded.getX(), 0.01d);
		Assert.assertEquals("Y", 20d, loaded.getY(), 0.01d);
	}
}
