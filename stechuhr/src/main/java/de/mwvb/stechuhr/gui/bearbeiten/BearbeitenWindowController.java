package de.mwvb.stechuhr.gui.bearbeiten;

import java.time.LocalTime;
import java.util.Optional;

import de.mwvb.stechuhr.Application;
import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.gui.Window;
import de.mwvb.stechuhr.gui.stechuhr.StechuhrWindow;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Stechuhr-Einträge bearbeiten
 * 
 * @author Marcus Warm
 */
public class BearbeitenWindowController {
	@FXML
	private TextField uhrzeit;
	@FXML
	private TextField leistung;
	@FXML
	private TextField ticket;
	@FXML
	private TextArea notizPrivat;
	@FXML
	private Button save;
	@FXML
	private Button delete;
	@FXML
	private Button close;
	@FXML
	private TableView<Stunden> grid;
	
	@FXML
	protected void initialize() {
		try {
			Window.disableTabKey(notizPrivat);
			save.setDefaultButton(true);
			
			grid.getSelectionModel().selectedItemProperty().addListener((a, b, sel) -> {
				display((Stunden) sel);
			});
			
			Platform.runLater(() -> leistung.requestFocus()); // Annahme: Man möchte i.d.R. eine Leistung eingeben
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	public void model2View() {
		try {
			getStage().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				KeyCode code = event.getCode();
				if (KeyCode.ESCAPE.equals(code)) {
					event.consume();
					onClose();
				}
			});

			StechuhrWindow.model.calculateDauer();
			grid.getItems().addAll(StechuhrWindow.model.getStundenliste());
			final int index = grid.getItems().size() - 1;
			if (index >= 0) {
				grid.getSelectionModel().select(index);
				Platform.runLater(() -> grid.scrollTo(index));
			}
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
	private void display(Stunden s) {
		try {
			if (s == null) return;
			if (s.getUhrzeit() != null) {
				uhrzeit.setText(s.getUhrzeit().toString());
			} else {
				uhrzeit.setText("");
			}
			ticket.setText(s.getTicket());
			leistung.setText(s.getLeistung());
			notizPrivat.setText(s.getNotizPrivat());
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	@FXML
	public void onSave() {
		try {
			int i = grid.getSelectionModel().getSelectedIndex();
			if (i < 0) return;
			Stunden s = grid.getItems().get(i);
			if (s == null) return;

			// Validierung
			String ut = validateUhrzeit(i);
			if (ut == null) return;
			String nr = ticket.getText().trim();
			if (nr.isEmpty()) {
				Window.alert("Bitte Ticketnummer eingeben!");
				return;
			}
			
			// Eingaben übernehmen
			s.setUhrzeit(LocalTime.parse(ut));
			s.setTicket(nr);
			s.setLeistung(leistung.getText().trim());
			s.setNotizPrivat(notizPrivat.getText());

			updateGrid_andSave();
			
			uhrzeit.setText(ut);
			ticket.setText(nr);
			leistung.setText(s.getLeistung());
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	private String validateUhrzeit(int i) {
		// Uhrzeit valide?
		String ut = uhrzeit.getText().trim();
		ut = validateUhrzeit(ut);
		if (ut == null) {
			return null;
		}
		
		// Nicht vor Vorgängeruhrzeit?
		LocalTime davor = LocalTime.MIDNIGHT;
		if (i > 0) {
			davor = grid.getItems().get(i - 1).getUhrzeit();
		}
		if (LocalTime.parse(ut).isBefore(davor)) {
			Window.alert("Bitte gebe eine Uhrzeit nach " + davor.toString() + " ein!"
					+ "\nAlternativ kann auch der Vorgängerdatensatz geändert werden.");
			return null;
		}
		return ut;
	}
	
	/**
	 * Eingegebene Uhrzeit validieren.
	 * @param uhrzeit eingegebene Uhrzeit
	 * @return formatierte Uhrzeit, oder null wenn die Uhrzeit nicht ok ist. Es wurde in dem Fall eine entsprechende Meldung ausgegeben.
	 */
	public static String validateUhrzeit(String uhrzeit) {
		uhrzeit = uhrzeit.replace(",", ":"); // Eingabevereinfachung
		if ((uhrzeit.length() == "S:MM".length() && uhrzeit.charAt(1) == ':') || (uhrzeit.length() == "S".length() && uhrzeit.charAt(0) != ':')) {
			uhrzeit = "0" + uhrzeit;
		}
		if (!uhrzeit.contains(":")) { // Aus Kurzfurm "9" wird "09:00".
			try {
				int h = Integer.parseInt(uhrzeit);
				if ("0".equals(uhrzeit) || h > 0) {
					uhrzeit += ":00";
				}
			} catch (NumberFormatException ignore) { //
			}
		}
		try {
			LocalTime ret = LocalTime.parse(uhrzeit).withSecond(0).withNano(0);
			return ret.toString();
		} catch (Exception e) {
			Window.alert("Bitte gebe eine Uhrzeit im Format SS:MM ein!");
			return null;
		}
	}
	
	@FXML
	public void onDelete() {
		try {
			Stunden s = grid.getSelectionModel().getSelectedItem();
			if (s == null) {
				return;
			}
			if (shallDelete(s.getTicket())) {
				StechuhrWindow.model.getStundenliste().remove(s);
				grid.getItems().remove(s);
	
				updateGrid_andSave();
			}
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
	private boolean shallDelete(String ticket) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Löschen");
		alert.setHeaderText("");
		alert.setContentText("Hiermit löschst Du den " + ticket + " Datensatz.");
		// TODO Buttons beschriften: Löschen - Nicht löschen
		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}
	
	private void updateGrid_andSave() {
		StechuhrWindow.model.calculateDauer();
		
		grid.getColumns().get(0).setVisible(false);  // Workaround f�r Refresh der Zeile
		grid.getColumns().get(0).setVisible(true);
		
		new StechuhrDAO().save(StechuhrWindow.model);
	}
	
	@FXML
	public void onClose() {
		try {
			Application.config.saveWindowPosition(BearbeitenWindow.class.getSimpleName(), getStage());
			getStage().close();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
	private Stage getStage() {
		return (Stage) uhrzeit.getScene().getWindow();
	}
}
