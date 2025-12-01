package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AddObligationCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private Scanner prepareScanner(String data) {
        return new Scanner(new ByteArrayInputStream(data.getBytes()));
    }

    private Derivative createDerivativeWithObligationsList(String name) {
        Derivative d = new Derivative(name);
        d.setObligations(new ArrayList<Obligation>());
        return d;
    }

    private Derivative createDerivativeWithNullObligations(String name) {
        return new Derivative(name); // obligations = null
    }

    @Test
    @DisplayName("getDescription повертає опис додавання облігації")
    void testGetDescription() {
        AddObligationCommand cmd = new AddObligationCommand();
        String desc = cmd.getDescription();

        assertNotNull(desc);
        assertTrue(desc.contains("Додати облігацію"));
        assertTrue(desc.contains("оберіть деривативу"));
    }

    @Test
    @DisplayName("Успішне додавання облігації (список облігацій вже ініціалізований)")
    void testAddObligationSuccessWithExistingList() {
        // 1) Ввід для меню команди (вибір деривативи й типу облігації)
        // ЧИТАЄТЬСЯ ЧЕРЕЗ scanner, НЕ з System.in
        String commandInput = "1\n1\n"; // дериватива №1, тип облігації №1 (AUTO)
        Scanner scanner = prepareScanner(commandInput);

        // 2) Ввід для конструктора AutoObligation (через System.in)
        // Перша лінія — назва, далі багато коректних чисел для всіх askDouble/askInt
        StringBuilder sbObligation = new StringBuilder();
        sbObligation.append("Test Obligation\n"); // name

        // insuredAmount, factor, period, interestRate, probability, maxCost, плюс запас
        for (int i = 0; i < 50; i++) {
            sbObligation.append("1000\n");
        }

        System.setIn(new ByteArrayInputStream(sbObligation.toString().getBytes()));

        Derivative d = createDerivativeWithObligationsList("Portfolio A");
        List<Derivative> list = List.of(d);

        AddObligationCommand cmd = new AddObligationCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        assertNotNull(d.getObligations(), "Список облігацій не повинен бути null");
        assertEquals(1, d.getObligations().size(),
                "Після успішного виконання повинна бути додана рівно одна облігація");

        assertTrue(output.contains("Оберіть деривативу"),
                "Повинно бути меню вибору деривативи");
        assertTrue(output.contains("Оберіть тип облігації для додавання"),
                "Повинно бути меню вибору типу облігації");
        assertTrue(output.contains("Додано облігацію типу"),
                "Повинно бути повідомлення про успішне додавання облігації");
        assertTrue(output.contains("до деривативи: Portfolio A"),
                "У фінальному повідомленні має бути вказана назва деривативи");
    }

    @Test
    @DisplayName("При null-списку облігацій Derivative ініціалізується через рефлексію і додається облігація")
    void testAddObligationInitializesNullList() {
        String commandInput = "1\n1\n"; // дериватива №1, тип облігації №1
        Scanner scanner = prepareScanner(commandInput);

        StringBuilder sbObligation = new StringBuilder();
        sbObligation.append("Test Obligation B\n"); // name
        for (int i = 0; i < 50; i++) {
            sbObligation.append("500\n");
        }
        System.setIn(new ByteArrayInputStream(sbObligation.toString().getBytes()));

        Derivative d = createDerivativeWithNullObligations("Portfolio B");
        assertNull(d.getObligations(), "Перед виконанням список облігацій має бути null");

        List<Derivative> list = List.of(d);

        AddObligationCommand cmd = new AddObligationCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        assertNotNull(d.getObligations(),
                "Після виконання список облігацій повинен бути ініціалізований");
        assertEquals(1, d.getObligations().size(),
                "В новоініціалізований список має бути додано одну облігацію");

        assertTrue(output.contains("Додано облігацію типу"),
                "Повинно бути повідомлення про успішне додавання облігації");
        assertTrue(output.contains("до деривативи: Portfolio B"),
                "Повинна згадуватись правильна назва деривативи");
    }

    @Test
    @DisplayName("Порожній список деривативів: виводиться попередження і нічого не відбувається")
    void testEmptyDerivativesList() {
        String input = "1\n";
        Scanner scanner = prepareScanner(input);

        AddObligationCommand cmd = new AddObligationCommand();
        cmd.execute(scanner, Collections.emptyList());

        String output = outContent.toString();
        assertTrue(output.contains("Немає дериватив для додавання."),
                "Користувач має побачити повідомлення, що дериватив немає");
    }

    @Test
    @DisplayName("Невірний номер деривативи: облігація не додається")
    void testInvalidDerivativeIndex() {
        String input = "5\n1\n"; // 5 — неіснуюча дериватива
        Scanner scanner = prepareScanner(input);

        Derivative d = createDerivativeWithObligationsList("Portfolio A");
        List<Derivative> list = List.of(d);

        AddObligationCommand cmd = new AddObligationCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        assertNotNull(d.getObligations());
        assertTrue(d.getObligations().isEmpty(),
                "При невірному номері деривативи облігації не повинні додаватися");
        assertFalse(output.contains("Додано облігацію типу"),
                "Не повинно бути повідомлення про додавання при невірному номері деривативи");
    }

    @Test
    @DisplayName("Невірний вибір типу облігації: виводиться повідомлення і нічого не додається")
    void testInvalidObligationTypeIndex() {
        // 1 — дериватива №1, 999 — тип облігації поза межами visibleValues()
        String input = "1\n999\n";
        Scanner scanner = prepareScanner(input);

        Derivative d = createDerivativeWithObligationsList("Portfolio A");
        List<Derivative> list = List.of(d);

        AddObligationCommand cmd = new AddObligationCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        assertNotNull(d.getObligations());
        assertTrue(d.getObligations().isEmpty(),
                "При невірному виборі типу облігації список облігацій не змінюється");
        assertTrue(output.contains("Невірний вибір типу."),
                "Повинно бути надруковано повідомлення про невірний вибір типу");
    }

    @Test
    @DisplayName("Некоректний ввід номера деривативи (не число) обробляється як помилка")
    void testInvalidNumberFormatForDerivativeIndex() {
        String input = "abc\n1\n"; // readInt поверне -1
        Scanner scanner = prepareScanner(input);

        Derivative d = createDerivativeWithObligationsList("Portfolio A");
        List<Derivative> list = List.of(d);

        AddObligationCommand cmd = new AddObligationCommand();
        cmd.execute(scanner, list);

        String output = outContent.toString();

        assertNotNull(d.getObligations());
        assertTrue(d.getObligations().isEmpty(),
                "При некоректному форматі вводу індексу деривативи не повинно нічого додаватися");
        assertTrue(output.contains("Оберіть деривативу"),
                "Користувач має побачити запит на вибір деривативи");
        assertFalse(output.contains("Додано облігацію типу"),
                "Не повинно бути повідомлення про успішне додавання");
    }
}
