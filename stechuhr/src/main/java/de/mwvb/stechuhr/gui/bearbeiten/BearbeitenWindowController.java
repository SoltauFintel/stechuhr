package de.mwvb.stechuhr.gui.bearbeiten;

import java.time.LocalTime;
import java.util.Optional;

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
					getStage().close();
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
			Stunden s = grid.getItems().get(i);
			if (s == null) return;

			String ut = validate(i);
			if (ut == null) return;
			
			s.setUhrzeit(LocalTime.parse(ut));
			s.setTicket(ticket.getText().trim());
			s.setLeistung(leistung.getText().trim());
			s.setNotizPrivat(notizPrivat.getText());

			updateGrid_andSave();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	private String validate(int i) {
		String ut = uhrzeit.getText();
		if (!ut.contains(":")) {
			try {
				int h = Integer.parseInt(ut);
				if ("0".equals(ut) || h > 0) {
					ut += ":00";
				}
			} catch (NumberFormatException ignore) {
			}
		}
		try {
			LocalTime u = LocalTime.parse(ut);
			LocalTime davor;
			if (i > 0) {
				davor = grid.getItems().get(i - 1).getUhrzeit();
			} else {
				davor = LocalTime.of(0, 0);
			}
			if (u.isBefore(davor)) {
				Window.alert("Bitte gebe eine Uhrzeit nach " + davor.toString() + " ein!"
						+ "\nAlternativ kann auch der Vorgängerdatensatz geändert werden.");
				return null;
			}
		} catch (Exception e) {
			Window.alert("Bitte gebe eine Uhrzeit im Format SS:MM ein!");
			return null;
		}
		return ut;
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
			getStage().close();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
	private Stage getStage() {
		return (Stage) uhrzeit.getScene().getWindow();
	}
}
