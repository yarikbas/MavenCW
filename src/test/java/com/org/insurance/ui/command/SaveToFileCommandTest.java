package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SaveToFileCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @TempDir
    Path tempDir; // JUnit створить цю папку перед тестом

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // --- Stub Class ---
    // Потрібен для тестування бінарного збереження (Serializable)
    static class TestSerializableObligation extends Obligation {
        @Serial
        private static final long serialVersionUID = 1L;

        public TestSerializableObligation(String name) {
            super(name, 100, 1, 1, 0, 0, 0);
        }
        public TestSerializableObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // ------------------

    private Scanner prepareInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    private Derivative createDerivative() {
        Derivative d = new Derivative("SaveMe");
        List<Obligation> obs = new ArrayList<>();
        obs.add(new TestSerializableObligation("Test Item"));
        d.setObligations(obs);
        return d;
    }

    @Test
    @DisplayName("getDescription повертає опис")
    void testGetDescription() {
        SaveToFileCommand cmd = new SaveToFileCommand();
        assertTrue(cmd.getDescription().contains("Зберегти"));
    }

    @Test
    @DisplayName("Успішне збереження у BIN формат")
    void testSaveToBinSuccess() {
        List<Derivative> list = List.of(createDerivative());

        // Шлях до файлу у tempDir
        Path filePath = tempDir.resolve("portfolio.bin");

        // Ввід: "1" (вибір деривативу) -> "bin" (формат) -> шлях до файлу
        String input = "1\nbin\n" + filePath.toString() + "\n";
        Scanner scanner = prepareInput(input);

        new SaveToFileCommand().execute(scanner, list);

        // Перевірка:
        assertTrue(Files.exists(filePath), "Файл .bin мав бути створений");
        assertTrue(outContent.toString().contains("Збережено у файл"));
    }

    @Test
    @DisplayName("Успішне збереження у TXT формат")
    void testSaveToTxtSuccess() {
        List<Derivative> list = List.of(createDerivative());

        Path filePath = tempDir.resolve("portfolio.txt");

        // Ввід: "1" -> "txt" -> шлях
        String input = "1\ntxt\n" + filePath.toString() + "\n";
        Scanner scanner = prepareInput(input);

        new SaveToFileCommand().execute(scanner, list);

        assertTrue(Files.exists(filePath), "Файл .txt мав бути створений");
        assertTrue(outContent.toString().contains("Збережено у файл"));
    }

    @Test
    @DisplayName("Якщо список деривативів порожній, команда виходить")
    void testEmptyList() {
        Scanner scanner = prepareInput("1\n");
        new SaveToFileCommand().execute(scanner, Collections.emptyList());

        assertTrue(outContent.toString().contains("Немає деривативів"));
    }

    @Test
    @DisplayName("Невірний вибір індексу деривативу")
    void testInvalidIndex() {
        List<Derivative> list = List.of(createDerivative());

        // Ввід: "5" (а є тільки 1)
        String input = "5\n";
        Scanner scanner = prepareInput(input);

        new SaveToFileCommand().execute(scanner, list);

        assertTrue(outContent.toString().contains("Невірний вибір"));
    }

    @Test
    @DisplayName("Невірний формат файлу (не bin і не txt)")
    void testInvalidFormat() {
        List<Derivative> list = List.of(createDerivative());

        // Ввід: "1" -> "pdf"
        String input = "1\npdf\n";
        Scanner scanner = prepareInput(input);

        new SaveToFileCommand().execute(scanner, list);

        assertTrue(outContent.toString().contains("Невідомий формат"));
    }

    @Test
    @DisplayName("Пустий шлях до файлу скасовує дію")
    void testEmptyPathCancels() {
        List<Derivative> list = List.of(createDerivative());

        // Ввід: "1" -> "bin" -> "" (Enter)
        String input = "1\nbin\n\n";
        Scanner scanner = prepareInput(input);

        new SaveToFileCommand().execute(scanner, list);

        assertTrue(outContent.toString().contains("Скасовано"));
    }

    @Test
    @DisplayName("Команда обробляє введення сміття замість індексу")
    void testGarbageIndexInput() {
        List<Derivative> list = List.of(createDerivative());

        // Ввід: "abc" замість числа
        String input = "abc\n";
        Scanner scanner = prepareInput(input);

        new SaveToFileCommand().execute(scanner, list);

        // catch (Exception e) { idx = -1; } -> потім перевірка if (idx < 1)
        assertTrue(outContent.toString().contains("Невірний вибір"));
    }
}