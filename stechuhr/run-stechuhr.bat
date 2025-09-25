REM stechuhr-2.0.0.jar im gleichen Ordner
REM Ordner javafx-sdk-17.0.16 im gleichen Ordner

start "" /B "%JAVA_HOME%\bin\javaw.exe" --module-path "javafx-sdk-17.0.16\lib" --add-modules javafx.controls,javafx.fxml -jar stechuhr-2.0.0.jar
