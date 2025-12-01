package com.org.insurance.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class BusinessObligationTest {

    // Зберігаємо оригінальний потік вводу, щоб відновити його після тестів
    private final InputStream originalSystemIn = System.in;

    @AfterEach
    void restoreSystemIn() {
        System.setIn(originalSystemIn);
    }

    @Test
    @DisplayName("Конструктор з усіма параметрами коректно ініціалізує поля")
    void testConstructorWithArguments() {
        BusinessObligation business = new BusinessObligation(
                "IT Factory", 100000.0, 1.1, 24, 0.05, 0.02, 500000.0,
                "REG-12345", "Software Development"
        );

        // Перевірка батьківських полів
        assertNotNull(business.getId());
        assertEquals("IT Factory", business.getName());
        assertEquals(100000.0, business.getInsuredAmount());

        // Перевірка власних полів
        assertEquals("REG-12345", business.getRegistrationNumber());
        assertEquals("Software Development", business.getIndustry());
    }

    @Test
    @DisplayName("Інтерактивний конструктор зчитує дані з консолі (System.in)")
    void testInteractiveConstructor() {
        // Порядок вводу (відповідно до Obligation + BusinessObligation):
        // 1. Name -> 2. Amount -> 3. Factor -> 4. Period -> 5. Rate -> 6. Prob -> 7. MaxCost
        // 8. RegistrationNumber -> 9. Industry
        String simulatedInput = """
                Coffee Shop
                5000
                1.2
                12
                0.08
                0.15
                10000
                UA-987654
                Food Service
                """;

        // Підміняємо консоль на наш рядок
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Створюємо об'єкт (він читає з нашого фейкового вводу)
        BusinessObligation business = new BusinessObligation();

        assertEquals("Coffee Shop", business.getName());
        assertEquals(5000.0, business.getInsuredAmount());
        assertEquals(12, business.getPeriod());
        assertEquals("UA-987654", business.getRegistrationNumber());
        assertEquals("Food Service", business.getIndustry());
    }

    @Test
    @DisplayName("setSpecificFields оновлює значення, але ігнорує пустий ввід")
    void testSetSpecificFieldsLogic() {
        // Початковий стан
        BusinessObligation business = new BusinessObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OLD-REG", "Old Industry"
        );

        // Сценарій вводу:
        // 1. RegistrationNumber: натискаємо Enter (пусто) -> має залишитись OLD-REG
        // 2. Industry: "New Industry" -> має оновитись
        String specificInput = """
                
                New Industry
                """;

        Scanner scanner = new Scanner(new ByteArrayInputStream(specificInput.getBytes()));

        business.setSpecificFields(scanner);

        assertEquals("OLD-REG", business.getRegistrationNumber(), "Мав залишитись старий номер");
        assertEquals("New Industry", business.getIndustry(), "Індустрія мала оновитись");
    }

    @Test
    @DisplayName("toString повертає рядок з потрібними даними")
    void testToString() {
        BusinessObligation business = new BusinessObligation(
                "Factory", 100, 1, 1, 0, 0, 0,
                "123-XYZ", "Manufacturing"
        );

        String result = business.toString();

        assertTrue(result.contains("BusinessObligation"));
        assertTrue(result.contains("Factory"));
        assertTrue(result.contains("123-XYZ"));
        assertTrue(result.contains("Manufacturing"));
    }

    @Test
    @DisplayName("Сеттери Lombok працюють коректно")
    void testLombokSetters() {
        // Використовуємо повний конструктор, щоб уникнути Scanner(System.in)
        BusinessObligation business = new BusinessObligation(null, 0,0,0,0,0,0, null, null);

        business.setRegistrationNumber("NEW-111");
        business.setIndustry("Logistics");

        assertEquals("NEW-111", business.getRegistrationNumber());
        assertEquals("Logistics", business.getIndustry());
    }
}