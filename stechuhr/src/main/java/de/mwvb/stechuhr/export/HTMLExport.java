package de.mwvb.stechuhr.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import de.mwvb.stechuhr.base.StechuhrUtils;
import de.mwvb.stechuhr.dao.StechuhrDAO;

public class HTMLExport implements Export { // TODO gemeinsame Basisklasse mit FileWriter und getExportFile Funktionalitäten
	
	public void export(List<Exportstunden> export) {
		if (export.isEmpty()) return;
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>");
		sb.append(export.get(0).getTagString());
		sb.append(" - Stunden</title><style>* { font-family: Verdana; } .w { font-size: 8pt; }</style></head>\n<body>");
		sb.append("<h2>Stunden vom ");
		sb.append(StechuhrUtils.formatWTDate(export.get(0).getTag()));
		sb.append("</h2>\n<table border='1' cellpadding='7' cellspacing='0'>\n<tr><th>Dauer</th><th>Ticket</th><th>Leistung</th></tr>");
		for (Exportstunden x : export) {
			sb.append("\n<tr onclick='changeColor(this)'><td align='right'>");
			sb.append(x.getDezimaldauer());
			sb.append("</td><td>");
			sb.append(x.getTicket()); // TODO HTML-Maskierung
			sb.append("</td><td>");
			sb.append(x.getLeistung().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")); // TODO HTML-Maskierung
			sb.append("</td></tr>");
		}
		// TODO Summe exkl. PAUSE
		
		// Wenn der Benutzer in eine <tr> klickt, soll die grau hinterlegt werden. Beim 2. Klick wieder weiß.
		// Im Idealfall soll die Markierung verschwinden, wenn der Benutzer eine weitere Zeile anklickt. TODO Das noch besser lösen.
		String farbe = "yellow";
		sb.append("\n</table><p><a class='w' href='https://github.com/SoltauFintel/stechuhr'>Stechuhr</a></p>"
				+ "<script>function changeColor(o){o.style.backgroundColor=(o.style.backgroundColor=='" + farbe
				+ "')?('transparent'):('" + farbe + "');}"
				+ "</script></body></html>");
		try {
			File file = getExportFile(export.get(0).getTag());
			FileWriter w = new FileWriter(file); // TODO FindBugs: Encoding
			try {
				w.write(sb.toString());
			} finally {
				w.close();
			}
			System.out.println(file.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected File getExportFile(LocalDate tag) {
		// TODO Monatspfad zentralisieren und Monat zweistellig
		String vorne = StechuhrDAO.getPfad().toString() + "/" + tag.getYear() + "/" + tag.getMonthValue() + "/" + tag.toString() + "_Export-";
		return StechuhrUtils.getNextFilename(vorne, ".html");
	}
}
