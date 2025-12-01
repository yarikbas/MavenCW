package com.org.insurance.domain;

import java.util.List;

public final class InsuranceCalculator {

    public double calculatePortfolioValue(Derivative derivative) {
        if (derivative == null) {
            return 0.0;
        }

        List<Obligation> obligations = derivative.getObligations();
        if (obligations == null || obligations.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;

        for (Obligation o : obligations) {
            if (o == null) {
                continue;
            }
            total += calculatePriceOfService(o);
        }

        return total;
    }

    public double calculateTotalRisk(Derivative derivative) {
        if (derivative == null) {
            return 0.0;
        }

        List<Obligation> obligations = derivative.getObligations();
        if (obligations == null || obligations.isEmpty()) {
            return 0.0;
        }

        double totalRisk = 0.0;

        for (int i = 0; i < obligations.size(); i++) {
            Obligation o = obligations.get(i);
            if (o == null) {
                continue;
            }

            double insuredAmount = o.getInsuredAmount();
            double factor = o.getFactor();
            double probability = o.getProbability();

            double riskScore = insuredAmount * factor * probability;
            totalRisk += riskScore;
        }

        return totalRisk;
    }

    public double calculatePriceOfService(Obligation obligation) {
        if (obligation == null) {
            return 0.0;
        }

        double insuredAmount = obligation.getInsuredAmount();
        double factor = obligation.getFactor();
        double periodMonths = obligation.getPeriod();
        double interestRate = obligation.getInterestRate();
        double probability = obligation.getProbability();
        double maxCost = obligation.getMaxCost();

        // Якщо базові параметри некоректні або нульові — премія = 0
        if (insuredAmount <= 0.0 || probability <= 0.0 || factor <= 0.0) {
            return 0.0;
        }

        // 1. Очікуваний збиток
        double expectedLoss = insuredAmount * probability * factor;

        // 2. Період у роках
        double years = periodMonths / 12.0;
        if (years < 0.0) {
            years = 0.0;
        }

        // 3. Коефіцієнт з урахуванням ставки та строку
        double timeCoeff = 1.0 + interestRate * years;

        // 4. Брутто-премія
        double grossPremium = expectedLoss * timeCoeff;

        // 5. Обмеження зверху maxCost (якщо заданий > 0)
        if (maxCost > 0.0 && grossPremium > maxCost) {
            grossPremium = maxCost;
        }

        // 6. Захист від від’ємних значень
        if (grossPremium < 0.0) {
            grossPremium = 0.0;
        }

        return grossPremium;
    }
}
