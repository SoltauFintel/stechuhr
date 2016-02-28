package de.mwvb.stechuhr.gui;

public class StageAdapterForTest extends StageAdapter {
	private double x;
	private double y;
	private double height;
	private double width;
	private boolean iconified = false;
	private boolean resizable = true;

	public StageAdapterForTest() {
		super();
	}
	
	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public boolean isIconified() {
		return iconified;
	}

	@Override
	public boolean isResizable() {
		return resizable;
	}

	@Override
	public void setHeight(double v) {
		height = v;
	}

	@Override
	public void setWidth(double v) {
		width = v;
	}

	@Override
	public void setX(double v) {
		x = v;
	}

	@Override
	public void setY(double v) {
		y = v;
	}

	@Override
	public void setIconified(boolean v) {
		iconified = v;
	}

	@Override
	public void setResizable(boolean v) {
		resizable = v;
	}
}
