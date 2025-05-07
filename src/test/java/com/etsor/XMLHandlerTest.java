package com.etsor;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.etsor.handler.XMLHandler;
import com.etsor.settings.SettingsReader;
import com.etsor.settings.model.Settings;

public class XMLHandlerTest {

    public XMLHandlerTest() {

    }

    @Test
    public void testSortXML() throws Exception {
        File input = new File("src/test/resources/test_input.xml");
        File settingsFile = new File("src/test/resources/test_settings.xml");
        File output = File.createTempFile("sorted_", ".xml");

        Settings settings = SettingsReader.readSettings(settingsFile);
        XMLHandler.processXML(input, output, settings);

        String result = Files.readString(output.toPath());

        assertTrue(result.indexOf("Lermontov") < result.indexOf("Pushkin"));

        output.deleteOnExit();
    }
}
