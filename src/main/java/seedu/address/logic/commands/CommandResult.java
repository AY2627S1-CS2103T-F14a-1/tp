package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Student;

/**
 * Represents the result of executing a command.
 *
 * <p>A command result contains feedback for the user and optional
 * control information, such as whether help should be shown, whether
 * the application should exit, or which student should be displayed
 * in the profile panel.</p>
 */
public class CommandResult {

    /**
     * Student to display in the profile panel, if any.
     */
    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** Student details requested by a view command, or null for ordinary feedback. */
    private final Student studentToView;
    private final List<Mission> missions;

    /**
     * Creates a {@code CommandResult} with the specified fields.
     *
     * @param feedbackToUser feedback message displayed to the user
     * @param showHelp whether the help window should be shown
     * @param exit whether the application should exit
     * @param studentToView student to display in the profile panel,
     *                      or {@code null} if no profile should be displayed
     * @param missions missions to display in the profile panel
     */
    public CommandResult(String feedbackToUser,
                         boolean showHelp,
                         boolean exit,
                         Student studentToView,
                         List<Mission> missions) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.studentToView = studentToView;
        this.missions = requireNonNull(missions);
    }

    /**
     * Creates a {@code CommandResult} without a student profile target.
     *
     * @param feedbackToUser feedback message displayed to the user
     * @param studentToView student to display in the profile panel
     * @param missions missions to display in the profile panel
     */
    public CommandResult(
            String feedbackToUser,
            Student studentToView,
            List<Mission> missions) {

        this(feedbackToUser, false, false, studentToView, missions);
    }

    /**
     * Creates a basic {@code CommandResult} with no help, exit,
     * or student-profile action.
     *
     * @param feedbackToUser feedback message displayed to the user
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, null, List.of());
    }

    /**
     * Creates a {@code CommandResult} that displays the specified student
     * in the profile panel.
     *
     * @param feedbackToUser feedback message displayed to the user
     * @param studentToView student to display in the profile panel
     */
    public CommandResult(String feedbackToUser, Student studentToView) {
        this(feedbackToUser, false, false, studentToView, List.of());
    }

    /**
     * Creates a {@code CommandResult} that shows help or exits the application.
     *
     * @param feedbackToUser feedback message displayed to the user
     * @param showHelp whether the help window should be shown
     * @param exit whether the application should exit
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, List.of());
    }

    /**
     * Returns the immutable catalogue snapshot for the student view.
     *
     * @return Missions to display.
     */
    public List<Mission> getMissions() {
        return missions;
    }

    /**
     * Returns the feedback message for the user.
     *
     * @return feedback message
     */
    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    /**
     * Returns the student to display in the profile panel, if one was supplied.
     *
     * @return optional student profile target
     */
    public Optional<Student> getStudentToView() {
        return Optional.ofNullable(studentToView);
    }

    /**
     * Returns whether the help window should be shown.
     *
     * @return {@code true} if help should be shown
     */
    public boolean isShowHelp() {
        return showHelp;
    }

    /**
     * Returns whether the application should exit.
     *
     * @return {@code true} if the application should exit
     */
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
                && Objects.equals(missions, otherCommandResult.missions);
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
                .add("studentToView", studentToView)
                .add("missions", missions)
                .toString();
    }

}
