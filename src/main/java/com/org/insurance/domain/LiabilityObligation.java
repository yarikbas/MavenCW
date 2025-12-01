package com.org.insurance.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

@Setter
@Getter
public class LiabilityObligation extends Obligation {

    private String coverageType;
    private String jurisdiction;

    public LiabilityObligation(){
        Scanner in =  new Scanner(System.in);
        super(in);
        this.setSpecificFields(in);
    }

    @Override
    public String toString() {
        return "LiabilityObligation{" +
                "name=" + getName() +
                ", id=" + getId() +
                ", insuredAmount=" + getInsuredAmount() +
                ", factor=" + getFactor() +
                ", period=" + getPeriod() +
                ", interestRate=" + getInterestRate() +
                ", probability=" + getProbability() +
                ", maxCost=" + getMaxCost() +
                "coverageType='" + coverageType + '\'' +
                ", jurisdiction='" + jurisdiction + '\'' +
                '}';
    }

    public LiabilityObligation(String name, double insuredAmount, double factor,
                               int period, double interestRate, double probability, double maxCost,
                               String coverageType, String jurisdiction) {
        super(name, insuredAmount, factor, period, interestRate, probability, maxCost);
        this.coverageType = coverageType;
        this.jurisdiction = jurisdiction;
    }

    @Override
    public void setSpecificFields(Scanner in) {
        System.out.print("coverageType: ");
        String ct = in.nextLine().trim();
        if (!ct.isEmpty()) this.coverageType = ct;

        System.out.print("jurisdiction: ");
        String jur = in.nextLine().trim();
        if (!jur.isEmpty()) this.jurisdiction = jur;
    }

}
