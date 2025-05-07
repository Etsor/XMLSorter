package com.etsor.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etsor.config.ErrorMessages;
import com.etsor.settings.model.Settings;

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
    public static Settings readSettings(File file) throws 
        FileNotFoundException, XMLStreamException {
        
        logger.info("Чтение настроек...");
        
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException(
                String.format(ErrorMessages.ERROR_SETTINGS_NOT_FOUND,
                    file.getAbsolutePath()));
        }

        try (InputStream input = new FileInputStream(file)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(input);
            Settings settings = new Settings();

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    if (elementName.equals("array")) {
                        settings.setArrayName(
                            startElement.getAttributeByName(
                                new QName("name")).getValue());
                    
                    } else if (elementName.equals("attributeName")) {
                        settings.setAttributeName(
                            startElement.getAttributeByName(
                                new QName("value")).getValue());
                    }
                }
            }

            if (settings.getArrayName() == null || settings.getAttributeName() == null) {
                throw new IllegalArgumentException(
                    "В файле настроек должны быть указаны array и attributeName");
            }

            logger.info("Настройки загружены: arrayName={}, attributeName={}", 
                settings.getArrayName(), settings.getAttributeName());
            
            return settings;
            
        } catch (XMLStreamException e) {
            logger.error(ErrorMessages.ERROR_SETTINGS, e);
            throw new IllegalArgumentException(
                ErrorMessages.ERROR_SETTINGS_FORMAT, e);
        
        } catch (FileNotFoundException e) {
            logger.error(ErrorMessages.ERROR_SETTINGS_NOT_FOUND, e);
            throw e;
        
        } catch (Exception e) {
            logger.error(ErrorMessages.ERROR_SETTINGS, e);
            throw new IllegalArgumentException(ErrorMessages.ERROR_SETTINGS, e);
        }
    }
}
