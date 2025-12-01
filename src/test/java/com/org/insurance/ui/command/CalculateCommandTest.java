package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
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

class CalculateCommandTest {

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
    // Проста облігація для розрахунків
    static class TestCalcObligation extends Obligation {
        public TestCalcObligation(double insuredAmount, double factor, int period, double rate, double probability) {
            // maxCost = 1,000,000 (велике число, щоб не спрацював ліміт)
            super("TestItem", insuredAmount, factor, period, rate, probability, 1_000_000.0);
        }
        public TestCalcObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // -----------------------------

    private Scanner prepareInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    private Derivative createTestDerivative() {
        Derivative d = new Derivative("Portfolio A");
        List<Obligation> obs = new ArrayList<>();

        // Створюємо облігацію з простими числами для перевірки
        // Amount = 1000
        // Factor = 1
        // Prob = 0.1
        // Period = 12
        // Rate = 0.0 (щоб простіше рахувати)
        // Очікувана ціна = 1000 * 0.1 * 1 * (1 + 0) = 100.0
        // Очікуваний ризик = 1000 * 1 * 0.1 = 100.0
        obs.add(new TestCalcObligation(1000.0, 1.0, 12, 0.0, 0.1));

        d.setObligations(obs);
        return d;
    }

    @Test
    @DisplayName("getDescription повертає меню розрахунків")
    void testGetDescription() {
        CalculateCommand cmd = new CalculateCommand();
        String desc = cmd.getDescription();
        assertTrue(desc.contains("Вартість портфеля"));
        assertTrue(desc.contains("Ціна сервісу"));
    }

    @Test
    @DisplayName("Дія 1: Вартість портфеля (Portfolio Value)")
    void testCalculatePortfolioValue() {
        // Ввід: "1" (Дія) -> "1" (Дериватив №1)
        String input = "1\n1\n";
        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createTestDerivative());

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        // Перевіряємо, що вивелось повідомлення і число 100.0
        assertTrue(output.contains("Вартість портфеля"));
        // Враховуємо, що роздільник може бути комою або крапкою
        assertTrue(output.contains("100,00") || output.contains("100.00"));
        // Перевіряємо, що викликався ConsolePrinter (детальний звіт)
        assertTrue(output.contains("РОЗРАХУНОК ВАРТОСТІ ПОРТФЕЛЯ"));
    }

    @Test
    @DisplayName("Дія 2: Сумарний ризик (Total Risk)")
    void testCalculateTotalRisk() {
        // Ввід: "2" (Дія) -> "1" (Дериватив №1)
        String input = "2\n1\n";
        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createTestDerivative());

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        assertTrue(output.contains("Сумарний ризик портфеля"));
        assertTrue(output.contains("100,00") || output.contains("100.00"));
        assertTrue(output.contains("РОЗРАХУНОК СУМАРНОГО РИЗИКУ"));
    }

    @Test
    @DisplayName("Дія 3: Ціна сервісу для конкретної облігації")
    void testCalculateServicePrice() {
        // Ввід: "3" (Дія) -> "1" (Дериватив №1) -> "1" (Облігація №1)
        String input = "3\n1\n1\n";
        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createTestDerivative());

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        assertTrue(output.contains("Ціна сервісу для облігації"));
        assertTrue(output.contains("100,00") || output.contains("100.00"));
        assertTrue(output.contains("РОЗРАХУНОК СТРАХОВОЇ ПРЕМІЇ"));
    }

    @Test
    @DisplayName("Обробка ситуації: Список деривативів порожній")
    void testEmptyDerivativeList() {
        // Ввід: "1" (спроба щось порахувати)
        String input = "1\n";
        Scanner scanner = prepareInput(input);

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, Collections.emptyList());

        String output = outContent.toString();
        assertTrue(output.contains("Список деривативів порожній"));
    }

    @Test
    @DisplayName("Обробка ситуації: Невірний номер деривативу")
    void testInvalidDerivativeIndex() {
        // Ввід: "1" (Дія) -> "5" (Неіснуючий номер)
        String input = "1\n5\n";
        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(createTestDerivative());

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();
        assertTrue(output.contains("Невірний номер"));
    }

    @Test
    @DisplayName("Обробка ситуації: Невірний вибір дії (меню)")
    void testInvalidMenuAction() {
        // Ввід: "99"
        String input = "99\n";
        Scanner scanner = prepareInput(input);

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, new ArrayList<>());

        String output = outContent.toString();
        assertTrue(output.contains("Невірний вибір"));
    }

    @Test
    @DisplayName("Обробка ситуації: У деривативі немає облігацій (для дії 3)")
    void testEmptyObligationsList() {
        Derivative emptyDer = new Derivative("Empty D");
        emptyDer.setObligations(new ArrayList<>()); // Пустий список

        // Ввід: "3" (Дія) -> "1" (Дериватив №1)
        String input = "3\n1\n";
        Scanner scanner = prepareInput(input);
        List<Derivative> list = List.of(emptyDer);

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();
        assertTrue(output.contains("У деривативі немає облігацій"));
    }

    @Test
    @DisplayName("readInt повертає -1 на некоректний ввід")
    void testInvalidInputFormat() {
        // Вводимо текст замість цифри: "abc"
        String input = "abc\n";
        Scanner scanner = prepareInput(input);

        CalculateCommand cmd = new CalculateCommand();
        cmd.execute(scanner, new ArrayList<>());

        String output = outContent.toString();
        // Оскільки readInt поверне -1, це потрапить у default switch case
        assertTrue(output.contains("Невірний вибір"));
    }
}