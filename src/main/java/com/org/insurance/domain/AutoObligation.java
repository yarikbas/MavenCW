package com.org.insurance.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

@Setter
@Getter
public class AutoObligation extends Obligation {

    private String vehicleType;
    private String driverClass;
    private double bonusMalus;

    public AutoObligation(){
        Scanner in =  new Scanner(System.in);
        super(in);
        this.setSpecificFields(in);
    }

    public AutoObligation(String name, double insuredAmount, double factor,
                          int period, double interestRate, double probability, double maxCost,
                          String vehicleType, String driverClass, double bonusMalus) {
        super(name, insuredAmount, factor, period, interestRate, probability, maxCost);
        this.vehicleType = vehicleType;
        this.driverClass = driverClass;
        this.bonusMalus = bonusMalus;
    }

    @Override
    public String toString() {
        return "AutoObligation{" +
                "name=" + getName() +
                ", id=" + getId() +
                ", insuredAmount=" + getInsuredAmount() +
                ", factor=" + getFactor() +
                ", period=" + getPeriod() +
                ", interestRate=" + getInterestRate() +
                ", probability=" + getProbability() +
                ", maxCost=" + getMaxCost() +
                "vehicleType='" + vehicleType + '\'' +
                ", driverClass='" + driverClass + '\'' +
                ", bonusMalus=" + bonusMalus +
                '}';
    }

    @Override
    public void setSpecificFields(Scanner in) {
        System.out.print("vehicleType: ");
        String vt = in.nextLine().trim();
        if (!vt.isEmpty()) this.vehicleType = vt;

        System.out.print("driverClass: ");
        String dc = in.nextLine().trim();
        if (!dc.isEmpty()) this.driverClass = dc;

        System.out.print("bonusMalus: ");
        String bm = in.nextLine().trim();
        if (!bm.isEmpty()) {
            try { this.bonusMalus = Double.parseDouble(bm); } catch (NumberFormatException ignored) {}
        }
    }

}
