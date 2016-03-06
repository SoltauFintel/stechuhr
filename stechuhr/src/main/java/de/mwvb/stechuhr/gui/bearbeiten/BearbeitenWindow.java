package de.mwvb.stechuhr.gui.bearbeiten;

import de.mwvb.stechuhr.gui.Window;
import javafx.stage.Stage;

public class BearbeitenWindow extends Window<BearbeitenWindowController> {

	@Override
	protected String getIcon() {
		return "BearbeitenWindow.gif";
	}
	
	@Override
	protected void initWindow(Stage stage) {
		stage.setWidth(800);
		stage.setHeight(530);
		stage.setMinWidth(600);
		stage.setMinHeight(350);
		stage.setTitle("Stunden bearbeiten");
		
		controller.model2View();
	}
	
	@Override
	protected BearbeitenWindowController createController() {
		return new BearbeitenWindowController();
	}
}
