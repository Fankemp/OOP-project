package university.research;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;


public interface Researcher {

    ResearchProfile getResearchProfile();

    default void addPaper(ResearchPaper paper) {
        if (getResearchProfile() != null) {
            getResearchProfile().addPaper(paper);
        }
    }

    default List<ResearchPaper> getPapers() {
        return getResearchProfile() != null ? getResearchProfile().getPapers() : List.of();
    }

    default void addProject(ResearchProject project) {
        if (getResearchProfile() != null) {
            getResearchProfile().addProject(project);
        }
    }

    default List<ResearchProject> getProjects() {
        return getResearchProfile() != null ? getResearchProfile().getProjects() : List.of();
    }

    default int calculateHIndex() {
        return getResearchProfile() != null ? getResearchProfile().calculateHIndex() : 0;
    }

    default void printPapers(Comparator<ResearchPaper> comparator) {
        if (getResearchProfile() != null) {
            List<ResearchPaper> sortedPapers = new ArrayList<>(getPapers());
            sortedPapers.sort(comparator);
            sortedPapers.forEach(System.out::println);
        }
    }
}