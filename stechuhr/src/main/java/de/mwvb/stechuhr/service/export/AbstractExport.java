package de.mwvb.stechuhr.service.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.dao.StechuhrDAO;

public abstract class AbstractExport implements Export {

	@Override
	public void export(List<Exportstunden> exportstunden) {
		try {
			File file = getExportFile(exportstunden.get(0).getTag());
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("windows-1252"));
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
	
	protected abstract void write(Writer w) throws IOException;
	
	protected File getExportFile(LocalDate tag) {
		String vorne = StechuhrDAO.getMonatsordner(tag) + tag.toString() + "_" + getPostfix() + "-";
		return StechuhrUtils.getNextFilename(vorne, getExtension());
	}

	protected String getPostfix() {
		return "Export";
	}
	
	protected abstract String getExtension();
}
