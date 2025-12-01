package com.org.insurance.ui;

import com.org.insurance.domain.Derivative;
import com.org.insurance.ui.command.*;

import java.util.*;

public class InsuranceMenu {

    private final List<Derivative> derivatives = new ArrayList<>();
    private final Scanner in = new Scanner(System.in);
    private final Map<String, Command> commands = new HashMap<>();
    private boolean running;

    public InsuranceMenu() {
        registerBuiltInCommands();
    }

    public void run() {
        running = true;
        System.out.println("Введіть назву команди (наприклад, 'add'). 'help' — описи, 'exit' — вихід.");
        while (running) {
            showShortMenu();
            System.out.print("> ");
            String line = in.hasNextLine() ? in.nextLine().trim() : null;
            if (line == null) break;
            if (line.isEmpty()) continue;
            executeCommand(line);
        }
    }

    public void executeCommand(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT);

        if (s.equals("help")) { showHelp(); return; }
        if (s.equals("exit") || s.equals("quit")) { exit(); return; }

        Command cmd = commands.get(s);
        if (cmd == null) {
            System.out.println("Невідома команда. Введіть слово з переліку або 'help'.");
            return;
        }
        cmd.execute(in, derivatives);
    }

    public void showHelp() {
        if (commands.isEmpty()) {
            System.out.println("Команди не зареєстровані.");
            return;
        }
        System.out.println("ОПИС КОМАНД:");
        int i = 1;
        for (Map.Entry<String, Command> e : commands.entrySet()) {
            System.out.printf("%2d) %-10s — %s%n", i++, e.getKey(), e.getValue().getDescription());
        }
        System.out.println("Доступні також: help, exit");
    }

    public void exit() {
        running = false;
        System.out.println("Завершення роботи...");
    }

    private void showShortMenu() {
        if (commands.isEmpty()) {
            System.out.println("[немає зареєстрованих команд]");
            return;
        }
        System.out.println("\nКОМАНДИ (вводьте слово):");
        int i = 1;
        for (String key : commands.keySet()) {
            System.out.printf("%2d) %s%n", i++, key);
        }
        System.out.println("help — описи,  exit — вихід");
    }

    private void registerBuiltInCommands() {
        registerCommand("add",    new AddObligationCommand());
        registerCommand("calc",   new CalculateCommand());
        registerCommand("create", new CreateDerivativeCommand());
        registerCommand("delete", new DeleteDerivativeCommand());
        registerCommand("find",   new FindObligationCommand());
        registerCommand("load",   new LoadFromFileCommand());
        registerCommand("remove", new RemoveObligationCommand());
        registerCommand("save",   new SaveToFileCommand());
        registerCommand("show",   new ShowDerivativesCommand());
        registerCommand("sort",   new SortByRiskCommand());
    }

    public void registerCommand(String name, Command command) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(command, "command");
        commands.put(name.trim().toLowerCase(Locale.ROOT), command);
    }
}