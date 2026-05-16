package university.exceptions;

public class NotAResearcherException extends Exception {

    public NotAResearcherException(String userName) {
        super(String.format(
                "User '%s' is not a Researcher and cannot join a ResearchProject.",
                userName
        ));
    }
}