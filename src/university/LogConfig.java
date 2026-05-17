package university;

import java.io.IOException;
import java.util.logging.*;

public class LogConfig {
    public static void setup() {
        LogManager.getLogManager().reset();

        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);

        try {

            FileHandler fileHandler = new FileHandler("university.log", true);

            fileHandler.setFormatter(new SimpleFormatter());

            rootLogger.addHandler(fileHandler);

        } catch (IOException e) {
            System.err.println("Критическая ошибка: не удалось запустить файловое логирование: " + e.getMessage());
        }
    }
}