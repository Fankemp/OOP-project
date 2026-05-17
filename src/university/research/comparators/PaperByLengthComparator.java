package research.comparators;

import research.ResearchPaper;
import java.util.Comparator;

public class PaperByLengthComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        return Integer.compare(p2.getPageCount(), p1.getPageCount());
    }
}
