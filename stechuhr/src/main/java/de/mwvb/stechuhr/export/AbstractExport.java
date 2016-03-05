package de.mwvb.stechuhr.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.dao.StechuhrDAO;

public abstract class AbstractExport implements Export {

	@Override
	public void export(List<Exportstunden> exportstunden) {
		try {
			File file = getExportFile(exportstunden.get(0).getTag());
			FileWriter w = new FileWriter(file); // TODO FindBugs: Encoding
			try {
				write(w);
			} finally {
				w.close();
			}
			//System.out.println(file.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected abstract void write(FileWriter w) throws IOException;
	
	protected File getExportFile(LocalDate tag) {
		String vorne = StechuhrDAO.getPfad().toString() + "/"
				+ tag.getYear() + "/"
				+ StechuhrUtils.zweistellig(tag.getMonthValue()) + "/"
				+ tag.toString() + "_" + getPostfix() + "-";
		return StechuhrUtils.getNextFilename(vorne, getExtension());
	}

	protected String getPostfix() {
		return "Export";
	}
	
	protected abstract String getExtension();
}
