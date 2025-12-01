package com.org.insurance.domain;

import com.org.insurance.domain.LifeObligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LifeObligationTest {

    private final InputStream originalSystemIn = System.in;

    @AfterEach
    void restoreSystemIn() {
        // Відновлюємо стандартний потік вводу, щоб не зламати інші тести
        System.setIn(originalSystemIn);
    }

    @Test
    @DisplayName("Конструктор з параметрами коректно ініціалізує всі поля (вкл. LocalDate)")
    void testConstructorWithArguments() {
        LocalDate dob = LocalDate.of(1990, 5, 20);
        LifeObligation life = new LifeObligation(
                "Life Policy", 100000.0, 1.0, 120, 0.03, 0.01, 500000.0,
                "ID-1234", dob, "Maria Smith"
        );

        // Перевірка батьківських полів
        assertNotNull(life.getId());
        assertEquals("Life Policy", life.getName());
        assertEquals(100000.0, life.getInsuredAmount());

        // Перевірка власних полів
        assertEquals("ID-1234", life.getInsuredPersonId());
        assertEquals(dob, life.getDateOfBirth());
        assertEquals("Maria Smith", life.getBeneficiaryName());
    }

    @Test
    @DisplayName("Інтерактивний конструктор (System.in) коректно парсить дату та рядки")
    void testInteractiveConstructor() {
        // Послідовність вводу:
        // 1. Name -> 2. Amount -> 3. Factor -> 4. Period -> 5. Rate -> 6. Prob -> 7. MaxCost (Parent)
        // 8. ID -> 9. DateOfBirth (YYYY-MM-DD) -> 10. Beneficiary (Specific)
        String inputData = """
                Family Protection
                20000
                1.2
                36
                0.05
                0.1
                40000
                PERSON-999
                1985-10-15
                John Doe
                """;

        System.setIn(new ByteArrayInputStream(inputData.getBytes()));

        LifeObligation life = new LifeObligation();

        assertEquals("Family Protection", life.getName());
        assertEquals("PERSON-999", life.getInsuredPersonId());
        assertEquals(LocalDate.of(1985, 10, 15), life.getDateOfBirth());
        assertEquals("John Doe", life.getBeneficiaryName());
    }

    @Test
    @DisplayName("setSpecificFields оновлює дані при коректному вводі")
    void testSetSpecificFieldsUpdate() {
        LifeObligation life = new LifeObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OLD-ID", LocalDate.of(2000, 1, 1), "Old Ben"
        );

        // Нові дані:
        // ID: "NEW-ID"
        // Date: "1999-12-31"
        // Ben: "New Ben"
        String input = """
                NEW-ID
                1999-12-31
                New Ben
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        life.setSpecificFields(scanner);

        assertEquals("NEW-ID", life.getInsuredPersonId());
        assertEquals(LocalDate.of(1999, 12, 31), life.getDateOfBirth());
        assertEquals("New Ben", life.getBeneficiaryName());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує пусті рядки (залишає старі значення)")
    void testSetSpecificFieldsSkipEmpty() {
        LocalDate originalDate = LocalDate.of(2000, 1, 1);
        LifeObligation life = new LifeObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "OriginalID", originalDate, "OriginalBen"
        );

        // Три пусті рядки (Enter, Enter, Enter)
        String input = """
                
                
                
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        life.setSpecificFields(scanner);

        assertEquals("OriginalID", life.getInsuredPersonId());
        assertEquals(originalDate, life.getDateOfBirth());
        assertEquals("OriginalBen", life.getBeneficiaryName());
    }

    @Test
    @DisplayName("setSpecificFields ігнорує некоректний формат дати")
    void testSetSpecificFieldsInvalidDate() {
        LocalDate originalDate = LocalDate.of(2020, 1, 1);
        LifeObligation life = new LifeObligation(
                "Test", 0, 0, 0, 0, 0, 0,
                "ID", originalDate, "Ben"
        );

        // Вводимо "invalid-date" замість YYYY-MM-DD
        String input = """
                
                invalid-date
                
                """;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        life.setSpecificFields(scanner);

        assertEquals(originalDate, life.getDateOfBirth(), "Дата не мала змінитись через помилку парсингу");
    }

    @Test
    @DisplayName("toString містить дату та імена")
    void testToString() {
        LifeObligation life = new LifeObligation(
                "LifeTest", 0, 0, 0, 0, 0, 0,
                "PID-555", LocalDate.of(2023, 5, 5), "Anna"
        );

        String result = life.toString();

        assertTrue(result.contains("LifeObligation"));
        assertTrue(result.contains("PID-555"));
        assertTrue(result.contains("2023-05-05")); // Перевіряємо формат дати у рядку
        assertTrue(result.contains("Anna"));
    }

    @Test
    @DisplayName("Сеттери Lombok працюють")
    void testSetters() {
        // Використовуємо повний конструктор з null, щоб не запускати Scanner
        LifeObligation life = new LifeObligation(null, 0,0,0,0,0,0, null, null, null);

        LocalDate date = LocalDate.now();
        life.setInsuredPersonId("X1");
        life.setDateOfBirth(date);
        life.setBeneficiaryName("Ben");

        assertEquals("X1", life.getInsuredPersonId());
        assertEquals(date, life.getDateOfBirth());
        assertEquals("Ben", life.getBeneficiaryName());
    }
}