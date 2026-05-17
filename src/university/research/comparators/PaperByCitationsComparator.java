package research.comparators;

import research.ResearchPaper;
import java.util.Comparator;

public class PaperByCitationsComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        return Integer.compare(p2.getCitations(), p1.getCitations());
    }
}
