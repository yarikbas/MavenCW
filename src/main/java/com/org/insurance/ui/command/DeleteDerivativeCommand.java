package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.ui.ConsolePrinter;

import java.util.*;

public class DeleteDerivativeCommand implements Command {

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        if (derivatives == null || derivatives.isEmpty()) {
            System.out.println("Немає деривативів для видалення.");
            return;
        }

        System.out.println("Список деривативів:");
        ConsolePrinter.printDerivatives(derivatives);

        System.out.println("Введіть №(и) для видалення: один номер (наприклад, 3),");
        System.out.println("список через кому (1,3,5), діапазони (2-4,7), або 'all'/'*' щоб видалити все.");
        System.out.print("> ");

        String input = in.nextLine().trim().toLowerCase(Locale.ROOT);
        if (input.isEmpty()) {
            System.out.println("Скасовано.");
            return;
        }

        if (input.equals("all") || input.equals("*")) {
            int n = derivatives.size();
            derivatives.clear();
            System.out.println("Видалено всі деривативи: " + n);
            return;
        }

        Set<Integer> toRemove = parseSelection(input, derivatives.size());
        if (toRemove.isEmpty()) {
            System.out.println("Не знайдено коректних номерів для видалення.");
            return;
        }

        List<Integer> sorted = new ArrayList<>(toRemove);
        sorted.sort(Comparator.reverseOrder());

        int removedCount = 0;
        for (int idx1 : sorted) {
            int idx0 = idx1 - 1;
            if (idx0 >= 0 && idx0 < derivatives.size()) {
                Derivative d = derivatives.remove(idx0);
                removedCount++;
                System.out.println("Видалено: " + (d.getName() != null ? d.getName() : d.getId()));
            }
        }
        System.out.println("Разом видалено: " + removedCount);
    }

    @Override
    public String getDescription() {
        return "Видалити дериватив(и) за номером/діапазоном або всі ('all'/'*').";
    }

    private static Set<Integer> parseSelection(String input, int max) {
        Set<Integer> set = new HashSet<>();
        String[] parts = input.split(",");
        for (String raw : parts) {
            String part = raw.trim();
            if (part.isEmpty()) continue;

            int dash = part.indexOf('-');
            if (dash > 0) {
                // діапазон a-b
                try {
                    int a = Integer.parseInt(part.substring(0, dash).trim());
                    int b = Integer.parseInt(part.substring(dash + 1).trim());
                    if (a > b) { int t = a; a = b; b = t; }
                    for (int i = a; i <= b; i++) {
                        if (i >= 1 && i <= max) set.add(i);
                    }
                } catch (NumberFormatException ignored) { }
            } else {
                try {
                    int i = Integer.parseInt(part);
                    if (i >= 1 && i <= max) set.add(i);
                } catch (NumberFormatException ignored) { }
            }
        }
        return set;
    }
}
