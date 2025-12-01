package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RemoveObligationCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // --- Stub Class ---
    static class TestRemoveObligation extends Obligation {
        public TestRemoveObligation(String name) {
            super(name, 100, 1, 1, 0, 0, 0);
        }
        public TestRemoveObligation(Scanner in) { super(in); }
        @Override public void setSpecificFields(Scanner in) {}
    }
    // ------------------

    private Scanner prepareInput(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    private Derivative createDerivative(int count) {
        Derivative d = new Derivative("Der" + count);
        List<Obligation> obs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            obs.add(new TestRemoveObligation("Item " + (i + 1)));
        }
        d.setObligations(obs);
        return d;
    }

    @Test
    @DisplayName("getDescription повертає опис")
    void testGetDescription() {
        RemoveObligationCommand cmd = new RemoveObligationCommand();
        assertTrue(cmd.getDescription().contains("Видалити"));
    }

    @Test
    @DisplayName("Видалення одного елемента за індексом")
    void testRemoveSingleIndex() {
        Derivative d = createDerivative(3); // Item 1, Item 2, Item 3
        List<Derivative> list = List.of(d);

        // Ввід: "1" (вибір деривативу) -> "2" (видалити 2-й елемент) -> "y" (підтвердити)
        String input = "1\n2\ny\n";
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        assertEquals(2, d.getObligations().size());
        assertEquals("Item 1", d.getObligations().get(0).getName());
        assertEquals("Item 3", d.getObligations().get(1).getName()); // Item 2 зник

        assertTrue(outContent.toString().contains("Видалено (index)"));
    }

    @Test
    @DisplayName("Видалення діапазону (Range) 2-4")
    void testRemoveRange() {
        Derivative d = createDerivative(5); // 1, 2, 3, 4, 5
        List<Derivative> list = List.of(d);

        // Ввід: "1" (Der) -> "2-4" (видалити 2, 3, 4) -> "y"
        String input = "1\n2-4\ny\n";
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        assertEquals(2, d.getObligations().size());
        assertEquals("Item 1", d.getObligations().get(0).getName());
        assertEquals("Item 5", d.getObligations().get(1).getName());
    }

    @Test
    @DisplayName("Видалення за UUID")
    void testRemoveByUuid() {
        Derivative d = createDerivative(2);
        Obligation target = d.getObligations().get(0); // Item 1
        UUID targetUuid = target.getId();

        List<Derivative> list = List.of(d);

        // Ввід: "1" (Der) -> UUID -> "y"
        String input = "1\n" + targetUuid.toString() + "\ny\n";
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        assertEquals(1, d.getObligations().size());
        assertEquals("Item 2", d.getObligations().get(0).getName()); // Item 1 зник
        assertTrue(outContent.toString().contains("Видалено (uuid)"));
    }

    @Test
    @DisplayName("Скасування видалення (відповідь 'n')")
    void testCancelDeletion() {
        Derivative d = createDerivative(1);
        List<Derivative> list = List.of(d);

        // Ввід: "1" -> "1" -> "n" (скасувати)
        String input = "1\n1\nn\n";
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        assertEquals(1, d.getObligations().size(), "Елемент не мав бути видалений");
        assertTrue(outContent.toString().contains("Скасовано"));
    }

    @Test
    @DisplayName("Комбінований ввід: індекси, коми, діапазони")
    void testComplexInput() {
        Derivative d = createDerivative(6); // 1, 2, 3, 4, 5, 6
        List<Derivative> list = List.of(d);

        // Видалити: 1, 3-4, 6
        // Залишиться: 2, 5
        String input = "1\n1, 3-4, 6\ny\n";
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        assertEquals(2, d.getObligations().size());
        assertEquals("Item 2", d.getObligations().get(0).getName());
        assertEquals("Item 5", d.getObligations().get(1).getName());
    }

    @Test
    @DisplayName("Ігнорування некоректних індексів та сміття")
    void testIgnoreInvalidInput() {
        Derivative d = createDerivative(3);
        List<Derivative> list = List.of(d);

        // Ввід: "99" (не існує), "abc" (сміття), "0" (не існує), "2" (валідне)
        String input = "1\n99, abc, 0, 2\ny\n";
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        assertEquals(2, d.getObligations().size());
        assertFalse(d.getObligations().stream().anyMatch(o -> o.getName().equals("Item 2"))); // Тільки 2 видалився
    }

    @Test
    @DisplayName("Якщо валідних цілей не знайдено, виводиться повідомлення")
    void testNoValidTargets() {
        Derivative d = createDerivative(3);
        List<Derivative> list = List.of(d);

        // Вводимо тільки сміття
        String input = "1\nabc, 5-10\n"; // 5-10 теж out of bounds
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        assertEquals(3, d.getObligations().size());
        assertTrue(outContent.toString().contains("Не знайдено коректних позицій"));
    }

    @Test
    @DisplayName("Діапазон у зворотному порядку (4-2) обробляється як (2-4)")
    void testReverseRange() {
        Derivative d = createDerivative(5);
        List<Derivative> list = List.of(d);

        // Вводимо "4-2"
        String input = "1\n4-2\ny\n";
        Scanner scanner = prepareInput(input);

        new RemoveObligationCommand().execute(scanner, list);

        // Мають видалитися 2, 3, 4
        assertEquals(2, d.getObligations().size());
        assertEquals("Item 1", d.getObligations().get(0).getName());
        assertEquals("Item 5", d.getObligations().get(1).getName());
    }

    @Test
    @DisplayName("Обробка пустих списків")
    void testEmptyLists() {
        RemoveObligationCommand cmd = new RemoveObligationCommand();

        // 1. Немає деривативів
        Scanner s1 = prepareInput("1\n");
        cmd.execute(s1, Collections.emptyList());
        assertTrue(outContent.toString().contains("Немає деривативів"));
        outContent.reset();

        // 2. Дериватив без облігацій
        Derivative d = new Derivative("Empty");
        d.setObligations(new ArrayList<>());
        Scanner s2 = prepareInput("1\n");
        cmd.execute(s2, List.of(d));
        assertTrue(outContent.toString().contains("У деривативі немає облігацій"));
    }
}