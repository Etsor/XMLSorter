package com.etsor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {
    private static final Logger logger = LoggerFactory
    .getLogger(App.class);

    public static void main(String[] args)
    throws FileNotFoundException, XMLStreamException,
    InterruptedException {
        
        if (args.length < 2) {
            logger.error("Usage: input1.xml input2.xml ... inputN.xml settings.xml");
            return;
        }

        File settingsFile = new File(args[args.length - 1]);
        Settings settings = SettingsReader.readSettings(settingsFile);

        ExecutorService executor = Executors
        .newFixedThreadPool(Runtime.getRuntime()
        .availableProcessors());

        for (int i = 0; i <= args.length - 1; i++) {
            File inputFile = new File(args[i]);
            File outputFile = new File("output_" + inputFile.getName());

            executor.submit(() -> {
                try {
                    XMLHandler.processXML(inputFile, outputFile, settings);
                    logger.info("Processed: {}", inputFile.getName());
                } catch (Exception e) {
                    logger.info("Error processing file: {}", inputFile.getName(), e);
                };
            });
        }

        executor.shutdown();
        if(!executor.awaitTermination(1, TimeUnit.HOURS)) {
            logger.warn("Executor did not finish in time");
        }

        logger.info("All files processed");
    }
}
