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
        System.out.println("Initializing KBTU system...");

        // Admin
        Admin rootAdmin = UserFactory.createAdmin("A001", "Admin", "Root",
                "admin@kbtu.kz", "admin", "123", 500000.0);
        university.addUser(rootAdmin);

        // Teacher
        Teacher teacher = UserFactory.createTeacher("T001", "Aigerim", "Bekova",
                "bekova@kbtu.kz", "teacher", "123", 300000.0, "SITE", TeacherPosition.PROFESSOR);
        university.addUser(teacher);

        // Manager
        Manager manager = UserFactory.createManager("M001", "Daniyar", "Seitkali",
                "manager@kbtu.kz", "manager", "123", 350000.0, ManagerType.OR);
        university.addUser(manager);

        // Student
        Student student = UserFactory.createStudent("S001", "Amir", "Nurlan",
                "amir@kbtu.kz", "student", "123", "SITE", 2);
        university.addUser(student);

        // TechSupport
        TechSupportSpecialist tech = UserFactory.createTechSupport("TS001", "Marat", "Omarov",
                "tech@kbtu.kz", "tech", "123", 200000.0);
        university.addUser(tech);

        // Courses
        Course oop = new Course("CSCI2101", "Object-Oriented Programming", 5, CourseType.MAJOR);
        Course math = new Course("MATH1101", "Calculus", 3, CourseType.MINOR);
        university.addCourse(oop);
        university.addCourse(math);
        
        university.research.journal.UniversityJournal journal =
                new university.research.journal.UniversityJournal("J001", "KBTU Research Journal", "1234-5678");
        university.addJournal(journal);
        System.out.println("Done. Logins: admin/teacher/manager/student/tech — password: 123");
    }
}