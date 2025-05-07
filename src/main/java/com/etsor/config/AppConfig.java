// package com.etsor.config;

// import java.io.IOException;
// import java.io.InputStream;
// import java.util.Properties;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// /**
//  * Класс для работы с конфигурацией.
//  */
// public class AppConfig {
//     private static final Logger logger = 
//         LoggerFactory.getLogger(AppConfig.class);
    
//     private static final Properties config = new Properties();
//     private static final String CONFIG_FILE = "application.properties";
    
//     static {
//         loadConfig();
//     }
    
//     private static void loadConfig() {
//         try (InputStream input = AppConfig.class
//                 .getClassLoader()
//                 .getResourceAsStream(CONFIG_FILE)) {
            
//             if (input == null) {
//                 logger.warn("Файл конфигурации {} не найден"
//                     + "используются значения по умолчанию",
//                     CONFIG_FILE);
                
//                 return;
//             }
            
//             config.load(input);
            
//             logger.info("Конфигурация успешно загружена из {}",
//                 CONFIG_FILE);
        
//         } catch (IOException e) {
//             logger.error(ErrorMessages.ERROR_CONFIG_LOAD, e);
//         }
//     }
    
//     public static String getProperty(String key, String defaultValue) {
//         String configValue = config.getProperty(key);
        
//         if (configValue != null) {
//             logger.debug("Используется значение из конфигурации {}: {}",
//                 key, configValue);
            
//             return configValue;
//         }
        
//         logger.debug("Используется значение по умолчанию для {}: {}",
//             key, defaultValue);
        
//         return defaultValue;
//     }
    
//     public static int getIntProperty(String key, int defaultValue) {
//         String value = getProperty(key, String.valueOf(defaultValue));
        
//         try {
//             return Integer.parseInt(value);
        
//         } catch (NumberFormatException e) {
//             logger.warn("Некорректное значение для {}: {}, " +
//                 "используется значение по умолчанию: {}", 
//                 key, value, defaultValue);
            
//             return defaultValue;
//         }
//     }
    
//     public static boolean getBooleanProperty(String key, boolean defaultValue) {
//         String value = getProperty(key, String.valueOf(defaultValue));
//         return Boolean.parseBoolean(value);
//     }
// } 