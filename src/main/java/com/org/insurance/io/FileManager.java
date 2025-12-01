package com.org.insurance.io;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FileManager {

    private static final Logger log = LogManager.getLogger(FileManager.class);

    /**
     * Зберегти деривативу у бінарний файл (серіалізація Java).
     */
    public void saveDerivative(Derivative derivative, String fileName) {
        if (derivative == null) {
            throw new IllegalArgumentException("derivative is null");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is null/blank");
        }

        Path path = Paths.get(fileName);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            log.warn("Не вдалося створити каталоги для файлу '{}'", fileName, e);
        }

        log.info("Спроба зберегти дериватив '{}' у файл '{}'",
                derivative.getName(), fileName);

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(path))) {

            oos.writeObject(derivative);
            log.info("Дериватив успішно збережено у '{}'", fileName);

        } catch (IOException e) {
            log.error("Помилка під час збереження деривативу в файл '{}'", fileName, e);
            throw new RuntimeException("Не вдалося зберегти файл: " + fileName, e);
        }
    }

    /**
     * Завантажити деривативу з бінарного файлу.
     * Якщо файл не існує або зламаний — кидаємо RuntimeException.
     * Цей ERROR тригерить відправку листа.
     */
    public Derivative loadDerivative(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is null/blank");
        }

        Path path = Paths.get(fileName);
        log.info("Спроба завантажити дериватив із файлу '{}'", fileName);

        if (!Files.exists(path)) {
            log.warn("Файл деривативу не знайдено: '{}'", fileName);
            throw new RuntimeException("Файл не знайдено: " + fileName);
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(path))) {

            Derivative derivative = (Derivative) ois.readObject();
            if (derivative != null) {
                log.info("Дериватив '{}' успішно завантажено з файлу '{}'",
                        derivative.getName(), fileName);
            } else {
                log.warn("З файлу '{}' прочитано null-деривативу", fileName);
            }
            return derivative;

        } catch (IOException | ClassNotFoundException e) {
            log.error("Не вдалося завантажити файл деривативу '{}'", fileName, e);
            // ⬆️ ОЦЕЙ ERROR іде в лог + на пошту
            throw new RuntimeException("Не вдалося завантажити файл: " + fileName, e);
        }
    }

    /**
     * Експорт деривативи у "гарно структурований" текстовий звіт.
     */
    public void exportToText(Derivative derivative, String fileName) {
        if (derivative == null) {
            throw new IllegalArgumentException("derivative is null");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is null/blank");
        }

        Path path = Paths.get(fileName);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            log.warn("Не вдалося створити каталоги для текстового файлу '{}'", fileName, e);
        }

        log.info("Експорт деривативи '{}' у текстовий файл '{}'",
                derivative.getName(), fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            // ===== Заголовок звіту =====
            String name = safe(derivative.getName(), "без назви");
            UUID id = derivative.getId();

            List<Obligation> obligations = safeList(derivative.getObligations());
            int count = obligations.size();

            writer.write("=== ЗВІТ ПО ДЕРИВАТИВУ ===");
            writer.newLine();
            writer.write("Назва: " + name);
            writer.newLine();
            writer.write("ID:    " + (id != null ? id.toString() : "—"));
            writer.newLine();
            writer.write("Кількість зобов'язань: " + count);
            writer.newLine();
            writer.newLine();

            if (obligations.isEmpty()) {
                writer.write("Зобов'язань немає.");
                writer.newLine();
                log.info("Експорт звіту завершено: дериватива без зобов'язань");
                return;
            }

            // ===== Кожна облігація – окремий блок =====
            for (int i = 0; i < obligations.size(); i++) {
                Obligation o = obligations.get(i);
                if (o == null) {
                    continue;
                }

                writer.write(String.format("--- Облігація #%d ---", i + 1));
                writer.newLine();

                String oName = safe(o.getName(), o.getClass().getSimpleName());
                String type = o.getClass().getSimpleName();
                UUID oid = o.getId();

                double insuredAmount = o.getInsuredAmount();
                double probability   = o.getProbability();
                double factor        = o.getFactor();
                int    periodMonths  = o.getPeriod();
                double interestRate  = o.getInterestRate();
                double maxCost       = o.getMaxCost();

                double expectedLoss  = insuredAmount * probability * factor;
                double years         = periodMonths / 12.0;
                if (years < 0.0) {
                    years = 0.0;
                }
                double timeCoeff     = 1.0 + interestRate * years;
                double risk          = insuredAmount * probability * factor; // базова оцінка ризику

                // Основні дані
                writer.write("Тип:   " + type);
                writer.newLine();
                writer.write("Назва: " + oName);
                writer.newLine();
                writer.write("ID:    " + (oid != null ? oid.toString() : "—"));
                writer.newLine();
                writer.newLine();

                // Базові числові поля
                writer.write(String.format("Сума страхування (insuredAmount) = %.2f", insuredAmount));
                writer.newLine();
                writer.write(String.format("Ймовірність (probability)       = %.6f", probability));
                writer.newLine();
                writer.write(String.format("Фактор (factor)                  = %.6f", factor));
                writer.newLine();
                writer.write(String.format("Період (periodMonths)            = %d", periodMonths));
                writer.newLine();
                writer.write(String.format("Ставка (interestRate)            = %.6f", interestRate));
                writer.newLine();
                writer.write(String.format("Макс. вартість (maxCost)         = %.2f", maxCost));
                writer.newLine();
                writer.newLine();

                // Формула очікуваних збитків
                writer.write("expectedLoss = insuredAmount * probability * factor");
                writer.newLine();
                writer.write(String.format("             = %.2f * %.6f * %.6f = %.2f",
                        insuredAmount, probability, factor, expectedLoss));
                writer.newLine();
                writer.newLine();

                // Формула коефіцієнта часу
                writer.write("years = periodMonths / 12.0");
                writer.newLine();
                writer.write(String.format("      = %d / 12.0 = %.6f",
                        periodMonths, years));
                writer.newLine();
                writer.newLine();

                writer.write("timeCoeff = 1.0 + interestRate * years");
                writer.newLine();
                writer.write(String.format("          = 1.0 + %.6f * %.6f = %.6f",
                        interestRate, years, timeCoeff));
                writer.newLine();
                writer.newLine();

                // Оцінка ризику портфеля (спрощена, як у printTotalRiskCalculation)
                writer.write("risk = insuredAmount * factor * probability");
                writer.newLine();
                writer.write(String.format("     = %.2f * %.6f * %.6f = %.2f",
                        insuredAmount, factor, probability, risk));
                writer.newLine();
                writer.newLine();

                writer.write("----------------------------------------");
                writer.newLine();
                writer.newLine();
            }

            log.info("Експорт деривативи в текстовий файл '{}' успішно завершено",
                    fileName);

        } catch (IOException e) {
            log.error("Помилка під час експорту деривативи в текстовий файл '{}'",
                    fileName, e);
            throw new RuntimeException("Не вдалося експортувати у текстовий файл: " + fileName, e);
        }
    }

    // ===== Допоміжні методи =====

    private static List<Obligation> safeList(List<Obligation> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private static String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }
}
