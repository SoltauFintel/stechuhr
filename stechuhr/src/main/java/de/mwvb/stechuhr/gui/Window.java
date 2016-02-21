package de.mwvb.stechuhr.gui;

import java.io.IOException;

import com.sun.javafx.scene.control.skin.TextAreaSkin;

import de.mwvb.stechuhr.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Basis-Fensterklasse
 * 
 * @param <CTR> Controllerklasse
 */
public abstract class Window<CTR> {
	protected CTR controller;
	
	public final void show(final Stage stage, boolean modal) {
		stage.getIcons().add(new Image(getClass().getResourceAsStream(getIcon())));
		Scene scene = new Scene(root());
		stage.setScene(scene);
		keyBindings(scene);
		initWindow(stage);
		Application.config.loadWindowPosition(getName(), stage);
		onCloseRequest(stage);
		if (modal) {
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} else {
			stage.show();
		}
	}

	private void onCloseRequest(final Stage stage) {
		stage.setOnCloseRequest(event -> {
			int mode = onClose();
			if (mode == 1) { // Fenster ganz normal schließen
				Application.config.saveWindowPosition(getName(), stage);
			} else {
				event.consume(); // Fenster nicht schließen
				if (mode == 2) { // Fenster ausblenden
					stage.setIconified(true);
				}
			}
		});
	}
	
	protected String getIcon() {
		return getClass().getSimpleName() + ".png";
	}

	protected void initWindow(Stage stage) {
		// Template-Methode
	}
	
	protected void keyBindings(Scene scene) {
		// Template-Methode
	}

	protected String getName() {
		return getClass().getSimpleName();
	}
	
	protected int onClose() {
		return 1; // Schlie�en ok
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
	
	public static void errorAlert(Exception ex) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Fehler");
		alert.setHeaderText("");
		alert.setContentText(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		alert.showAndWait();
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
	
	public static void alert(String meldung) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Meldung");
		alert.setHeaderText("");
		alert.setContentText(meldung);
		alert.showAndWait();
	}
}
