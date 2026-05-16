package university.users; // ИСПРАВЛЕНО: пакет теперь совпадает со всем проектом

import java.io.Serializable; // ИСПРАВЛЕНО: импорт для сохранения данных
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Objects;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 2026L;
    private static final Logger LOGGER = Logger.getLogger(User.class.getName());

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String login;
    private String password;
    private String language;
    private boolean isLoggedIn;

    public User(String id, String firstName, String lastName, String email, String login, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.login = login;
        this.password = password;
        this.language = "EN";
        this.isLoggedIn = false;

        LOGGER.log(Level.INFO, "User created: {0} {1} (ID: {2})", new Object[]{firstName, lastName, id});
    }


    public boolean verifyCredentials(String inputLogin, String inputPassword) {
        if (inputLogin == null || inputPassword == null) {
            return false;
        }
        return this.login.equals(inputLogin.trim()) && this.password.equals(inputPassword);
    }

    public boolean login(String username, String password) {
        if (this.login.equals(username) && this.password.equals(password)) {
            this.isLoggedIn = true;
            LOGGER.log(Level.INFO, "User {0} successfully logged in.", id);
            return true;
        }
        LOGGER.log(Level.WARNING, "Failed login attempt for username: {0}", username);
        return false;
    }

    public void logout() {
        if (this.isLoggedIn) {
            this.isLoggedIn = false;
            LOGGER.log(Level.INFO, "User {0} logged out.", id);
        }
    }

    public void switchLanguage(String newLanguage) {
        if (newLanguage != null && !newLanguage.trim().isEmpty()) {
            this.language = newLanguage.toUpperCase();
            LOGGER.log(Level.INFO, "User {0} switched language to {1}", new Object[]{id, this.language});
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getLanguage() { return language; }
    public boolean isLoggedIn() { return isLoggedIn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', lang='%s', authenticated=%b}",
                id, getFullName(), language, isLoggedIn);
    }
}