package university.research;

import university.exceptions.NotAResearcherException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResearchProject {
    private final String projectId;
    private String topic;
    private final List<ResearchPaper> publishedPapers;
    private final List<Researcher> participants;

    public ResearchProject(String projectId, String topic) {
        this.projectId = projectId;
        this.topic = topic;
        this.publishedPapers = new ArrayList<>();
        this.participants = new ArrayList<>();
    }

    public void addParticipant(Object person) throws NotAResearcherException {
        if (!(person instanceof Researcher)) {
            throw new NotAResearcherException(
                    person.getClass().getSimpleName() + " is not a Researcher.");
        }
        Researcher r = (Researcher) person;
        if (!participants.contains(r)) {
            participants.add(r);
            r.addProject(this);
        }
    }

    public void removeParticipant(Researcher r) {
        participants.remove(r);
    }

    public void addPaper(ResearchPaper paper) {
        if (!publishedPapers.contains(paper)) publishedPapers.add(paper);
    }

    public String getProjectId()                    { return projectId; }
    public String getTopic()                        { return topic; }
    public void setTopic(String topic)              { this.topic = topic; }
    public List<ResearchPaper> getPublishedPapers() { return new ArrayList<>(publishedPapers); }
    public List<Researcher> getParticipants()       { return new ArrayList<>(participants); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        return Objects.equals(projectId, ((ResearchProject) o).projectId);
    }

    @Override
    public int hashCode() { return Objects.hash(projectId); }

    @Override
    public String toString() {
        return "ResearchProject{id='" + projectId + "', topic='" + topic +
                "', participants=" + participants.size() + ", papers=" + publishedPapers.size() + "}";
    }
}
