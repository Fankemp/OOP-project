package university.user;

import university.enums.DegreeType;
import university.exceptions.LowHIndexException;
import university.research.ResearchPaper;
import university.research.ResearchProject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GraduateStudent extends Student implements Researcher {

    private static final int MIN_SUPERVISOR_H_INDEX = 3;

    private Researcher supervisor;
    private DegreeType degreeType;
    private List<ResearchPaper> diplomaPapers;
    private List<ResearchPaper> researchPapers;
    private List<ResearchProject> researchProjects;

    public GraduateStudent(String id, String firstName, String lastName,
                           String email, String login, String password,
                           String major, int yearOfStudy, DegreeType degreeType) {
        super(id, firstName, lastName, email, login, password, major, yearOfStudy);
        this.degreeType = degreeType;
        this.diplomaPapers   = new ArrayList<>();
        this.researchPapers  = new ArrayList<>();
        this.researchProjects = new ArrayList<>();
    }

    public void setSupervisor(Researcher r) throws LowHIndexException {
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
            if (!researchPapers.contains(paper)) {
                researchPapers.add(paper);
            }
        }
    }

    public List<ResearchPaper> getDiplomaPapers() {
        return new ArrayList<>(diplomaPapers);
    }

    @Override
    public int calculateHIndex() {
        List<Integer> citations = researchPapers.stream()
                .map(ResearchPaper::getCitations)
                .sorted(Comparator.reverseOrder())
                .toList();

        int h = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) {
                h = i + 1;
            } else {
                break;
            }
        }
        return h;
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        researchPapers.stream()
                .sorted(c)
                .forEach(System.out::println);
    }

    @Override
    public List<ResearchPaper> getResearchPapers() {
        return new ArrayList<>(researchPapers);
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return new ArrayList<>(researchProjects);
    }

    public void addResearchPaper(ResearchPaper paper) {
        if (paper != null && !researchPapers.contains(paper)) {
            researchPapers.add(paper);
        }
    }

    public void addResearchProject(ResearchProject project) {
        if (project != null && !researchProjects.contains(project)) {
            researchProjects.add(project);
        }
    }

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    @Override
    public String toString() {
        return String.format("GraduateStudent{id='%s', name='%s', degree=%s, major='%s', gpa=%.2f, supervisor=%s}",
                getId(), getFullName(), degreeType,
                getMajor(), getGpa(),
                supervisor != null ? ((User) supervisor).getFullName() : "none");
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