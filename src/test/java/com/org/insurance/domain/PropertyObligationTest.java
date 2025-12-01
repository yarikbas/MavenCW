package com.org.insurance.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class PropertyObligationTest {

    private final InputStream originalSystemIn = System.in;

    @AfterEach
    void restoreSystemIn() {
        // Відновлюємо стандартний ввід після кожного тесту
        System.setIn(originalSystemIn);
    }

    @Test
    @DisplayName("Конструктор з параметрами повинен коректно встановлювати всі поля")
    void testConstructorWithArguments() {
        PropertyObligation property = new PropertyObligation(
                "House Insurance", 200000.0, 1.0, 12, 0.04, 0.05, 1000000.0,
                "Baker Street 221B", "Apartment"
        );

        // Перевірка полів батьківського класу
        assertNotNull(property.getId());
        assertEquals("House Insurance", property.getName());
        assertEquals(200000.0, property.getInsuredAmount());

        // Перевірка власних полів
        assertEquals("Baker Street 221B", property.getPropertyAddress());
        assertEquals("Apartment", property.getPropertyType());
    }

    @Test
    @DisplayName("Інтерактивний конструктор (System.in) зчитує дані правильно")
    void testInteractiveConstructor() {
        // Послідовність вводу:
        // 1-7. Батьківські поля (Name, Amount, Factor, Period, Rate, Prob, MaxCost)
        // 8. Address
        // 9. Type
        String simulatedInput = """
                Villa Insurance
                500000
                1.2
                24
                0.05
                0.1
                1000000
                Miami Beach, FL
                Luxury Villa
                """;

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PropertyObligation property = new PropertyObligation();

        assertEquals("Villa Insurance", property.getName());
        assertEquals(500000.0, property.getInsuredAmount());
        assertEquals("Miami Beach, FL", property.getPropertyAddress());
        assertEquals("Luxury Villa", property.getPropertyType());
    }

    @Test
    @DisplayName("setSpecificFields оновлює дані при наявності вводу")
    void testSetSpecificFieldsUpdate() {
        PropertyObligation property = new PropertyObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "Old Address", "Old Type"
        );

        String input = """
                New Address 123
                Office Building
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        property.setSpecificFields(scanner);

        assertEquals("New Address 123", property.getPropertyAddress());
        assertEquals("Office Building", property.getPropertyType());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує пусті рядки (залишає старі значення)")
    void testSetSpecificFieldsSkipEmpty() {
        PropertyObligation property = new PropertyObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "Original Address", "Original Type"
        );

        // Два пусті рядки (Enter, Enter)
        String input = """
                
                
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        property.setSpecificFields(scanner);

        assertEquals("Original Address", property.getPropertyAddress());
        assertEquals("Original Type", property.getPropertyType());
    }

    @Test
    @DisplayName("toString містить адресу та тип")
    void testToString() {
        PropertyObligation property = new PropertyObligation(
                "PropTest", 0, 0, 0, 0, 0, 0,
                "Khreshchatyk 1", "Commercial"
        );

        String result = property.toString();

        assertTrue(result.contains("PropertyObligation"));
        assertTrue(result.contains("PropTest"));
        assertTrue(result.contains("Khreshchatyk 1"));
        assertTrue(result.contains("Commercial"));
    }

    @Test
    @DisplayName("Lombok @Setter працює коректно")
    void testLombokSetters() {
        // Використовуємо повний конструктор з null/0, щоб не запускати логіку Scanner
        PropertyObligation property = new PropertyObligation(null, 0,0,0,0,0,0, null, null);

        property.setPropertyAddress("Lviv, Rynok Sq");
        property.setPropertyType("Historical");

        assertEquals("Lviv, Rynok Sq", property.getPropertyAddress());
        assertEquals("Historical", property.getPropertyType());
    }
}