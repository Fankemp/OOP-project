package university.users;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class StudentOrganization implements Serializable {
    @Serial
    private static final long serialVersionUID = 2026L;

    private final String name;
    private Student head;
    private final List<Student> members;

    public StudentOrganization(String name, Student head) {
        this.name = Objects.requireNonNull(name);
        this.head = head;
        this.members = new ArrayList<>();
        if (head != null) members.add(head);
    }

    public void addMember(Student student) {
        if (student != null && !members.contains(student)) {
            members.add(student);
            System.out.printf("Student %s joined organization '%s'.%n",
                    student.getFullName(), name);
        }
    }

    public void removeMember(Student student) {
        members.remove(student);
        System.out.printf("Student %s left organization '%s'.%n",
                student.getFullName(), name);
    }

    public void setHead(Student student) {
        if (members.contains(student)) {
            this.head = student;
            System.out.printf("%s is now head of '%s'.%n",
                    student.getFullName(), name);
        } else {
            System.out.println("Student must be a member first.");
        }
    }

    public String getName() { return name; }
    public Student getHead() { return head; }
    public List<Student> getMembers() { return Collections.unmodifiableList(members); }

    @Override
    public String toString() {
        String headName = head != null ? head.getFullName() : "none";
        return String.format("Organization{name='%s', head=%s, members=%d}",
                name, headName, members.size());
    }
}