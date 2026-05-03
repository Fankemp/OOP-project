package university.user;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Objects;

public abstract class User {
    private static final Logger LOGGER = Logger.getLogger(User.class.getName());

    String id;
    String firstName;
    String lastName;
    String email;
    String login;
    String password;

    public User(String id, String firstName, String lastName, String email, String login, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.login = login;
        this.password = password;

        LOGGER.log(Level.INFO, "User created: {0} {1} (ID: {2})", new Object[]{firstName, lastName, id});
    }

    public String getId() { return id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    public boolean checkPassword(String password) { return this.password.equals(password); }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

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
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + firstName + '\'' +
                ", surname='" + lastName + '\'' +
                '}';
    }
}
