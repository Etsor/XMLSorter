package com.etsor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import com.etsor.config.AppConfig;

import com.etsor.config.ErrorMessages;
import com.etsor.handler.XMLHandler;
import com.etsor.settings.SettingsReader;
import com.etsor.settings.model.Settings;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            ExecutorService executor = null;
            try {
                validateArgs(args);
                executor = processFiles(args);
            
            } catch (FileNotFoundException | 
                    InterruptedException | 
                    XMLStreamException e) {
                
                logger.error(ErrorMessages.ERROR_CRITICAL, e);
                throw new RuntimeException(ErrorMessages.ERROR_CRITICAL, e);
            
            } finally {
                if (executor != null) {
                    executor.shutdownNow();
                }
            }
        
        } catch (RuntimeException e) {
            logger.error(ErrorMessages.ERROR_UNHANDLED_ERROR, e);
            System.exit(1);
        }
    }

    private static void validateArgs(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                ErrorMessages.ERROR_INVALID_ARGS);
        }

        // Проверка на наличие файла настроек
        File settingsFile = new File(args[args.length - 1]);
        if (!settingsFile.exists() || !settingsFile.isFile()) {
            throw new IllegalArgumentException(
                String.format(ErrorMessages.ERROR_SETTINGS_NOT_FOUND, 
                    settingsFile.getAbsolutePath()));
        }

        // Проверка на наличие входных файлов
        for (int i = 0; i < args.length - 1; i++) {
            File inputFile = new File(args[i]);
            if (!inputFile.exists() || !inputFile.isFile()) {
                throw new IllegalArgumentException(
                    String.format(ErrorMessages.ERROR_INPUT_NOT_FOUND,
                        inputFile.getAbsolutePath()));
            }
        }
    }

    private static ExecutorService processFiles(String[] args) 
            throws FileNotFoundException, XMLStreamException, InterruptedException {
        
        File settingsFile = new File(args[args.length - 1]);
        Settings settings = SettingsReader.readSettings(settingsFile);
        
        if (settings == null) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_SETTINGS_NOT_FOUND);
        }

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int inputFilesCount = args.length - 1; // settings.xml
        int threadPoolSize = Math.min(inputFilesCount, availableProcessors * 2);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        for (int i = 0; i < args.length - 1; i++) {
            File inputFile = new File(args[i]);
            File outputFile = new File("output_" + inputFile.getName());

            executor.submit(() -> {
                try {
                    logger.info("Обработка файла: {}", inputFile.getName());
                    XMLHandler.processXML(inputFile, outputFile, settings);
                    logger.info("Файл успешно обработан: {}", inputFile.getName());
                
                } catch (FileNotFoundException | XMLStreamException e) {
                    logger.error("{}: {}", 
                        ErrorMessages.ERROR_FILE_PROCESS, inputFile.getName(), e);
                    
                    throw new IllegalArgumentException(
                        String.format(ErrorMessages.ERROR_INPUT_NOT_FOUND,
                            inputFile.getAbsolutePath()));
                }
            });
        }

        executor.shutdown();

        logger.info("Все файлы обработаны");
        return executor;
    }

    // Для теста
    public static void process(String[] args) throws 
    FileNotFoundException, XMLStreamException, InterruptedException {

        ExecutorService executor = null;
        try {
            App.validateArgs(args);
            executor = App.processFiles(args);

            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                throw new InterruptedException();
            }
        
        } finally {
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }
}
