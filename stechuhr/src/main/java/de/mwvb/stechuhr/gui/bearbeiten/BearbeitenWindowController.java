package de.mwvb.stechuhr.gui.bearbeiten;

import java.time.LocalTime;
import java.util.Optional;

import de.mwvb.stechuhr.Application;
import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.gui.StageAdapter;
import de.mwvb.stechuhr.gui.Window;
import de.mwvb.stechuhr.gui.stechuhr.StechuhrWindow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
	private ComboBox<String> leistung;
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
			addDeleteKeyListener();
			addSaveButtonListeners();
			Platform.runLater(() -> leistung.requestFocus()); // Annahme: Man möchte i.d.R. eine Leistung eingeben
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	private void addDeleteKeyListener() {
		grid.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			KeyCode code = event.getCode();
			if (KeyCode.DELETE.equals(code)) {
				onDelete();
			}
		});
	}

	private void addSaveButtonListeners() {
		grid.getSelectionModel().selectedItemProperty().addListener((a, b, sel) -> {
			display((Stunden) sel);
			save.setDisable(true);
		});
		save.setDisable(true);
		ChangeListener<? super String> listener = (observable, oldValue, newValue) -> {
			if (!grid.getSelectionModel().getSelectedIndices().isEmpty()) {
				save.setDisable(false);
			}
		};
		this.uhrzeit.textProperty().addListener(listener);
		this.ticket.textProperty().addListener(listener);
		this.leistung.getEditor().textProperty().addListener(listener); // Texteingabe
		this.leistung.valueProperty().addListener(listener); // Auswahl in Liste
		this.notizPrivat.textProperty().addListener(listener);
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
			} else {
				delete.setDisable(true);
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
			leistung.getEditor().setText(s.getLeistung());
			notizPrivat.setText(s.getNotizPrivat());
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	@FXML
	public void onSave() {
		try {
			int i = grid.getSelectionModel().getSelectedIndex();
			if (i < 0) {
				return; // Nicht speichern, wenn kein Stechuhreintrag gewählt ist.
			}
			Stunden stunden = grid.getItems().get(i);
			if (stunden == null) {
				return; // Nicht speichern, wenn seltsamerweise der Stechuhreintrag fehlt.
			}

			// Validierung
			String eingegebeneUhrzeit = validateUhrzeit(i);
			if (eingegebeneUhrzeit == null) {
				return; // Nicht speichern, wenn Validierung nicht ok.
			}
			String nr = validateTicket(ticket.getText());
			if (nr == null) {
				return; // Nicht speichern, wenn Validierung nicht ok.
			}
			
			// Eingaben übernehmen
			stunden.setUhrzeit(LocalTime.parse(eingegebeneUhrzeit));
			stunden.setTicket(nr);
			stunden.setLeistung(leistung.getEditor().getText().trim());
			stunden.setNotizPrivat(notizPrivat.getText());

			updateGrid_andSave();
			
			// Evtl. umformatierte Eingaben anzeigen
			uhrzeit.setText(eingegebeneUhrzeit);
			ticket.setText(nr);
			leistung.getEditor().setText(stunden.getLeistung());
			save.setDisable(true);
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	/**
	 * @param i Index des aktuell gewählten Stechuhreintrags
	 * @return null wenn Validierung nicht ok
	 */
	private String validateUhrzeit(int i) {
		// Uhrzeit valide?
		String eingegebeneUhrzeit = uhrzeit.getText().trim();
		eingegebeneUhrzeit = validateUhrzeit(eingegebeneUhrzeit); // Format der Uhrzeit validieren.
		if (eingegebeneUhrzeit == null) {
			return null; // Validierung nicht ok.
		}
		
		// weitere Validierung: Nicht vor Vorgängeruhrzeit?
		LocalTime davor = LocalTime.MIDNIGHT;
		if (i > 0) {
			davor = grid.getItems().get(i - 1).getUhrzeit();
		}
		if (LocalTime.parse(eingegebeneUhrzeit).isBefore(davor)) {
			Window.alert("Bitte gebe eine Uhrzeit nach " + davor.toString() + " ein!"
					+ "\nAlternativ kann auch der Vorgängerdatensatz geändert werden.");
			return null;
		}
		return eingegebeneUhrzeit;
	}
	
	/**
	 * Eingegebene Uhrzeit validieren.
	 * @param uhrzeit eingegebene Uhrzeit
	 * @return formatierte Uhrzeit, oder null wenn die Uhrzeit nicht ok ist. Es wurde in dem Fall eine entsprechende Meldung ausgegeben.
	 */
	public static String validateUhrzeit(String uhrzeit) {
		uhrzeit = uhrzeit.replace(",", ":"); // Eingabevereinfachung
		// TODO Idee: "SMM" und "SSMM" Eingabe auch erlauben
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
	
	/**
	 * Eingegebene Ticketnummer validieren
	 * @param ticket eingegebene Ticketnummer
	 * @return Ticket wenn ok, sonst null. Es wurde in dem Fall eine entsprechende Meldung ausgegeben.
	 */
	public static String validateTicket(String ticket) {
		ticket = ticket.trim();
		if (ticket.isEmpty()) {
			Window.alert("Bitte Ticketnummer eingeben!");
			return null;
		} else if (ticket.contains(";")) {
			Window.alert("Das Zeichen \";\" ist in der Ticketnummer nicht erlaubt!");
			return null;
		}
		return ticket;
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
				if (grid.getItems().isEmpty()) {
					uhrzeit.setText("");
					ticket.setText("");
					leistung.getEditor().setText("");
					notizPrivat.setText("");
					save.setDisable(true);
					delete.setDisable(true);
				} else {
					int neuerIndex = grid.getSelectionModel().getSelectedIndex() + 1;
					if (neuerIndex >= grid.getItems().size()) {
						neuerIndex = grid.getItems().size() - 1;
					}
					grid.getSelectionModel().select(neuerIndex);
				}
	
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
		alert.setContentText("Hiermit l\u00F6schst Du den " + ticket + " Datensatz.");
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
			Application.config.saveWindowPosition(BearbeitenWindow.class.getSimpleName(), new StageAdapter(getStage()));
			getStage().close();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
	private Stage getStage() {
		return (Stage) uhrzeit.getScene().getWindow();
	}
}
