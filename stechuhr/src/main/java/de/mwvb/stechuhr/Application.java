package de.mwvb.stechuhr;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.export.CSVExport;
import de.mwvb.stechuhr.export.ExportManager;
import de.mwvb.stechuhr.export.HTMLExport;
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
public class Application extends javafx.application.Application { // TODO rename to StechuhrApplication (FindBugs)
	public static final String APP_NAME = "Stechuhr";
	public static final String APP_VERSION = "0.3";
	public static StechuhrConfig config; // TODO private machen + public Getter (FindBugs)
	
	public static void main(String[] args) {
		StechuhrDAO.init();
		config = new StechuhrConfig();
		StechuhrWindow.model = new StechuhrDAO().load(StechuhrModel.today());
		launch(Application.class, new String[] {});
	}

	@Override
	public void start(Stage stage) {
		initExporteure();
		new StechuhrWindow().show(stage, false);
	}
	
	protected void initExporteure() {
		ExportManager.getInstance().register(new CSVExport());
		ExportManager.getInstance().register(new HTMLExport());
	}
}
