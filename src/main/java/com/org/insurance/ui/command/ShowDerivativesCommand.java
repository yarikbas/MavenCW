package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.ui.ConsolePrinter;

import java.util.List;
import java.util.Scanner;

public class ShowDerivativesCommand implements Command {

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        if (derivatives == null || derivatives.isEmpty()) {
            System.out.println("Немає деривативів.");
            return;
        }

        System.out.println("Список деривативів:");
        ConsolePrinter.printDerivativesWithObligations(derivatives);
    }

    @Override
    public String getDescription() {
        return "Показати список деривативів";
    }
}
