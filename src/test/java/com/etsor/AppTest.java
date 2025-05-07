package com.etsor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class AppTest {
    private Path tempDir;
    private File testSettingsFile;
    private File testInputFile;
    private File outputFile;
    private File secondOutputFile;

    @Before
    public void setUp() throws IOException {
        // Временная папка для тестов
        tempDir = Files.createTempDirectory("xmlsorter_test");
        
        // Тестовый файл настроек
        testSettingsFile = new File(tempDir.toFile(), "settings.xml");
        String settingsContent = """
                                <?xml version="1.0" encoding="UTF-8"?>
                                <settings>
                                    <array name="root"/>
                                    <attributeName value="name"/>
                                </settings>""";
        
        Files.write(testSettingsFile.toPath(), settingsContent.getBytes());

        // Тестовый входной файл
        testInputFile = new File(tempDir.toFile(), "test.xml");
        String inputContent = """
                                <?xml version="1.0" encoding="UTF-8"?>
                                <root>
                                    <item name="B">B</item>
                                    <item name="A">A</item>
                                    <item name="C">C</item>
                                </root>
                                """;
        
        Files.write(testInputFile.toPath(), inputContent.getBytes());
        
        outputFile = new File("output_" + testInputFile.getName());
    }

    @After
    public void tearDown() throws IOException {
        // Удаление временных файлов
        if (tempDir != null) {
            Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
            
            Files.delete(tempDir);
        }
        
        if (outputFile != null && outputFile.exists()) {
            outputFile.delete();
        }
        
        if (secondOutputFile != null && secondOutputFile.exists()) {
            secondOutputFile.delete();
        }
    }

    @Test
    public void testProcessWithValidArgs() throws Exception {
        String[] args = {testInputFile.getAbsolutePath(), testSettingsFile.getAbsolutePath()};
        App.process(args);
        
        assertTrue("Выходной файл должен быть создан", outputFile.exists());
        assertTrue("Выходной файл должен содержать данные", outputFile.length() > 0);
        
        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertTrue("Выходной файл должен содержать XML", 
            lines.get(0).contains("<?xml"));
        assertTrue("Элементы должны быть отсортированы", 
            lines.stream().anyMatch(line -> line.contains("name=\"A\"")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithInvalidArgs() throws Exception {
        String[] args = {testInputFile.getAbsolutePath()};
        App.process(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNonExistentFile() throws Exception {
        String[] args = {"nonexistent.xml", testSettingsFile.getAbsolutePath()};
        App.process(args);
    }
    
    @Test
    public void testProcessWithInvalidSettings() throws Exception {
        File invalidSettings = new File(tempDir.toFile(), "invalid_settings.xml");
        Files.write(invalidSettings.toPath(), "invalid xml content".getBytes());
        
        String[] args = {testInputFile.getAbsolutePath(), invalidSettings.getAbsolutePath()};
        try {
            App.process(args);
            fail("Должно быть выброшено исключение для неверного файла настроек");
        
        } catch (IllegalArgumentException e) {
            assertTrue("Сообщение об ошибке должно содержать информацию о некорректном формате",
                e.getMessage().contains("Некорректный формат файла настроек"));
        }
    }
    
    @Test
    public void testProcessWithMultipleFiles() throws Exception {
        File secondInputFile = new File(tempDir.toFile(), "test2.xml");
        
        String inputContent = """
                                <?xml version="1.0" encoding="UTF-8"?>
                                <root>
                                    <item name="Z">Z</item>
                                    <item name="Y">Y</item>
                                    <item name="X">X</item>
                                </root>
                                """;
        
        Files.write(secondInputFile.toPath(), inputContent.getBytes());
        
        String[] args = {
            testInputFile.getAbsolutePath(),
            secondInputFile.getAbsolutePath(),
            testSettingsFile.getAbsolutePath()
        };
        
        App.process(args);
        
        outputFile = new File("output_" + testInputFile.getName());
        secondOutputFile = new File("output_" + secondInputFile.getName());
        
        assertTrue("Первый выходной файл должен быть создан", outputFile.exists());
        assertTrue("Второй выходной файл должен быть создан", secondOutputFile.exists());
    }
}
