package com.org.insurance.io;

import com.org.insurance.domain.Derivative;
import com.org.insurance.domain.Obligation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileManager {

    /**
     * Зберігає об'єкт Derivative у бінарний файл за допомогою ObjectOutputStream.
     */
    public void saveDerivative(Derivative derivative, String filename) {
        ensureParentDir(filename);
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(derivative);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося зберегти файл: " + filename, e);
        }
    }

    /**
     * Завантажує об'єкт Derivative з бінарного файлу за допомогою ObjectInputStream.
     * * Виправлення: Розбито створення потоків, щоб гарантувати закриття FileInputStream
     * навіть у разі помилки ObjectInputStream.
     */
    public Derivative loadDerivative(String filename) {
        try (FileInputStream fis = new FileInputStream(filename); // Фізичний потік, який закривається
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            Object obj = ois.readObject();
            if (obj instanceof Derivative d) {
                return d;
            }
            throw new RuntimeException("Файл не містить Derivative: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            // Тут використовується власна реалізація RuntimeException, щоб не змінювати підпис методу
            throw new RuntimeException("Не вдалося завантажити файл: " + filename, e);
        }
    }

    /**
     * Експортує вміст Derivative у читабельний текстовий файл.
     */
    public void exportToText(Derivative derivative, String filename) {
        ensureParentDir(filename);
        Path path = Path.of(filename);

        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write("DERIVATIVE\n");
            w.write("id: " + derivative.getId() + "\n");
            w.write("name: " + derivative.getName() + "\n");
            w.write("obligations: " + (derivative.getObligations() == null ? 0 : derivative.getObligations().size()) + "\n");
            w.write("\n");

            if (derivative.getObligations() != null) {
                int i = 1;
                for (Obligation o : derivative.getObligations()) {
                    if (o == null) continue;
                    w.write("[" + i++ + "] " + o.getClass().getSimpleName() + "\n");
                    w.write("  id            : " + o.getId() + "\n");
                    w.write("  name          : " + o.getName() + "\n");
                    w.write("  insuredAmount : " + o.getInsuredAmount() + "\n");
                    w.write("  factor        : " + o.getFactor() + "\n");
                    w.write("  period        : " + o.getPeriod() + "\n");
                    w.write("  interestRate  : " + o.getInterestRate() + "\n");
                    w.write("  probability   : " + o.getProbability() + "\n");
                    w.write("  maxCost       : " + o.getMaxCost() + "\n");
                    w.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося експортувати у текст: " + filename, e);
        }
    }

    /**
     * Забезпечує існування батьківського каталогу для файлу.
     */
    private static void ensureParentDir(String filename) {
        try {
            Path p = Path.of(filename).toAbsolutePath();
            Path parent = p.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося створити каталог для: " + filename, e);
        }
    }
}