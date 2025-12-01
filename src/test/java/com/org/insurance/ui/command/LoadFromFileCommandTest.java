package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import com.org.insurance.io.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LoadFromFileCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // FileManager потрібен нам у тесті, щоб створити файл, який ми потім будемо завантажувати
    private final FileManager fileManager = new FileManager();

    @TempDir
    Path tempDir; // JUnit автоматично створить і видалить цю папку

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // --- Stub Class (Заглушка для серіалізації) ---
    // Нам потрібен конкретний клас, щоб покласти його в файл
    static class TestLoadableObligation extends Obligation {
        @Serial
        private static final long serialVersionUID = 1L;

        public TestLoadableObligation(String name) {
            super(name, 100.0, 1.0, 12, 0.05, 0.1, 1000.0);
        }
        public TestLoadableObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // ----------------------------------------------

    private Scanner prepareInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    @DisplayName("getDescription повертає коректний опис")
    void testGetDescription() {
        LoadFromFileCommand cmd = new LoadFromFileCommand();
        assertNotNull(cmd.getDescription());
        assertTrue(cmd.getDescription().contains("Завантажити"));
    }

    @Test
    @DisplayName("execute: Успішно завантажує існуючий файл і додає дериватив у список")
    void testExecuteSuccess() {
        // 1. ПІДГОТОВКА: Створюємо файл з даними
        Derivative original = new Derivative("Saved Portfolio");
        List<Obligation> obs = new ArrayList<>();
        obs.add(new TestLoadableObligation("Item A"));
        original.setObligations(obs);

        // Шлях до файлу у тимчасовій папці
        String filename = tempDir.resolve("data.bin").toString();
        fileManager.saveDerivative(original, filename);

        // 2. ДІЯ: Запускаємо команду, передаючи їй шлях до файлу
        String input = filename + "\n"; // Імітуємо ввід шляху користувачем
        Scanner scanner = prepareInput(input);

        List<Derivative> derivatives = new ArrayList<>();
        LoadFromFileCommand cmd = new LoadFromFileCommand();

        cmd.execute(scanner, derivatives);

        // 3. ПЕРЕВІРКА
        assertEquals(1, derivatives.size(), "Список має збільшитися на 1");

        Derivative loaded = derivatives.get(0);
        // Порівнюємо ID, бо це найнадійніший спосіб перевірити, що це той самий об'єкт
        assertEquals(original.getId(), loaded.getId());
        assertEquals("Saved Portfolio", loaded.getName());
        assertEquals(1, loaded.getObligations().size());

        // Перевіряємо консоль
        assertTrue(outContent.toString().contains("Завантажено"));
    }

    @Test
    @DisplayName("execute: Нічого не робить, якщо введено пустий рядок")
    void testExecuteEmptyPath() {
        // Користувач просто натиснув Enter
        String input = "\n";
        Scanner scanner = prepareInput(input);

        List<Derivative> derivatives = new ArrayList<>();
        LoadFromFileCommand cmd = new LoadFromFileCommand();

        cmd.execute(scanner, derivatives);

        assertTrue(derivatives.isEmpty(), "Список має залишитися пустим");
    }

    @Test
    @DisplayName("execute: Викидає помилку (RuntimeException), якщо файл не існує")
    void testExecuteFileNotFound() {
        // Важливо: Ваш клас FileManager кидає RuntimeException, а LoadFromFileCommand його не ловить (немає try-catch).
        // Тому ми очікуємо, що тест впаде з помилкою, і це правильна поведінка коду.

        String badPath = tempDir.resolve("ghost.bin").toString();
        String input = badPath + "\n";
        Scanner scanner = prepareInput(input);

        List<Derivative> derivatives = new ArrayList<>();
        LoadFromFileCommand cmd = new LoadFromFileCommand();

        // Перевіряємо, що вилітає помилка (бо файл не існує)
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                cmd.execute(scanner, derivatives)
        );

        // Перевіряємо, що повідомлення про помилку йде від FileManager
        assertTrue(ex.getMessage().contains("Не вдалося завантажити файл"));
    }
}