package de.mwvb.stechuhr.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.base.xml.XMLDocument;
import de.mwvb.base.xml.XMLElement;
import de.mwvb.stechuhr.entity.Exportstunden;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;

/**
 * alleinige Klasse für Persistierung
 * 
 * @author Marcus Warm
 */
public class StechuhrDAO {
	/** Dateiversion für den Fall, dass sich das Dateiformat zukünftig mal ändern könnte. */
	private static final int DATEI_VERSION = 0;
	private static String pfad;
	
	public static void init() {
		pfad = System.getProperty("user.home").replace("\\", "/") + "/stechuhr";
		new File(pfad).mkdirs();
	}

	public StechuhrModel load(LocalDate tag) {
		StechuhrModel model = new StechuhrModel(tag);
		File file = getStechuhrModelFile(tag);
		if (file.exists()) {
			XMLDocument dok = XMLDocument.load(file.getAbsolutePath());
			for (XMLElement e : dok.getChildren()) {
				Stunden s = new Stunden();
				s.setTag(tag);
				s.setUhrzeit(LocalTime.parse(e.getValue("uhrzeit")));
				s.setTicket(e.getValue("ticket"));
				s.setLeistung(e.getValue("leistung"));
				s.setNotizPrivat(e.getMultiLineValue("notizPrivat"));
				model.getStundenliste().add(s);
			}
		}
		return model;
	}
	
	public void save(StechuhrModel model) {
		File file = getStechuhrModelFile(model.getTag());
		file.getParentFile().mkdirs();
		
		XMLDocument dok = new XMLDocument("<Stechuhr version=\"" + DATEI_VERSION + "\"/>");
		XMLElement root = dok.getElement();
		root.setValue("tag", model.getTagString());
		for (Stunden s : model.getStundenliste()) {
			XMLElement e = root.add("Stunden");
			e.setValue("uhrzeit", s.getUhrzeit().toString());
			e.setValue("ticket", s.getTicket());
			e.setValue("leistung", s.getLeistung());
			e.setMultiLineValue("notizPrivat", s.getNotizPrivat());
		}
		dok.saveFile(file.getAbsolutePath());
	}
	
	public void delete(StechuhrModel model) {
		File file = getStechuhrModelFile(model.getTag());
		file.delete();
	}
	
	public void save(String id, List<String> data) {
		try {
			FileWriter w = new FileWriter(getFile(id));
			try {
				for (String line : data) {
					w.write(line + "\r\n");
				}
			} finally {
				w.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> load(String id) {
		List<String> ret = new ArrayList<>();
		try {
			File file = getFile(id);
			if (!file.exists()) {
				return ret;
			}
			BufferedReader r = new BufferedReader(new FileReader(file));
			try {
				String line;
				while ((line = r.readLine()) != null) {
					ret.add(line);
				}
				return ret;
			} finally {
				r.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void saveExport(List<Exportstunden> export) {
		if (export.isEmpty()) return;
		try {
			File file = getExportFile(export.get(0).getTag());
			FileWriter w = new FileWriter(file);
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
	
	public File getStechuhrModelFile(LocalDate tag) {
		return new File(pfad.toString() + "/" + tag.getYear() + "/" + tag.getMonthValue() + "/" + tag.toString() + ".xml");
	}
	
	public boolean existsStechuhrModelFile(LocalDate tag) {
		return getStechuhrModelFile(tag).exists();
	}
	
	private File getExportFile(LocalDate tag) {
		String vorne = pfad.toString() + "/" + tag.getYear() + "/" + tag.getMonthValue() + "/" + tag.toString() + "_Export-";
		String hinten = ".txt";
		int zaehler = 0;
		File f;
		do {
			f = new File(vorne + ++zaehler + hinten);
		} while (f.exists());
		return f;
	}

	private File getFile(String id) {
		return new File(pfad.toString() + "/" + id + ".txt");
	}
}
