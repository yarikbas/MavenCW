package com.org.insurance.io;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import com.org.insurance.io.FileManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    private final FileManager fileManager = new FileManager();

    // JUnit автоматично створить цю папку перед тестом і видалить після
    @TempDir
    Path tempDir;

    // --- Stub Class (Заглушка для серіалізації) ---
    // Важливо: Obligation імплементує Serializable, тому цей клас теж буде серіалізованим.
    static class TestSerializableObligation extends Obligation {
        @Serial
        private static final long serialVersionUID = 1L;

        public TestSerializableObligation(String name, double amount) {
            super(name, amount, 1.0, 12, 0.05, 0.1, 1000.0);
        }

        // Конструктор для Scanner (не використовується тут, але потрібен для наслідування)
        public TestSerializableObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // ----------------------------------------------

    @Test
    @DisplayName("saveDerivative та loadDerivative коректно зберігають і відновлюють об'єкт")
    void testSaveAndLoadBinary() {
        // 1. Підготовка даних
        Derivative original = new Derivative("My Portfolio");
        List<Obligation> list = new ArrayList<>();
        list.add(new TestSerializableObligation("Item 1", 100.0));
        list.add(new TestSerializableObligation("Item 2", 200.0));
        original.setObligations(list);

        // Створюємо шлях до файлу у тимчасовій папці
        Path filePath = tempDir.resolve("portfolio.dat");
        String filename = filePath.toString();

        // 2. Збереження
        assertDoesNotThrow(() -> fileManager.saveDerivative(original, filename));
        assertTrue(Files.exists(filePath), "Файл повинен бути створений");

        // 3. Завантаження
        Derivative loaded = fileManager.loadDerivative(filename);

        // 4. Перевірка
        assertNotNull(loaded);
        assertEquals(original.getId(), loaded.getId());
        assertEquals(original.getName(), loaded.getName());
        assertEquals(2, loaded.getObligations().size());
        assertEquals("Item 1", loaded.getObligations().get(0).getName());
    }

    @Test
    @DisplayName("saveDerivative створює вкладені папки, якщо їх немає")
    void testSaveCreatesDirectories() {
        Derivative derivative = new Derivative("Test");

        // Шлях: tempDir / folder1 / folder2 / file.dat
        Path deepPath = tempDir.resolve("folder1").resolve("folder2").resolve("data.bin");

        fileManager.saveDerivative(derivative, deepPath.toString());

        assertTrue(Files.exists(deepPath), "Файл мав бути створений разом із папками");
    }

    @Test
    @DisplayName("loadDerivative кидає RuntimeException, якщо файл не існує")
    void testLoadNonExistentFile() {
        Path badPath = tempDir.resolve("ghost.dat");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                fileManager.loadDerivative(badPath.toString())
        );

        assertTrue(ex.getMessage().contains("Не вдалося завантажити файл"));
    }

    @Test
    @DisplayName("loadDerivative кидає помилку, якщо файл містить не Derivative (або сміття)")
    void testLoadInvalidContent() throws IOException {
        Path filePath = tempDir.resolve("bad_data.dat");

        // Записуємо просто рядок тексту замість об'єкта Derivative
        // Це перевіряє, чи правильно обробляється виняток і чи не блокується файл.
        Files.writeString(filePath, "This is not a binary object");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                fileManager.loadDerivative(filePath.toString())
        );

        assertTrue(ex.getMessage().contains("Не вдалося завантажити файл"));
    }

    @Test
    @DisplayName("exportToText створює читабельний текстовий файл")
    void testExportToText() throws IOException {
        // 1. Підготовка
        Derivative derivative = new Derivative("Report Derivative");
        List<Obligation> list = new ArrayList<>();
        list.add(new TestSerializableObligation("Insurance A", 500.0));
        derivative.setObligations(list);

        Path txtPath = tempDir.resolve("report.txt");

        // 2. Експорт
        fileManager.exportToText(derivative, txtPath.toString());

        // 3. Перевірка існування
        assertTrue(Files.exists(txtPath));

        // 4. Перевірка вмісту
        String content = Files.readString(txtPath);

        // Використовуємо System.lineSeparator() для крос-платформеної сумісності
        String expectedContent = "DERIVATIVE" + System.lineSeparator() +
                "id: " + derivative.getId() + System.lineSeparator() +
                "name: Report Derivative" + System.lineSeparator() +
                "obligations: 1" + System.lineSeparator() +
                System.lineSeparator() +
                "[1] TestSerializableObligation" + System.lineSeparator() +
                "  id            : " + derivative.getObligations().get(0).getId() + System.lineSeparator() +
                "  name          : Insurance A" + System.lineSeparator() +
                "  insuredAmount : 500.0" + System.lineSeparator() +
                "  factor        : 1.0" + System.lineSeparator() +
                "  period        : 12" + System.lineSeparator() +
                "  interestRate  : 0.05" + System.lineSeparator() +
                "  probability   : 0.1" + System.lineSeparator() +
                "  maxCost       : 1000.0" + System.lineSeparator() +
                System.lineSeparator();

        // Порівнюємо повністю або перевіряємо ключові елементи
        assertTrue(content.contains("DERIVATIVE"));
        assertTrue(content.contains("name: Report Derivative"));
        assertTrue(content.contains("Insurance A"));
        assertTrue(content.contains("insuredAmount : 500.0"));
        assertTrue(content.contains("maxCost       : 1000.0")); // Додано для повноти перевірки

        // Додаткова перевірка кількості рядків, якщо потрібно
    }

    @Test
    @DisplayName("exportToText працює коректно навіть з пустим списком зобов'язань")
    void testExportEmptyDerivative() throws IOException {
        Derivative derivative = new Derivative("Empty One");

        Path txtPath = tempDir.resolve("empty.txt");
        fileManager.exportToText(derivative, txtPath.toString());

        String content = Files.readString(txtPath);
        assertTrue(content.contains("obligations: 0"));
        assertFalse(content.contains("TestSerializableObligation")); // Переконатися, що нічого зайвого немає
    }
}