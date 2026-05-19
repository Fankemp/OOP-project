package university;

import university.storage.University;
import university.storage.UserFactory;
import university.users.Admin;
import university.view.ConsoleInterface;

public class Main {
    private static final University university = University.getInstance();

    public static void main(String[] args) {
        LogConfig.setup();

        try {
            university.load();
        } catch (Exception e) {
            System.out.println("⚠️ Файл базы данных не найден. Будет создана чистая сессия.");
        }

        if (university.getUsers().isEmpty()) {
            initSystemData();
        }


        ConsoleInterface.start();
    }


    private static void initSystemData() {
        System.out.println("Первоначальная инициализация системы KBTU...");

        Admin rootAdmin = UserFactory.createAdmin("A001", "Admin", "Root",
                "admin@kbtu.kz", "admin", "123", 500000.0);
        university.addUser(rootAdmin);

        System.out.println("✅ База данных успешно развернута.");
        System.out.println("Создана единственная учетная запись Системного Администратора.");
        System.out.println("=======================================================");
    }
}