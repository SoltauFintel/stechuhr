[![Build Status](https://travis-ci.org/SoltauFintel/stechuhr.png?branch=master)](https://travis-ci.org/SoltauFintel/stechuhr)

# Stechuhr
Stechuhr zur einfachen Erfassung von Stunden des aktuellen Tages

## Beschreibung

Mit dem Programm Stechuhr kann man für den aktuellen Tag Stunden erfassen.
Man gibt dabei eine Ticketnummer oder sonstigen Begriff (z.B. "Besprechung") ein. Die Stechuhr
ist wie ein Kassettenrekorder aufgebaut: **Play / Pause / Stop**.

Bei jeder Eingabe des Tickets klickt man Play an. Dann läuft die Zeit für dieses Arbeitsthema.
Zu Beginn der Mittagspause klickt man auf Pause. Damit unterbricht man die Arbeit am Arbeitsthema.
Nach der Pause wählt man die vorige Ticketnummer aus der Combobox und klickt erneut Play an.
Zum Feierabend hin klickt man einfach auf Stop. Damit endet das aktuelle Arbeitsthema für diese Tag.
Die Stunden werden für den Export vorbereitet, optimiert und als Textdatei ausgegeben. Die Stechuhr
wird dann beendet.

An der rechten Seite gibt es bis zu 10 vorbelegbare Schnellbuttons. Diese werden nur angezeigt,
wenn sie mit einer Ticketnummer vorbelegt sind - oder man die Strg-Taste gedrückt hält. Mit
**gedrückter Strg-Taste** kann man die Vorbelegung ändern. Es wird dann die Ticketnummer abgefragt.
Bei leerer Eingabe wird der Schnellbutton ausgeblendet.

Unten links gibt es den Bearbeiten Button. Dieser verzweigt in einen Bearbeiten Dialog. Hier
werden alle Eingabe aufgelistet. Man kann also dort die erfassten Daten nachbearbeiten.
Nach der Änderung einer Zeile muss der Speichern Button betätigt werden. Auch Löschen ist möglich.

Das Fenster-schließen-Symbol des Hauptfensters beendet nicht die Stechuhr, sondern minimiert
nur das Fenster. Gleiches bei Betätigung der Esc-Taste. So wird verhindert, dass man aus Versehen
die Stechuhr beendet - denn sie soll ja den ganzen Tag laufen. **Das Programm wird über das Pulldownmenü
Datei > Beenden beendet.**

Das Programm Stechuhr legt viel Wert auf Datensicherheit. Alle Daten werden immer sofort auf die
lokale Festplatte geschrieben.

## Build und Ausführung

Ant-Datei build.xml (Taget build) ausführen. Die ausführbare Anwendung liegt dann im Ordner stechuhr/build/install/stechuhr
und kann mit bin/stechuhr.bat auf einem Windows-PC gestartet werden.

Tooling: Java 8, JavaFX, Gradle 1.9, Ant, Eclipse Mars.1

## Lizenz

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
