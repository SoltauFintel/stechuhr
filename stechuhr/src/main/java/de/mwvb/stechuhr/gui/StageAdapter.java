package de.mwvb.stechuhr.gui;

import javafx.stage.Stage;

public class StageAdapter {
	private Stage stage;
	
	protected StageAdapter() {
	}

	public StageAdapter(Stage stage) {
		this.stage = stage;
	}

	public double getHeight() {
		return stage.getHeight();
	}

	public double getWidth() {
		return stage.getWidth();
	}

	public double getX() {
		return stage.getX();
	}

	public double getY() {
		return stage.getY();
	}

	public boolean isIconified() {
		return stage.isIconified();
	}

	public boolean isResizable() {
		return stage.isResizable();
	}

	public void setHeight(double v) {
		stage.setHeight(v);
	}

	public void setWidth(double v) {
		stage.setWidth(v);
	}

	public void setX(double v) {
		stage.setX(v);
	}

	public void setY(double v) {
		stage.setY(v);
	}
	
	public void setIconified(boolean v) {
		stage.setIconified(v);
	}

	public void setResizable(boolean v) {
		stage.setResizable(v);
	}
}
