package com.org.insurance.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

@Setter
@Getter
public class BusinessObligation extends Obligation {

    private String registrationNumber;
    private String industry;

    public BusinessObligation(){
        Scanner in =  new Scanner(System.in);
        super(in);
        this.setSpecificFields(in);
    }

    @Override
    public String toString() {
        return "BusinessObligation{" +
                "name=" + getName() +
                ", id=" + getId() +
                ", insuredAmount=" + getInsuredAmount() +
                ", factor=" + getFactor() +
                ", period=" + getPeriod() +
                ", interestRate=" + getInterestRate() +
                ", probability=" + getProbability() +
                ", maxCost=" + getMaxCost() +
                "registrationNumber='" + registrationNumber + '\'' +
                ", industry='" + industry + '\'' +
                '}';
    }

    public BusinessObligation(String name, double insuredAmount, double factor,
                              int period, double interestRate, double probability, double maxCost,
                              String registrationNumber, String industry) {
        super(name, insuredAmount, factor, period, interestRate, probability, maxCost);
        this.registrationNumber = registrationNumber;
        this.industry = industry;
    }

    @Override
    public void setSpecificFields(Scanner in) {
        System.out.print("registrationNumber: ");
        String reg = in.nextLine().trim();
        if (!reg.isEmpty()) this.registrationNumber = reg;

        System.out.print("industry: ");
        String ind = in.nextLine().trim();
        if (!ind.isEmpty()) this.industry = ind;
    }

}
