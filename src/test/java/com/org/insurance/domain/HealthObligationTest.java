package com.org.insurance.domain;

import com.org.insurance.domain.HealthObligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class HealthObligationTest {

    private final InputStream originalSystemIn = System.in;

    @AfterEach
    void restoreSystemIn() {
        // Відновлюємо стандартний потік вводу, щоб не зламати інші тести
        System.setIn(originalSystemIn);
    }

    @Test
    @DisplayName("Конструктор з параметрами коректно ініціалізує всі поля")
    void testConstructorWithArguments() {
        HealthObligation health = new HealthObligation(
                "Life Insurance", 10000.0, 1.5, 12, 0.05, 0.01, 50000.0,
                "Full Coverage", true, 20000.0
        );

        // Перевірка полів батьківського класу
        assertNotNull(health.getId());
        assertEquals("Life Insurance", health.getName());
        assertEquals(10000.0, health.getInsuredAmount());

        // Перевірка власних полів
        // Увага: використовуємо ваші назви методів (getcoverageType з маленької літери)
        assertEquals("Full Coverage", health.getCoverageType());
        assertTrue(health.isHasPreExistingConditions());
        assertEquals(20000.0, health.getAnnualLimit());
    }

    @Test
    @DisplayName("Інтерактивний конструктор (System.in) коректно зчитує дані")
    void testInteractiveConstructor() {
        // Імітація вводу користувача.
        // Послідовність:
        // 1. Name -> 2. Amount -> 3. Factor -> 4. Period -> 5. Rate -> 6. Prob -> 7. MaxCost (Base)
        // 8. coverageType -> 9. hasPreExistingConditions -> 10. annualLimit (Specific)
        String inputData = """
                Basic Health
                5000
                1.2
                24
                0.1
                0.2
                10000
                Basic
                false
                15000.50
                """;

        System.setIn(new ByteArrayInputStream(inputData.getBytes()));

        HealthObligation health = new HealthObligation();

        assertEquals("Basic Health", health.getName());
        assertEquals(5000.0, health.getInsuredAmount());
        assertEquals("Basic", health.getCoverageType());
        assertFalse(health.isHasPreExistingConditions());
        assertEquals(15000.50, health.getAnnualLimit());
    }

    @Test
    @DisplayName("setSpecificFields оновлює значення при коректному вводі")
    void testSetSpecificFieldsUpdate() {
        HealthObligation health = new HealthObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OldType", false, 0
        );

        String input = """
                Premium
                true
                50000
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        health.setSpecificFields(scanner);

        assertEquals("Premium", health.getCoverageType());
        assertTrue(health.isHasPreExistingConditions());
        assertEquals(50000.0, health.getAnnualLimit());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує пусті рядки (залишає старі значення)")
    void testSetSpecificFieldsSkipEmpty() {
        HealthObligation health = new HealthObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OldType", true, 1000
        );

        // Три пусті рядки (користувач просто натиснув Enter 3 рази)
        String input = """
                
                
                
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        health.setSpecificFields(scanner);

        assertEquals("OldType", health.getCoverageType());
        assertTrue(health.isHasPreExistingConditions());
        assertEquals(1000.0, health.getAnnualLimit());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує некоректний формат числа для annualLimit")
    void testSetSpecificFieldsInvalidNumber() {
        HealthObligation health = new HealthObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "T", false, 500.0
        );

        String input = """
                
                
                not-a-number
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        health.setSpecificFields(scanner);

        assertEquals(500.0, health.getAnnualLimit(), "Значення не мало змінитись при помилці");
    }

    @Test
    @DisplayName("Геттери та Сеттери працюють коректно")
    void testGettersAndSetters() {
        // Використовуємо повний конструктор, щоб не запускати Scanner logic
        HealthObligation health = new HealthObligation(null, 0,0,0,0,0,0, null, false, 0);

        health.setCoverageType("Dental");
        health.setHasPreExistingConditions(true);
        health.setAnnualLimit(999.99);

        assertEquals("Dental", health.getCoverageType());
        assertTrue(health.isHasPreExistingConditions());
        assertEquals(999.99, health.getAnnualLimit());
    }

    @Test
    @DisplayName("toString містить ключові поля")
    void testToString() {
        HealthObligation health = new HealthObligation(
                "TestH", 0, 0, 0, 0, 0, 0,
                "Surgery", true, 777
        );

        String result = health.toString();

        assertTrue(result.contains("HealthObligation"));
        assertTrue(result.contains("TestH"));
        assertTrue(result.contains("Surgery"));
        assertTrue(result.contains("hasPreExistingConditions=true"));
        assertTrue(result.contains("annualLimit=777.0"));
    }
}