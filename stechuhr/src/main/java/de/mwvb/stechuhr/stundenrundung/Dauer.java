package de.mwvb.stechuhr.stundenrundung;

public class Dauer {
	public static final Dauer ZERO = new Dauer(0, 0);
	private final int stundenTeil;
	private final int minutenTeil;
	
	public Dauer(String ssmm) {
		int o = ssmm.indexOf(":");
		if (o >= 0) {
			stundenTeil = Integer.parseInt(ssmm.substring(0, o));
			minutenTeil = Integer.parseInt(ssmm.substring(o + 1));
			if (minutenTeil < 0 || minutenTeil >= 60) {
				throw new IllegalArgumentException("Minuten müssen im Bereich 0..59 sein!");
			}
		} else {
			stundenTeil = Integer.parseInt(ssmm);
			minutenTeil = 0;
		}
	}

	public Dauer(int stundenTeil, int minutenTeil) {
		this.stundenTeil = stundenTeil;
		this.minutenTeil = minutenTeil;
	}

	public Dauer(int minuten) {
		this.stundenTeil = minuten / 60;
		this.minutenTeil = minuten % 60;
	}

	public Dauer add(Dauer b) {
		return new Dauer(getMinuten() + b.getMinuten());
	}
	
	public int getStundenTeil() {
		return stundenTeil;
	}
	
	public int getMinutenTeil() {
		return minutenTeil;
	}
	
	public int getMinuten() {
		return stundenTeil * 60 + minutenTeil;
	}
	
	@Override
	public String toString() {
		return minutenTeil < 10 ? stundenTeil + ":0" + minutenTeil : stundenTeil + ":" + minutenTeil;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Dauer && getMinuten() == ((Dauer) obj).getMinuten();
	}
	
	@Override
	public int hashCode() {
		return getMinuten();
	}
}
