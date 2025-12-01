package com.org.insurance.domain;

import java.util.Comparator;

public final class RiskComparator implements Comparator<Obligation> {

    @Override
    public int compare(Obligation o1, Obligation o2) {
        double r1 = riskScore(o1);
        double r2 = riskScore(o2);
        return Double.compare(r1, r2);
    }

    public static double riskScore(Obligation o) {
        if (o == null) return 0.0;
        return o.getProbability() * o.getFactor() * o.getInsuredAmount();
    }
}
