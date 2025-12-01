package com.org.insurance.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceCalculatorTest {

    private final InsuranceCalculator calculator = new InsuranceCalculator();

    // --- Stub Class (Заглушка) ---
    // Створюємо просту реалізацію Obligation, щоб передавати тестові дані
    static class TestObligation extends Obligation {
        public TestObligation(double insuredAmount, double factor, int period, double interestRate, double probability, double maxCost) {
            super("Test Item", insuredAmount, factor, period, interestRate, probability, maxCost);
        }

        // Ці методи не використовуються в калькуляторі, але потрібні для компіляції
        public TestObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // -----------------------------

    @Test
    @DisplayName("calculatePriceOfService: Базовий сценарій (Happy Path)")
    void testCalculatePriceOfServiceNormal() {
        // Дані:
        // Amount = 1000
        // Factor = 1.0
        // Probability = 0.1 (10%)
        // Period = 12 міс (1 рік)
        // Rate = 0.05 (5%)
        // MaxCost = 2000 (не обмежує)
        Obligation ob = new TestObligation(1000.0, 1.0, 12, 0.05, 0.1, 2000.0);

        // Логіка вручну:
        // 1. ExpectedLoss = 1000 * 0.1 * 1.0 = 100
        // 2. Years = 12 / 12 = 1.0
        // 3. TimeCoeff = 1 + 0.05 * 1.0 = 1.05
        // 4. Gross = 100 * 1.05 = 105.0
        // 5. 105 < 2000 -> return 105.0

        double price = calculator.calculatePriceOfService(ob);
        assertEquals(105.0, price, 0.0001);
    }

    @Test
    @DisplayName("calculatePriceOfService: Обрізка по maxCost")
    void testCalculatePriceOfServiceMaxCostCap() {
        // Розрахункова ціна буде 105.0 (як вище), але MaxCost ставимо 50.0
        Obligation ob = new TestObligation(1000.0, 1.0, 12, 0.05, 0.1, 50.0);

        double price = calculator.calculatePriceOfService(ob);
        assertEquals(50.0, price, "Ціна має бути обрізана до maxCost");
    }

    @Test
    @DisplayName("calculatePriceOfService: Період менше року (дробові роки)")
    void testCalculatePriceOfServicePartialYear() {
        // Period = 6 місяців (0.5 року)
        // Rate = 0.1
        // Amount = 1000, Prob = 0.1, Factor = 1
        Obligation ob = new TestObligation(1000.0, 1.0, 6, 0.1, 0.1, 1000.0);

        // 1. Loss = 100
        // 2. Years = 0.5
        // 3. Coeff = 1 + 0.1 * 0.5 = 1.05
        // 4. Price = 105.0

        double price = calculator.calculatePriceOfService(ob);
        assertEquals(105.0, price, 0.0001);
    }

    @Test
    @DisplayName("calculatePriceOfService: Повертає 0, якщо вхідні дані некоректні (<=0)")
    void testCalculatePriceOfServiceInvalidInputs() {
        Obligation zeroAmount = new TestObligation(0, 1, 12, 0.1, 0.1, 100);
        assertEquals(0.0, calculator.calculatePriceOfService(zeroAmount));

        Obligation zeroProb = new TestObligation(1000, 1, 12, 0.1, 0.0, 100);
        assertEquals(0.0, calculator.calculatePriceOfService(zeroProb));
    }

    @Test
    @DisplayName("calculatePriceOfService: Null input повертає 0")
    void testCalculatePriceOfServiceNull() {
        assertEquals(0.0, calculator.calculatePriceOfService(null));
    }

    @Test
    @DisplayName("calculatePortfolioValue: Сумує вартість усіх облігацій")
    void testCalculatePortfolioValue() {
        // Створюємо Derivative
        Derivative derivative = new Derivative("Test Portfolio");
        List<Obligation> list = new ArrayList<>();

        // Облігація 1: Ціна 105.0 (з першого тесту)
        list.add(new TestObligation(1000.0, 1.0, 12, 0.05, 0.1, 2000.0));

        // Облігація 2:
        // Amount 2000, Prob 0.1, Factor 1 -> Loss 200
        // Period 0 -> Years 0 -> Coeff 1.0
        // Price = 200
        list.add(new TestObligation(2000.0, 1.0, 0, 0.0, 0.1, 5000.0));

        derivative.setObligations(list);

        double total = calculator.calculatePortfolioValue(derivative);
        // 105.0 + 200.0 = 305.0
        assertEquals(305.0, total, 0.0001);
    }

    @Test
    @DisplayName("calculatePortfolioValue: Ігнорує null у списку та повертає 0 для пустих списків")
    void testCalculatePortfolioValueEdgeCases() {
        assertEquals(0.0, calculator.calculatePortfolioValue(null), "Null derivative -> 0");

        Derivative emptyDer = new Derivative();
        assertEquals(0.0, calculator.calculatePortfolioValue(emptyDer), "Null list -> 0");

        emptyDer.setObligations(Collections.emptyList());
        assertEquals(0.0, calculator.calculatePortfolioValue(emptyDer), "Empty list -> 0");

        // Список з null-елементом
        Derivative nullItemDer = new Derivative();
        List<Obligation> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        listWithNull.add(new TestObligation(1000.0, 1.0, 12, 0.05, 0.1, 2000.0)); // Ціна 105.0
        nullItemDer.setObligations(listWithNull);

        assertEquals(105.0, calculator.calculatePortfolioValue(nullItemDer), "Should skip null items");
    }

    @Test
    @DisplayName("calculateTotalRisk: Рахує суму riskScore (Amount * Factor * Prob)")
    void testCalculateTotalRisk() {
        Derivative derivative = new Derivative();
        List<Obligation> list = new ArrayList<>();

        // Item 1: 1000 * 1.5 * 0.1 = 150.0
        list.add(new TestObligation(1000.0, 1.5, 12, 0, 0.1, 0));

        // Item 2: 2000 * 1.0 * 0.2 = 400.0
        list.add(new TestObligation(2000.0, 1.0, 12, 0, 0.2, 0));

        derivative.setObligations(list);

        double risk = calculator.calculateTotalRisk(derivative);
        // 150 + 400 = 550
        assertEquals(550.0, risk, 0.0001);
    }

    @Test
    @DisplayName("calculateTotalRisk: Повертає 0 для null або пустих списків")
    void testCalculateTotalRiskEdgeCases() {
        assertEquals(0.0, calculator.calculateTotalRisk(null));

        Derivative d = new Derivative();
        d.setObligations(new ArrayList<>()); // Empty
        assertEquals(0.0, calculator.calculateTotalRisk(d));
    }
}