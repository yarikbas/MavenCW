package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateDerivativeCommand implements Command {

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        System.out.print("Назва нового деривативу: ");
        String name = in.nextLine().trim();

        Derivative d = new Derivative();
        d.setName(name.isEmpty() ? null : name);
        if (d.getObligations() == null) {
            d.setObligations(new ArrayList<>());
        }

        derivatives.add(d);
        System.out.println("Створено дериватив: " + (d.getName() == null ? "(без назви)" : d.getName())
                + " | id=" + d.getId());
    }

    @Override
    public String getDescription() {
        return "Створити дериватив та додати його до списку";
    }
}
