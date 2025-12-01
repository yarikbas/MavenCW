package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import com.org.insurance.domain.InsuranceCalculator;
import com.org.insurance.ui.ConsolePrinter;

import java.util.List;
import java.util.Scanner;

public class CalculateCommand implements Command {

    private final InsuranceCalculator calculator = new InsuranceCalculator();

    @Override
    public String getDescription() {
        return String.join("\n",
                "Доступні розрахунки InsuranceCalculator:",
                "  1) Вартість портфеля",
                "  2) Сумарний ризик портфеля",
                "  3) Ціна сервісу для облігації"
        );
    }

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        System.out.println("Оберіть дію:");
        System.out.println("1) Вартість портфеля");
        System.out.println("2) Сумарний ризик портфеля");
        System.out.println("3) Ціна сервісу для облігації");
        System.out.print("> ");
        int action = readInt(in);

        switch (action) {
            case 1 -> {
                Derivative d = chooseDerivative(in, derivatives);
                if (d == null) return;

                double value = calculator.calculatePortfolioValue(d);

                System.out.printf("Вартість портфеля для '%s': %.6f%n",
                        d.getName() != null ? d.getName() : d.getId(), value);

                ConsolePrinter.printPortfolioValueCalculation(d, calculator);
            }
            case 2 -> {
                Derivative d = chooseDerivative(in, derivatives);
                if (d == null) return;

                double risk = calculator.calculateTotalRisk(d);
                System.out.printf("Сумарний ризик портфеля '%s': %.6f%n",
                        d.getName() != null ? d.getName() : d.getId(), risk);

                ConsolePrinter.printTotalRiskCalculation(d);
            }
            case 3 -> {
                Derivative d = chooseDerivative(in, derivatives);
                if (d == null) return;
                Obligation o = chooseObligation(in, d);
                if (o == null) return;

                double price = calculator.calculatePriceOfService(o);

                System.out.printf("Ціна сервісу для облігації '%s': %.6f%n",
                        o.getName() != null ? o.getName() : o.getId(), price);

                ConsolePrinter.printPriceCalculation(o, price);
            }
            default -> System.out.println("Невірний вибір.");

        }
    }

    private Derivative chooseDerivative(Scanner in, List<Derivative> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Список деривативів порожній.");
            return null;
        }
        System.out.println("Оберіть деривативу:");
        ConsolePrinter.printDerivatives(list);
        System.out.print("Оберіть № деривативу: ");
        int idx = readInt(in);
        if (idx < 1 || idx > list.size()) {
            System.out.println("Невірний номер.");
            return null;
        }
        return list.get(idx - 1);
    }

    private Obligation chooseObligation(Scanner in, Derivative derivative) {
        var obs = derivative.getObligations();
        if (obs == null || obs.isEmpty()) {
            System.out.println("У деривативі немає облігацій.");
            return null;
        }
        System.out.println("Оберіть облігацію:");
        ConsolePrinter.printObligationsOf(derivative);
        System.out.print("Оберіть № облігації: ");
        int idx = readInt(in);
        if (idx < 1 || idx > obs.size()) {
            System.out.println("Невірний номер.");
            return null;
        }
        return obs.get(idx - 1);
    }

    private int readInt(Scanner in) {
        try {
            return Integer.parseInt(in.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
