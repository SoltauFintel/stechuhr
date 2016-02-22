package de.mwvb.stechuhr;

import java.util.List;

import de.mwvb.stechuhr.entity.Exportstunden;

public interface IStechuhrPlugin {

	void export(List<Exportstunden> exportstunden);
}
