package university.user;

public abstract class Employee extends User {
    private double salary;
    // Сюда еще департмент добавим

    public Employee(String id, String firstName, String lastName, String email, String login, String password, double salary) {
        super(id, firstName, lastName, email, login, password);
        this.salary = salary;
    }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}