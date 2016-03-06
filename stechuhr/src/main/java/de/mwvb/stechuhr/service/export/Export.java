package de.mwvb.stechuhr.service.export;

import java.util.List;

/**
 * Implementierer können Stunden exportieren
 */
public interface Export {

	/**
	 * Exportstunden exportieren
	 */
	void export(List<Exportstunden> exportstunden);
}
