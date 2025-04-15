#!/bin/bash
mvn clean package
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi
echo "Build successful!"
echo
echo "Your jar file is located at:"
echo "target/xmlsorter-1.0-SNAPSHOT-jar-with-dependencies.jar"
echo
echo "To run the program use:"
echo "java -jar target/xmlsorter-1.0-SNAPSHOT-jar-with-dependencies.jar [input files] settings.xml"