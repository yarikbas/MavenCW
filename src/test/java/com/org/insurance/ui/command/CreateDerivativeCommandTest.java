package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CreateDerivativeCommandTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private Scanner prepareInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    @DisplayName("getDescription повертає коректний опис")
    void testGetDescription() {
        CreateDerivativeCommand command = new CreateDerivativeCommand();
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("Створити дериватив"));
    }

    @Test
    @DisplayName("execute: Створює дериватив із введеною назвою")
    void testExecuteValidName() {
        // Ввід: "My Invest Portfolio"
        String input = "My Invest Portfolio\n";
        Scanner scanner = prepareInput(input);

        List<Derivative> derivatives = new ArrayList<>();
        CreateDerivativeCommand command = new CreateDerivativeCommand();

        command.execute(scanner, derivatives);

        // Перевірка списку
        assertEquals(1, derivatives.size(), "Список має містити 1 елемент");

        // Перевірка створеного об'єкта
        Derivative created = derivatives.get(0);
        assertEquals("My Invest Portfolio", created.getName());
        assertNotNull(created.getId(), "ID має генеруватися");
        assertNotNull(created.getObligations(), "Список зобов'язань має бути ініціалізований");
        assertTrue(created.getObligations().isEmpty(), "Список зобов'язань має бути пустим");

        // Перевірка виводу в консоль
        String output = outContent.toString();
        assertTrue(output.contains("Створено дериватив"));
        assertTrue(output.contains("My Invest Portfolio"));
    }

    @Test
    @DisplayName("execute: Якщо назва пуста, встановлюється null (згідно логіки коду)")
    void testExecuteEmptyName() {
        // Ввід: просто Enter (пустий рядок)
        String input = "\n";
        Scanner scanner = prepareInput(input);

        List<Derivative> derivatives = new ArrayList<>();
        CreateDerivativeCommand command = new CreateDerivativeCommand();

        command.execute(scanner, derivatives);

        assertEquals(1, derivatives.size());
        Derivative created = derivatives.get(0);

        // Логіка у вашому коді: name.isEmpty() ? null : name
        assertNull(created.getName(), "Пуста назва має перетворитися на null");

        String output = outContent.toString();
        assertTrue(output.contains("(без назви)")); // Перевірка виводу для null-імені
    }

    @Test
    @DisplayName("execute: Пробіли в назві обрізаються (trim)")
    void testExecuteWhitespaceName() {
        // Ввід: "   Test   " -> має стати "Test"
        // Ввід: "     "      -> має стати null (бо trim() зробить "", а empty -> null)

        // Сценарій 1: "   Clean Name   "
        Scanner scanner1 = prepareInput("   Clean Name   \n");
        List<Derivative> derivatives = new ArrayList<>();
        new CreateDerivativeCommand().execute(scanner1, derivatives);

        assertEquals("Clean Name", derivatives.get(0).getName());

        // Сценарій 2: "      " (лише пробіли)
        Scanner scanner2 = prepareInput("      \n");
        new CreateDerivativeCommand().execute(scanner2, derivatives);

        assertNull(derivatives.get(1).getName(), "Лише пробіли мають стати null");
    }
}