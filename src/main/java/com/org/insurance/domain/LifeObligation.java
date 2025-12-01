package com.org.insurance.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Setter
@Getter
public class LifeObligation extends Obligation {
    @Serial
    private static final long serialVersionUID = 1L;

    private String insuredPersonId;
    private LocalDate dateOfBirth;
    private String beneficiaryName;

    public LifeObligation(){
        Scanner in =  new Scanner(System.in);
        super(in);
        this.setSpecificFields(in);
    }

    @Override
    public String toString() {
        return "LifeObligation{" +
                "name=" + getName() +
                ", id=" + getId() +
                ", insuredAmount=" + getInsuredAmount() +
                ", factor=" + getFactor() +
                ", period=" + getPeriod() +
                ", interestRate=" + getInterestRate() +
                ", probability=" + getProbability() +
                ", maxCost=" + getMaxCost() +
                "insuredPersonId='" + insuredPersonId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                '}';
    }

    public LifeObligation(String name, double insuredAmount, double factor,
                          int period, double interestRate, double probability, double maxCost,
                          String insuredPersonId, LocalDate dateOfBirth, String beneficiaryName) {
        super(name, insuredAmount, factor, period, interestRate, probability, maxCost);
        this.insuredPersonId = insuredPersonId;
        this.dateOfBirth = dateOfBirth;
        this.beneficiaryName = beneficiaryName;
    }

    @Override
    public void setSpecificFields(Scanner in) {
        System.out.print("ID застрахованої особи: ");
        String pid = in.nextLine().trim();
        if (!pid.isEmpty()) this.insuredPersonId = pid;

        System.out.print("Дата народження (YYYY-MM-DD): ");
        String dob = in.nextLine().trim();
        if (!dob.isEmpty()) {
            try { this.dateOfBirth = LocalDate.parse(dob); }
            catch (DateTimeParseException ignored) {}
        }

        System.out.print("Бенефіціар (ім'я/ПІБ): ");
        String ben = in.nextLine().trim();
        if (!ben.isEmpty()) this.beneficiaryName = ben;
    }

}
