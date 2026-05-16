package university.users;

import java.io.Serial;

public class Admin extends Employee {
    @Serial
    private static final long serialVersionUID = 2026L;

    public Admin(String id, String firstName, String lastName, String email, String login, String password, double salary) {
        super(id, firstName, lastName, email, login, password, salary);
    }
}