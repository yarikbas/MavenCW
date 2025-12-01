package com.org.insurance.ui;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.InsuranceCalculator;
import com.org.insurance.domain.Obligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsolePrinterTest {
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
    static class TestObligation extends Obligation {
        public TestObligation(String name, double insuredAmount, double factor, double probability, double maxCost) {
            // Period = 12, Rate = 0.1 (10%)
            super(name, insuredAmount, factor, 12, 0.1, probability, maxCost);
        }
        public TestObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // -----------------------------

    @Test
    @DisplayName("printDerivatives: Виводить повідомлення, якщо список порожній")
    void testPrintDerivativesEmpty() {
        ConsolePrinter.printDerivatives(Collections.emptyList());

        String output = outContent.toString();
        assertTrue(output.contains("Список порожній"));
    }

    @Test
    @DisplayName("printDerivatives: Виводить список деривативів з індексами")
    void testPrintDerivativesNormal() {
        List<Derivative> list = new ArrayList<>();
        list.add(new Derivative("Portfolio A"));
        list.add(new Derivative("Portfolio B"));

        ConsolePrinter.printDerivatives(list);

        String output = outContent.toString();
        assertTrue(output.contains("1) Portfolio A"));
        assertTrue(output.contains("2) Portfolio B"));
    }

    @Test
    @DisplayName("printDerivativesWithObligations: Виводить деривативи та їх вміст")
    void testPrintDerivativesWithObligations() {
        Derivative d = new Derivative("Main Portfolio");
        List<Obligation> obs = new ArrayList<>();
        obs.add(new TestObligation("Item 1", 100, 1, 0.1, 1000));
        d.setObligations(obs);

        List<Derivative> list = List.of(d);

        ConsolePrinter.printDerivativesWithObligations(list);

        String output = outContent.toString();
        assertTrue(output.contains("Main Portfolio"));

        assertTrue(output.contains("1 зобов'язань") || output.contains("1 зобов'язання"));

        assertTrue(output.contains("Item 1"));

        assertTrue(output.contains("risk="));
    }

    @Test
    @DisplayName("printObligationsOf: Обробляє null та пусті списки")
    void testPrintObligationsOfEdgeCases() {
        // 1. Null derivative
        ConsolePrinter.printObligationsOf(null);
        assertTrue(outContent.toString().contains("Дериватив не обрано"));
        outContent.reset(); // Очистити буфер

        // 2. Empty obligations
        Derivative d = new Derivative("Empty");
        ConsolePrinter.printObligationsOf(d);
        assertTrue(outContent.toString().contains("Зобов'язань немає"));
    }
    @Test
    @DisplayName("printObligationsOf: Виводить деталі кожної облігації")
    void testPrintObligationsOfNormal() {
        Derivative d = new Derivative("Rich Portfolio");
        List<Obligation> obs = new ArrayList<>();
        // Ім'я облігації - "Ob1"
        obs.add(new TestObligation("Ob1", 1000, 1.5, 0.1, 5000));
        d.setObligations(obs);

        ConsolePrinter.printObligationsOf(d);

        String output = outContent.toString();
        assertTrue(output.contains("1) Ob1 (Test"));
        assertTrue(output.contains("risk=150.000"));
        assertTrue(output.contains("id="));
    }

    @Test
    @DisplayName("printPriceCalculation: Виводить детальний математичний розрахунок")
    void testPrintPriceCalculation() {
        Obligation o = new TestObligation("TestItem", 1000.0, 1.2, 0.05, 2000.0);
        double fakePremium = 123.45;

        ConsolePrinter.printPriceCalculation(o, fakePremium);

        String output = outContent.toString();

        // Перевіряємо наявність заголовків та змінних
        assertTrue(output.contains("РОЗРАХУНОК СТРАХОВОЇ ПРЕМІЇ"));
        assertTrue(output.contains("insuredAmount = 1000,00") || output.contains("insuredAmount = 1000.00"));
        assertTrue(output.contains("expectedLoss ="));
        assertTrue(output.contains("timeCoeff ="));
        assertTrue(output.contains("grossPremium"));

        // Перевіряємо кінцевий результат
        assertTrue(output.contains("123,45") || output.contains("123.45"));
    }

    @Test
    @DisplayName("printPriceCalculation: Виводить повідомлення про ліміт maxCost")
    void testPrintPriceCalculationWithMaxCostLimit() {
        // Створюємо ситуацію, де розрахункова сума буде великою, а ліміт малим
        Obligation o = new TestObligation("Expensive Item", 1000000.0, 2.0, 0.5, 10.0);

        ConsolePrinter.printPriceCalculation(o, 10.0);

        String output = outContent.toString();
        assertTrue(output.contains("застосовуємо ліміт maxCost"));
    }

    @Test
    @DisplayName("printPortfolioValueCalculation: Виводить суму по портфелю")
    void testPrintPortfolioValueCalculation() {
        Derivative d = new Derivative("My Der");
        List<Obligation> obs = new ArrayList<>();
        obs.add(new TestObligation("O1", 100, 1, 0.1, 1000));
        d.setObligations(obs);

        InsuranceCalculator calculator = new InsuranceCalculator();

        ConsolePrinter.printPortfolioValueCalculation(d, calculator);

        String output = outContent.toString();
        assertTrue(output.contains("РОЗРАХУНОК ВАРТОСТІ ПОРТФЕЛЯ"));
        assertTrue(output.contains("O1"));
        assertTrue(output.contains("СУМА премій"));
    }

    @Test
    @DisplayName("printTotalRiskCalculation: Виводить формулу ризику")
    void testPrintTotalRiskCalculation() {
        Derivative d = new Derivative("Risk Der");
        List<Obligation> obs = new ArrayList<>();
        obs.add(new TestObligation("RiskyItem", 1000, 2.0, 0.1, 0));
        d.setObligations(obs);

        ConsolePrinter.printTotalRiskCalculation(d);

        String output = outContent.toString();
        assertTrue(output.contains("РОЗРАХУНОК СУМАРНОГО РИЗИКУ"));
        assertTrue(output.contains("risk = insuredAmount * factor * probability"));
        assertTrue(output.contains("200,00") || output.contains("200.00"));
        assertTrue(output.contains("СУМАРНИЙ РИЗИК ПОРТФЕЛЯ"));
    }
}