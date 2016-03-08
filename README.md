[![Build Status](https://travis-ci.org/SoltauFintel/stechuhr.png?branch=master)](https://travis-ci.org/SoltauFintel/stechuhr)
[![Apache 2.0 Lizenz](https://img.shields.io/badge/license-Apache2-4cc61e.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Stechuhr

Stechuhr zur einfachen Erfassung von Arbeitsstunden des aktuellen Tages

:arrow_down: ![Download stechuhr-0.3.1.jar](https://github.com/SoltauFintel/stechuhr/releases/download/0.3.1/stechuhr-0.3.1.jar)

## Beschreibung

Mit dem Programm Stechuhr kann man für den aktuellen Tag Stunden erfassen.
Man gibt dabei eine Ticketnummer, Auftragsnummer oder sonstigen Begriff (z.B. "Besprechung") ein. Die Stechuhr
ist wie ein Kassettenrekorder aufgebaut: **Play / Pause / Stop**.

Bei jeder Eingabe des Tickets klickt man Play an. Dann läuft die Zeit für dieses Arbeitsthema.
Zu Beginn der Mittagspause klickt man auf Pause. Damit unterbricht man die Arbeit am Arbeitsthema.
Nach der Pause wählt man die vorige Ticketnummer aus der Combobox und klickt erneut auf Play.
Zum Feierabend hin klickt man einfach auf Stop. Damit endet das aktuelle Arbeitsthema für diesen Tag.
Die Stunden werden für den Export vorbereitet, optimiert, gerundet und als Text- und HTML-Datei ausgegeben.
Die Stechuhr wird dann beendet.
Falls man am Vortag nicht auf Stop geklickt hat, fragt die Stechuhr die Vortag-Feierabend-Uhrzeit ab und beendet den Tag.

An der rechten Seite gibt es bis zu 10 vorbelegbare Schnellbuttons. Diese werden nur angezeigt,
wenn sie mit einer Ticketnummer vorbelegt sind - oder man die Strg-Taste gedrückt hält. Mit
**gedrückter Strg-Taste** kann man die Vorbelegung ändern. Es wird dann die Ticketnummer abgefragt.
Bei leerer Eingabe wird der Schnellbutton ausgeblendet.

Unten links gibt es den Bearbeiten Button (F2). Dieser verzweigt in einen Bearbeiten Dialog. Hier
werden alle Eingaben aufgelistet. Man kann also dort die erfassten Daten nachbearbeiten.
Nach der Änderung einer Zeile muss der Speichern Button betätigt werden. Auch Löschen ist möglich.

Falls zu einer Ticketnummer bereits ein Leistungstext erfasst worden ist, wird dieser bei erneuter Aktivierung
des Tickets automatisch eingetragen. In der Leistung Combobox sind die 99 zuletzt eingegebenen Leistungstexte enthalten.

Das Fenster-schließen-Symbol des Hauptfensters beendet nicht die Stechuhr, sondern minimiert
nur das Fenster. Gleiches bei Betätigung der Esc-Taste. So wird verhindert, dass man aus Versehen
die Stechuhr beendet - denn sie soll ja den ganzen Tag laufen. **Das Programm wird über das Pulldownmenü
_Datei > Beenden_ (Alt+X) beendet.**

Das Programm Stechuhr legt viel Wert auf Datensicherheit. Alle Daten werden immer sofort auf die
lokale Festplatte (Verzeichnis *user*/stechuhr) geschrieben.

## Build und Ausführung

Ant-Datei build.xml (Taget build) ausführen. Die ausführbare Anwendung liegt dann im Ordner stechuhr/build/libs
und kann im Windows-Explorer durch Doppelklicken auf stechuhr-x.x.jar gestartet werden.

Tooling: Java 8 >=u40, GUI mit JavaFX, Build mit Gradle 2.10, Ant, Travis CI und codecov, IDE: Eclipse Mars.2, SCM+Bugtracker: github

[![Test Code Coverage](http://img.shields.io/codecov/c/github/SoltauFintel/stechuhr.svg) ](https://codecov.io/github/SoltauFintel/stechuhr)
