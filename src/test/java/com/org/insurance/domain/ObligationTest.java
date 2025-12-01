package com.org.insurance.domain;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ObligationTest {

    /**
     * Тестова конкретна реалізація абстрактного Obligation.
     */
    private static class TestObligation extends Obligation {

        private String specificField;

        public TestObligation(String name,
                              double insuredAmount,
                              double factor,
                              int period,
                              double interestRate,
                              double probability,
                              double maxCost) {
            super(name, insuredAmount, factor, period, interestRate, probability, maxCost);
        }

        public TestObligation(Scanner in) {
            super(in);
        }

        @Override
        public void setSpecificFields(Scanner in) {
            if (in.hasNextLine()) {
                specificField = in.nextLine();
            }
        }

        public String getSpecificField() {
            return specificField;
        }
    }

    @Test
    void constructorAssignsFieldsCorrectly() {
        String name = "Test obligation";
        double insuredAmount = 1000.0;
        double factor = 2.0;
        int period = 12;
        double interestRate = 0.07;
        double probability = 0.5;
        double maxCost = 5000.0;

        TestObligation obligation = new TestObligation(
                name,
                insuredAmount,
                factor,
                period,
                interestRate,
                probability,
                maxCost
        );

        assertNotNull(obligation.getId(), "id має створюватися автоматично");
        assertEquals(name, obligation.getName());
        assertEquals(insuredAmount, obligation.getInsuredAmount());
        assertEquals(factor, obligation.getFactor());
        assertEquals(period, obligation.getPeriod());
        assertEquals(interestRate, obligation.getInterestRate());
        assertEquals(probability, obligation.getProbability());
        assertEquals(maxCost, obligation.getMaxCost());
    }

    @Test
    void settersUpdateFields() {
        TestObligation obligation = new TestObligation(
                "old",
                1.0,
                1.0,
                1,
                0.01,
                0.1,
                10.0
        );

        obligation.setName("new");
        obligation.setInsuredAmount(2000.0);
        obligation.setFactor(3.0);
        obligation.setPeriod(24);
        obligation.setInterestRate(0.15);
        obligation.setProbability(0.8);
        obligation.setMaxCost(9000.0);

        assertEquals("new", obligation.getName());
        assertEquals(2000.0, obligation.getInsuredAmount());
        assertEquals(3.0, obligation.getFactor());
        assertEquals(24, obligation.getPeriod());
        assertEquals(0.15, obligation.getInterestRate());
        assertEquals(0.8, obligation.getProbability());
        assertEquals(9000.0, obligation.getMaxCost());
    }

    @Test
    void scannerConstructorReadsValidInput() {
        String input =
                "My obligation\n" + // name
                        "1000\n" +          // insuredAmount
                        "1.5\n" +           // factor
                        "12\n" +            // period
                        "0.07\n" +          // interestRate
                        "0.4\n" +           // probability
                        "10000\n";          // maxCost

        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
        );

        TestObligation obligation = new TestObligation(scanner);

        assertEquals("My obligation", obligation.getName());
        assertEquals(1000.0, obligation.getInsuredAmount());
        assertEquals(1.5, obligation.getFactor());
        assertEquals(12, obligation.getPeriod());
        assertEquals(0.07, obligation.getInterestRate());
        assertEquals(0.4, obligation.getProbability());
        assertEquals(10000.0, obligation.getMaxCost());
    }

    @Test
    void scannerConstructorRetriesOnInvalidNumericInput() {
        String input =
                "Name\n" +     // name
                        "abc\n" +      // insuredAmount (Невірно)
                        "1000\n" +     // insuredAmount (Вірно)
                        "2.5\n" +      // factor
                        "xyz\n" +      // period (Невірно)
                        "18\n" +       // period (Вірно)
                        "0.05\n" +     // interestRate
                        "0.3\n" +      // probability
                        "5000\n";      // maxCost

        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
        );

        TestObligation obligation = new TestObligation(scanner);

        assertEquals("Name", obligation.getName());
        assertEquals(1000.0, obligation.getInsuredAmount());
        assertEquals(2.5, obligation.getFactor());
        assertEquals(18, obligation.getPeriod());
        assertEquals(0.05, obligation.getInterestRate());
        assertEquals(0.3, obligation.getProbability());
        assertEquals(5000.0, obligation.getMaxCost());
    }

    @Test
    void setSpecificFieldsReadsAdditionalLine() {
        String input =
                "Name\n" +      // name
                        "1000\n" +      // insuredAmount
                        "1.0\n" +       // factor
                        "12\n" +        // period
                        "0.05\n" +      // interestRate
                        "0.3\n" +       // probability
                        "5000\n" +      // maxCost
                        "ExtraField\n"; // додаткове поле для setSpecificFields

        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
        );

        TestObligation obligation = new TestObligation(scanner);
        // Після конструктора в Scanner ще є рядок "ExtraField"
        obligation.setSpecificFields(scanner);

        assertEquals("ExtraField", obligation.getSpecificField());
    }

    @Test
    void isSerializableAndDeserializable() throws Exception {
        TestObligation original = new TestObligation(
                "Name",
                1000.0,
                1.0,
                12,
                0.05,
                0.3,
                5000.0
        );

        // серіалізація в масив байтів
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(original);
            oos.flush();
            bytes = bos.toByteArray();
        }

        TestObligation copy;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            copy = (TestObligation) ois.readObject();
        }

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getInsuredAmount(), copy.getInsuredAmount());
        assertEquals(original.getFactor(), copy.getFactor());
        assertEquals(original.getPeriod(), copy.getPeriod());
        assertEquals(original.getInterestRate(), copy.getInterestRate());
        assertEquals(original.getProbability(), copy.getProbability());
        assertEquals(original.getMaxCost(), copy.getMaxCost());
        assertEquals(original.getId(), copy.getId());
    }
}
