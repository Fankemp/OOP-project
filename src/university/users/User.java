package university.users;

import java.io.Serializable;
import java.io.Serial;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private static final Logger LOGGER = Logger.getLogger(User.class.getName());

    // ИНКАПСУЛЯЦИЯ: Все поля строго private
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private String login;
    private String password;

    public User(String id, String firstName, String lastName, String email, String login, String password) {
        this.id = Objects.requireNonNull(id, "ID не может быть null");
        this.firstName = Objects.requireNonNull(firstName, "Имя не может быть null");
        this.lastName = Objects.requireNonNull(lastName, "Фамилия не может быть null");
        this.email = Objects.requireNonNull(email, "Email не может быть null");
        this.login = Objects.requireNonNull(login, "Логин не может быть null");
        this.password = Objects.requireNonNull(password, "Пароль не может быть null");

        LOGGER.log(Level.INFO, "User создан: {0} {1} (ID: {2})", new Object[]{firstName, lastName, id});
    }

    // Публичный интерфейс доступа (API класса)
    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setLogin(String login) { this.login = Objects.requireNonNull(login); }
    public void setPassword(String password) { this.password = Objects.requireNonNull(password); }

    public boolean checkPassword(String password) { return this.password.equals(password); }

    public boolean verifyCredentials(String inputLogin, String inputPassword) {
        if (inputLogin == null || inputPassword == null) {
            return false;
        }
        return this.login.equals(inputLogin.trim()) && this.password.equals(inputPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("%s %s (ID: %s)", firstName, lastName, id);
    }
}