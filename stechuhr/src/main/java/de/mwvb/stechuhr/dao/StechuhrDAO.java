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
import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;

/**
 * alleinige Klasse für Persistierung
 * 
 * @author Marcus Warm
 */
public class StechuhrDAO { // TODO DAO zerlegen: Stechuhr-File, Exporteur, Config-Files
	/** Dateiversion für den Fall, dass sich das Dateiformat zukünftig mal ändern könnte. */
	private static final int DATEI_VERSION = 0;
	private static String pfad;
	
	public static void init() {
		pfad = System.getProperty("user.home").replace("\\", "/") + "/stechuhr";
		File file = new File(pfad);
		if (!file.isDirectory() && !file.mkdirs()) {
			throw new RuntimeException("Fehler bei init(). Verzeichnis konnte nicht erstellt werden: " + file.getAbsolutePath());
		}
	}

	public StechuhrModel load(LocalDate tag) {
		StechuhrModel model = new StechuhrModel(tag);
		File file = getStechuhrModelFile(tag);
		if (file.exists()) {
			XMLDocument dok = StechuhrUtils.loadXMLFile(file.getAbsolutePath());
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
		if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
			throw new RuntimeException("Fehler bei save(). Verzeichnis konnte nicht erstellt werden: " + file.getParentFile().getAbsolutePath());
		}
		
		XMLDocument dok = new XMLDocument("<?xml version=\"1.0\" encoding=\"windows-1252\"?><Stechuhr version=\"" + DATEI_VERSION + "\"/>");
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
		if (file.exists() && !file.delete()) {
			throw new RuntimeException("Fehler bei delete(). Datei konnte nicht gelöscht werden: " + file.getAbsolutePath());
		}
	}
	
	public void save(String id, List<String> data) {
		if (id == null || id.trim().isEmpty() || data == null) {
			throw new IllegalArgumentException("Bitte id angeben!");
		}
		try {
			FileWriter w = new FileWriter(getFile(id)); // TODO FindBugs: Encoding
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
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("Bitte id angeben!");
		}
		List<String> ret = new ArrayList<>();
		try {
			File file = getFile(id);
			if (!file.exists()) {
				return ret;
			}
			BufferedReader r = new BufferedReader(new FileReader(file)); // TODO FindBugs: Encoding
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
	
	public File getStechuhrModelFile(LocalDate tag) {
		return new File(getMonatsordner(tag) + tag.toString() + ".xml");
	}
	
	/**
	 * @return Pfad endet mit "/"
	 */
	public static String getMonatsordner(LocalDate datum) {
		return getPfad().toString() + "/"
				+ datum.getYear() + "/"
				+ StechuhrUtils.zweistellig(datum.getMonthValue()) + "/";
	}
	
	public boolean existsStechuhrModelFile(LocalDate tag) {
		return getStechuhrModelFile(tag).exists();
	}
	
	private File getFile(String id) {
		return new File(pfad.toString() + "/" + id + ".txt");
	}
	
	public void delete(String id) {
		getFile(id).delete();
	}
	
	public static String getPfad() {
		return pfad;
	}
}
