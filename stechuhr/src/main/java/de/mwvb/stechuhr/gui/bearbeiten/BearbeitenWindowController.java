package de.mwvb.stechuhr.gui.bearbeiten;

import java.time.LocalTime;
import java.util.Optional;

import de.mwvb.stechuhr.StechuhrApplication;
import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.Dauer;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.gui.StageAdapter;
import de.mwvb.stechuhr.gui.Window;
import de.mwvb.stechuhr.gui.stechuhr.StechuhrWindow;
import de.mwvb.stechuhr.service.StechuhrValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
	// TODO Alle Alerts die ausgegeben werden, sollten zentrisch auf dem BearbeitenWindow angezeigt werden.
	@FXML
	private TextField uhrzeit;
	@FXML
	private ComboBox<String> leistung;
	@FXML
	private TextField ticket;
	@FXML
	private TextArea notizPrivat;
	@FXML
	private Button close;
	@FXML
	private TableView<Stunden> grid;
	@FXML
	private Label info;
	
	@FXML
	protected void initialize() {
		try {
			Window.disableTabKey(notizPrivat);
			fillLeistungCombobox();
			addSaveButtonListeners();
			addDeleteKeyListener();
			updateInfo();
			Platform.runLater(() -> leistung.requestFocus()); // Annahme: Man möchte i.d.R. eine Leistung eingeben
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	private void fillLeistungCombobox() {
		leistung.getItems().clear();
		for (String text : StechuhrWindow.getModel().getLeistungen()) {
			leistung.getItems().add(text);
		}
	}

	private void addSaveButtonListeners() {
		grid.getSelectionModel().selectedItemProperty().addListener((a, b, sel) -> {
			display((Stunden) sel);
		});
	}

	private void addDeleteKeyListener() {
		grid.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			KeyCode code = event.getCode();
			if (KeyCode.DELETE.equals(code)) {
				onDelete();
			}
		});
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

			StechuhrWindow.getModel().calculateDauer();
			grid.getItems().addAll(StechuhrWindow.getModel().getStundenliste());
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
			String eingegebeneUhrzeit = StechuhrValidator.validateUhrzeit(uhrzeit.getText(), i > 0 ? grid.getItems().get(i - 1) : null);
			if (eingegebeneUhrzeit == null) {
				return; // Nicht speichern, wenn Validierung nicht ok.
			}
			String nr = StechuhrValidator.validateTicket(ticket.getText());
			if (nr == null) {
				return; // Nicht speichern, wenn Validierung nicht ok.
			}
			
			// Speichern
			save(stunden, eingegebeneUhrzeit, nr);
			
			// Evtl. umformatierte Eingaben anzeigen
			uhrzeit.setText(eingegebeneUhrzeit);
			ticket.setText(nr);
			leistung.getEditor().setText(stunden.getLeistung());
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	/** Eingaben übernehmen, inkl. Persistierung */
	private void save(final Stunden stunden, final String eingegebeneUhrzeit, final String nr) {
		stunden.setUhrzeit(LocalTime.parse(eingegebeneUhrzeit));
		stunden.setTicket(nr);
		String eingegebeneLeistung = leistung.getEditor().getText().trim().replace(";", ",");
		stunden.setLeistung(eingegebeneLeistung);
		stunden.setNotizPrivat(notizPrivat.getText());

		updateGrid_andSave();
		
		StechuhrWindow.getModel().getLeistungen().add(nr, eingegebeneLeistung);
		fillLeistungCombobox();
	}
	
	@FXML
	public void onDelete() {
		try {
			Stunden s = grid.getSelectionModel().getSelectedItem();
			if (s!= null && shallDelete(s.getTicket())) {
				delete(s);
			}
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
	private boolean shallDelete(String ticket) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Löschen");
		alert.setHeaderText("");
		alert.setContentText("Hiermit löschst Du unwiderruflich den " + ticket + " Datensatz.");
		
		ButtonType loeschenBtn = new ButtonType("Löschen", ButtonData.OK_DONE);
		ButtonType nichtLoeschenBtn = new ButtonType("Nicht löschen", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(loeschenBtn, nichtLoeschenBtn);
		
		// Fenster über BearbeitenWindow zentrieren
		Stage stage = getStage();
		alert.setX(stage.getX() + (stage.getWidth() - 434) / 2d); // Alert-Fenstergröße noch unbekannt, daher mit 434 x 141 fest einprogrammiert.
		alert.setY(stage.getY() + (stage.getHeight() - 141) / 2d);
		
		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == loeschenBtn;
	}

	private void delete(Stunden s) {
		StechuhrWindow.getModel().getStundenliste().remove(s);
		grid.getItems().remove(s);
		if (grid.getItems().isEmpty()) {
			uhrzeit.setText("");
			ticket.setText("");
			leistung.getEditor().setText("");
			notizPrivat.setText("");
		} else {
			int neuerIndex = grid.getSelectionModel().getSelectedIndex() + 1;
			if (neuerIndex >= grid.getItems().size()) {
				neuerIndex = grid.getItems().size() - 1;
			}
			grid.getSelectionModel().select(neuerIndex);
		}

		updateGrid_andSave();
	}
	
	private void updateGrid_andSave() {
		StechuhrWindow.getModel().calculateDauer();
		
		grid.getColumns().get(0).setVisible(false);  // Workaround für Refresh der Zeile
		grid.getColumns().get(0).setVisible(true);
		updateInfo();
		
		new StechuhrDAO().save(StechuhrWindow.getModel());
	}
	
	@FXML
	public void onClose() {
		try {
			StechuhrApplication.getConfig().saveWindowPosition(BearbeitenWindow.class.getSimpleName(), new StageAdapter(getStage()));
			getStage().close();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	private void updateInfo() {
		Dauer dauerOhnePausen = StechuhrWindow.getModel().getDauerOhnePausen(LocalTime.now());
		if (dauerOhnePausen == null) {
			info.setText("");
			return;
		}
		String a = "Summe: " + dauerOhnePausen;
		String b = "";
		LocalTime fa = StechuhrWindow.getModel().getFeierabendUhrzeit();
		if (fa != null) {
			b = " / Feierabend um " + fa.toString() + " Uhr";
		}
		info.setText(a + b);
	}
	
	private Stage getStage() {
		return (Stage) uhrzeit.getScene().getWindow();
	}
}
