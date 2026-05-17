package university.users;

import university.enums.DegreeType;
import university.exceptions.LowHIndexException;
import university.research.ResearchPaper;
import university.research.ResearchProject;
import university.research.Researcher;
import university.research.ResearchProfile;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GraduateStudent extends Student implements Researcher {
    @Serial
    private static final long serialVersionUID = 2026L;

    private static final int MIN_SUPERVISOR_H_INDEX = 3;

    private Researcher supervisor;
    private DegreeType degreeType;

    private final List<ResearchPaper> diplomaPapers;

    private final ResearchProfile researchProfile;

    public GraduateStudent(String id, String firstName, String lastName,
                           String email, String login, String password,
                           String major, int yearOfStudy, DegreeType degreeType) {
        super(id, firstName, lastName, email, login, password, major, yearOfStudy);
        this.degreeType = degreeType;
        this.diplomaPapers = new ArrayList<>();

        // ИСПРАВЛЕНО: Инициализируем делегат
        this.researchProfile = new ResearchProfile(id);
    }


    @Override
    public ResearchProfile getResearchProfile() {
        return this.researchProfile;
    }


    public void setSupervisor(Researcher r) throws LowHIndexException {
        if (r == null) throw new IllegalArgumentException("Supervisor cannot be null.");

        int hIndex = r.calculateHIndex();
        if (hIndex < MIN_SUPERVISOR_H_INDEX) {
            String name = (r instanceof User) ? ((User) r).getFullName() : r.toString();
            throw new LowHIndexException(name, hIndex);
        }
        this.supervisor = r;
    }

    public Researcher getSupervisor() {
        return supervisor;
    }


    public void addDiplomaPaper(ResearchPaper paper) {
        if (paper != null && !diplomaPapers.contains(paper)) {
            diplomaPapers.add(paper);
            // Автоматически отправляем статью в наш общий профиль исследователя
            addPaper(paper);
        }
    }

    public List<ResearchPaper> getDiplomaPapers() {
        return Collections.unmodifiableList(diplomaPapers);
    }

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    @Override
    public String toString() {
        String supervisorName = "none";
        if (supervisor != null) {
            supervisorName = (supervisor instanceof User) ? ((User) supervisor).getFullName() : supervisor.toString();
        }

        return String.format("GraduateStudent{id='%s', name='%s', degree=%s, major='%s', gpa=%.2f, supervisor=%s}",
                getId(), getFullName(), degreeType, getMajor(), getGpa(), supervisorName);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}