package research.journal;

import research.ResearchPaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UniversityJournal {

    private final String journalId;
    private String name;
    private String issn;
    private final List<ResearchPaper> papers = new ArrayList<>();
    private final List<JournalObserver> subscribers = new ArrayList<>();

    public UniversityJournal(String journalId, String name, String issn) {
        this.journalId = journalId;
        this.name = name;
        this.issn = issn;
    }

    public void subscribe(JournalObserver observer) {
        if (!subscribers.contains(observer)) subscribers.add(observer);
    }

    public void unsubscribe(JournalObserver observer) {
        subscribers.remove(observer);
    }

    public void publishPaper(ResearchPaper paper) {
        if (!papers.contains(paper)) papers.add(paper);
        System.out.println("[" + name + "] New paper: \"" + paper.getTitle() + "\"");
        for (JournalObserver o : subscribers) o.onNewPaperPublished(paper, this);
    }

    public String getJournalId()                { return journalId; }
    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }
    public String getIssn()                     { return issn; }
    public void setIssn(String issn)            { this.issn = issn; }
    public List<ResearchPaper> getPapers()      { return new ArrayList<>(papers); }
    public int getSubscriberCount()             { return subscribers.size(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniversityJournal)) return false;
        return Objects.equals(journalId, ((UniversityJournal) o).journalId);
    }

    @Override
    public int hashCode() { return Objects.hash(journalId); }

    @Override
    public String toString() {
        return "UniversityJournal{name='" + name + "', issn='" + issn +
               "', papers=" + papers.size() + ", subscribers=" + subscribers.size() + "}";
    }
}
