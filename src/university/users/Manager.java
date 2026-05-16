package university.users;

import university.enums.ManagerType; // Предположим, что тип менеджера лежит в enums
import java.io.Serial;

public class Manager extends Employee {
    @Serial
    private static final long serialVersionUID = 2026L;

    private ManagerType managerType;

    public Manager(String id, String firstName, String lastName, String email, String login, String password, double salary, ManagerType managerType) {
        super(id, firstName, lastName, email, login, password, salary);
        this.managerType = managerType;
    }

    public ManagerType getManagerType() { return managerType; }
    public void setManagerType(ManagerType managerType) { this.managerType = managerType; }
}