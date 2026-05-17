package research.journal;

import research.ResearchPaper;

public interface JournalObserver {
    void onNewPaperPublished(ResearchPaper paper, UniversityJournal journal);
}
