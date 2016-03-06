package de.mwvb.stechuhr;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.gui.stechuhr.StechuhrWindow;
import de.mwvb.stechuhr.service.StechuhrConfig;
import de.mwvb.stechuhr.service.export.CSVExport;
import de.mwvb.stechuhr.service.export.ExportManager;
import de.mwvb.stechuhr.service.export.HTMLExport;
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
	public static final String APP_VERSION = "0.3";
	private static StechuhrConfig config;
	
	public static void main(String[] args) {
		StechuhrDAO.init();
		config = new StechuhrConfig();
		StechuhrWindow.setModel(new StechuhrDAO().load(StechuhrModel.today()));
		launch(StechuhrApplication.class, new String[] {});
	}

	@Override
	public void start(Stage stage) {
		initExporteure();
		new StechuhrWindow().show(stage, false, null);
	}
	
	protected void initExporteure() {
		ExportManager.getInstance().register(new CSVExport());
		ExportManager.getInstance().register(new HTMLExport());
	}
	
	public static StechuhrConfig getConfig() {
		return config;
	}
}
