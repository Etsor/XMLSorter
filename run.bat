@echo off
call mvn clean package
if errorlevel 1 (
    echo Build failed!
    exit /b 1
)

echo Build successful!
echo.
echo Your jar file is located at:
echo target/xmlsorter-1-jar-with-dependencies.jar
echo.
echo To run the program use:
echo java -jar target/xmlsorter-1-jar-with-dependencies.jar [input files] [settings.xml file]
echo Для теста: java -jar target/xmlsorter-1-jar-with-dependencies.jar  src/test/resources/test_input.xml src/test/resources/test_settings.xml