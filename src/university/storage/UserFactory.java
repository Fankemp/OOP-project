package university.storage;

import university.users.*;
import university.enums.*;

public class UserFactory {

    public static Student createStudent(String id, String firstName, String lastName,
                                        String email, String login, String password,
                                        String faculty, int year) {
        return new Student(id, firstName, lastName, email, login, password, faculty, year);
    }

    public static Teacher createTeacher(String id, String firstName, String lastName,
                                        String email, String login, String password,
                                        double salary, String department, TeacherPosition position) {
        return new Teacher(id, firstName, lastName, email, login, password, salary, department, position);
    }

    public static Manager createManager(String id, String firstName, String lastName,
                                        String email, String login, String password,
                                        double salary, ManagerType type) {
        return new Manager(id, firstName, lastName, email, login, password, salary, type);
    }

    public static Admin createAdmin(String id, String firstName, String lastName,
                                    String email, String login, String password, double salary) {
        return new Admin(id, firstName, lastName, email, login, password, salary);
    }

    public static TechSupportSpecialist createTechSupport(String id, String firstName, String lastName,
                                                          String email, String login, String password, double salary) {
        return new TechSupportSpecialist(id, firstName, lastName, email, login, password, salary);
    }
}