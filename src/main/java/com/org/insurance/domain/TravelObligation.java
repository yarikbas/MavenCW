package com.org.insurance.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Setter
@Getter
public class TravelObligation extends Obligation {

    private String destinationCountry;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;

    public  TravelObligation(){
        Scanner in =  new Scanner(System.in);
        super(in);
        this.setSpecificFields(in);
    }

    @Override
    public String toString() {
        return "TravelObligation{" +
                "name=" + getName() +
                ", id=" + getId() +
                ", insuredAmount=" + getInsuredAmount() +
                ", factor=" + getFactor() +
                ", period=" + getPeriod() +
                ", interestRate=" + getInterestRate() +
                ", probability=" + getProbability() +
                ", maxCost=" + getMaxCost() +
                "destinationCountry='" + destinationCountry + '\'' +
                ", tripStartDate=" + tripStartDate +
                ", tripEndDate=" + tripEndDate +
                '}';
    }

    public TravelObligation(String name, double insuredAmount, double factor,
                            int period, double interestRate, double probability, double maxCost,
                            String destinationCountry, LocalDate tripStartDate, LocalDate tripEndDate) {
        super(name, insuredAmount, factor, period, interestRate, probability, maxCost);
        this.destinationCountry = destinationCountry;
        this.tripStartDate = tripStartDate;
        this.tripEndDate = tripEndDate;
    }

    @Override
    public void setSpecificFields(Scanner in) {
        System.out.print("Країна подорожі: ");
        String dest = in.nextLine().trim();
        if (!dest.isEmpty()) this.destinationCountry = dest;

        System.out.print("Дата початку (YYYY-MM-DD): ");
        String start = in.nextLine().trim();
        if (!start.isEmpty()) {
            try { this.tripStartDate = LocalDate.parse(start); } catch (DateTimeParseException ignored) {}
        }

        System.out.print("Дата завершення (YYYY-MM-DD): ");
        String end = in.nextLine().trim();
        if (!end.isEmpty()) {
            try { this.tripEndDate = LocalDate.parse(end); } catch (DateTimeParseException ignored) {}
        }
    }

}
