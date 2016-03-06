package de.mwvb.stechuhr.service.export;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.entity.Dauer;
import de.mwvb.stechuhr.entity.Stunden;
import de.mwvb.stechuhr.service.stundenrundung.Stundenrundung;

/**
 * HTML-Bericht erzeugen, der die Exportstunden enthält
 */
public class HTMLExport extends AbstractExport {
	private StringBuilder sb;
	
	public void export(List<Exportstunden> exportstunden) {
		if (exportstunden.isEmpty()) return;
		sb = new StringBuilder();
		sb.append("<html><head><title>");
		sb.append(exportstunden.get(0).getTagString());
		sb.append(" - Stunden</title><style>* { font-family: Verdana; } .w { font-size: 8pt; }</style></head>\n<body>");
		sb.append("<h2>Stunden vom ");
		sb.append(StechuhrUtils.formatWTDate(exportstunden.get(0).getTag()));
		sb.append("</h2>\n<table border='1' cellpadding='7' cellspacing='0'>\n<tr><th>Dauer</th><th>Ticket</th><th>Leistung</th></tr>");
		Stundenrundung rundung = new Stundenrundung(); // Diese Klasse kann Stunden summieren.
		for (Exportstunden x : exportstunden) {
			sb.append("\n<tr onclick='changeColor(this)'><td align='right'>");
			sb.append(x.getDezimaldauer());
			sb.append("</td><td>");
			sb.append(htmlEncode(x.getTicket()));
			sb.append("</td><td>");
			sb.append(htmlEncode(x.getLeistung()));
			sb.append("</td></tr>");
			if (!Stunden.PAUSE.equals(x.getTicket())) {
				rundung.add(new Dauer(x.getStunden(), x.getMinuten()));
			}
		}
		Dauer summe = rundung.getSumme();
		sb.append("\n<tr onclick='changeColor(this)'><td align='right'><b>");
		sb.append(summe.toDezimalString());
		sb.append("</b></td><td>Summe ohne Pause</td><td></td></tr>");
		
		// Wenn der Benutzer in eine <tr> klickt, soll die grau hinterlegt werden. Beim 2. Klick wieder weiß.
		// Im Idealfall soll die Markierung verschwinden, wenn der Benutzer eine weitere Zeile anklickt. TODO Das noch besser lösen.
		String farbe = "yellow";
		sb.append("\n</table>\n<p><a class='w' href='https://github.com/SoltauFintel/stechuhr'>Stechuhr</a></p>"
				+ "\n<script>function changeColor(o){o.style.backgroundColor=(o.style.backgroundColor=='" + farbe
				+ "')?('transparent'):('" + farbe + "');}"
				+ "</script></body></html>");
		super.export(exportstunden);
	}
	
	private String htmlEncode(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("ä", "&auml;")
				.replace("ö", "&ouml;")
				.replace("ü", "&uuml;")
				.replace("Ä", "&Auml;")
				.replace("Ö", "&Ouml;")
				.replace("Ü", "&Uuml;")
				.replace("ß", "&szlig;");
	}
	
	@Override
	protected void write(Writer w) throws IOException {
		w.write(sb.toString());
		sb = null;
	}
	
	@Override
	protected String getExtension() {
		return ".html";
	}
}
