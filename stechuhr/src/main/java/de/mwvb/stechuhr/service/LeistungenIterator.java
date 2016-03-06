package de.mwvb.stechuhr.service;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.mwvb.stechuhr.entity.Leistung;

class LeistungenIterator implements Iterator<String> {
	private Leistung next;
	
	LeistungenIterator(Leistung first) {
		this.next = first;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public String next() {
		if (next == null) {
			throw new NoSuchElementException();
		}
		String nextLeistung = next.getLeistung();
		next = next.next();
		return nextLeistung;
	}
}
