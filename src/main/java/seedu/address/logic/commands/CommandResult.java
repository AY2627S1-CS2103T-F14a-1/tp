package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Student;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** Student details requested by a view command, or null for ordinary feedback. */
    private final Student studentToView;
    private final List<Mission> missions;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.studentToView = null;
        this.missions = List.of();
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Constructs a result displaying a student's details and mission submissions.
     *
     * @param feedbackToUser Textual student details for logging and command feedback.
     * @param studentToView Student whose records should be displayed.
     * @throws NullPointerException If either argument is null.
     */
    public CommandResult(String feedbackToUser, Student studentToView) {
        this(feedbackToUser, studentToView, List.of());
    }

    /**
     * Constructs a student view with a snapshot of configured assignments.
     *
     * @param feedbackToUser Student summary.
     * @param studentToView Student to display.
     * @param missions Missions whose submission status should be displayed.
     * @throws NullPointerException If any argument or mission is null.
     */
    public CommandResult(String feedbackToUser, Student studentToView, List<Mission> missions) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.studentToView = requireNonNull(studentToView);
        this.missions = List.copyOf(missions);
        this.showHelp = false;
        this.exit = false;
    }

    /**
     * Returns the student requested by a view command, if present.
     *
     * @return The student to display, or an empty optional for ordinary feedback.
     */
    public Optional<Student> getStudentToView() {
        return Optional.ofNullable(studentToView);
    }

    /**
     * Returns the immutable catalogue snapshot for the student view.
     *
     * @return Missions to display.
     */
    public List<Mission> getMissions() {
        return missions;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && Objects.equals(studentToView, otherCommandResult.studentToView)
                && missions.equals(otherCommandResult.missions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, studentToView, missions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .toString();
    }

}
