package com.org.insurance.io;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    private Path tempDir;
    private FileManager fileManager;

    /**
     * Заглушка для Obligation, щоб не запускати інтерактивний Scanner.
     */
    static class TestObligationStub extends Obligation {
        public TestObligationStub(String name, double insuredAmount) {
            super(name, insuredAmount, 1.0, 12, 0.05, 0.1, 1000.0);
        }

        @Override
        public void setSpecificFields(java.util.Scanner in) {
            // нічого не робимо
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("fm-test-");
        fileManager = new FileManager();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    @DisplayName("saveDerivative + loadDerivative: збереження і відновлення працюють")
    void testSaveAndLoad() {
        Derivative original = new Derivative("Test derivative");
        List<Obligation> obligations = new ArrayList<>();
        obligations.add(new TestObligationStub("Stub", 1234.0));
        original.setObligations(obligations);

        Path file = tempDir.resolve("derivative.bin");
        fileManager.saveDerivative(original, file.toString());

        Derivative loaded = fileManager.loadDerivative(file.toString());

        assertNotNull(loaded);
        assertEquals(original.getName(), loaded.getName());
        assertNotNull(loaded.getObligations());
        assertEquals(1, loaded.getObligations().size());
        assertEquals("Stub", loaded.getObligations().get(0).getName());
    }

    @Test
    @DisplayName("loadDerivative: неіснуючий файл -> RuntimeException з повідомленням про відсутність файлу")
    void testLoadNonExistentFile() {
        String filename = tempDir.resolve("no-such-file.bin").toString();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileManager.loadDerivative(filename));

        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("файл не знайдено") || msg.contains("file not found"),
                "Повідомлення про помилку має містити інформацію про відсутність файлу");
    }

    @Test
    @DisplayName("exportToText: для порожнього деривативу створюється структурований звіт")
    void testExportEmptyDerivative() throws IOException {
        Derivative derivative = new Derivative("Empty derivative");
        derivative.setObligations(null); // порожній список

        Path file = tempDir.resolve("empty.txt");

        fileManager.exportToText(derivative, file.toString());

        assertTrue(Files.exists(file), "Файл експорту має бути створений");

        String text = Files.readString(file);

        // Заголовок
        assertTrue(text.contains("ЗВІТ ПО ДЕРИВАТИВУ"),
                "Звіт має містити заголовок 'ЗВІТ ПО ДЕРИВАТИВУ'");
        // Назва деривативу
        assertTrue(text.contains("Назва: Empty derivative"),
                "Звіт має містити назву деривативу");
        // Кількість зобов'язань = 0
        assertTrue(text.contains("Кількість зобов'язань: 0"),
                "Звіт має вказувати, що зобов'язань 0");
        // Текст про відсутність зобов'язань
        assertTrue(text.contains("Зобов'язань немає"),
                "Звіт має містити повідомлення про відсутність зобов'язань");
    }
}
