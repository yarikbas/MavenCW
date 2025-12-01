package com.org.insurance.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

public class HealthObligation extends Obligation {

    private String coverageType;
    @Getter
    @Setter
    private boolean hasPreExistingConditions;
    @Getter
    @Setter
    private double annualLimit;

    public HealthObligation(){
        Scanner in =  new Scanner(System.in);
        super(in);
        this.setSpecificFields(in);
    }

    @Override
    public String toString() {
        return "HealthObligation{" +
                "name=" + getName() +
                ", id=" + getId() +
                ", insuredAmount=" + getInsuredAmount() +
                ", factor=" + getFactor() +
                ", period=" + getPeriod() +
                ", interestRate=" + getInterestRate() +
                ", probability=" + getProbability() +
                ", maxCost=" + getMaxCost() +
                ", coverageType='" + coverageType + '\'' +
                ", hasPreExistingConditions=" + hasPreExistingConditions +
                ", annualLimit=" + annualLimit +
                '}';
    }


    public HealthObligation(String name, double insuredAmount, double factor, int period,
                            double interestRate, double probability, double maxCost,
                            String coverageType, boolean hasPreExistingConditions, double annualLimit) {
        super(name, insuredAmount, factor, period, interestRate, probability, maxCost);
        this.coverageType = coverageType;
        this.hasPreExistingConditions = hasPreExistingConditions;
        this.annualLimit = annualLimit;
    }

    @Override
    public void setSpecificFields(Scanner in) {
        System.out.print("тип покриття (coverageType): ");
        String ct = in.nextLine().trim();
        if (!ct.isEmpty()) this.coverageType = ct;

        System.out.print("наявні хронічні/попередні стани? (true/false): ");
        String s = in.nextLine().trim().toLowerCase();
        if (!s.isEmpty()) this.hasPreExistingConditions = Boolean.parseBoolean(s);

        System.out.print("річний ліміт (annualLimit): ");
        String lim = in.nextLine().trim();
        if (!lim.isEmpty()) {
            try { this.annualLimit = Double.parseDouble(lim); } catch (NumberFormatException ignored) {}
        }
    }

    public String getCoverageType() { return coverageType; }
    public void setCoverageType(String coverageType) { this.coverageType = coverageType; }

}
