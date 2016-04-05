package de.mwvb.stechuhr;

import de.mwvb.stechuhr.gui.stechuhr.StechuhrWindow;
import de.mwvb.stechuhr.service.StechuhrConfig;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Stechuhr
 * 
 * <p>Startklasse, Java 8
 * 
 * @author Marcus Warm
 * @since 21.02.2016
 */
public class StechuhrApplication extends Application {
	public static final String APP_NAME = "Stechuhr";
	public static final String APP_VERSION = "0.4.1";
	private static StechuhrConfig config;
	
	public static void main(String[] args) {
		launch(StechuhrApplication.class, new String[] {});
	}

	@Override
	public void start(Stage stage) {
		new StechuhrWindow().show(stage, false, null);
	}
	
	public static StechuhrConfig getConfig() {
		return config;
	}

	public static void setConfig(StechuhrConfig pConfig) {
		config = pConfig;
	}
}
