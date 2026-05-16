package university.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Employee extends User {
    private static final Logger EMPLOYEE_LOGGER = Logger.getLogger(Employee.class.getName());

    private double salary;

    private String department;

    private List<String> messages;

    private LocalDate hireDate;

    public Employee(String id, String firstName, String lastName, String email, String login, String password, double salary, String department) {
        super(id, firstName, lastName, email, login, password);
        this.salary = salary;
        this.department = department;
        this.hireDate = LocalDate.now();
        this.messages = new ArrayList<>();
    }

    public void sendMessage(Employee receiver, String message) {
        if (receiver == null) {
            EMPLOYEE_LOGGER.log(Level.WARNING, "Attempted to send a message to a null employee.");
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Cannot send an empty message.");
            return;
        }

        String formattedMessage = String.format("From [%s, %s]: %s",
                this.getFullName(), this.getDepartment(), message);

        receiver.receiveMessage(formattedMessage);
        EMPLOYEE_LOGGER.log(Level.INFO, "Message sent from {0} to {1}",
                new Object[]{this.getId(), receiver.getId()});
    }

    public void receiveMessage(String message) {
        if (message != null) {
            this.messages.add(message);
        }
    }

    public void viewMessages() {
        System.out.println("=== Inbox for " + getFullName() + " ===");
        if (messages.isEmpty()) {
            System.out.println("No messages.");
            return;
        }
        messages.forEach(System.out::println);
    }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(department, employee.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), department);
    }

    @Override
    public String toString() {
        return String.format("Employee{id='%s', name='%s', dept='%s', salary=%.2f, hired=%s}",
                getId(), getFullName(), department, salary, hireDate);
    }
}