package com.org.insurance.domain;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Scanner;
import java.util.UUID;

@Getter
public abstract class Obligation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final UUID id;
    protected String name;
    protected double insuredAmount;
    protected double factor;
    protected int period;
    protected double interestRate;
    protected double probability;
    protected double maxCost;

    public Obligation(String name,
                      double insuredAmount,
                      double factor,
                      int period,
                      double interestRate,
                      double probability,
                      double maxCost) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.insuredAmount = insuredAmount;
        this.factor = factor;
        this.period = period;
        this.interestRate = interestRate;
        this.probability = probability;
        this.maxCost = maxCost;
    }

    protected Obligation(Scanner in) {
        this.id = UUID.randomUUID();
        System.out.print("Назва облігації: ");
        this.name = in.nextLine().trim();
        this.insuredAmount = askDouble(in, "Сума страхування (insuredAmount): ");
        this.factor        = askDouble(in, "Фактор (factor): ");
        this.period        = askInt(in,    "Період у місяцях (period): ");
        this.interestRate  = askDouble(in, "Відсоткова ставка (interestRate, напр. 0.07): ");
        this.probability   = askDouble(in, "Ймовірність (probability, 0..1): ");
        this.maxCost       = askDouble(in, "Гранична вартість (maxCost): ");
    }

    public abstract void setSpecificFields(Scanner in);

    public void setName(String name) { this.name = name; }
    public void setInsuredAmount(double insuredAmount) { this.insuredAmount = insuredAmount; }
    public void setFactor(double factor) { this.factor = factor; }
    public void setPeriod(int period) { this.period = period; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public void setProbability(double probability) { this.probability = probability; }
    public void setMaxCost(double maxCost) { this.maxCost = maxCost; }

    private static double askDouble(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (Exception e) { System.out.println("Введіть число (наприклад, 123.45)."); }
        }
    }
    private static int askInt(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (Exception e) { System.out.println("Введіть ціле число (наприклад, 12)."); }
        }
    }
}
