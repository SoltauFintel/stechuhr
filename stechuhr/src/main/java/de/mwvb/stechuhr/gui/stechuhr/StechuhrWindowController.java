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
import de.mwvb.stechuhr.service.StechuhrValidator;
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
			quickButtons.get(i).setText(StechuhrApplication.getConfig().getQuickButtonLabels()[i]);
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
		int index = Integer.parseInt(btn.getId().substring("quick".length())) - 1;
		btn.setText(nr);
		StechuhrApplication.getConfig().getQuickButtonLabels()[index] = nr;
		// Wenn es bereits einen anderen Button mit dieser Beschriftung gegeben hat, soll dieser ausgeblendet werden.
		for (int i = 0; i < StechuhrApplication.getConfig().getQuickButtonLabels().length; i++) {
			if (index != i && StechuhrApplication.getConfig().getQuickButtonLabels()[i].equals(nr)) {
				StechuhrApplication.getConfig().getQuickButtonLabels()[i] = "";
				quickButtons.get(i).setText("");
			}
		}
		StechuhrApplication.getConfig().saveQuickButtonLabels();
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
		ticket.getItems().addAll(StechuhrApplication.getConfig().getOldTickets());
		ticket.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			KeyCode code = event.getCode();
			if (KeyCode.ENTER.equals(code)) { // Klappt ab Java 8u60 nicht mehr! Durch Workaround in StechuhrWindow gel�st.
				onPlay();
			} else if (KeyCode.ESCAPE.equals(code)) {
				minimize();
			}
		});
		showCurrentTicket();
	}

	private void showCurrentTicket() {
		String currentTicket = StechuhrWindow.getModel().getCurrentTicket();
		if (currentTicket != null) {
			ticket.setValue(currentTicket);
			updateInfo();
		}
	}
	
	// Achtung: Diese Methode hat Aufrufer in dieser Klasse!
	@FXML
	public void onPlay() {
		try {
			String nr;
			if (Stunden.PAUSE.equals(StechuhrWindow.getModel().getCurrentTicket()) && Stunden.PAUSE.equals(ticket.getValue())) {
				// Sondermodus: Wenn man in PAUSE ist und dann PLAY dr�ckt, wird das vorige Ticket reaktiviert.
				// Es ist �brigens nun bewusst so programmiert, dass man bei aktiver PAUSE nicht ein 2. Mal die PAUSE mit PLAY starten kann.
				nr = StechuhrWindow.getModel().getPreviousTicket();
			} else {
				nr = StechuhrValidator.validateTicket(ticket.getEditor().getText()); /* Nicht ticket.getValue(), da sonst Mausklick
				auf Play Button die von Hand eingegebene Ticketnr. nicht �bernehmen w�rde! */
				
                if (isNumeric(nr)) {
                    nr = "XDEV-" + nr;
                }
			}
			if (nr != null) {
				updateOldTickets(nr);
				newEntry(nr);
			}
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
    private boolean isNumeric(String nr) {
        if (nr == null) {
            return false;
        }
        for (int i = 0; i < nr.length(); i++) {
            char c = nr.charAt(i);
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return !nr.isEmpty();
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
			StechuhrWindow.getModel().stop();
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
		StechuhrApplication.getConfig().getOldTickets().clear();
		for (String t : items) {
			StechuhrApplication.getConfig().getOldTickets().add(t);
		}
		StechuhrApplication.getConfig().saveOldTickets();
	}
	
	private void newEntry(String nr) {
		Stunden stunden = new Stunden(nr);
		stunden.setLeistung(StechuhrWindow.getModel().getLeistungen().getLeistungForTicket(nr));
		StechuhrWindow.getModel().getStundenliste().add(stunden);
		updateInfo();
		new StechuhrDAO().save(StechuhrWindow.getModel());
		ticket.setValue(nr); // NICHT ticket.getEditor().setText() verwenden!
	}
	
	@FXML
	public void onBeenden() {
		quit();
	}
	
	private void quit() {
		StechuhrApplication.getConfig().saveWindowPosition(StechuhrWindow.class.getSimpleName(), new StageAdapter(getStage()));
		getStage().close();
		// Programm ist nun beendet.
	}

	public void minimize() {
		getStage().setIconified(true);
	}
	
	private void updateInfo() {
		String text = "";
		List<Stunden> list = StechuhrWindow.getModel().getStundenliste();
		if (!list.isEmpty()) {
			Stunden s = list.get(list.size() - 1);
			text = "aktiv: " + s.getTicket();
		}
		updateInfo(text);
	}

	private void updateInfo(String pText) {
		String text = StechuhrUtils.formatWTDate(StechuhrWindow.getModel().getTag());
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
