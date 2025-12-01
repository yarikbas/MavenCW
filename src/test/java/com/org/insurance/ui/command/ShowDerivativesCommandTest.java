package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ShowDerivativesCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Перехоплюємо вивід консолі
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Відновлюємо стандартний вивід
        System.setOut(originalOut);
    }

    // --- Stub Class ---
    // Нам потрібна реалізація Obligation, щоб додати її в список для повноти тесту
    static class TestShowObligation extends Obligation {
        public TestShowObligation(String name) {
            super(name, 100, 1, 1, 0, 0, 0);
        }
        public TestShowObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // ------------------

    @Test
    @DisplayName("getDescription повертає коректний опис")
    void testGetDescription() {
        ShowDerivativesCommand cmd = new ShowDerivativesCommand();
        assertNotNull(cmd.getDescription());
        assertTrue(cmd.getDescription().contains("Показати список"));
    }

    @Test
    @DisplayName("execute: Виводить 'Немає деривативів', якщо список null")
    void testExecuteNullList() {
        ShowDerivativesCommand cmd = new ShowDerivativesCommand();
        // Передаємо null замість списку
        cmd.execute(new Scanner(""), null);

        assertTrue(outContent.toString().contains("Немає деривативів"));
    }

    @Test
    @DisplayName("execute: Виводить 'Немає деривативів', якщо список пустий")
    void testExecuteEmptyList() {
        ShowDerivativesCommand cmd = new ShowDerivativesCommand();
        // Передаємо пустий список
        cmd.execute(new Scanner(""), new ArrayList<>());

        assertTrue(outContent.toString().contains("Немає деривативів"));
    }

    @Test
    @DisplayName("execute: Виводить список деривативів та їх облігацій")
    void testExecuteWithData() {
        // 1. Створюємо дериватив з облігаціями
        Derivative d1 = new Derivative("Alpha Portfolio");
        List<Obligation> obs = new ArrayList<>();
        obs.add(new TestShowObligation("Test Bond 1"));
        d1.setObligations(obs);

        Derivative d2 = new Derivative("Beta Portfolio");
        // d2 без облігацій

        List<Derivative> list = List.of(d1, d2);

        // 2. Виконуємо команду
        ShowDerivativesCommand cmd = new ShowDerivativesCommand();
        cmd.execute(new Scanner(""), list);

        String output = outContent.toString();

        // 3. Перевіряємо наявність ключових рядків
        assertTrue(output.contains("Список деривативів:"));

        // Перевіряємо, що вивелись назви деривативів
        assertTrue(output.contains("Alpha Portfolio"));
        assertTrue(output.contains("Beta Portfolio"));

        // Перевіряємо, що вивелась назва облігації (завдяки ConsolePrinter)
        assertTrue(output.contains("Test Bond 1"));

        // Перевіряємо нумерацію списку (1), 2))
        assertTrue(output.contains("1)"));
        assertTrue(output.contains("2)"));
    }
}