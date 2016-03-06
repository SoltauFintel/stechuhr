package de.mwvb.stechuhr.service.export;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExport extends AbstractExport {
	private List<Exportstunden> exportstunden;
	
	public void export(List<Exportstunden> exportstunden) {
		if (exportstunden.isEmpty()) return;
		this.exportstunden = exportstunden;
		super.export(exportstunden);
	}
	
	@Override
	protected void write(FileWriter w) throws IOException {
		for (Exportstunden x : exportstunden) {
			w.write(x.toFileString());
			w.write("\r\n");
		}
		exportstunden = null;
	}

	@Override
	protected String getExtension() {
		return ".txt";
	}
}
