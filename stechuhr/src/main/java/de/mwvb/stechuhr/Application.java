package de.mwvb.stechuhr;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.gui.stechuhr.StechuhrWindow;
import javafx.stage.Stage;

/**
 * Stechuhr
 * 
 * <p>Startklasse, Java 8
 * 
 * @author Marcus Warm
 * @since 21.02.2016
 */
public class Application extends javafx.application.Application {
	public static final String APP_NAME = "Stechuhr";
	public static final String APP_VERSION = "0.2-s";
	public static StechuhrConfig config;
	
	public static void main(String[] args) {
		StechuhrDAO.init();
		config = new StechuhrConfig();
		StechuhrWindow.model = new StechuhrDAO().load(StechuhrModel.today());
		launch(Application.class, new String[] {});
	}

	@Override
	public void start(Stage stage) {
		new StechuhrWindow().show(stage, false);
	}
}
