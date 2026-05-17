package research.comparators;

import research.ResearchPaper;
import java.util.Comparator;

public class PaperByDateComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        return p2.getPublishedDate().compareTo(p1.getPublishedDate());
    }
}
