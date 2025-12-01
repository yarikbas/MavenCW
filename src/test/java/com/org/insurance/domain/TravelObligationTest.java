package com.org.insurance.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TravelObligationTest {

    private final InputStream originalSystemIn = System.in;

    @AfterEach
    void restoreSystemIn() {
        // Відновлюємо стандартний потік вводу після кожного тесту
        System.setIn(originalSystemIn);
    }

    @Test
    @DisplayName("Конструктор з параметрами коректно ініціалізує дати та поля")
    void testConstructorWithArguments() {
        LocalDate start = LocalDate.of(2023, 6, 1);
        LocalDate end = LocalDate.of(2023, 6, 15);

        TravelObligation travel = new TravelObligation(
                "Travel Safe", 50000.0, 1.2, 1, 0.0, 0.05, 20000.0,
                "France", start, end
        );

        // Перевірка батьківських полів
        assertNotNull(travel.getId());
        assertEquals("Travel Safe", travel.getName());
        assertEquals(50000.0, travel.getInsuredAmount());

        // Перевірка власних полів
        assertEquals("France", travel.getDestinationCountry());
        assertEquals(start, travel.getTripStartDate());
        assertEquals(end, travel.getTripEndDate());
    }

    @Test
    @DisplayName("Інтерактивний конструктор (System.in) коректно парсить дати")
    void testInteractiveConstructor() {
        // Послідовність вводу:
        // 1. Name -> 2. Amount -> 3. Factor -> 4. Period -> 5. Rate -> 6. Prob -> 7. MaxCost (Parent)
        // 8. Country -> 9. StartDate -> 10. EndDate (Specific)
        String inputData = """
                Europe Trip
                30000
                1.5
                1
                0.0
                0.1
                50000
                Italy
                2024-05-10
                2024-05-20
                """;

        System.setIn(new ByteArrayInputStream(inputData.getBytes()));

        TravelObligation travel = new TravelObligation();

        assertEquals("Europe Trip", travel.getName());
        assertEquals("Italy", travel.getDestinationCountry());
        assertEquals(LocalDate.of(2024, 5, 10), travel.getTripStartDate());
        assertEquals(LocalDate.of(2024, 5, 20), travel.getTripEndDate());
    }

    @Test
    @DisplayName("setSpecificFields оновлює дані при коректному форматі YYYY-MM-DD")
    void testSetSpecificFieldsUpdate() {
        TravelObligation travel = new TravelObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OldCountry", LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 2)
        );

        // Нові дані
        String input = """
                Spain
                2025-01-01
                2025-01-10
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        travel.setSpecificFields(scanner);

        assertEquals("Spain", travel.getDestinationCountry());
        assertEquals(LocalDate.of(2025, 1, 1), travel.getTripStartDate());
        assertEquals(LocalDate.of(2025, 1, 10), travel.getTripEndDate());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує пусті рядки (Enter)")
    void testSetSpecificFieldsSkipEmpty() {
        LocalDate start = LocalDate.of(2022, 1, 1);
        TravelObligation travel = new TravelObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "Japan", start, start.plusDays(5)
        );

        // Три пусті рядки
        String input = """
                
                
                
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        travel.setSpecificFields(scanner);

        assertEquals("Japan", travel.getDestinationCountry());
        assertEquals(start, travel.getTripStartDate());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує некоректний формат дати (Exception swallowed)")
    void testSetSpecificFieldsInvalidDate() {
        LocalDate originalDate = LocalDate.of(2022, 1, 1);
        TravelObligation travel = new TravelObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "Germany", originalDate, originalDate
        );

        // Вводимо "bla-bla" замість дати
        String input = """
                
                not-a-date
                wrong-format
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        travel.setSpecificFields(scanner);

        // Дати повинні залишитись старими, програма не повинна впасти
        assertEquals(originalDate, travel.getTripStartDate());
        assertEquals(originalDate, travel.getTripEndDate());
    }

    @Test
    @DisplayName("toString містить країну та дати")
    void testToString() {
        TravelObligation travel = new TravelObligation(
                "TravelTest", 0, 0, 0, 0, 0, 0,
                "Canada", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)
        );

        String result = travel.toString();

        assertTrue(result.contains("TravelObligation"));
        assertTrue(result.contains("Canada"));
        assertTrue(result.contains("2023-07-01"));
        assertTrue(result.contains("2023-07-10"));
    }

    @Test
    @DisplayName("Сеттери Lombok працюють")
    void testLombokSetters() {
        // Повний конструктор з null, щоб уникнути Scanner
        TravelObligation travel = new TravelObligation(null, 0,0,0,0,0,0, null, null, null);

        travel.setDestinationCountry("Poland");
        travel.setTripStartDate(LocalDate.now());

        assertEquals("Poland", travel.getDestinationCountry());
        assertNotNull(travel.getTripStartDate());
    }
}