# Stechuhr

## Java 17

Die Stechuhr funktioniert nun für Java 17.

Man mus JavaFX von https://openjfx.io/ (https://gluonhq.com/products/javafx/) herunterladen.

Für den Eclipse Start muss man diese Argumente angeben: --module-path "C:\dat\javafx-sdk-17.0.16\lib" --add-modules javafx.controls,javafx.fxml

## Bauen

gradlew fatJar

Danach run-stechuhr.bat und den Ordner javafx-sdk-17.0.16 in einen neuen Ordner zusammen mit der .jar Datei kopieren.
