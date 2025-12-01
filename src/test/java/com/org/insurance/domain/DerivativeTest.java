package com.org.insurance.domain;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class DerivativeTest {

    // --- Helper Class (Заглушка) ---
    // Оскільки Obligation абстрактний, ми створюємо просту реалізацію,
    // щоб мати змогу додати об'єкти в список деривативу.
    static class TestObligationStub extends Obligation {
        public TestObligationStub(String name, double insuredAmount) {
            super(name, insuredAmount, 1.0, 12, 0.05, 0.1, 1000.0);
        }

        @Override
        public void setSpecificFields(Scanner in) {
        }
    }

    @Test
    @DisplayName("Пустий конструктор має генерувати ID, але Name та List мають бути null")
    void testNoArgsConstructor() {
        Derivative derivative = new Derivative();

        assertNotNull(derivative.getId(), "ID повинен генеруватися автоматично");
        assertNull(derivative.getName(), "Name має бути null за замовчуванням");
        assertNull(derivative.getObligations(), "Список зобов'язань має бути null");
    }

    @Test
    @DisplayName("Конструктор з ім'ям має встановлювати ім'я та генерувати ID")
    void testNameConstructor() {
        String expectedName = "My Derivative";
        Derivative derivative = new Derivative(expectedName);

        assertNotNull(derivative.getId());
        assertEquals(expectedName, derivative.getName());
        assertNull(derivative.getObligations());
    }

    @Test
    @DisplayName("Сеттери Lombok (setName, setObligations) повинні працювати")
    void testSettersAndListHandling() {
        Derivative derivative = new Derivative();

        derivative.setName("Updated Name");
        assertEquals("Updated Name", derivative.getName());

        List<Obligation> obligations = new ArrayList<>();
        obligations.add(new TestObligationStub("Obligation 1", 1000.0));
        obligations.add(new TestObligationStub("Obligation 2", 2000.0));

        derivative.setObligations(obligations);

        assertNotNull(derivative.getObligations());
        assertEquals(2, derivative.getObligations().size());
        assertEquals("Obligation 1", derivative.getObligations().get(0).getName());
    }

    @Test
    @DisplayName("ID є фінальним і унікальним для різних об'єктів")
    void testIdIsFinalAndUnique() {
        Derivative d1 = new Derivative("D1");
        Derivative d2 = new Derivative("D2");

        assertNotNull(d1.getId());
        assertNotNull(d2.getId());
        assertNotEquals(d1.getId(), d2.getId(), "ID різних об'єктів мають відрізнятися");

    }
}