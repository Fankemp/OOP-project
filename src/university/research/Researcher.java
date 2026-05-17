package research;

import java.util.Comparator;
import java.util.List;

public interface Researcher {
    void addPaper(ResearchPaper paper);
    List<ResearchPaper> getPapers();
    void addProject(ResearchProject project);
    List<ResearchProject> getProjects();
    int calculateHIndex();
    void printPapers(Comparator<ResearchPaper> comparator);
    ResearchProfile getResearchProfile();
}
