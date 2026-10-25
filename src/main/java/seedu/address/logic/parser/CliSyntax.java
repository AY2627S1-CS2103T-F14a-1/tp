package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/",
            "Mark a name using %1$s.",
            "Name must be specified only once.");
    public static final Prefix PREFIX_INDEX = new Prefix("id/",
            "Mark a student index using %1$s.",
            "Student index must be specified only once.");
    public static final Prefix PREFIX_NUMBER = new Prefix("num/",
            "Mark a mastery check number using %1$s.",
            "Mastery check number must be specified only once.");
    public static final Prefix PREFIX_PHONE = new Prefix("p/",
            "Mark a phone number using %1$s.",
            "Phone number must be specified only once.");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/",
            "Mark an email address using %1$s.",
            "Email address must be specified only once.");
    public static final Prefix PREFIX_TAG = new Prefix("t/",
            "Mark a tag using %1$s.",
            "Tag must be specified only once.");
    public static final Prefix PREFIX_WEEK = new Prefix("w/",
            "Mark a studio week using %1$s.",
            "Studio week must be specified only once.");
    public static final Prefix PREFIX_STUDIO_GROUP = new Prefix("g/",
            "Mark a studio group using %1$s.",
            "Studio group must be specified only once.");
    public static final Prefix PREFIX_STUDIO_EFFORT = new Prefix("ef/",
            "Mark a studio effort score using %1$s.",
            "Studio effort score must be specified only once.");
    public static final Prefix PREFIX_DATE = new Prefix("d/",
            "Mark a deadline using %1$s.",
            "Deadline must be specified only once.");
    public static final Prefix PREFIX_DATETIME = new Prefix("dt/",
            "Mark a mastery check timing using %1$s.",
            "Mastery check timing must be specified only once.");
    public static final Prefix PREFIX_MISSION = new Prefix("m/",
            "Mark a mission ID using %1$s.",
            "Mission ID must be specified only once.");
    public static final Prefix PREFIX_NOTE_ID = new Prefix("ni/",
            "Mark a note ID using %1$s.",
            "Note ID must be specified only once.");
    public static final Prefix PREFIX_NOTE_TEXT = new Prefix("nt/",
            "Mark a note text using %1$s.",
            "Note text must be specified only once.");
}
