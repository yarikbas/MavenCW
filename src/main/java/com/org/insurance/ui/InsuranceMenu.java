package com.org.insurance.ui;

import com.org.insurance.domain.Derivative;
import com.org.insurance.ui.command.*;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InsuranceMenu {

    private static final Logger log = LogManager.getLogger(InsuranceMenu.class);

    private final List<Derivative> derivatives = new ArrayList<>();
    private final Scanner in = new Scanner(System.in);
    private final Map<String, Command> commands = new HashMap<>();

    public InsuranceMenu() {
        registerBuiltInCommands();
        log.info("Ініціалізовано InsuranceMenu, зареєстровано {} команд(и)", commands.size());
    }

    public void run() {
        log.info("Початок роботи меню страхування");
        System.out.println("Введіть назву команди (наприклад, 'add'). 'help' — описи, 'exit' — вихід.");

        while (true) {
            showShortMenu();
            System.out.print("> ");

            String line = in.hasNextLine() ? in.nextLine().trim() : null;

            if (line == null) {
                log.info("Вхідний потік команд завершено (line == null), вихід із меню");
                break;
            }

            if (line.isEmpty()) {
                continue;
            }

            String cmdText = line.toLowerCase(Locale.ROOT);

            if (cmdText.equals("exit") || cmdText.equals("quit")) {
                log.info("Користувач обрав вихід із програми (команда '{}')", cmdText);
                System.out.println("Завершення роботи...");
                break;
            }

            executeCommand(cmdText);
        }

        log.info("Завершення роботи меню страхування");
    }

    public void executeCommand(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT);
        log.info("Отримано команду від користувача: '{}'", s);

        if (s.equals("help")) {
            showHelp();
            return;
        }

        Command cmd = commands.get(s);
        if (cmd == null) {
            log.warn("Користувач ввів невідому команду: '{}'", s);
            System.out.println("Невідома команда. Введіть слово з переліку або 'help'.");
            return;
        }

        try {
            log.info("Виконання команди '{}'", s);
            cmd.execute(in, derivatives);
        } catch (Exception e) {
            log.error("Помилка при виконанні команди '{}'", s, e);
            System.out.println("Сталася помилка під час виконання команди. Деталі дивіться в логах.");
        }
    }

    public void showHelp() {
        if (commands.isEmpty()) {
            System.out.println("Команди не зареєстровані.");
            log.warn("showHelp викликано, але commands порожній");
            return;
        }
        System.out.println("ОПИС КОМАНД:");
        int i = 1;
        for (Map.Entry<String, Command> e : commands.entrySet()) {
            System.out.printf("%2d) %-10s — %s%n", i++, e.getKey(), e.getValue().getDescription());
        }
        System.out.println("Доступні також: help, exit");
    }

    private void showShortMenu() {
        if (commands.isEmpty()) {
            System.out.println("Команди не зареєстровані.");
            log.warn("showShortMenu викликано, але commands порожній");
            return;
        }
        System.out.print("Доступні команди: ");
        System.out.print("help, exit");
        for (String k : commands.keySet()) {
            System.out.print(", " + k);
        }
        System.out.println();
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
