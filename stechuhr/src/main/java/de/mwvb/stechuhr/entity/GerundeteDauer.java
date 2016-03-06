package de.mwvb.stechuhr.entity;

/**
 * GerundeteDauer ist eine Dauer, die ihren alten ungerundeten Wert noch kennt
 */
public final class GerundeteDauer extends Dauer {
	private final Dauer ungerundet;

	public GerundeteDauer(int minuten, Dauer ungerundet) {
		super(minuten);
		this.ungerundet = ungerundet;
	}
	
	public static GerundeteDauer runde(Dauer zuRunden, int rundung) {
		int rest = zuRunden.getMinuten() % rundung;
		if (rest >= rundung / 2) { // aufrunden
			return new GerundeteDauer((zuRunden.getMinuten() / rundung + 1) * rundung, zuRunden);
		} else { // abrunden
			return new GerundeteDauer(zuRunden.getMinuten() / rundung * rundung, zuRunden);
		}
	}
	
	public GerundeteDauer rundeAb(int rundung) {
		return new GerundeteDauer(getMinuten() - rundung, ungerundet);
	}

	public GerundeteDauer rundeAuf(int rundung) {
		return new GerundeteDauer(getMinuten() + rundung, ungerundet);
	}

	public Dauer getUngerundet() {
		return ungerundet;
	}
}
