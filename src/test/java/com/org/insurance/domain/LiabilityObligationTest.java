package com.org.insurance.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LiabilityObligationTest {

    private final InputStream originalSystemIn = System.in;

    @AfterEach
    void restoreSystemIn() {
        // Відновлюємо консоль після кожного тесту
        System.setIn(originalSystemIn);
    }

    @Test
    @DisplayName("Конструктор з параметрами коректно встановлює всі поля")
    void testConstructorWithArguments() {
        LiabilityObligation liability = new LiabilityObligation(
                "Public Liability", 100000.0, 1.3, 12, 0.05, 0.02, 200000.0,
                "Professional Indemnity", "UK"
        );

        // Перевірка батьківських полів
        assertNotNull(liability.getId());
        assertEquals("Public Liability", liability.getName());
        assertEquals(100000.0, liability.getInsuredAmount());

        // Перевірка власних полів
        assertEquals("Professional Indemnity", liability.getCoverageType());
        assertEquals("UK", liability.getJurisdiction());
    }

    @Test
    @DisplayName("Інтерактивний конструктор зчитує дані з System.in")
    void testInteractiveConstructor() {
        // Послідовність вводу:
        // 1. Name -> 2. Amount -> 3. Factor -> 4. Period -> 5. Rate -> 6. Prob -> 7. MaxCost (Parent)
        // 8. coverageType -> 9. jurisdiction (Specific)
        String inputData = """
                General Liability
                50000
                1.1
                24
                0.04
                0.1
                100000
                Third Party
                EU
                """;

        System.setIn(new ByteArrayInputStream(inputData.getBytes()));

        LiabilityObligation liability = new LiabilityObligation();

        assertEquals("General Liability", liability.getName());
        assertEquals(50000.0, liability.getInsuredAmount());
        assertEquals(24, liability.getPeriod());
        assertEquals("Third Party", liability.getCoverageType());
        assertEquals("EU", liability.getJurisdiction());
    }

    @Test
    @DisplayName("setSpecificFields оновлює дані при коректному вводі")
    void testSetSpecificFieldsUpdate() {
        LiabilityObligation liability = new LiabilityObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OldType", "OldJur"
        );

        String input = """
                NewType
                NewJur
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        liability.setSpecificFields(scanner);

        assertEquals("NewType", liability.getCoverageType());
        assertEquals("NewJur", liability.getJurisdiction());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує пусті рядки (Enter)")
    void testSetSpecificFieldsSkipEmpty() {
        LiabilityObligation liability = new LiabilityObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OriginalType", "OriginalJur"
        );

        // Два пусті рядки
        String input = """
                
                
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        liability.setSpecificFields(scanner);

        assertEquals("OriginalType", liability.getCoverageType(), "Тип не мав змінитись");
        assertEquals("OriginalJur", liability.getJurisdiction(), "Юрисдикція не мала змінитись");
    }

    @Test
    @DisplayName("Сеттери та Геттери Lombok працюють")
    void testLombok() {
        // Використовуємо повний конструктор, щоб уникнути Scanner(System.in)
        LiabilityObligation liability = new LiabilityObligation(null, 0,0,0,0,0,0, null, null);

        liability.setCoverageType("Product Liability");
        liability.setJurisdiction("USA");

        assertEquals("Product Liability", liability.getCoverageType());
        assertEquals("USA", liability.getJurisdiction());
    }

    @Test
    @DisplayName("toString формує коректний рядок")
    void testToString() {
        LiabilityObligation liability = new LiabilityObligation(
                "LiabTest", 0, 0, 0, 0, 0, 0,
                "D&O", "Global"
        );

        String result = liability.toString();

        assertTrue(result.contains("LiabilityObligation"));
        assertTrue(result.contains("LiabTest"));
        assertTrue(result.contains("D&O"));
        assertTrue(result.contains("Global"));
    }
}