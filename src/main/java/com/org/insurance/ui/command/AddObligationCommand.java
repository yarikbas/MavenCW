package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import com.org.insurance.domain.ObligationType;
import com.org.insurance.ui.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddObligationCommand implements Command {

    @Override
    public String getDescription() {
        return "Додати облігацію: оберіть деривативу, потім тип — і він буде доданий";
    }

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        Derivative d = pickDerivative(in, derivatives);
        if (d == null) return;

        Obligation o = pickAndCreateObligation(in);
        if (o == null) {
            System.out.println("Невірний вибір типу.");
            return;
        }

        if (d.getObligations() == null) {
            try {
                d.getClass().getMethod("setObligations", List.class)
                        .invoke(d, new ArrayList<Obligation>());
            } catch (Exception e) {
                System.out.println("[Увага] Список зобов'язань не ініціалізований у Derivative. Ініціалізуйте його у конструкторі або додайте setObligations(List).");
                return;
            }
        }

        d.getObligations().add(o);
        System.out.println("Додано облігацію типу " + o.getClass().getSimpleName() +
                " до деривативи: " + (d.getName() != null ? d.getName() : d.getId()));
    }

    private Derivative pickDerivative(Scanner in, List<Derivative> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("Немає дериватив для додавання.");
            return null;
        }
        System.out.println("Оберіть деривативу:");
        ConsolePrinter.printDerivatives(list);
        System.out.print("> №: ");
        int idx = readInt(in);
        if (idx < 1 || idx > list.size()) return null;
        return list.get(idx - 1);
    }

    private Obligation pickAndCreateObligation(Scanner in) {
        ObligationType[] menu = ObligationType.visibleValues();
        System.out.println("Оберіть тип облігації для додавання:");
        ObligationType.printAll(menu);
        System.out.print("> №: ");
        int idx = readInt(in);
        return ObligationType.createByIndex(menu, idx);
    }

    private int readInt(Scanner in) {
        try { return Integer.parseInt(in.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }
}
