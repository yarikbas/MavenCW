package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.ui.command.Command;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    // --- Stub Class (Заглушка) ---
    // Створюємо просту реалізацію інтерфейсу спеціально для тесту.
    // Вона допомагає перевірити, чи передаються аргументи правильно.
    static class TestCommandImplementation implements Command {

        private boolean wasExecuted = false;
        private List<Derivative> receivedList;
        private Scanner receivedScanner;

        @Override
        public void execute(Scanner in, List<Derivative> derivatives) {
            this.wasExecuted = true;
            this.receivedScanner = in;
            this.receivedList = derivatives;
        }

        @Override
        public String getDescription() {
            return "Test Description";
        }

        // Геттери для перевірки стану в тестах
        public boolean isWasExecuted() { return wasExecuted; }
        public List<Derivative> getReceivedList() { return receivedList; }
        public Scanner getReceivedScanner() { return receivedScanner; }
    }
    // -----------------------------

    @Test
    @DisplayName("Реалізація інтерфейсу повинна коректно повертати опис")
    void testGetDescription() {
        Command command = new TestCommandImplementation();

        assertEquals("Test Description", command.getDescription());
    }

    @Test
    @DisplayName("Метод execute повинен приймати Scanner та List")
    void testExecuteContract() {
        // 1. Підготовка (Arrange)
        TestCommandImplementation command = new TestCommandImplementation();
        Scanner scanner = new Scanner("test input");
        List<Derivative> derivatives = new ArrayList<>();
        derivatives.add(new Derivative("Test Portfolio"));

        // 2. Дія (Act)
        command.execute(scanner, derivatives);

        // 3. Перевірка (Assert)
        assertTrue(command.isWasExecuted(), "Метод execute мав бути викликаний");

        // Перевіряємо, що передались саме ті об'єкти, які ми створили (посилання збігаються)
        assertSame(scanner, command.getReceivedScanner());
        assertSame(derivatives, command.getReceivedList());
        assertEquals(1, command.getReceivedList().size());
    }

    @Test
    @DisplayName("Можна створити анонімний клас на основі інтерфейсу")
    void testAnonymousClass() {
        // Це перевіряє, що інтерфейс можна реалізувати "на льоту"
        Command anonCommand = new Command() {
            @Override
            public void execute(Scanner in, List<Derivative> derivatives) {
                // нічого не робимо
            }

            @Override
            public String getDescription() {
                return "Anonymous";
            }
        };

        assertNotNull(anonCommand);
        assertEquals("Anonymous", anonCommand.getDescription());
    }
}