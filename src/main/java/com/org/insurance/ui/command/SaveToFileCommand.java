package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.io.FileManager;
import com.org.insurance.ui.ConsolePrinter;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class SaveToFileCommand implements Command {
    private final FileManager fileManager = new FileManager();

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        if (derivatives == null || derivatives.isEmpty()) {
            System.out.println("Немає деривативів для збереження.");
            return;
        }

        System.out.println("Список деривативів:");
        ConsolePrinter.printDerivatives(derivatives);

        System.out.print("Оберіть № деривативу для збереження: ");
        int idx;
        try { idx = Integer.parseInt(in.nextLine().trim()); } catch (Exception e) { idx = -1; }
        if (idx < 1 || idx > derivatives.size()) {
            System.out.println("Невірний вибір.");
            return;
        }
        Derivative chosen = derivatives.get(idx - 1);

        System.out.print("Формат (bin/txt): ");
        String fmt = in.nextLine().trim().toLowerCase(Locale.ROOT);
        if (!fmt.equals("bin") && !fmt.equals("txt")) {
            System.out.println("Невідомий формат. Використайте 'bin' або 'txt'.");
            return;
        }

        System.out.print("Шлях для збереження (напр., data/derivative." + fmt + "): ");
        String path = in.nextLine().trim();
        if (path.isEmpty()) {
            System.out.println("Скасовано.");
            return;
        }

        if (fmt.equals("bin")) {
            fileManager.saveDerivative(chosen, path);
        } else {
            fileManager.exportToText(chosen, path);
        }
        System.out.println("Збережено у файл: " + path);
    }

    @Override
    public String getDescription() {
        return "Зберегти вибраний дериватив (бінарно або текстом)";
    }
}
