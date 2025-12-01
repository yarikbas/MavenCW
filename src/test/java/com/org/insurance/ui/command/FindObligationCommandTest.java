package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import com.org.insurance.ui.command.FindObligationCommand;
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

class FindObligationCommandTest {

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

    // --- Stub Class (Заглушка) ---
    static class TestFindObligation extends Obligation {
        public TestFindObligation(String name, double amount, double factor, int period, double rate, double prob, double maxCost) {
            super(name, amount, factor, period, rate, prob, maxCost);
        }
        public TestFindObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // -----------------------------

    private Scanner prepareInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    private Derivative createDerivativeWithData() {
        Derivative d = new Derivative("SearchPortfolio");
        List<Obligation> obs = new ArrayList<>();

        // 1. "Auto Low" - дешева, малий ризик
        obs.add(new TestFindObligation("Auto Low", 100.0, 1.0, 6, 0.05, 0.1, 1000.0));

        // 2. "Auto High" - дорога, високий ризик
        obs.add(new TestFindObligation("Auto High", 5000.0, 2.0, 12, 0.1, 0.5, 10000.0));

        // 3. "Home" - середнє
        obs.add(new TestFindObligation("Home Insurance", 1000.0, 1.5, 24, 0.05, 0.2, 5000.0));

        d.setObligations(obs);
        return d;
    }

    @Test
    @DisplayName("getDescription повертає опис команди")
    void testGetDescription() {
        FindObligationCommand cmd = new FindObligationCommand();
        assertNotNull(cmd.getDescription());
        assertTrue(cmd.getDescription().contains("Пошук зобов’язань"));
    }

    @Test
    @DisplayName("Пошук за частковим збігом назви (case-insensitive)")
    void testFilterByName() {
        // Сценарій:
        // 1. Вибір деривативу "1"
        // 2. Назва містить "auto"
        // 3...14. Пропускаємо всі числові фільтри (12 натискань Enter)
        String input = "1\n" + "auto\n" + "\n".repeat(12);

        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createDerivativeWithData());

        FindObligationCommand cmd = new FindObligationCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        // Має знайти "Auto Low" і "Auto High", але не "Home Insurance"
        assertTrue(output.contains("Auto Low"));
        assertTrue(output.contains("Auto High"));
        assertFalse(output.contains("Home Insurance"));
    }

    @Test
    @DisplayName("Пошук за діапазоном суми (Amount Range)")
    void testFilterByAmountRange() {
        // Сценарій: Знайти все, де сума від 500 до 2000
        // 1. Der: "1"
        // 2. Name: "" (skip)
        // 3. Min Amount: "500"
        // 4. Max Amount: "2000"
        // 5...14. Skip rest
        String input = "1\n" + "\n" + "500\n2000\n" + "\n".repeat(10);

        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createDerivativeWithData());

        new FindObligationCommand().execute(scanner, list);
        String output = outContent.toString();

        // Має знайти тільки "Home Insurance" (1000.0)
        // "Auto Low" (100) - замало
        // "Auto High" (5000) - забагато
        assertTrue(output.contains("Home Insurance"));
        assertFalse(output.contains("Auto Low"));
        assertFalse(output.contains("Auto High"));
    }

    @Test
    @DisplayName("Комбінація фільтрів: Factor + Period")
    void testCombinedFilters() {
        // Знайти: Factor > 1.8 ТА Period <= 12
        // 1. Der: 1
        // 2. Name: skip
        // 3-4. Amount: skip
        // 5. Min Factor: 1.8
        // 6. Max Factor: skip
        // 7. Min Period: skip
        // 8. Max Period: 12
        // ... rest skip
        String input = "1\n" + "\n" + "\n\n" + "1.8\n\n" + "\n12\n" + "\n".repeat(6);

        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createDerivativeWithData());

        new FindObligationCommand().execute(scanner, list);
        String output = outContent.toString();

        // "Auto High": Factor 2.0 (>1.8), Period 12 (<=12) -> MATCH
        // "Home": Factor 1.5 (<1.8) -> FAIL
        // "Auto Low": Factor 1.0 (<1.8) -> FAIL
        assertTrue(output.contains("Auto High"));
        assertFalse(output.contains("Home Insurance"));
        assertFalse(output.contains("Auto Low"));
    }

    @Test
    @DisplayName("Якщо нічого не знайдено, виводиться відповідне повідомлення")
    void testNothingFound() {
        // Шукаємо суму > 999999
        String input = "1\n" + "\n" + "999999\n\n" + "\n".repeat(10);

        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createDerivativeWithData());

        new FindObligationCommand().execute(scanner, list);
        String output = outContent.toString();

        assertTrue(output.contains("Нічого не знайдено"));
    }

    @Test
    @DisplayName("Пустий ввід (Enter) у числових полях означає 'пропустити фільтр'")
    void testSkipFilters() {
        // Просто натискаємо Enter скрізь -> має показати всі облігації
        String input = "1\n" + "\n" + "\n".repeat(12);

        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createDerivativeWithData());

        new FindObligationCommand().execute(scanner, list);
        String output = outContent.toString();

        assertTrue(output.contains("Auto Low"));
        assertTrue(output.contains("Auto High"));
        assertTrue(output.contains("Home Insurance"));
    }

    @Test
    @DisplayName("Некоректний ввід у числових полях ігнорується (сприймається як null/skip)")
    void testGarbageInputInNumericFields() {
        // Вводимо "abc" замість Min Amount -> має проігнорувати і показати все
        // Der: 1 -> Name: "" -> MinAmt: "abc" -> rest: ""
        String input = "1\n" + "\n" + "abc\n" + "\n".repeat(11);

        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createDerivativeWithData());

        new FindObligationCommand().execute(scanner, list);
        String output = outContent.toString();

        // Очікуємо, що фільтр зламається (поверне null) і покаже всі записи
        assertTrue(output.contains("Auto Low"));
        assertTrue(output.contains("Auto High"));
    }

    @Test
    @DisplayName("Обробка пустих списків деривативів або облігацій")
    void testEmptyLists() {
        FindObligationCommand cmd = new FindObligationCommand();

        // 1. Немає деривативів
        Scanner s1 = prepareInput("1\n");
        cmd.execute(s1, Collections.emptyList());
        assertTrue(outContent.toString().contains("Немає деривативів"));
        outContent.reset();

        // 2. Дериватив без облігацій
        Derivative emptyD = new Derivative("EmptyD");
        emptyD.setObligations(new ArrayList<>());
        Scanner s2 = prepareInput("1\n");
        cmd.execute(s2, List.of(emptyD));
        assertTrue(outContent.toString().contains("У деривативі немає облігацій"));
    }
}