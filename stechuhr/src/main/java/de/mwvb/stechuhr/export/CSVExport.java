package de.mwvb.stechuhr.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import de.mwvb.stechuhr.dao.StechuhrDAO;

public class CSVExport implements Export {
	
	public void export(List<Exportstunden> export) {
		if (export.isEmpty()) return;
		try {
			File file = getExportFile(export.get(0).getTag());
			FileWriter w = new FileWriter(file); // TODO FindBugs: Encoding
			try {
				for (Exportstunden x : export) {
					w.write(x.toFileString() + "\r\n");
				}
			} finally {
				w.close();
			}
			System.out.println(file.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private File getExportFile(LocalDate tag) {
		String vorne = StechuhrDAO.getPfad().toString() + "/" + tag.getYear() + "/" + tag.getMonthValue() + "/" + tag.toString() + "_Export-";
		String hinten = ".txt";
		int zaehler = 0;
		File f;
		do {
			f = new File(vorne + ++zaehler + hinten);
		} while (f.exists());
		return f;
	}
}
