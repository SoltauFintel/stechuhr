package de.mwvb.stechuhr.gui;

import java.io.IOException;
import java.io.InputStream;

import com.sun.javafx.scene.control.skin.TextAreaSkin;

import de.mwvb.stechuhr.StechuhrApplication;
import de.mwvb.stechuhr.base.StechuhrUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Basis-Fensterklasse
 * 
 * @param <CTR> Controllerklasse
 */
public abstract class Window<CTR> {
	protected CTR controller;
	public static boolean testmodus = false;
	
	public final void show(final Stage stage, boolean modal, final javafx.stage.Window owner) {
		stage.getIcons().add(new Image(getIcon()));
		Scene scene = new Scene(root());
		stage.setScene(scene);
		if (owner != null) {
			stage.initOwner(owner);
		}
		keyBindings(scene);
		initWindow(stage);
		StechuhrApplication.getConfig().loadWindowPosition(getName(), new StageAdapter(stage));
		onCloseRequest(stage);
		if (modal) {
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} else {
			stage.show();
			displayed();
		}
	}
	
	/** Liefert Stream des Fenstericons */
	protected abstract InputStream getIcon();

	protected void keyBindings(Scene scene) {
		// Template-Methode
	}

	protected void initWindow(Stage stage) {
		// Template-Methode
	}
	
	public static void disableTabKey(final TextArea textArea) {
		textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode().equals(KeyCode.TAB)) {
				TextAreaSkin skin = (TextAreaSkin) textArea.getSkin();
				if (event.isShiftDown()) {
					skin.getBehavior().traversePrevious();
				} else {
					skin.getBehavior().traverseNext();
				}
				event.consume();
			}
		});
	}

	protected String getName() {
		return getClass().getSimpleName();
	}

	protected void displayed() {
		// Template Methode
	}
	
	private void onCloseRequest(final Stage stage) {
		stage.setOnCloseRequest(event -> {
			int mode = onClose();
			if (mode == 1) { // Fenster ganz normal schließen
				StechuhrApplication.getConfig().saveWindowPosition(getName(), new StageAdapter(stage));
			} else {
				event.consume(); // Fenster nicht schließen
				if (mode == 2) { // Fenster ausblenden
					stage.setIconified(true);
				}
			}
		});
	}

	/**
	 * @return 0: Fenster nicht schließen, 1: Fenster ganz normal schließen, 2: Fenster ausblenden
	 */
	protected int onClose() {
		return 1; // Schließen ok
	}
	
	protected Parent root() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(getClass().getSimpleName() + ".fxml"));
			controller = createController();
			loader.setController(controller);
			return loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected abstract CTR createController();
	
	public static void alert(String meldung) {
		if (testmodus) return;
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Meldung");
		alert.setHeaderText("");
		alert.setContentText(meldung);
		alert.showAndWait();
	}
	
	public static void errorAlert(Exception ex) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Entschuldigung, das hätte nicht passieren dürfen.");
		alert.setHeaderText("Die Stechuhr hat ein Problem festgestellt.");
		if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
			alert.setContentText("Es ist ein " + ex.getClass().getSimpleName() + " Fehler aufgetreten.");
		} else {
			alert.setContentText(ex.getMessage());
		}
		
		// ausklappbarer Bereich mit Stacktrace
		Label label = new Label("Details:");
		TextArea textArea = new TextArea(StechuhrUtils.getExceptionText(ex));
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setExpandableContent(expContent);
		
		alert.showAndWait();
	}
}
