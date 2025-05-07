package com.etsor.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etsor.settings.model.Settings;


/**
 * Обработчик XML.
 * Сортирует элементы в XML файле по заданному атрибуту.
 */
public class XMLHandler {
    private static final Logger logger = LoggerFactory
        .getLogger(XMLHandler.class);
    
    
    /**
     * Сортирует элементы в XML файле по заданному атрибуту.
     * 
     * @param inputFile входной XML файл
     * @param outputFile выходной XML файл
     * @param settings заданные настройки
     * @throws FileNotFoundException если входной файл не найден
     * @throws XMLStreamException если возникла ошибка при обработке XML
     */
    public static void processXML(
            File inputFile, 
            File outputFile, 
            Settings settings) throws FileNotFoundException, XMLStreamException {

        logger.debug("Начало обработки файла: {}", inputFile.getName());
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        XMLEventReader reader = inputFactory
            .createXMLEventReader(new FileInputStream(inputFile));
        
        XMLEventWriter writer = outputFactory
            .createXMLEventWriter(new FileOutputStream(outputFile));
        
        List<XMLEvent> buffer = new ArrayList<>();
        
        boolean insideTargetArray = false;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isStartElement() &&
                event.asStartElement()
                    .getName()
                    .getLocalPart()
                    .equals(settings.getArrayName())) {
            
                writer.add(event);
                insideTargetArray = true;
            
                continue;
            }

            if (insideTargetArray) {
                if (event.isEndElement() &&
                    event.asEndElement()
                        .getName()
                        .getLocalPart()
                        .equals(settings.getArrayName())) {
                    
                    List<List<XMLEvent>> elements = extractElements(buffer);
                    
                    elements.sort(Comparator.comparing(
                        eventList -> getAttributeValue(eventList.get(0),
                        settings.getAttributeName())));
                    
                    for (List<XMLEvent> eventList : elements) {
                        for (XMLEvent ev : eventList) {
                            writer.add(ev);
                        }
                    }

                    buffer.clear();
                    writer.add(event);

                    insideTargetArray = false;
                
                } else {
                    buffer.add(event);
                }
            
            } else {
                writer.add(event);
            }
        }    

        reader.close();
        writer.close();
        
        logger.debug("Обработка файла завершена: {}", inputFile.getName());
    }

    /**
     * Извлекает XML элементы из буфера событий.
     * 
     * @param buffer список XML событий
     * @return список списков событий
     */
    private static List<List<XMLEvent>> extractElements(List<XMLEvent> buffer) {
        
        List<List<XMLEvent>> result = new ArrayList<>();
        List<XMLEvent> current = null;

        int depth = 0;

        for (XMLEvent event : buffer) {
            if (event.isStartElement()) {
                if (depth == 0) {
                    current = new ArrayList<>();
                }
                ++depth;
            }
            if (current != null) {
                current.add(event);
            }
            if (event.isEndElement()) {
                --depth;
                if (depth == 0 && current != null) {
                    result.add(current);
                    current = null;
                }
            }
        }

        return result;
    }

    /**
     * Получает значение атрибута из XML события.
     * 
     * @param event XML событие
     * @param attributeName имя атрибута
     * @return значение атрибута/пустую строку
     */
    private static String getAttributeValue(XMLEvent event, String attributeName) {
        
        if (event.isStartElement()) {
            StartElement start = event.asStartElement();
            
            Attribute attribute = start
                .getAttributeByName(new QName(attributeName));

            if (attribute != null) {
                return attribute.getValue();
            } else {
                return "";
            }
        }
    
        return "";
    }
}
