package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import com.org.insurance.ui.command.SortByRiskCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SortByRiskCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // --- Stub Class ---
    static class TestRiskObligation extends Obligation {
        public TestRiskObligation(String name, double insuredAmount, double factor, double probability) {
            // Period=1, Rate=0, MaxCost=0 (не важливі для RiskComparator)
            super(name, insuredAmount, factor, 1, 0.0, probability, 0.0);
        }
        public TestRiskObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // ------------------

    private Scanner prepareInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    @DisplayName("getDescription повертає опис")
    void testGetDescription() {
        SortByRiskCommand cmd = new SortByRiskCommand();
        assertTrue(cmd.getDescription().contains("Сортувати"));
    }

    @Test
    @DisplayName("execute: Сортує облігації за зменшенням ризику (High -> Low)")
    void testExecuteSorting() {
        Derivative d = new Derivative("Risk Portfolio");
        List<Obligation> obs = new ArrayList<>();

        // 1. LOW RISK: 100 * 1.0 * 0.1 = 10.0
        Obligation low = new TestRiskObligation("Low", 100.0, 1.0, 0.1);

        // 2. HIGH RISK: 1000 * 2.0 * 0.5 = 1000.0
        Obligation high = new TestRiskObligation("High", 1000.0, 2.0, 0.5);

        // 3. MEDIUM RISK: 500 * 1.0 * 0.2 = 100.0
        Obligation med = new TestRiskObligation("Medium", 500.0, 1.0, 0.2);

        // Додаємо в хаотичному порядку: Low -> High -> Medium
        obs.add(low);
        obs.add(high);
        obs.add(med);
        d.setObligations(obs);

        // Ввід: "1" (обрати перший дериватив)
        Scanner scanner = prepareInput("1\n");

        // Виконання
        new SortByRiskCommand().execute(scanner, List.of(d));

        // ПЕРЕВІРКА ПОРЯДКУ (High -> Medium -> Low)
        List<Obligation> result = d.getObligations();
        assertEquals("High", result.get(0).getName());   // 1000.0
        assertEquals("Medium", result.get(1).getName()); // 100.0
        assertEquals("Low", result.get(2).getName());    // 10.0

        assertTrue(outContent.toString().contains("Відсортовано"));
    }

    @Test
    @DisplayName("execute: Обробка пустого списку облігацій")
    void testExecuteEmptyObligations() {
        Derivative d = new Derivative("EmptyD");
        d.setObligations(new ArrayList<>()); // Пустий список

        Scanner scanner = prepareInput("1\n");

        new SortByRiskCommand().execute(scanner, List.of(d));

        assertTrue(outContent.toString().contains("Порожньо"));
    }

    @Test
    @DisplayName("execute: Обробка відсутності деривативів")
    void testExecuteNoDerivatives() {
        Scanner scanner = prepareInput("1\n");

        new SortByRiskCommand().execute(scanner, Collections.emptyList());

        assertTrue(outContent.toString().contains("Немає деривативів"));
    }

    @Test
    @DisplayName("execute: Невірний вибір індексу деривативу")
    void testInvalidDerivativeIndex() {
        Derivative d = new Derivative("D");
        d.setObligations(new ArrayList<>());
        d.getObligations().add(new TestRiskObligation("O", 1,1,1));

        // Ввід: "5" (а є тільки 1)
        Scanner scanner = prepareInput("5\n");

        new SortByRiskCommand().execute(scanner, List.of(d));

        // Список не мав змінитись (хоча він і так був 1 елемент)
        // Головне, що програма не впала і нічого не вивела про "Відсортовано"
        assertFalse(outContent.toString().contains("Відсортовано"));
    }

    @Test
    @DisplayName("execute: Некоректний формат вводу (літери)")
    void testGarbageInput() {
        Derivative d = new Derivative("D");
        // Ввід: "abc"
        Scanner scanner = prepareInput("abc\n");

        new SortByRiskCommand().execute(scanner, List.of(d));

        // Має спрацювати catch(Exception) -> return null -> return з методу execute
        assertFalse(outContent.toString().contains("Відсортовано"));
    }
}