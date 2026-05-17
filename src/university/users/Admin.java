package university.users;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Admin extends Employee {

    private String logFilePath;

    public Admin(String id, String firstName, String lastName,
                 String email, String login, String password,
                 double salary) {
        super(id, firstName, lastName, email, login, password, salary, "Administration");
        this.logFilePath = "logs/university.log";
    }

    public Admin(String id, String firstName, String lastName,
                 String email, String login, String password,
                 double salary, String logFilePath) {
        this(id, firstName, lastName, email, login, password, salary);
        this.logFilePath = logFilePath;
    }

    public void addUser(User user, UserRegistry registry) {
        if (user == null) throw new IllegalArgumentException("User cannot be null.");
        registry.add(user);
        System.out.printf("[Admin %s] Added user: %s%n", getFullName(), user);
    }

    /**
     * Removes a user by their unique ID.
     *
     * @param userId the ID of the user to remove
     * @param registry the central user store
     * @return true if the user was found and removed, false otherwise
     */
    public boolean removeUser(String userId, UserRegistry registry) {
        boolean removed = registry.removeById(userId);
        if (removed) {
            System.out.printf("[Admin %s] Removed user with id: %s%n", getFullName(), userId);
        } else {
            System.out.printf("[Admin %s] No user found with id: %s%n", getFullName(), userId);
        }
        return removed;
    }

    /**
     * Replaces an existing user record with updated data.
     * Matching is done by user ID.
     */
    public void updateUser(User updatedUser, UserRegistry registry) {
        if (updatedUser == null) throw new IllegalArgumentException("Updated user cannot be null.");
        registry.update(updatedUser);
        System.out.printf("[Admin %s] Updated user: %s%n", getFullName(), updatedUser);
    }


    public List<String> viewLogs() {
        List<String> lines = new ArrayList<>();
        System.out.printf("[Admin %s] === System Logs (%s) ===%n", getFullName(), logFilePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Could not read log file: " + e.getMessage());
        }
        return lines;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public String toString() {
        return String.format("Admin{id='%s', name='%s', department='%s'}",
                getId(), getFullName(), getDepartment());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public interface UserRegistry {
        void add(User user);
        boolean removeById(String id);
        void update(User user);
        List<User> getAll();
    }
}