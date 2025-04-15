package com.etsor;

import org.junit.Test;

import javax.xml.stream.XMLStreamException;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;

public class SettingsReaderTest {

    public SettingsReaderTest() {

    }

    @Test
    public void testReadSettings() throws FileNotFoundException, XMLStreamException {
        File testSettings = new File("src/test/resources/test_settings.xml");

        Settings settings = SettingsReader.readSettings(testSettings);

        assertEquals("BookStore", settings.arrayName);
        assertEquals("Author", settings.attributeName);
    }
}
