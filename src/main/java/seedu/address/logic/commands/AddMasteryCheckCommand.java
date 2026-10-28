package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.Student;

/**
 * Records a numbered mastery check appointment for a student in the displayed list.
 * An existing appointment with the same number is retained without changes.
 */
public class AddMasteryCheckCommand extends Command {
    public static final String COMMAND_WORD = "addmc";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a mastery check appointment for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NUMBER + "NUMBER " + PREFIX_DATETIME + "DATE-TIME\n"
            + "NUMBER must be a positive integer identifying a mastery check.\n"
            + "DATE-TIME must be a real date in dd-MM|HH:mm format.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 " + PREFIX_NUMBER + "1 " + PREFIX_DATETIME
            + "27-09|23:59";

    public static final String MESSAGE_SUCCESS =
            "Added %1$s's mastery check %2$s at %3$s.";

    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s mastery check has already been recorded for number %2$d. No changes made.";

    private static final DateTimeFormatter APPOINTMENT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.ENGLISH);

    private final Index targetIndex;
    private final int number;
    private final MasteryCheck masteryCheck;

    /**
     * Creates a command to register a numbered mastery check appointment.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param number Positive mastery check number used to identify the appointment.
     * @param masteryCheck Appointment to register for the student.
     * @throws NullPointerException If the index or mastery check is null.
     * @throws IllegalArgumentException If the mastery check number is not positive.
     */
    public AddMasteryCheckCommand(Index targetIndex, int number, MasteryCheck masteryCheck) {
        requireNonNull(targetIndex);
        requireNonNull(masteryCheck);

        checkArgument(number > 0, RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.number = number;
        this.masteryCheck = masteryCheck;
    }

    /**
     * Registers the appointment without changing the current student-list filter.
     * If the number is already registered, leaves the existing appointment unchanged.
     *
     * @param model Model containing the students.
     * @return Feedback describing the registered appointment or an already registered number.
     * @throws NullPointerException If the model is null.
     * @throws CommandException If the index does not identify a displayed student.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredMasteryChecks registeredMasteryChecks = studentToUpdate.getRegisteredMasteryChecks();

        if (registeredMasteryChecks.hasMasteryCheck(number)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, studentToUpdate.getName(), number));
        }

        Student updatedStudent = studentToUpdate.withRegisteredMasteryChecks(
                registeredMasteryChecks.withMasteryCheck(number, masteryCheck));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(
                MESSAGE_SUCCESS, studentToUpdate.getName(), number,
                masteryCheck.getAppointmentTime().format(APPOINTMENT_TIME_FORMATTER)));
    }

    /**
     * Compares commands by their student index, mastery check number, and appointment details.
     *
     * @param other Object to compare with this command.
     * @return True if the other object is a command with the same target and appointment details.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddMasteryCheckCommand otherAddMasteryCheckCommand)) {
            return false;
        }

        return targetIndex.equals(otherAddMasteryCheckCommand.targetIndex)
                && number == otherAddMasteryCheckCommand.number
                && masteryCheck.equals(otherAddMasteryCheckCommand.masteryCheck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, number, masteryCheck);
    }

    /**
     * Returns the target index, mastery check number, and appointment details for diagnostic output.
     *
     * @return A diagnostic representation of this command.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("number", number)
                .add("masteryCheck", masteryCheck)
                .toString();
    }
}
