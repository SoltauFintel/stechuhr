package de.mwvb.stechuhr.export;

import java.util.ArrayList;
import java.util.List;

/**
 * Export Manager
 * (Singleton)
 * 
 * @author Marcus Warm
 */
public class ExportManager implements Export {
	private static final ExportManager instance = new ExportManager();
	private final List<Export> exporteure = new ArrayList<>();
	
	private ExportManager() {
	}
	
	public static ExportManager getInstance() {
		return instance;
	}
	
	public void register(Export exporteur) {
		exporteure.add(exporteur);
	}

	public void clear() {
		exporteure.clear();
	}
	
	@Override
	public void export(List<Exportstunden> exportstunden) {
		for (Export exporteur : exporteure) {
			exporteur.export(exportstunden);
		}
	}
}
