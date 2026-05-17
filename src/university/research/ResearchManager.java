package research;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResearchManager {

    private static ResearchManager instance;
    private final List<Researcher> researchers = new ArrayList<>();

    private ResearchManager() {}

    public static ResearchManager getInstance() {
        if (instance == null) instance = new ResearchManager();
        return instance;
    }

    public void registerResearcher(Researcher r) {
        if (r != null && !researchers.contains(r)) researchers.add(r);
    }

    public void unregisterResearcher(Researcher r) {
        researchers.remove(r);
    }

    public List<Researcher> getAllResearchers() {
        return new ArrayList<>(researchers);
    }

    public List<ResearchPaper> getAllPapers() {
        List<ResearchPaper> all = new ArrayList<>();
        for (Researcher r : researchers) {
            for (ResearchPaper p : r.getPapers()) {
                if (!all.contains(p)) all.add(p);
            }
        }
        return all;
    }

    public void printAllPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> all = getAllPapers();
        if (all.isEmpty()) { System.out.println("No papers found."); return; }
        all.sort(comparator);
        System.out.println("All university papers (" + all.size() + "):");
        for (int i = 0; i < all.size(); i++) {
            System.out.println((i + 1) + ". " + all.get(i));
        }
    }

    public Researcher getTopCitedResearcher() {
        Researcher top = null;
        int max = -1;
        for (Researcher r : researchers) {
            int total = r.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum();
            if (total > max) { max = total; top = r; }
        }
        return top;
    }

    public void printTopCitedResearcherOfYear(int year) {
        Researcher top = null;
        int max = -1;
        for (Researcher r : researchers) {
            int total = r.getPapers().stream()
                    .filter(p -> p.getPublishedDate().getYear() == year)
                    .mapToInt(ResearchPaper::getCitations).sum();
            if (total > max) { max = total; top = r; }
        }
        if (top == null || max <= 0) {
            System.out.println("No papers published in " + year + ".");
        } else {
            System.out.println("Top cited researcher of " + year + ": " + top +
                               " (" + max + " citations)");
        }
    }

    public static void printTopCitedResearcherOfSchool(List<Researcher> group, String school) {
        Researcher top = null;
        int max = -1;
        for (Researcher r : group) {
            int total = r.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum();
            if (total > max) { max = total; top = r; }
        }
        System.out.println("Top cited in " + school + ": " +
                (top != null ? top + " (" + max + " citations)" : "none"));
    }
}
