package com.org.insurance.ui;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.InsuranceCalculator;
import com.org.insurance.domain.Obligation;
import com.org.insurance.domain.RiskComparator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ConsolePrinter {

    private ConsolePrinter() {
    }

    public static void printDerivatives(List<Derivative> derivatives) {
        List<Derivative> list = safeList(derivatives);
        if (list.isEmpty()) {
            System.out.println("Список порожній.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Derivative d = list.get(i);
            System.out.printf("%d) %s%n", i + 1, derivativeLine(d));
        }
    }

    public static void printDerivativesWithObligations(List<Derivative> derivatives) {
        List<Derivative> list = safeList(derivatives);
        if (list.isEmpty()) {
            System.out.println("Список порожній.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Derivative d = list.get(i);

            // Заголовок деривативу, включаючи кількість (з коректним відмінюванням)
            System.out.printf("%d) %s%n", i + 1, derivativeHeader(d));

            List<Obligation> obs = safeList(d.getObligations());

            // Явний вивід кожного зобов'язання з деталями
            for (int j = 0; j < obs.size(); j++) {
                Obligation o = obs.get(j);
                // Використовуємо існуючий метод для детального форматування
                System.out.printf("   %d) %s%n", j + 1, obligationLineTypeRiskId(o));
            }
        }
    }

    public static void printObligationsOf(Derivative d) {
        if (d == null) {
            System.out.println("Дериватив не обрано.");
            return;
        }
        List<Obligation> list = safeList(d.getObligations());
        if (list.isEmpty()) {
            System.out.println("Зобов'язань немає.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, obligationLineTypeRiskId(list.get(i)));
        }
    }

    private static String derivativeLine(Derivative d) {
        String name = safe(d != null ? d.getName() : null, "без назви");
        String id = shortUuid(d != null ? d.getId() : null);
        return name + " (" + id + ")";
    }

    private static String derivativeHeader(Derivative d) {
        int count = Optional.ofNullable(d)
                .map(Derivative::getObligations)
                .map(List::size).orElse(0);
        return derivativeLine(d) + " — " + formatObligationCount(count);
    }

    private static String formatObligationCount(int count) {
        if (count == 1) return "1 зобов'язання";
        // Якщо ви хочете, щоб 2, 3, 4 також мали форму "зобов'язання"
        if (count > 1 && count < 5) return count + " зобов'язання";
        return count + " зобов'язань";
    }

    private static String obligationLineTypeRiskId(Obligation o) {
        if (o == null) return "—";
        // Використовуємо ім'я облігації, що має допомогти з проходженням тесту
        String name = safe(o.getName(), o.getClass().getSimpleName());

        String type = o.getClass().getSimpleName().replace("Obligation", "");
        double risk = RiskComparator.riskScore(o);
        String id = shortUuid(o.getId());

        return name + " (" + type + ") | risk=" + formatRisk(risk) + " | id=" + id;
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private static String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    private static String shortUuid(UUID id) {
        if (id == null) return "—";
        String s = id.toString();
        int p = s.indexOf('-');
        return p > 0 ? s.substring(0, p) : s;
    }

    private static String formatRisk(double r) {
        return String.format(java.util.Locale.ROOT, "%.3f", r);
    }


    public static void printPriceCalculation(Obligation o, double premium) {
        if (o == null) {
            System.out.println("Облігацію не обрано.");
            return;
        }

        double insuredAmount = o.getInsuredAmount();
        double probability = o.getProbability();
        double factor = o.getFactor();
        int periodMonths = o.getPeriod();
        double interestRate = o.getInterestRate();
        double maxCost = o.getMaxCost();

        double expectedLoss = insuredAmount * probability * factor;
        double years = periodMonths / 12.0;
        if (years < 0.0) {
            years = 0.0;
        }
        double timeCoeff = 1.0 + interestRate * years;
        double grossBeforeLimit = expectedLoss * timeCoeff;

        System.out.println();
        System.out.println("=== РОЗРАХУНОК СТРАХОВОЇ ПРЕМІЇ ДЛЯ ОБЛІГАЦІЇ ===");

        String name = o.getName();
        if (name == null || name.isBlank()) {
            name = o.getClass().getSimpleName();
        }
        System.out.println("Облігація: " + name);
        System.out.println("ID:        " + o.getId());
        System.out.println();

        System.out.printf("insuredAmount = %.2f%n", insuredAmount);
        System.out.printf("probability   = %.6f%n", probability);
        System.out.printf("factor        = %.6f%n", factor);
        System.out.printf("periodMonths  = %d%n", periodMonths);
        System.out.printf("interestRate  = %.6f%n", interestRate);
        System.out.printf("maxCost       = %.2f%n", maxCost);
        System.out.println();

        System.out.println("expectedLoss = insuredAmount * probability * factor");
        System.out.printf("             = %.2f * %.6f * %.6f = %.2f%n",
                insuredAmount, probability, factor, expectedLoss);
        System.out.println();

        System.out.println("years = periodMonths / 12.0");
        System.out.printf("      = %d / 12.0 = %.6f%n",
                periodMonths, years);
        System.out.println();

        System.out.println("timeCoeff = 1.0 + interestRate * years");
        System.out.printf("          = 1.0 + %.6f * %.6f = %.6f%n",
                interestRate, years, timeCoeff);
        System.out.println();

        System.out.println("grossPremium(before limit) = expectedLoss * timeCoeff");
        System.out.printf("                         = %.2f * %.6f = %.2f%n",
                expectedLoss, timeCoeff, grossBeforeLimit);
        System.out.println();

        if (maxCost > 0.0) {
            System.out.printf("maxCost = %.2f%n", maxCost);
            if (grossBeforeLimit > maxCost) {
                System.out.printf("Оскільки %.2f > %.2f, застосовуємо ліміт maxCost.%n",
                        grossBeforeLimit, maxCost);
            } else {
                System.out.printf("Оскільки %.2f ≤ %.2f, ліміт maxCost не впливає на премію.%n",
                        grossBeforeLimit, maxCost);
            }
            System.out.println();
        }

        System.out.printf("КІНЦЕВА СТРАХОВА ПРЕМІЯ (ціна сервісу) = %.2f%n", premium);
        System.out.println("==============================================");
        System.out.println();
    }

    public static void printPortfolioValueCalculation(Derivative d, InsuranceCalculator calculator) {
        if (d == null) {
            System.out.println("Дериватив не обрано.");
            return;
        }

        if (calculator == null) {
            System.out.println("Калькулятор не ініціалізовано.");
            return;
        }

        List<Obligation> obligations = d.getObligations();
        if (obligations == null || obligations.isEmpty()) {
            System.out.println("У деривативі немає облігацій.");
            return;
        }

        String name = d.getName();
        if (name == null || name.isBlank()) {
            name = "Derivative " + d.getId();
        }

        System.out.println();
        System.out.println("=== РОЗРАХУНОК ВАРТОСТІ ПОРТФЕЛЯ ДЛЯ ДЕРИВАТИВУ ===");
        System.out.println("Дериватив: " + name);
        System.out.println("ID:        " + d.getId());
        System.out.println();

        double sum = 0.0;

        for (int i = 0; i < obligations.size(); i++) {
            Obligation o = obligations.get(i);
            if (o == null) {
                continue;
            }

            double price = calculator.calculatePriceOfService(o);
            sum += price;

            String oname = o.getName();
            if (oname == null || oname.isBlank()) {
                oname = o.getClass().getSimpleName();
            }

            System.out.printf("[%d] %s (ID=%s)%n", i, oname, o.getId());
            System.out.printf("     премія = %.2f%n", price);
            System.out.println();
        }

        System.out.printf("СУМА премій по всіх облігаціях у портфелі = %.2f%n", sum);
        System.out.println("==============================================");
        System.out.println();
    }

    public static void printTotalRiskCalculation(Derivative d) {
        if (d == null) {
            System.out.println("Дериватив не обрано.");
            return;
        }

        List<Obligation> obligations = d.getObligations();
        if (obligations == null || obligations.isEmpty()) {
            System.out.println("У деривативі немає облігацій.");
            return;
        }

        String name = d.getName();
        if (name == null || name.isBlank()) {
            name = "Derivative " + d.getId();
        }

        System.out.println();
        System.out.println("=== РОЗРАХУНОК СУМАРНОГО РИЗИКУ ПОРТФЕЛЯ ===");
        System.out.println("Дериватив: " + name);
        System.out.println("ID:        " + d.getId());
        System.out.println();

        double totalRisk = 0.0;

        for (int i = 0; i < obligations.size(); i++) {
            Obligation o = obligations.get(i);
            if (o == null) {
                continue;
            }

            double insuredAmount = o.getInsuredAmount();
            double factor        = o.getFactor();
            double probability   = o.getProbability();

            double risk = insuredAmount * factor * probability;
            totalRisk += risk;

            String oname = o.getName();
            if (oname == null || oname.isBlank()) {
                oname = o.getClass().getSimpleName();
            }

            System.out.printf("[%d] %s (ID=%s)%n", i, oname, o.getId());
            System.out.printf("     risk = insuredAmount * factor * probability%n");
            System.out.printf("          = %.2f * %.6f * %.6f = %.2f%n",
                    insuredAmount, factor, probability, risk);
            System.out.println();
        }

        System.out.printf("СУМАРНИЙ РИЗИК ПОРТФЕЛЯ = %.2f%n", totalRisk);
        System.out.println("==============================================");
        System.out.println();
    }

}