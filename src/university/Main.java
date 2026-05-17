package university;

import university.communications.Message;
import university.storage.University;
import university.academic.Course;
import university.enums.*;
import university.storage.UserFactory;
import university.users.*;
import university.view.ConsoleInterface;

import java.util.List;


public class Main {
    private static final University university = University.getInstance();
    public static void main(String[] args) {
        LogConfig.setup();

        try {
            university.load();
        } catch (Exception e) {
            System.out.println("Файл базы данных не найден. Будет создана чистая сессия.");
        }

        if (university.getUsers().isEmpty()) {
            initSystemData();
        }


        ConsoleInterface.start();
    }




    static void initSystemData() {
        System.out.println("Запуск первичной инициализации системы KBTU...");

        // ИСПРАВЛЕНО: Создаем только одного Root-Админа. Все остальные пользователи будут созданы им вручную.
        Admin rootAdmin = UserFactory.createAdmin(
                "A001",
                "Главный",
                "Администратор",
                "admin@kbtu.kz",
                "admin",
                "123",
                500000.0
        );
        university.addUser(rootAdmin);

        // Оставляем один базовый курс в каталоге, чтобы админу/менеджеру было с чем работать на демо
        Course oop = new Course("CSCI2101", "Object-Oriented Programming", 5, CourseType.MAJOR);
        university.addCourse(oop);

        System.out.println("Инициализация завершена. Создан аккаунт Администратора (Логин: admin, Пароль: 123).");
    }
}