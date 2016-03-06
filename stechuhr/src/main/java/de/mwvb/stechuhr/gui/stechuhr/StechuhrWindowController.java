package de.mwvb.stechuhr.gui.stechuhr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.mwvb.stechuhr.StechuhrApplication;
import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.gui.StageAdapter;
import de.mwvb.stechuhr.gui.Window;
import de.mwvb.stechuhr.gui.bearbeiten.BearbeitenWindow;
import de.mwvb.stechuhr.gui.bearbeiten.BearbeitenWindowController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Stechuhr Hauptfenster
 * 
 * @author Marcus Warm
 */
public class StechuhrWindowController {
	@FXML
	private Button play;
	@FXML
	private Button quick1;
	@FXML
	private Button quick2;
	@FXML
	private Button quick3;
	@FXML
	private Button quick4;
	@FXML
	private Button quick5;
	@FXML
	private Button quick6;
	@FXML
	private Button quick7;
	@FXML
	private Button quick8;
	@FXML
	private Button quick9;
	@FXML
	private Button quick10;
	private final List<Button> quickButtons = new ArrayList<>();
	@FXML
	private ComboBox<String> ticket;
	@FXML
	private Label info;
	
	@FXML
	protected void initialize() {
		try {
			updateInfo();
			initQuickButtons();
			initTicketCB();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	private void initQuickButtons() {
		quick3.setVisible(false);
		quickButtons.add(quick1);
		quickButtons.add(quick2);
		quickButtons.add(quick3);
		quickButtons.add(quick4);
		quickButtons.add(quick5);
		quickButtons.add(quick6);
		quickButtons.add(quick7);
		quickButtons.add(quick8);
		quickButtons.add(quick9);
		quickButtons.add(quick10);
		for (int i = 0; i < 10; i++) {
			quickButtons.get(i).setText(StechuhrApplication.config.getQuickButtonLabels()[i]);
		}
		quickButtons.forEach(it -> it.setOnMouseClicked(event -> {
			Button btn = (Button) event.getSource();
			if (btn.getText().trim().isEmpty() || event.isControlDown()) {
				quickButtonBelegen(btn);
			} else {
				ticket.setValue(btn.getText());
				onPlay();
			}
		}));
		hideQuickButtons();
	}

	private void quickButtonBelegen(Button btn) {
		Optional<String> result = askForButtonLabel(btn.getText());
		if (!result.isPresent()) return;
		
		String nr = result.get().trim();
		int i = Integer.parseInt(btn.getId().substring("quick".length())) - 1;
		btn.setText(nr);
		StechuhrApplication.config.getQuickButtonLabels()[i] = nr;
		StechuhrApplication.config.saveQuickButtonLabels();
		quickButtons.forEach(itx -> {
			itx.setVisible(!itx.getText().isEmpty());
		});
	}
	
	public void hideQuickButtons() {
		quickButtons.forEach(it -> {
			it.setVisible(!it.getText().isEmpty());
		});
	}
	
	public void showQuickButtons() {
		quickButtons.forEach(it -> {
			it.setVisible(true);
		});
	}

	private void initTicketCB() {
		ticket.getItems().addAll(StechuhrApplication.config.getOldTickets());
		ticket.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			KeyCode code = event.getCode();
			if (KeyCode.ENTER.equals(code)) { // TODO Klappt ab Java 8u60 nicht mehr!
				onPlay();
			} else if (KeyCode.ESCAPE.equals(code)) {
				minimize();
			}
		});
		showCurrentTicket();
	}

	private void showCurrentTicket() {
		List<Stunden> list = StechuhrWindow.model.getStundenliste();
		if (!list.isEmpty()) {
			Stunden s = list.get(list.size() - 1);
			ticket.getEditor().setText(s.getTicket());
			updateInfo();
		}
	}

	@FXML
	public void onPlay() {
		try {
			String nr = BearbeitenWindowController.validateTicket(ticket.getEditor().getText());
			if (nr != null) {
				updateOldTickets(nr);
				newEntry(nr);
			}
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	@FXML
	public void onPause() {
		try {
			newEntry(Stunden.PAUSE);
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	@FXML
	public void onStop() {
		try {
			updateInfo("");
			newEntry(Stunden.STOP);
			StechuhrWindow.model.stop();
			quit();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	@FXML
	public void onBearbeiten() {
		try {
			new BearbeitenWindow().show(new Stage(), true, getStage());
			showCurrentTicket();
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	private void updateOldTickets(String nr) {
		ObservableList<String> items = ticket.getItems();
		for (int i = items.size() - 1; i >= 0; i--) {
			if (items.get(i).equals(nr)) {
				items.remove(i);
			}
		}
		items.add(0, nr);
		ticket.getSelectionModel().select(0);
		StechuhrApplication.config.getOldTickets().clear();
		for (String t : items) {
			StechuhrApplication.config.getOldTickets().add(t);
		}
		StechuhrApplication.config.saveOldTickets();
	}
	
	private void newEntry(String nr) {
		Stunden stunden = new Stunden(nr);
		stunden.setLeistung(StechuhrWindow.model.getLeistungen().getLeistungForTicket(nr));
		StechuhrWindow.model.getStundenliste().add(stunden);
		updateInfo();
		new StechuhrDAO().save(StechuhrWindow.model);
		ticket.getEditor().setText(nr);
	}
	
	@FXML
	public void onBeenden() {
		quit();
	}
	
	private void quit() {
		StechuhrApplication.config.saveWindowPosition(StechuhrWindow.class.getSimpleName(), new StageAdapter(getStage()));
		getStage().close();
		// Programm ist nun beendet.
	}

	public void minimize() {
		getStage().setIconified(true);
	}
	
	private void updateInfo() {
		String text = "";
		List<Stunden> list = StechuhrWindow.model.getStundenliste();
		if (!list.isEmpty()) {
			Stunden s = list.get(list.size() - 1);
			text = "aktiv: " + s.getTicket();
		}
		updateInfo(text);
	}

	private void updateInfo(String pText) {
		String text = StechuhrUtils.formatWTDate(StechuhrWindow.model.getTag());
		if (!pText.isEmpty()) {
			text += "  |  " + pText;
		}
		info.setText(text);
	}

	private Optional<String> askForButtonLabel(String now) {
		TextInputDialog dialog = new TextInputDialog(now);
		dialog.setTitle("Quickbutton");
		dialog.setHeaderText("");
		dialog.setContentText("Ticketnummer:");
		return dialog.showAndWait();
	}
	
	private Stage getStage() {
		return (Stage) play.getScene().getWindow();
	}
}
