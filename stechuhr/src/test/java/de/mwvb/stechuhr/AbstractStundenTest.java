package de.mwvb.stechuhr;

import java.time.LocalTime;

import de.mwvb.stechuhr.entity.StechuhrModel;
import de.mwvb.stechuhr.entity.Stunden;

public abstract class AbstractStundenTest {
	protected StechuhrModel model;
	
	protected Stunden createStunden(LocalTime uhrzeit, String ticket, String leistung) {
		Stunden s = new Stunden(uhrzeit);
		s.setTag(model.getTag());
		s.setTicket(ticket);
		s.setLeistung(leistung);
		model.getStundenliste().add(s);
		return s;
	}
}

