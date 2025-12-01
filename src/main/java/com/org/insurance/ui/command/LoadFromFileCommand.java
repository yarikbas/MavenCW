package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;
import com.org.insurance.io.FileManager;

import java.util.List;
import java.util.Scanner;

public class LoadFromFileCommand implements Command {

    @Override
    public void execute(Scanner in, List<Derivative> derivatives) {
        System.out.print("Введіть шлях до файлу для завантаження: ");

        // Перевірка, що у Scanner взагалі є наступний рядок
        if (!in.hasNextLine()) {
            System.out.println("Ввід перервано. Завантаження скасовано.");
            return;
        }

        String filename = in.nextLine().trim();
        if (filename.isEmpty()) {
            System.out.println("Шлях до файлу не може бути порожнім.");
            return;
        }

        FileManager fileManager = new FileManager();
        try {
            Derivative derivative = fileManager.loadDerivative(filename);
            if (derivative != null) {
                derivatives.add(derivative);
                System.out.println("Дериватив успішно завантажено.");
            } else {
                System.out.println("Файл прочитано, але дериватив порожній або некоректний.");
            }
        } catch (RuntimeException e) {
            System.out.println("Помилка при завантаженні файлу: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Завантажити дериватив з файлу";
    }
}
