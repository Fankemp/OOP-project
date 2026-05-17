import exceptions.LowHIndexException;
import exceptions.NotAResearcherException;
import research.*;
import research.comparators.*;
import research.journal.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ResearchDemo {

    static class SimpleResearcher implements Researcher, JournalObserver {
        private final String name;
        private final ResearchProfile profile;

        SimpleResearcher(String id, String name) {
            this.name = name;
            this.profile = new ResearchProfile(id);
        }

        @Override public void addPaper(ResearchPaper p)                  { profile.addPaper(p); }
        @Override public List<ResearchPaper> getPapers()                  { return profile.getPapers(); }
        @Override public void addProject(ResearchProject proj)            { profile.addProject(proj); }
        @Override public List<ResearchProject> getProjects()              { return profile.getProjects(); }
        @Override public int calculateHIndex()                             { return profile.calculateHIndex(); }
        @Override public void printPapers(Comparator<ResearchPaper> c)    { profile.printPapers(c); }
        @Override public ResearchProfile getResearchProfile()             { return profile; }

        @Override
        public void onNewPaperPublished(ResearchPaper paper, UniversityJournal journal) {
            System.out.println("  [" + name + "] New paper in '" +
                    journal.getName() + "': " + paper.getTitle());
        }

        @Override public String toString() { return name; }
    }

    static class PlainEmployee {
        private final String name;
        PlainEmployee(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    static void assignSupervisor(Researcher candidate, String student) throws LowHIndexException {
        if (candidate.calculateHIndex() < 3)
            throw new LowHIndexException(candidate.toString(), candidate.calculateHIndex());
        System.out.println("Supervisor assigned to " + student);
    }

    public static void main(String[] args) {

        ResearchPaper p1 = new ResearchPaper(
                "LMS Logs and Student Performance: The Influence of Retaking a Course",
                List.of("Shamoi Pakizar", "Baisalbayeva Kamila"),
                "IEEE Access", 1, 14,
                LocalDate.of(2023, 5, 10), "10.1109/ACCESS.2023.123456", 87);

        ResearchPaper p2 = new ResearchPaper(
                "Deep Learning for Early Dropout Prediction in MOOCs",
                List.of("Shamoi Pakizar", "Nurmagambetov Aibek"),
                "Computers & Education", 55, 70,
                LocalDate.of(2022, 11, 3), "10.1016/j.compedu.2022.987654", 54);

        ResearchPaper p3 = new ResearchPaper(
                "Transformer-based Models in NLP: A Survey",
                List.of("Akhmetov Ilyas"),
                "ACM Computing Surveys", 1, 38,
                LocalDate.of(2024, 1, 20), "10.1145/3579654", 22);

        ResearchPaper p4 = new ResearchPaper(
                "Graph Neural Networks for Citation Prediction",
                List.of("Akhmetov Ilyas", "Seitkali Aigerim"),
                "Pattern Recognition Letters", 100, 108,
                LocalDate.of(2021, 7, 15), "10.1016/j.patrec.2021.112233", 9);

        // Citation formats
        System.out.println(p1.getCitation(CitationFormat.PLAIN_TEXT));
        System.out.println();
        System.out.println(p1.getCitation(CitationFormat.BIBTEX));

        // Researchers
        SimpleResearcher pakizar = new SimpleResearcher("R001", "Pakizar Shamoi");
        pakizar.addPaper(p1);
        pakizar.addPaper(p2);

        SimpleResearcher ilyas = new SimpleResearcher("R002", "Ilyas Akhmetov");
        ilyas.addPaper(p3);
        ilyas.addPaper(p4);

        System.out.println("\n" + pakizar + " h-index = " + pakizar.calculateHIndex());
        System.out.println(ilyas + " h-index = " + ilyas.calculateHIndex());

        // Supervisor guard
        try { assignSupervisor(pakizar, "Master Student A"); }
        catch (LowHIndexException e) { System.out.println(e.getMessage()); }

        ResearchPaper p5 = new ResearchPaper(
                "Attention Mechanisms in Educational Data Mining",
                List.of("Shamoi Pakizar"), "Expert Systems with Applications",
                1, 12, LocalDate.of(2024, 3, 1),
                "10.1016/j.eswa.2024.000001", 5);
        pakizar.addPaper(p5);

        try { assignSupervisor(pakizar, "Master Student A"); }
        catch (LowHIndexException e) { System.out.println(e.getMessage()); }

        // Comparators
        System.out.println("\nBy citations:");
        pakizar.printPapers(new PaperByCitationsComparator());
        System.out.println("\nBy date:");
        pakizar.printPapers(new PaperByDateComparator());
        System.out.println("\nBy length:");
        pakizar.printPapers(new PaperByLengthComparator());

        // ResearchProject
        ResearchProject project = new ResearchProject("P001", "AI in Education");
        try {
            project.addParticipant(pakizar);
            project.addParticipant(ilyas);
            project.addParticipant(new PlainEmployee("Someone"));
        } catch (NotAResearcherException e) {
            System.out.println(e.getMessage());
        }

        // Journal / Observer
        UniversityJournal journal = new UniversityJournal("J001", "KBTU Research Journal", "1234-5678");
        journal.subscribe(pakizar);
        journal.subscribe(ilyas);
        journal.publishPaper(p5);

        // ResearchManager
        ResearchManager rm = ResearchManager.getInstance();
        rm.registerResearcher(pakizar);
        rm.registerResearcher(ilyas);

        System.out.println();
        rm.printAllPapers(new PaperByCitationsComparator());
        rm.printTopCitedResearcherOfYear(2023);
    }
}
