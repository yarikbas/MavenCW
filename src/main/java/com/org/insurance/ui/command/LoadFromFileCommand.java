package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.io.FileManager;

import java.util.List;
import java.util.Scanner;

public class LoadFromFileCommand implements Command {
    private final FileManager fileManager = new FileManager();

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        System.out.print("Шлях до файлу (напр., data/derivative.bin): ");
        String path = in.nextLine().trim();
        if (path.isEmpty()) return;

        Derivative d = fileManager.loadDerivative(path);
        if (d != null) {
            derivatives.add(d);
            System.out.println("Завантажено: " + (d.getName() != null ? d.getName() : d.getId()));
        }
    }

    @Override
    public String getDescription() {
        return "Завантажити дериватив із бінарного файлу";
    }
}
