package com.org.insurance.ui;

import com.org.insurance.ui.command.Command;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InsuranceMenuTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUpOutput() {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("run() має завершуватись при команді 'exit'")
    void testExitCommand() {
        String input = "exit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();
        menu.run();

        String output = outContent.toString();
        assertTrue(output.contains("Завершення роботи..."),
                "Має вивести повідомлення про вихід");
    }

    @Test
    @DisplayName("executeCommand: Невідома команда виводить помилку, але не крешить програму")
    void testUnknownCommand() {
        String input = "abrakadabra\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();
        menu.run();

        String output = outContent.toString();
        assertTrue(output.contains("Невідома команда"),
                "Має повідомити про невідому команду");
    }

    @Test
    @DisplayName("executeCommand: 'help' виводить список команд")
    void testHelpCommand() {
        String input = "help\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();
        menu.run();

        String output = outContent.toString();

        // Заголовок опису команд
        assertTrue(output.contains("ОПИС КОМАНД"),
                "Має бути заголовок 'ОПИС КОМАНД'");

        // Рядок із додатковими командами, які не в мапі (help/exit)
        assertTrue(output.contains("Доступні також: help, exit"),
                "Має бути рядок 'Доступні також: help, exit'");
    }

    @Test
    @DisplayName("registerCommand: Додана команда успішно виконується через run() (Mockito)")
    void testCustomCommandExecution() {
        // користувач вводить нашу кастомну команду, потім exit
        String input = "testcmd\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();

        // Mockito-мок замість ручної MockCommand
        Command mockCommand = mock(Command.class);
        menu.registerCommand("testcmd", mockCommand);

        menu.run();

        // перевіряємо, що execute() реально викликався рівно один раз
        verify(mockCommand, times(1))
                .execute(any(Scanner.class), anyList());
    }

    @Test
    @DisplayName("Команди мають бути регістронезалежними (UpperCase) (Mockito)")
    void testCaseInsensitive() {
        // Вводимо команду великими літерами, exit теж великими
        String input = "TESTCMD\nEXIT\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();

        // реєструємо команду в нижньому регістрі
        Command mockCommand = mock(Command.class);
        menu.registerCommand("testcmd", mockCommand);

        menu.run();

        // якщо все окей з Locale.ROOT та toLowerCase,
        // ця команда повинна бути викликана
        verify(mockCommand, times(1))
                .execute(any(Scanner.class), anyList());
    }

    @Test
    @DisplayName("Пустий ввід (Enter) ігнорується і цикл продовжується")
    void testEmptyInput() {
        String input = "\n\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();
        menu.run();

        String output = outContent.toString();
        assertTrue(output.contains("Завершення роботи..."),
                "Програма має коректно завершити роботу після 'exit'");
    }

    @Test
    @DisplayName("registerCommand кидає помилку при null аргументах")
    void testRegisterNulls() {
        InsuranceMenu menu = new InsuranceMenu();

        assertThrows(NullPointerException.class,
                () -> menu.registerCommand(null, mock(Command.class)));
        assertThrows(NullPointerException.class,
                () -> menu.registerCommand("test", null));
    }
}
