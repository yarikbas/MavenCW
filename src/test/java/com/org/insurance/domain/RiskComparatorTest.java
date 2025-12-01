package com.org.insurance.domain;

import com.org.insurance.domain.Obligation;
import com.org.insurance.domain.RiskComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class RiskComparatorTest {

    private final RiskComparator comparator = new RiskComparator();

    // --- Stub Class (Заглушка) ---
    // Створюємо просту реалізацію для тестів
    static class TestObligation extends Obligation {
        public TestObligation(double insuredAmount, double factor, double probability) {
            // Інші поля (name, period, interestRate, maxCost) не впливають на riskScore,
            // тому заповнюємо їх дефолтними значеннями.
            super("Test", insuredAmount, factor, 12, 0.0, probability, 0.0);
        }

        // Ці методи потрібні для компіляції, але в цьому тесті не використовуються
        public TestObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // -----------------------------

    @Test
    @DisplayName("riskScore: Коректно рахує за формулою (Amount * Factor * Probability)")
    void testRiskScoreCalculation() {
        // Data: Amount=1000, Factor=1.5, Prob=0.2
        // Formula: 1000 * 1.5 * 0.2 = 300.0
        Obligation obligation = new TestObligation(1000.0, 1.5, 0.2);

        double result = RiskComparator.riskScore(obligation);

        assertEquals(300.0, result, 0.0001);
    }

    @Test
    @DisplayName("riskScore: Повертає 0.0, якщо передано null")
    void testRiskScoreNull() {
        assertEquals(0.0, RiskComparator.riskScore(null));
    }

    @Test
    @DisplayName("riskScore: Повертає 0.0, якщо хоча б один з множників = 0")
    void testRiskScoreZero() {
        // Amount = 0
        assertEquals(0.0, RiskComparator.riskScore(new TestObligation(0, 1.5, 0.2)));

        // Probability = 0
        assertEquals(0.0, RiskComparator.riskScore(new TestObligation(1000, 1.5, 0.0)));
    }

    @Test
    @DisplayName("compare: Повертає від'ємне число, якщо ризик першого менший")
    void testCompareLess() {
        // Risk = 100 (1000 * 1 * 0.1)
        Obligation lowRisk = new TestObligation(1000.0, 1.0, 0.1);

        // Risk = 500 (1000 * 5 * 0.1)
        Obligation highRisk = new TestObligation(1000.0, 5.0, 0.1);

        // lowRisk < highRisk -> результат має бути < 0
        int result = comparator.compare(lowRisk, highRisk);
        assertTrue(result < 0, "Має повернути від'ємне число");
    }

    @Test
    @DisplayName("compare: Повертає додатне число, якщо ризик першого більший")
    void testCompareGreater() {
        // Risk = 500
        Obligation highRisk = new TestObligation(1000.0, 5.0, 0.1);

        // Risk = 100
        Obligation lowRisk = new TestObligation(1000.0, 1.0, 0.1);

        // highRisk > lowRisk -> результат має бути > 0
        int result = comparator.compare(highRisk, lowRisk);
        assertTrue(result > 0, "Має повернути додатне число");
    }

    @Test
    @DisplayName("compare: Повертає 0, якщо ризики рівні")
    void testCompareEqual() {
        // Risk = 200
        Obligation o1 = new TestObligation(1000.0, 2.0, 0.1);
        // Risk = 200
        Obligation o2 = new TestObligation(2000.0, 1.0, 0.1);

        int result = comparator.compare(o1, o2);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("compare: Коректно обробляє null (вважаючи його ризик 0.0)")
    void testCompareNulls() {
        Obligation o1 = new TestObligation(1000.0, 1.0, 0.1); // Risk > 0

        // null (0.0) < o1 (100.0) -> negative
        assertTrue(comparator.compare(null, o1) < 0);

        // o1 (100.0) > null (0.0) -> positive
        assertTrue(comparator.compare(o1, null) > 0);

        // null == null -> 0
        assertEquals(0, comparator.compare(null, null));
    }
}