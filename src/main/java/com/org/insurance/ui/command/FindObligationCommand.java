package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FindObligationCommand implements Command {
    @Override public String getDescription() { return "Пошук зобов’язань у деривативі (діапазони числових полів + підрядок у назві)"; }

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        Derivative d = pickDerivative(in, derivatives);
        if (d == null) return;
        var obs = d.getObligations();
        if (obs.isEmpty()) {
            System.out.println("У деривативі немає облігацій.");
            return;
        }

        System.out.println("Введи фільтри (порожнє — пропустити).");
        System.out.print("назва містить: ");
        String namePart = in.nextLine().trim().toLowerCase();

        Double minAmount  = askDoubleOpt(in, "мін. сума страхування: ");
        Double maxAmount  = askDoubleOpt(in, "макс. сума страхування: ");
        Double minFactor  = askDoubleOpt(in, "мін. фактор: ");
        Double maxFactor  = askDoubleOpt(in, "макс. фактор: ");
        Integer minPeriod = askIntOpt(   in, "мін. період (міс.): ");
        Integer maxPeriod = askIntOpt(   in, "макс. період (міс.): ");
        Double minRate    = askDoubleOpt(in, "мін. відсоткова ставка: ");
        Double maxRate    = askDoubleOpt(in, "макс. відсоткова ставка: ");
        Double minProb    = askDoubleOpt(in, "мін. ймовірність: ");
        Double maxProb    = askDoubleOpt(in, "макс. ймовірність: ");
        Double minMaxCost = askDoubleOpt(in, "мін. гранична вартість: ");
        Double maxMaxCost = askDoubleOpt(in, "макс. гранична вартість: ");

        List<Obligation> found = new ArrayList<>();
        for (Obligation o : obs) {
            if (!matches(namePart, o.getName())) continue;
            if (!inRange(o.getInsuredAmount(), minAmount, maxAmount)) continue;
            if (!inRange(o.getFactor(),        minFactor, maxFactor)) continue;
            if (!inRange(o.getPeriod(),        minPeriod, maxPeriod)) continue;
            if (!inRange(o.getInterestRate(),  minRate,   maxRate))   continue;
            if (!inRange(o.getProbability(),   minProb,   maxProb))   continue;
            if (!inRange(o.getMaxCost(),       minMaxCost,maxMaxCost))continue;
            found.add(o);
        }

        if (found.isEmpty()) { System.out.println("Нічого не знайдено."); return; }
        System.out.println("Знайдені зобов’язання:");
        for (int i=0;i<found.size();i++) {
            var o = found.get(i);
            System.out.printf("%2d) %s  amount=%.2f factor=%.3f prob=%.3f%n",
                    i+1, o.getName()!=null?o.getName():"—", o.getInsuredAmount(), o.getFactor(), o.getProbability());
        }
    }

    private Derivative pickDerivative(Scanner in, List<Derivative> list) {
        if (list.isEmpty()) { System.out.println("Немає деривативів."); return null; }
        for (int i=0;i<list.size();i++) System.out.printf("%d) %s%n", i+1, nameOf(list.get(i)));
        System.out.print("> №: ");
        try {
            int idx=Integer.parseInt(in.nextLine().trim());
            return (idx>=1&&idx<=list.size())?list.get(idx-1):null;
        }
        catch (Exception e){
            return null;
        }
    }
    private static String nameOf(Derivative d){
        return d.getName()!=null?d.getName():"без назви"; }

    private static boolean matches(String part, String name) {
        if (part == null || part.isEmpty()) return true;
        return name != null && name.toLowerCase().contains(part);
    }
    private static boolean inRange(double v, Double min, Double max) {
        if (min != null && v < min) return false;
        if (max != null && v > max) return false;
        return true;
    }
    private static boolean inRange(int v, Integer min, Integer max) {
        if (min != null && v < min) return false;
        if (max != null && v > max) return false;
        return true;
    }
    private static Double askDoubleOpt(Scanner in, String prompt) {
        System.out.print(prompt);
        String s = in.nextLine().trim();
        if (s.isEmpty()) return null;
        try {
            return Double.parseDouble(s);
        } catch (Exception e) { return null; }
    }
    private static Integer askIntOpt(Scanner in, String prompt) {
        System.out.print(prompt);
        String s = in.nextLine().trim();
        if (s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) { return null; }
    }
}
