package com.org.insurance;

import com.org.insurance.ui.InsuranceMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    static void main() {
        log.info("=== Старт програми MavenCW ===");

        try {
            new InsuranceMenu().run();
            log.info("Нормальне завершення роботи меню");
        } catch (Exception e) {
            log.error("Несподівана критична помилка в main()", e);
        }

        log.info("=== Програма MavenCW завершилася ===");
    }
}
