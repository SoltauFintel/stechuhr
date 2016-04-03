package de.mwvb.stechuhr.service.export;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVExport extends AbstractExport {
	private List<Exportstunden> exportstunden;
	
	public void export(List<Exportstunden> exportstunden) {
		if (exportstunden.isEmpty()) return;
		this.exportstunden = exportstunden;
		super.export(exportstunden);
	}
	
	@Override
	protected void write(Writer w) throws IOException {
		for (Exportstunden x : exportstunden) {
			w.write(toFileString(x));
			w.write("\r\n");
		}
		exportstunden = null;
	}

	public static String toFileString(Exportstunden x) {
		return x.getTag() + " ; " + x.getSSMM() + " ; " + x.getDezimaldauer() + " ; " + x.getTicket() + " ; " + x.getLeistung();
	}
	
	@Override
	protected String getExtension() {
		return ".txt";
	}
}
