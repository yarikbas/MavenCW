package com.org.insurance.ui;

import com.org.insurance.domain.Derivative;
import com.org.insurance.ui.command.Command;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceMenuTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    // --- Stub Command (Заглушка) ---
    // Проста команда, яка просто фіксує факт свого виконання
    static class MockCommand implements Command {
        boolean wasExecuted = false;

        @Override
        public void execute(Scanner in, List<Derivative> derivatives) {
            wasExecuted = true;
            System.out.println("Mock executed!");
        }

        @Override
        public String getDescription() {
            return "Test mock command";
        }
    }
    // -------------------------------

    // Встановлюємо перехоплення System.out перед кожним тестом
    // (System.in ми будемо налаштовувати окремо в кожному тесті)
    @org.junit.jupiter.api.BeforeEach
    void setUpOutput() {
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
        // Підготовка вводу: користувач вводить "exit"
        String input = "exit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Створюємо меню ПІСЛЯ того, як підмінили System.in
        InsuranceMenu menu = new InsuranceMenu();

        // Запускаємо
        menu.run();

        String output = outContent.toString();
        assertTrue(output.contains("Завершення роботи..."), "Має вивести повідомлення про вихід");
    }

    @Test
    @DisplayName("executeCommand: Невідома команда виводить помилку, але не крешить програму")
    void testUnknownCommand() {
        // Вводимо сміття, потім exit
        String input = "abrakadabra\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();
        menu.run();

        String output = outContent.toString();
        assertTrue(output.contains("Невідома команда"), "Має повідомити про невідому команду");
    }

    @Test
    @DisplayName("executeCommand: 'help' виводить список команд")
    void testHelpCommand() {
        String input = "help\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();
        menu.run();

        String output = outContent.toString();
        assertTrue(output.contains("ОПИС КОМАНД"), "Має бути заголовок опису");
        assertTrue(output.contains("exit — вихід"), "Має бути опис команди exit");
    }

    @Test
    @DisplayName("registerCommand: Додана команда успішно виконується через run()")
    void testCustomCommandExecution() {
        // Сценарій:
        // 1. Вводимо "testcmd" (наша кастомна команда)
        // 2. Вводимо "exit"
        String input = "testcmd\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();

        // Створюємо нашу заглушку
        MockCommand mock = new MockCommand();
        // Реєструємо її в меню
        menu.registerCommand("testcmd", mock);

        menu.run();

        // Перевіряємо, чи виконалась команда
        assertTrue(mock.wasExecuted, "Метод execute() кастомної команди мав викликатись");
        assertTrue(outContent.toString().contains("Mock executed!"), "Вивід команди має потрапити в консоль");
    }

    @Test
    @DisplayName("Команди мають бути регістронезалежними (UpperCase)")
    void testCaseInsensitive() {
        // Вводимо команду великими літерами "TESTCMD"
        String input = "TESTCMD\nEXIT\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();

        MockCommand mock = new MockCommand();
        // Реєструємо маленькими, а вводимо великими
        menu.registerCommand("testcmd", mock);

        menu.run();

        assertTrue(mock.wasExecuted, "Команда повинна працювати незалежно від регістру вводу");
    }

    @Test
    @DisplayName("Пустий ввід (Enter) ігнорується і цикл продовжується")
    void testEmptyInput() {
        // Enter -> Enter -> exit
        String input = "\n\nexit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InsuranceMenu menu = new InsuranceMenu();
        menu.run();

        String output = outContent.toString();
        // Просто перевіряємо, що вийшли нормально
        assertTrue(output.contains("Завершення роботи..."));
    }

    @Test
    @DisplayName("registerCommand кидає помилку при null аргументах")
    void testRegisterNulls() {
        // Тут не треба setIn, бо ми не запускаємо run()
        InsuranceMenu menu = new InsuranceMenu(); // Створиться зі стандартним System.in (пусте або старе значення)

        assertThrows(NullPointerException.class, () -> menu.registerCommand(null, new MockCommand()));
        assertThrows(NullPointerException.class, () -> menu.registerCommand("test", null));
    }
}