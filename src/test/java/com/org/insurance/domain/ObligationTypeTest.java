package com.org.insurance.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ObligationTypeTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        // Перехоплюємо System.out, щоб протестувати метод printAll
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Відновлюємо стандартні потоки після кожного тесту
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Допоміжний метод, який готує "фейковий" ввід для Scanner-ів.
     * Ми передаємо коректні числа, щоб цикли while(true) у класі Obligation не зациклились.
     */
    private void mockSystemIn() {
        String safeInput = """
                TestObligation
                1000.0
                1.0
                12
                0.05
                0.1
                5000.0
                SpecificValue1
                SpecificValue2
                0.5
                2020-01-01
                SpecificValue3
                """;
        System.setIn(new ByteArrayInputStream(safeInput.getBytes()));
    }

    @Test
    @DisplayName("Кожен тип ENUM має створювати правильний клас об'єкта")
    void testCreateMethodFactory() {
        for (ObligationType type : ObligationType.values()) {
            // Підготовка вводу перед створенням, бо конструктор почне читати дані
            mockSystemIn();

            Obligation obligation = type.create();
            assertNotNull(obligation, "Obligation не має бути null для " + type);

            // Перевіряємо відповідність класів
            switch (type) {
                case AUTO -> assertTrue(obligation instanceof AutoObligation);
                case BUSINESS -> assertTrue(obligation instanceof BusinessObligation);
                case HEALTH -> assertTrue(obligation instanceof HealthObligation);
                case LIABILITY -> assertTrue(obligation instanceof LiabilityObligation);
                case LIFE -> assertTrue(obligation instanceof LifeObligation);
                case PROPERTY -> assertTrue(obligation instanceof PropertyObligation);
                case TRAVEL -> assertTrue(obligation instanceof TravelObligation);
            }
        }
    }

    @Test
    @DisplayName("Properties: displayName та visible мають правильні значення")
    void testEnumProperties() {
        // Перевіримо один для прикладу
        ObligationType auto = ObligationType.AUTO;
        assertEquals("auto", auto.getDisplayName());
        assertTrue(auto.isVisible());
    }

    @Test
    @DisplayName("visibleValues() повертає тільки видимі елементи")
    void testVisibleValues() {
        // У поточному коді всі visible = true
        ObligationType[] visible = ObligationType.visibleValues();
        assertEquals(ObligationType.values().length, visible.length);

        // Перевіримо, що список не пустий
        assertTrue(visible.length > 0);
    }

    @Test
    @DisplayName("printAll() виводить нумерований список у консоль")
    void testPrintAll() {
        ObligationType[] menu = {ObligationType.AUTO, ObligationType.LIFE};

        ObligationType.printAll(menu);

        // Отримуємо те, що надрукувалось у консоль
        String output = outContent.toString();

        // Очікуємо:
        // 1) auto
        // 2) life
        assertTrue(output.contains("1) auto"));
        assertTrue(output.contains("2) life"));
    }

    @Test
    @DisplayName("createByIndex() створює об'єкт за номером меню (1-based index)")
    void testCreateByIndexValid() {
        mockSystemIn();
        ObligationType[] menu = ObligationType.values();

        // Спробуємо створити перший елемент (індекс 1 -> масив[0] -> AUTO)
        Obligation result = ObligationType.createByIndex(menu, 1);

        assertNotNull(result);
        assertTrue(result instanceof AutoObligation);
    }

    @Test
    @DisplayName("createByIndex() повертає null для некоректних індексів")
    void testCreateByIndexInvalid() {
        ObligationType[] menu = ObligationType.values();

        assertNull(ObligationType.createByIndex(menu, 0));              // Менше 1
        assertNull(ObligationType.createByIndex(menu, -5));             // Від'ємний
        assertNull(ObligationType.createByIndex(menu, menu.length + 1)); // Більше довжини
    }
}
