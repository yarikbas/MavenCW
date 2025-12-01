package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LoadFromFileCommandTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("execute: неіснуючий файл не змінює список деривативів і не крешить програму")
    void testExecuteFileNotFound() {
        // завідомо неіснуючий файл
        String filename = "definitely-no-such-file-123456789.der";
        String input = filename + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        LoadFromFileCommand cmd = new LoadFromFileCommand();
        List<Derivative> derivatives = new ArrayList<>();
        derivatives.add(new Derivative("Existing"));

        int before = derivatives.size();

        assertDoesNotThrow(() -> cmd.execute(new Scanner(System.in), derivatives),
                "Команда не повинна кидати неконтрольовані винятки при відсутності файлу");

        int after = derivatives.size();
        assertEquals(before, after,
                "Список деривативів не повинен змінюватись у разі помилки завантаження");

        String console = outContent.toString().toLowerCase();
        // Дуже м'яка перевірка, щоб не ламатись від тексту
        assertTrue(console.contains("помилка")
                        || console.contains("error")
                        || console.contains("не вдалося")
                        || console.contains("не вдалось"),
                "У консоль має бути виведено якесь повідомлення про помилку завантаження");
    }
}
