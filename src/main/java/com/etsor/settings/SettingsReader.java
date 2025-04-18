package com.etsor.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.LoggerFactory;

import com.etsor.settings.model.Settings;

import org.slf4j.Logger;

/**
 * Читает настройки из XML файла конфигурации и загружает их в объект класса 
 * {@link com.etsor.settings.model.Settings}.
 */
public class SettingsReader {
    private static final Logger logger = LoggerFactory
    .getLogger(SettingsReader.class);
    
    /**
     * Читает настройки из XML файла конфигурации и загружает их в объект класса 
     * {@link com.etsor.settings.model.Settings}.
     * 
     * @param file файл настроек
     * @return объект настроек
     * @throws FileNotFoundException если файл настроек не найден
     * @throws XMLStreamException если возникла ошибка при обработке XML
     */
    public static Settings readSettings(File file)
    throws FileNotFoundException, XMLStreamException {

        Settings settings = new Settings();
        logger.info("Reading settings...");

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory
        .createXMLEventReader(new FileInputStream(file));

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                String tagName = start.getName().getLocalPart();

                if (tagName.equals("array")) {
                    settings.setArrayName(start.getAttributeByName(
                        new QName("name")
                    ).getValue());
                } else if (tagName.equals("attributeName")) {
                    settings.setAttributeName(start.getAttributeByName(
                        new QName("value")
                    ).getValue());
                }
            }
        }
        reader.close();
        
        logger.info(
            "Settings loaded: arrayName={}, attributeName={}",
            settings.getArrayName(),
            settings.getAttributeName());
        
            return settings;
    }
}
