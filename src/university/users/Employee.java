package university.users;

import java.io.Serial;

public abstract class Employee extends User {
    @Serial
    private static final long serialVersionUID = 2026L;

    private double salary;

    public Employee(String id, String firstName, String lastName, String email, String login, String password, double salary) {
        super(id, firstName, lastName, email, login, password);
        if (salary < 0) throw new IllegalArgumentException("Зарплата не может быть отрицательной");
        this.salary = salary;
    }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}