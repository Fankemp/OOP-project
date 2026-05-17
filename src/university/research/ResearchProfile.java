package research;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResearchProfile {

    private final String ownerId;
    private final List<ResearchPaper> papers;
    private final List<ResearchProject> projects;

    public ResearchProfile(String ownerId) {
        this.ownerId = ownerId;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public void addPaper(ResearchPaper paper) {
        if (!papers.contains(paper)) papers.add(paper);
    }

    public void addProject(ResearchProject project) {
        if (!projects.contains(project)) projects.add(project);
    }

    public List<ResearchPaper> getPapers()       { return new ArrayList<>(papers); }
    public List<ResearchProject> getProjects()   { return new ArrayList<>(projects); }
    public String getOwnerId()                   { return ownerId; }

    public int calculateHIndex() {
        if (papers.isEmpty()) return 0;

        List<Integer> counts = new ArrayList<>();
        for (ResearchPaper p : papers) counts.add(p.getCitations());
        counts.sort(Comparator.reverseOrder());

        int h = 0;
        for (int i = 0; i < counts.size(); i++) {
            if (counts.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }

    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (papers.isEmpty()) {
            System.out.println("No papers found.");
            return;
        }
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        sorted.sort(comparator);

        System.out.println("Papers for: " + ownerId + " (h-index = " + calculateHIndex() + ")");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.println((i + 1) + ". " + sorted.get(i));
        }
    }

    public int getTotalCitations() {
        return papers.stream().mapToInt(ResearchPaper::getCitations).sum();
    }

    @Override
    public String toString() {
        return "ResearchProfile{owner='" + ownerId + "', papers=" + papers.size() +
               ", projects=" + projects.size() + ", h-index=" + calculateHIndex() + "}";
    }
}
