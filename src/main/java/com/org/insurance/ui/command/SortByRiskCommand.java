package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import com.org.insurance.domain.RiskComparator;
import com.org.insurance.ui.ConsolePrinter;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SortByRiskCommand implements Command {

    private final RiskComparator comparator = new RiskComparator();

    @Override
    public String getDescription() {
        return "Сортувати облігації у деривативі за зменшенням ризику (RiskComparator як поле)";
    }

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        Derivative d = pickDerivative(in, derivatives);
        if (d == null) return;

        List<Obligation> obs = d.getObligations();
        if (obs == null || obs.isEmpty()) {
            System.out.println("Порожньо.");
            return;
        }

        Collections.sort(obs, comparator.reversed());

        System.out.println("Відсортовано (risk ↓):");
        ConsolePrinter.printObligationsOf(d);
    }

    private Derivative pickDerivative(Scanner in, List<Derivative> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Немає деривативів.");
            return null;
        }
        System.out.println("Оберіть деривативу:");
        ConsolePrinter.printDerivatives(list);
        System.out.print("> №: ");
        try {
            int idx = Integer.parseInt(in.nextLine().trim());
            return (idx >= 1 && idx <= list.size()) ? list.get(idx - 1) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
