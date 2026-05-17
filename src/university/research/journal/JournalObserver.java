package university.research.journal;

import university.research.ResearchPaper;

public interface JournalObserver {
    void onNewPaperPublished(ResearchPaper paper, UniversityJournal journal);
}
