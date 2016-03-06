package de.mwvb.stechuhr.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mwvb.stechuhr.dao.StechuhrDAO;
import de.mwvb.stechuhr.entity.StechuhrModel;

/**
 * Hilfsklasse für VortagCheckTest
 */
public class VortagCheckAccess extends StechuhrDAO {
	public List<LocalDate> existingDays = new ArrayList<>();
	public Map<LocalDate, StechuhrModel> models = new HashMap<>();
	public boolean saved = false;
	
	@Override
	public StechuhrModel load(LocalDate tag) {
		return models.get(tag);
	}
	
	@Override
	public void save(StechuhrModel model) {
		saved = true;
	}
	
	@Override
	public boolean existsStechuhrModelFile(LocalDate tag) {
		return existingDays.contains(tag);
	}
}
