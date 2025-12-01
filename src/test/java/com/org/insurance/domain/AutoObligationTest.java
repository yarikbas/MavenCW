package com.org.insurance.domain;

import com.org.insurance.domain.AutoObligation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AutoObligationTest {

    // --- Helper для створення Scanner з рядка ---
    private Scanner createScanner(String input) {
        // Використовуємо .useDelimiter("\\A") для зчитування всього вмісту,
        // якщо Scanner використовується в режимі зчитування токенів.
        // Але для nextLine() це не потрібно, тому просто повертаємо Scanner.
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Конструктор з усіма аргументами має коректно встановлювати поля")
    void testAllArgsConstructor() {
        // Given
        String name = "Test Auto";
        double insuredAmount = 1000.0;
        double factor = 1.2;
        int period = 12;
        double interestRate = 0.05;
        double probability = 0.1;
        double maxCost = 5000.0;
        String vehicleType = "Sedan";
        String driverClass = "A";
        double bonusMalus = 0.9;

        // When
        AutoObligation auto = new AutoObligation(
                name, insuredAmount, factor, period, interestRate, probability, maxCost,
                vehicleType, driverClass, bonusMalus
        );

        // Then (Перевіряємо власні поля класу)
        assertEquals(vehicleType, auto.getVehicleType(), "VehicleType має бути встановлений");
        assertEquals(driverClass, auto.getDriverClass(), "DriverClass має бути встановлений");
        assertEquals(bonusMalus, auto.getBonusMalus(), 0.001, "BonusMalus має бути встановлений"); // 0.001 - дельта для double

        // Перевіряємо, що батьківські поля теж передались (через super)
        assertEquals(name, auto.getName(), "Name має бути встановлений");
        assertEquals(insuredAmount, auto.getInsuredAmount(), 0.001, "InsuredAmount має бути встановлений");
        assertEquals(factor, auto.getFactor(), 0.001, "Factor має бути встановлений");
        assertEquals(period, auto.getPeriod(), "Period має бути встановлений");
        assertEquals(interestRate, auto.getInterestRate(), 0.001, "InterestRate має бути встановлений");
        assertEquals(probability, auto.getProbability(), 0.001, "Probability має бути встановлений");
        assertEquals(maxCost, auto.getMaxCost(), 0.001, "MaxCost має бути встановлений");
    }

    @Test
    @DisplayName("setSpecificFields має коректно зчитувати валідні дані")
    void testSetSpecificFields_ValidInput() {
        // Given
        // Створюємо об'єкт (можна з null, бо ми тестуємо тільки сеттер полів)
        AutoObligation auto = new AutoObligation(null, 0, 0, 0, 0, 0, 0, null, null, 0);

        // Імітуємо ввід користувача: Тип -> Клас -> Бонус
        // Використовуємо багаторядковий рядок для чистоти, без \n
        String input = """
                Truck
                Category C
                1.5
                """;
        Scanner scanner = createScanner(input);

        // When
        auto.setSpecificFields(scanner);

        // Then
        assertEquals("Truck", auto.getVehicleType());
        assertEquals("Category C", auto.getDriverClass());
        assertEquals(1.5, auto.getBonusMalus(), 0.001);
    }

    @Test
    @DisplayName("setSpecificFields не повинен змінювати поля, якщо ввід пустий (натиснуто Enter)")
    void testSetSpecificFields_EmptyInput() {
        // Given
        String initialType = "Old Type";
        String initialClass = "Old Class";
        double initialBonus = 1.0;

        AutoObligation auto = new AutoObligation(null, 0, 0, 0, 0, 0, 0,
                initialType, initialClass, initialBonus);

        // Імітуємо три пусті натискання Enter
        String input = "\n\n\n";
        Scanner scanner = createScanner(input);

        // When
        auto.setSpecificFields(scanner);

        // Then (Значення мають залишитися старими)
        assertEquals(initialType, auto.getVehicleType());
        assertEquals(initialClass, auto.getDriverClass());
        assertEquals(initialBonus, auto.getBonusMalus(), 0.001);
    }

    @Test
    @DisplayName("setSpecificFields має ігнорувати некоректний формат числа (bonusMalus)")
    void testSetSpecificFields_InvalidNumber() {
        // Given
        double initialBonus = 1.0;
        AutoObligation auto = new AutoObligation(null, 0, 0, 0, 0, 0, 0, "T", "C", initialBonus);

        // Імітуємо: Пусто -> Пусто -> "not-a-number"
        String input = """
                
                
                not-a-number
                """;
        Scanner scanner = createScanner(input);

        // When
        auto.setSpecificFields(scanner);

        // Then (BonusMalus не має змінитися і програма не має впасти)
        assertEquals(initialBonus, auto.getBonusMalus(), 0.001);
    }

    @Test
    @DisplayName("Lombok @Setter повинен працювати")
    void testSetters() {
        // Given
        AutoObligation auto = new AutoObligation(null, 0, 0, 0, 0, 0, 0, null, null, 0);

        // When
        auto.setVehicleType("Bike");
        auto.setDriverClass("M");
        auto.setBonusMalus(2.0);

        // Then
        assertEquals("Bike", auto.getVehicleType());
        assertEquals("M", auto.getDriverClass());
        assertEquals(2.0, auto.getBonusMalus(), 0.001);
    }

    @Test
    @DisplayName("toString має містити специфічні поля")
    void testToString() {
        // Given
        AutoObligation auto = new AutoObligation(
                "Name", 100, 1, 1, 0, 0, 0,
                "SuperCar", "S", 0.5
        );

        // When
        String result = auto.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("vehicleType='SuperCar'"));
        assertTrue(result.contains("driverClass='S'"));
        assertTrue(result.contains("bonusMalus=0.5"));
    }
}