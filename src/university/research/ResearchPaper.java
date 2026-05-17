package university.research;

import university.enums.CitationFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResearchPaper implements Comparable<ResearchPaper> {

    private String title;
    private List<String> authors;
    private String journal;
    private int startPage;
    private int endPage;
    private LocalDate publishedDate;
    private String doi;
    private int citations;

    public ResearchPaper(String title, List<String> authors, String journal,
                         int startPage, int endPage, LocalDate publishedDate,
                         String doi, int citations) {
        this.title = title;
        this.authors = new ArrayList<>(authors);
        this.journal = journal;
        this.startPage = startPage;
        this.endPage = endPage;
        this.publishedDate = publishedDate;
        this.doi = doi;
        this.citations = citations;
    }

    public String getCitation(CitationFormat format) {
        if (format == CitationFormat.PLAIN_TEXT) return buildPlainText();
        if (format == CitationFormat.BIBTEX) return buildBibtex();
        return "";
    }

    private String buildPlainText() {
        return String.join(", ", authors) +
                " (" + publishedDate.getYear() + "). " +
                title + ". " +
                journal + ", pp. " + startPage + "–" + endPage + ". " +
                "https://doi.org/" + doi;
    }

    private String buildBibtex() {
        String lastName = authors.get(0).contains(" ")
                ? authors.get(0).split(" ")[authors.get(0).split(" ").length - 1].toLowerCase()
                : authors.get(0).toLowerCase();
        String key = lastName + publishedDate.getYear();

        return "@article{" + key + ",\n" +
                "  author  = {" + String.join(" and ", authors) + "},\n" +
                "  title   = {" + title + "},\n" +
                "  journal = {" + journal + "},\n" +
                "  year    = {" + publishedDate.getYear() + "},\n" +
                "  pages   = {" + startPage + "--" + endPage + "},\n" +
                "  doi     = {" + doi + "}\n" +
                "}";
    }

    public int getPageCount() {
        return endPage - startPage + 1;
    }

    public String getTitle()            { return title; }
    public List<String> getAuthors()    { return new ArrayList<>(authors); }
    public String getJournal()          { return journal; }
    public int getStartPage()           { return startPage; }
    public int getEndPage()             { return endPage; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public String getDoi()              { return doi; }
    public int getCitations()           { return citations; }
    public void setCitations(int c)     { this.citations = c; }

    @Override
    public int compareTo(ResearchPaper other) {
        return Integer.compare(other.citations, this.citations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        return Objects.equals(doi, ((ResearchPaper) o).doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }

    @Override
    public String toString() {
        return "\"" + title + "\" by " + String.join(", ", authors) +
                " | " + journal + " (" + publishedDate.getYear() + ")" +
                " | pp." + startPage + "-" + endPage +
                " | cited " + citations + " | DOI:" + doi;
    }
}
