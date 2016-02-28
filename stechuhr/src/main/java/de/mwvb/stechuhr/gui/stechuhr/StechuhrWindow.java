package de.mwvb.stechuhr.gui.stechuhr;

import de.mwvb.stechuhr.Application;
import de.mwvb.stechuhr.VortagCheck;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.gui.Window;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class StechuhrWindow extends Window<StechuhrWindowController> {
	public static StechuhrModel model;
	
	@Override
	protected String getIcon() {
		return "StechuhrWindow.gif";
	}
	
	@Override
	protected void initWindow(Stage stage) {
		stage.setX(600);
		stage.setY(250);
		stage.setWidth(480);
		stage.setHeight(276);
		String version = System.getProperty("java.version");
		if (version == null) version = "";
		stage.setTitle(Application.APP_NAME + " " + Application.APP_VERSION + " (Java " + version.replace("1.8.0_", "8u") + ")");
		stage.setResizable(false);
	}
	
	@Override
	protected void keyBindings(Scene scene) {
		scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			final KeyCode code = event.getCode();
			if (KeyCode.F5.equals(code)) {
				controller.onPlay();
			} else if (KeyCode.F6.equals(code)) {
				controller.onPause();
			} else if (KeyCode.F8.equals(code)) {
				controller.onBearbeiten();
			} else if (KeyCode.CONTROL.equals(code)) {
				controller.hideQuickButtons();
			}
		});
		scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			final KeyCode code = event.getCode();
			if (KeyCode.ESCAPE.equals(code)) {
				controller.minimize();
			} else if (KeyCode.CONTROL.equals(code)) {
				controller.showQuickButtons();
			}
		});
	}
	

	@Override
	protected void displayed() {
		new VortagCheck().check(StechuhrWindow.model);
	}
	
	@Override
	protected int onClose() {
		return 2; // Fenster verstecken
	}

	@Override
	protected StechuhrWindowController createController() {
		return new StechuhrWindowController();
	}
}
